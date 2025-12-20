package com.edumento.category.services;

import java.io.IOException;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.edumento.category.domain.Category;
import com.edumento.category.domain.CategoryGradesAndChapter;
import com.edumento.category.model.CategoryModel;
import com.edumento.category.model.CreateCategoryModel;
import com.edumento.category.model.LoadCategoryModel;
import com.edumento.category.repos.CategoryRepository;
import com.edumento.category.repos.mongodb.CategoryGradesAndChapterRepository;
import com.edumento.core.configuration.auditing.Auditable;
import com.edumento.core.configuration.notifications.Message;
import com.edumento.core.constants.Code;
import com.edumento.core.constants.Services;

import com.edumento.core.constants.notification.EntityAction;
import com.edumento.core.exception.ExistException;

import com.edumento.core.exception.MintException;
import com.edumento.core.exception.NotFoundException;
import com.edumento.core.exception.NotPermittedException;
import com.edumento.core.model.PageResponseModel;
import com.edumento.core.model.ResponseModel;

import com.edumento.core.model.messages.From;
import com.edumento.core.model.messages.category.CategoryMessageInfo;
import com.edumento.core.security.SecurityUtils;

import com.edumento.space.repos.SpaceRepository;
import com.edumento.user.constant.UserType;
import com.edumento.user.repo.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;

/** Created by ahmad on 3/13/16. */
@Service
public class CategoryService {

	private final Logger log = LoggerFactory.getLogger(CategoryService.class);

	private final CategoryRepository categoryRepository;

	private final UserRepository userRepository;

	private final SpaceRepository spaceRepository;

	private final CategoryGradesAndChapterRepository categoryGradesAndChapterRepository;

	@Value("${mint.url}")
	private String url;

	@Autowired
	public CategoryService(CategoryRepository categoryRepository, UserRepository userRepository,
			SpaceRepository spaceRepository, CategoryGradesAndChapterRepository categoryGradesAndChapterRepository) {
		this.categoryRepository = categoryRepository;
		this.userRepository = userRepository;
		this.spaceRepository = spaceRepository;
		this.categoryGradesAndChapterRepository = categoryGradesAndChapterRepository;
	}

	@Transactional
	@Auditable(EntityAction.CATEGORY_CREATE)
	@PreAuthorize("hasAuthority('CATEGORY_CREATE') AND hasAuthority('ADMIN')")
	@Message(entityAction = EntityAction.CATEGORY_CREATE, services = Services.NOTIFICATIONS)
	public ResponseModel createCategory(CreateCategoryModel createCategoryModel) {
		log.debug("Create category with model {}", createCategoryModel);
		return userRepository.findOneByUserNameAndDeletedFalse(SecurityUtils.getCurrentUserLogin()).map(user -> {
			if (user.getType() == UserType.SUPER_ADMIN || user.getType() == UserType.SYSTEM_ADMIN) {
				if (categoryRepository.findOneByNameAndDeletedFalse(
						createCategoryModel.getName()).isPresent()) {
					throw new ExistException("error.category.exist");
				}
				var category = new Category();
				category.setName(createCategoryModel.getName());
				category.setImage(createCategoryModel.getImage());
				category.setNameAr(createCategoryModel.getNameAr());
				category.setThumbnail(createCategoryModel.getThumbnail());
				categoryRepository.saveAndFlush(category);
				var categoryMessageInfo = new CategoryMessageInfo(category.getId(), category.getName(),
						new From(SecurityUtils.getCurrentUser()));
				return ResponseModel.done(categoryMessageInfo);
			}
			throw new NotPermittedException();
		}).orElseThrow(NotPermittedException::new);
	}

	@Transactional(readOnly = true)
	@PreAuthorize("hasAuthority('CATEGORY_READ')")
	public ResponseModel getCategories(PageRequest page, String filter, boolean all, String lang) {
		log.debug("get categories");

		Specification<Category> name = null;
		Specification<Category> deletedFalse = (root, query, cb) -> cb.equal(root.get("deleted"), Boolean.FALSE);

		if (filter != null) {
			name = (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder
					.like(criteriaBuilder.lower(root.get("name")), "%" + filter.toLowerCase() + "%");
		}

		Page<CategoryModel> categoryModels = categoryRepository
				.findAll(Specification.where(name).and(deletedFalse), page)
				.map(category -> getCategoryModel(category, "en"));
		return PageResponseModel.done(categoryModels.getContent(), categoryModels.getTotalPages(),
				categoryModels.getNumber(), categoryModels.getTotalElements());
	}

	@Transactional(readOnly = true)
	@PreAuthorize("hasAuthority('CATEGORY_READ')")
	public ResponseModel getUserCategoriesRelatedWithSpaces() {
		return ResponseModel.done(categoryRepository
				.findRelatedCategoryWithSpaceForUserAndDeletedFalse(SecurityUtils.getCurrentUser().getId())
				.map(category -> getCategoryModel(category, "en")).collect(Collectors.toSet()));
	}

	@Transactional(readOnly = true)
	@PreAuthorize("hasAuthority('CATEGORY_READ') and hasAuthority('SYSTEM_ADMIN')")
	public ResponseModel getCloudCategories() {
		return ResponseModel.done(categoryRepository.findByDeletedFalse()
				.map(category -> getCategoryModel(category, "en")).collect(Collectors.toSet()));
	}

	@Transactional
	@Auditable(EntityAction.CATEGORY_UPDATE)
	@PreAuthorize("hasAuthority('CATEGORY_UPDATE') AND hasAuthority('ADMIN')")
	// TODO: Business Required
	@Message(entityAction = EntityAction.CATEGORY_UPDATE, services = Services.NOTIFICATIONS, indexOfId = 1)
	public ResponseModel updateCategory(CreateCategoryModel createCategoryModel, Long id) {
		log.debug("update category: {}", createCategoryModel);
		return userRepository.findOneByUserNameAndDeletedFalse(SecurityUtils.getCurrentUserLogin())
				.map(user -> categoryRepository.findById(id).map(category -> {
					if (!createCategoryModel.getName().equals(category.getName())) {
						if (categoryRepository.findOneByNameAndDeletedFalse(
								createCategoryModel.getName()).isPresent()) {
							log.warn("category named {} not Exist", category.getName());
							throw new ExistException();
						}
					}
					category.setColor(createCategoryModel.getColor());
					category.setName(createCategoryModel.getName());
					category.setNameAr(createCategoryModel.getNameAr());
					category.setThumbnail(createCategoryModel.getThumbnail());
					category.setImage(createCategoryModel.getImage());
					var categoryGradesAndChapter = categoryGradesAndChapterRepository
							.findByCategoryId(category.getId());
					if (categoryGradesAndChapter == null) {
						categoryGradesAndChapter = new CategoryGradesAndChapter(category.getId());
					}
					if (!createCategoryModel.getGrades().isEmpty()) {
						categoryGradesAndChapter.setGrades(createCategoryModel.getGrades());
					}
					if (!createCategoryModel.getChapters().isEmpty()) {
						categoryGradesAndChapter.setChapters(createCategoryModel.getChapters());
					}
					categoryRepository.save(category);
					categoryGradesAndChapterRepository.save(categoryGradesAndChapter);
					log.debug("category named {} and id {} updated", category.getName(), category.getId());
					categoryRepository.saveAndFlush(category);
					var categoryMessageInfo = new CategoryMessageInfo(category.getId(), category.getName(),
							new From(SecurityUtils.getCurrentUser()));
					return ResponseModel.done(null, categoryMessageInfo);
				}).orElseThrow(NotFoundException::new)).orElseThrow(NotPermittedException::new);
	}

	@Transactional(readOnly = true)
	@PreAuthorize("hasAuthority('CATEGORY_READ')")
	public ResponseModel getCategory(Long id) {
		log.debug("get category with id {}", id);
		return categoryRepository.findById(id).map(category -> {
			var categoryModel = getCategoryModel(category, "en");
			log.debug("category got {}", categoryModel);
			return ResponseModel.done(categoryModel);
		}).orElseThrow(NotFoundException::new);
	}

	@Transactional
	@Auditable(EntityAction.CATEGORY_DELETE)
	@PreAuthorize("hasAuthority('CATEGORY_DELETE') AND hasAuthority('ADMIN')")
	@Message(entityAction = EntityAction.CATEGORY_DELETE, services = Services.NOTIFICATIONS)
	public ResponseModel delete(Long id) {
		log.debug("Delete category with id {}", id);
		return userRepository.findOneByUserNameAndDeletedFalse(SecurityUtils.getCurrentUserLogin())
				.map(user -> categoryRepository.findById(id).map(category -> {

					if (spaceRepository.countByCategoryAndDeletedFalse(category) > 0) {
						throw new MintException(Code.INVALID, "error.category.spaces");
					}
					categoryRepository.deleteById(id);
					categoryRepository.flush();
					log.debug("category {} deleted", id);
					var categoryMessageInfo = new CategoryMessageInfo(category.getId(), category.getName(),
							new From(SecurityUtils.getCurrentUser()));
					return ResponseModel.done(null, categoryMessageInfo);
				}).orElseThrow(NotFoundException::new)).orElseThrow(NotPermittedException::new);
	}

	private CategoryModel getCategoryModel(Category category, String lang) {
		var categoryModel = new CategoryModel();
		categoryModel.setId(category.getId());
		categoryModel.setName(category.getName());
		if ("ar".equals(lang) && category.getNameAr() != null) {
			categoryModel.setName(category.getNameAr());
		}
		categoryModel.setNameAr(category.getNameAr());
		categoryModel.setColor(category.getColor());
		categoryModel.setImage(category.getImage());
		if (category.getImage() != null && !category.getImage().startsWith(url)
				&& !category.getImage().startsWith("http://") && !category.getImage().startsWith("//")) {
			categoryModel.setImage(url + category.getImage());
		}
		categoryModel.setThumbnail(category.getThumbnail());
		if (category.getThumbnail() != null && !category.getThumbnail().startsWith(url)
				&& !category.getThumbnail().startsWith("http://") && !category.getThumbnail().startsWith("//")) {
			categoryModel.setThumbnail(url + category.getThumbnail());
		}

		// var categoryGradesAndChapter =
		// categoryGradesAndChapterRepository.findByCategoryId(category.getId());
		// if (categoryGradesAndChapter != null) {
		// categoryModel.getGrades().addAll(categoryGradesAndChapter.getGrades());
		// categoryModel.getChapters().addAll(categoryGradesAndChapter.getChapters());
		// }
		return categoryModel;
	}

	@PostConstruct
	@Transactional
	protected void initializeCategories() throws IOException {
		var classPathResource = new ClassPathResource("data/category/category.json");
		var objectMapper = new ObjectMapper();
		var file = classPathResource.getInputStream();
		Set<LoadCategoryModel> createCategoryModels = objectMapper.readValue(file,
				objectMapper.getTypeFactory().constructCollectionType(HashSet.class, LoadCategoryModel.class));
		log.debug("categories ===> {}", createCategoryModels);

		createCategoryModels.forEach(createCategoryModel -> {
			var categoryOptional = categoryRepository
					.findOneByNameAndDeletedFalse(
							createCategoryModel.getName());
			if (!categoryOptional.isPresent()) {
				var category = new Category();
				category.setName(createCategoryModel.getName());
				category.setNameAr(createCategoryModel.getNameAr());
				category.setImage(createCategoryModel.getImage());
				category.setThumbnail(createCategoryModel.getThumbnail());
				categoryRepository.save(category);
				return;
			}
			var category = categoryOptional.get();
			if (category.getNameAr() == null && !createCategoryModel.getNameAr().isEmpty()) {
				category.setNameAr(createCategoryModel.getNameAr());
				categoryRepository.save(category);
			}
		});
	}
}
