package com.edumento.category.services;

import java.io.IOException;
import java.io.InputStream;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
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

import com.edumento.b2b.domain.Foundation;
import com.edumento.b2b.domain.Organization;
import com.edumento.b2b.model.organization.SimpleOrganizationModel;
import com.edumento.b2b.repo.FoundationRepository;
import com.edumento.b2b.repo.OrganizationRepository;
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
import com.edumento.core.constants.SpaceRole;
import com.edumento.core.constants.notification.EntityAction;
import com.edumento.core.exception.ExistException;
import com.edumento.core.exception.InvalidException;
import com.edumento.core.exception.MintException;
import com.edumento.core.exception.NotFoundException;
import com.edumento.core.exception.NotPermittedException;
import com.edumento.core.model.PageResponseModel;
import com.edumento.core.model.ResponseModel;
import com.edumento.core.model.SimpleModel;
import com.edumento.core.model.messages.From;
import com.edumento.core.model.messages.category.CategoryMessageInfo;
import com.edumento.core.security.SecurityUtils;
import com.edumento.core.util.PermissionCheck;
import com.edumento.space.model.space.response.SpaceListingModel;
import com.edumento.space.repos.JoinedRepository;
import com.edumento.space.repos.SpaceRepository;
import com.edumento.space.services.SpaceService;
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

	private final OrganizationRepository organizationRepository;

	private final FoundationRepository foundationRepository;

	private final SpaceRepository spaceRepository;

	private final CategoryGradesAndChapterRepository categoryGradesAndChapterRepository;

	private final JoinedRepository joinedRepository;

	private final SpaceService spaceService;

	@Value("${mint.url}")
	private String url;

	@Autowired
	public CategoryService(CategoryRepository categoryRepository, UserRepository userRepository,
			OrganizationRepository organizationRepository, FoundationRepository foundationRepository,
			SpaceRepository spaceRepository, CategoryGradesAndChapterRepository categoryGradesAndChapterRepository,
			JoinedRepository joinedRepository, SpaceService spaceService) {
		this.categoryRepository = categoryRepository;
		this.userRepository = userRepository;
		this.organizationRepository = organizationRepository;
		this.foundationRepository = foundationRepository;
		this.spaceRepository = spaceRepository;
		this.categoryGradesAndChapterRepository = categoryGradesAndChapterRepository;
		this.joinedRepository = joinedRepository;
		this.spaceService = spaceService;
	}

	@Transactional
	@Auditable(EntityAction.CATEGORY_CREATE)
	@PreAuthorize("hasAuthority('CATEGORY_CREATE') AND hasAuthority('ADMIN')")
	@Message(entityAction = EntityAction.CATEGORY_CREATE, services = Services.NOTIFICATIONS)
	public ResponseModel createCategory(CreateCategoryModel createCategoryModel) {
		log.debug("Create category with model {}", createCategoryModel);
		return userRepository.findOneByUserNameAndDeletedFalse(SecurityUtils.getCurrentUserLogin()).map(user -> {
			if (createCategoryModel.getOrganizationId() == null && createCategoryModel.getFoundationId() == null) {
				if (user.getType() == UserType.SUPER_ADMIN || user.getType() == UserType.SYSTEM_ADMIN) {
					if (categoryRepository.findOneByNameAndOrganizationIsNullAndFoundationIsNullAndDeletedFalse(
							createCategoryModel.getName()).isPresent()) {
						throw new ExistException("error.category.exist");
					}
					Category category = new Category();
					category.setName(createCategoryModel.getName());
					category.setImage(createCategoryModel.getImage());
					category.setNameAr(createCategoryModel.getNameAr());
					category.setThumbnail(createCategoryModel.getThumbnail());
					categoryRepository.saveAndFlush(category);
					CategoryMessageInfo categoryMessageInfo = new CategoryMessageInfo(category.getId(),
							category.getName(), null, null, new From(SecurityUtils.getCurrentUser()));
					return ResponseModel.done(categoryMessageInfo);
				}
				throw new NotPermittedException();
			} else {

				Foundation foundation = createCategoryModel.getFoundationId() != null
						? foundationRepository.findById(createCategoryModel.getFoundationId()).orElse(null)
						: null;
				Organization organization = createCategoryModel.getOrganizationId() != null
						? organizationRepository.findById(createCategoryModel.getOrganizationId()).orElse(null)
						: null;

				if (foundation == null && organization == null) {
					throw new NotFoundException("organization or foundation");
				}
				if (foundation != null && organization != null && !organization.getFoundation().equals(foundation)) {
					throw new InvalidException("organization");
				}
				if (organization != null && foundation == null) {
					foundation = organization.getFoundation();
				}

				if (categoryRepository
						.findOneByNameAndOrganizationAndDeletedFalse(createCategoryModel.getName(), organization)
						.isPresent() || categoryRepository.findOneByNameAndFoundationAndOrganizationIsNullAndDeletedFalse(
						createCategoryModel.getName(), foundation).isPresent()) {
					log.warn("category named {} Exist", createCategoryModel.getName());
					throw new ExistException("error.category.exist");
				}

				// PermissionCheck.checkUserForFoundationAndOrgOperation(
				// user,
				// createCategoryModel.getOrganizationId(),
				// createCategoryModel.getFoundationId());
				Category category = new Category();
				category.setName(createCategoryModel.getName());
				category.setNameAr(createCategoryModel.getNameAr());
				category.setImage(createCategoryModel.getImage());
				category.setThumbnail(createCategoryModel.getThumbnail());
				category.setFoundation(foundation);
				category.setOrganization(organization);
				categoryRepository.save(category);
				CategoryGradesAndChapter categoryGradesAndChapter = new CategoryGradesAndChapter();
				categoryGradesAndChapter.setCategoryId(category.getId());
				categoryGradesAndChapter.setUserId(user.getId());
				if (!createCategoryModel.getGrades().isEmpty()) {
					categoryGradesAndChapter.setGrades(createCategoryModel.getGrades());
				}
				if (!createCategoryModel.getChapters().isEmpty()) {
					categoryGradesAndChapter.setChapters(createCategoryModel.getChapters());
				}
				categoryGradesAndChapterRepository.save(categoryGradesAndChapter);
				categoryRepository.saveAndFlush(category);
				CategoryMessageInfo categoryMessageInfo = new CategoryMessageInfo(category.getId(), category.getName(),
						category.getOrganization() == null ? null : category.getOrganization().getId(),
						category.getFoundation().getId(), new From(SecurityUtils.getCurrentUser()));
				return ResponseModel.done(null, categoryMessageInfo);
			}
		}).orElseThrow(NotPermittedException::new);
	}

	@Transactional(readOnly = true)
	@PreAuthorize("hasAuthority('CATEGORY_READ')")
	public ResponseModel getCategories(PageRequest page, Long foundationId, Long organizationId, String filter,
			boolean all, String lang) {
		log.debug("get categories");
		return userRepository.findOneByUserNameAndDeletedFalse(SecurityUtils.getCurrentUserLogin()).map(user -> {
			Specification<Category> byFoundation = null;
			Specification<Category> byOrganization = null;
			Specification<Category> name = null;
			Specification<Category> deletedFalse = (root, query, cb) -> cb.equal(root.get("deleted"), Boolean.FALSE);

			if (foundationId != null) {
				Foundation foundation = foundationRepository.findById(foundationId).orElseThrow(NotFoundException::new);

				byFoundation = (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get("foundation"),
						foundation);
			} else {
				if (!all) {
					byFoundation = (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder
							.isNull(root.get("foundation"));
				}
			}

			if (organizationId != null) {
				Organization organization = organizationRepository.findById(organizationId)
						.orElseThrow(NotFoundException::new);

				byOrganization = (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder
						.equal(root.get("organization"), organization);
			} else {
				if (!all) {
					byOrganization = (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder
							.isNull(root.get("organization"));
				}
			}

			if (filter != null) {
				name = (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder
						.like(criteriaBuilder.lower(root.get("name")), "%" + filter.toLowerCase() + "%");
			}
			switch (user.getType()) {
			case SUPER_ADMIN:
			case SYSTEM_ADMIN:
				break;
			case FOUNDATION_ADMIN:
				byFoundation = (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get("foundation"),
						user.getFoundation());

			case ADMIN:
				byFoundation = (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get("foundation"),
						user.getFoundation());
				byOrganization = (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder
						.equal(root.get("organization"), user.getOrganization());
				if (all) {
					byOrganization = (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.or(
							criteriaBuilder.equal(root.get("organization"), user.getOrganization()),
							criteriaBuilder.isNull(root.get("organization")));
				}
				break;
			default:
				if (user.getOrganization() != null) {
					byFoundation = (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder
							.equal(root.get("foundation"), user.getFoundation());

					byOrganization = (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.or(
							criteriaBuilder.equal(root.get("organization"), user.getOrganization()),
							criteriaBuilder.isNull(root.get("organization")));
				} else if (user.getFoundation() != null) {
					byFoundation = (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder
							.equal(root.get("foundation"), user.getFoundation());
					byOrganization = (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder
							.isNull(root.get("organization"));

				} else {
					byFoundation = (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder
							.isNull(root.get("foundation"));
					byOrganization = (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder
							.isNull(root.get("organization"));
				}
			}

			Page<CategoryModel> categoryModels = categoryRepository
					.findAll(Specification.where(name).and(byFoundation).and(byOrganization).and(deletedFalse), page)
					.map(category -> getCategoryModel(category, "en"));
			return PageResponseModel.done(categoryModels.getContent(), categoryModels.getTotalPages(),
					categoryModels.getNumber(), categoryModels.getTotalElements());
		}).orElseThrow(NotPermittedException::new);
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
		return ResponseModel.done(categoryRepository.findByOrganizationIsNullAndFoundationIsNullAndDeletedFalse()
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
					PermissionCheck.checkUserForFoundationAndOrgOperation(user,
							category.getOrganization() != null ? category.getOrganization().getId() : null,
							category.getFoundation() != null ? category.getFoundation().getId() : null);
					if (!createCategoryModel.getName().equals(category.getName())) {
						if (category.getOrganization() != null) {
							if (categoryRepository.findOneByNameAndOrganizationAndDeletedFalse(
									createCategoryModel.getName(), category.getOrganization()).isPresent()) {
								log.warn("category named {} not Exist", category.getName());
								throw new ExistException();
							}
						}
						if (category.getFoundation() != null) {
							if (categoryRepository.findOneByNameAndFoundationAndOrganizationIsNullAndDeletedFalse(
									createCategoryModel.getName(), category.getFoundation()).isPresent()) {
								log.warn("category named {} not Exist", category.getName());
								throw new ExistException();
							}
						}
						if (categoryRepository.findOneByNameAndOrganizationIsNullAndFoundationIsNullAndDeletedFalse(
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
					CategoryGradesAndChapter categoryGradesAndChapter = categoryGradesAndChapterRepository
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
					CategoryMessageInfo categoryMessageInfo = new CategoryMessageInfo(category.getId(),
							category.getName(),
							(category.getOrganization() == null) ? null : category.getOrganization().getId(),
							(category.getFoundation() == null) ? null : category.getFoundation().getId(),
							new From(SecurityUtils.getCurrentUser()));
					return ResponseModel.done(null, categoryMessageInfo);
				}).orElseThrow(NotFoundException::new)).orElseThrow(NotPermittedException::new);
	}

	@Transactional(readOnly = true)
	@PreAuthorize("hasAuthority('CATEGORY_READ')")
	public ResponseModel getCategory(Long id) {
		log.debug("get category with id {}", id);
		return categoryRepository.findById(id).map(category -> {
			CategoryModel categoryModel = getCategoryModel(category, "en");
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
					PermissionCheck.checkUserForFoundationAndOrgOperation(user,
							category.getOrganization() != null ? category.getOrganization().getId() : null,
							category.getFoundation() != null ? category.getFoundation().getId() : null);
					if (spaceRepository.countByCategoryAndDeletedFalse(category) > 0) {
						throw new MintException(Code.INVALID, "error.category.spaces");
					}
					categoryRepository.deleteById(id);
					categoryRepository.flush();
					log.debug("category {} deleted", id);
					CategoryMessageInfo categoryMessageInfo = new CategoryMessageInfo(category.getId(),
							category.getName(),
							(category.getOrganization() == null) ? null : category.getOrganization().getId(),
							(category.getFoundation() == null) ? null : category.getFoundation().getId(),
							new From(SecurityUtils.getCurrentUser()));
					return ResponseModel.done(null, categoryMessageInfo);
				}).orElseThrow(NotFoundException::new)).orElseThrow(NotPermittedException::new);
	}

	@Transactional
	@Auditable(EntityAction.CATEGORY_DELETE)
	@PreAuthorize("hasAuthority('CATEGORY_DELETE') AND hasAuthority('ADMIN')")
	public void deleteInOrganization(Organization organization) {
		log.debug("Delete category in organization {}", organization.getId());
		List<Category> categories = categoryRepository.findByOrganizationAndDeletedFalse(organization)
				.collect(Collectors.toList());
		if (!categories.isEmpty()) {
			categoryRepository.deleteAll(categories);
			log.debug("{} catrgories deleted", categories.size());
		}
	}

	@Transactional
	@Auditable(EntityAction.CATEGORY_DELETE)
	@PreAuthorize("hasAuthority('CATEGORY_DELETE') AND hasAuthority('ADMIN')")
	public void deleteInFoundation(Foundation foundation) {
		log.debug("Delete category in foundation {}", foundation.getId());
		List<Category> categories = categoryRepository.findByFoundationAndDeletedFalse(foundation)
				.collect(Collectors.toList());
		if (!categories.isEmpty()) {
			categoryRepository.deleteAll(categories);
			log.debug("{} catrgories deleted", categories.size());
		}
	}

	@Transactional(readOnly = true)
	@PreAuthorize("hasAuthority('CATEGORY_READ')")
	public ResponseModel getSpacesByCategory(Long id, PageRequest pageRequest, String lang, Boolean owned) {
		return userRepository.findOneByUserNameAndDeletedFalse(SecurityUtils.getCurrentUserLogin())
				.map(user -> categoryRepository.findById(id).map(category -> {
					if (user.getType() == UserType.USER) {
						Page<SpaceListingModel> spaceListingModels = joinedRepository
								.findByUserAndSpaceCategoryIdAndDeletedFalse(user, category.getId(), pageRequest)
								.map(joined -> {
									if (owned && joined.getSpaceRole() != SpaceRole.OWNER) {
										return null;
									}
									return spaceService.getUpdatesForSpaces(joined, null, lang);
								});
						return PageResponseModel.done(spaceListingModels.getContent(),
								spaceListingModels.getTotalPages(), spaceListingModels.getNumber(),
								spaceListingModels.getTotalElements());
					}
					PermissionCheck.checkUserForFoundationAndOrgOperation(user,
							category.getOrganization() != null ? category.getOrganization().getId() : null,
							category.getFoundation() != null ? category.getFoundation().getId() : null);
					return ResponseModel.done(spaceRepository.findByCategoryAndDeletedFalse(category)
							.map(space -> new SimpleModel(space.getId(), space.getName())).collect(Collectors.toSet()));
				}).orElseThrow(NotFoundException::new)).orElseThrow(NotPermittedException::new);
	}

	@Transactional(readOnly = true)
	@PreAuthorize("hasAuthority('CATEGORY_READ')")
	public ResponseModel getCategoriesByOrganization(Organization organization) {
		return ResponseModel.done(categoryRepository.findByOrganizationAndDeletedFalse((organization))
				.map((Category category) -> getCategoryModel(category, "en"))
				.sorted(Comparator.comparing(CreateCategoryModel::getName)).collect(Collectors.toList()));
	}

	@Transactional(readOnly = true)
	@PreAuthorize("hasAuthority('CATEGORY_READ')")
	public ResponseModel getCategoriesByFoundation(Foundation foundation) {
		return ResponseModel.done(categoryRepository.findByFoundationAndDeletedFalse((foundation))
				.map((Category category) -> getCategoryModel(category, "en"))
				.sorted(Comparator.comparing(CreateCategoryModel::getName)).collect(Collectors.toList()));
	}

	private CategoryModel getCategoryModel(Category category, String lang) {
		CategoryModel categoryModel = new CategoryModel();
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
		if (category.getFoundation() != null) {
			categoryModel.setFoundation(new SimpleOrganizationModel(category.getFoundation().getId(),
					category.getFoundation().getName(), category.getFoundation().getCode()));
			if (category.getOrganization() != null) {
				categoryModel.setOrganization(new SimpleOrganizationModel(category.getOrganization().getId(),
						category.getOrganization().getName(), category.getOrganization().getOrgId()));
			}
		}
		CategoryGradesAndChapter categoryGradesAndChapter = categoryGradesAndChapterRepository
				.findByCategoryId(category.getId());
		if (categoryGradesAndChapter != null) {
			categoryModel.getGrades().addAll(categoryGradesAndChapter.getGrades());
			categoryModel.getChapters().addAll(categoryGradesAndChapter.getChapters());
		}
		return categoryModel;
	}

	@PostConstruct
	@Transactional
	protected void initializeCategories() throws IOException {
		ClassPathResource classPathResource = new ClassPathResource("data/category/category.json");
		ObjectMapper objectMapper = new ObjectMapper();
		InputStream file = classPathResource.getInputStream();
		Set<LoadCategoryModel> createCategoryModels = objectMapper.readValue(file,
				objectMapper.getTypeFactory().constructCollectionType(HashSet.class, LoadCategoryModel.class));
		log.debug("categories ===> {}", createCategoryModels);

		createCategoryModels.forEach(createCategoryModel -> {
			Optional<Category> categoryOptional = categoryRepository
					.findOneByNameAndOrganizationIsNullAndFoundationIsNullAndDeletedFalse(
							createCategoryModel.getName());
			if (!categoryOptional.isPresent()) {
				Category category = new Category();
				category.setName(createCategoryModel.getName());
				category.setNameAr(createCategoryModel.getNameAr());
				category.setImage(createCategoryModel.getImage());
				category.setThumbnail(createCategoryModel.getThumbnail());
				categoryRepository.save(category);
				return;
			}
			Category category = categoryOptional.get();
			if (category.getNameAr() == null && !createCategoryModel.getNameAr().isEmpty()) {
				category.setNameAr(createCategoryModel.getNameAr());
				categoryRepository.save(category);
			}
		});
	}
}
