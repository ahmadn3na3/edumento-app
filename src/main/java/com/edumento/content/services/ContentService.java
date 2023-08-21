package com.edumento.content.services;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.TimeZone;
import java.util.stream.Collectors;

import org.apache.catalina.mapper.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.edumento.content.domain.Content;
import com.edumento.content.domain.ContentUser;
import com.edumento.content.models.ContentCreateModel;
import com.edumento.content.models.ContentModel;
import com.edumento.content.models.ContentUpdateModel;
import com.edumento.content.models.ContentUserData;
import com.edumento.content.repos.ContentRepository;
import com.edumento.content.repos.ContentUserRepository;
import com.edumento.core.configuration.auditing.Auditable;
import com.edumento.core.configuration.notifications.Message;
import com.edumento.core.constants.Code;
import com.edumento.core.constants.ContentStatus;
import com.edumento.core.constants.ContentType;
import com.edumento.core.constants.Services;
import com.edumento.core.constants.SpaceRole;
import com.edumento.core.constants.notification.EntityAction;
import com.edumento.core.exception.ExistException;
import com.edumento.core.exception.InvalidException;
import com.edumento.core.exception.MintException;
import com.edumento.core.exception.NotFoundException;
import com.edumento.core.exception.NotPermittedException;
import com.edumento.core.model.DateModel;
import com.edumento.core.model.PageResponseModel;
import com.edumento.core.model.ResponseModel;
import com.edumento.core.model.messages.From;
import com.edumento.core.model.messages.content.ContentInfoMessage;
import com.edumento.core.model.messages.user.UserInfoMessage;
import com.edumento.core.security.CurrentUserDetail;
import com.edumento.core.security.SecurityUtils;
import com.edumento.core.util.DateConverter;
import com.edumento.core.util.PermissionCheck;
import com.edumento.space.domain.Joined;
import com.edumento.space.domain.Space;
import com.edumento.space.repos.JoinedRepository;
import com.edumento.space.repos.SpaceRepository;
import com.edumento.space.services.SpaceService;
import com.edumento.user.constant.UserType;
import com.edumento.user.repo.UserRepository;

/** Created by ahmad on 7/2/16. */
@Service
public class ContentService {
	private final Logger log = LoggerFactory.getLogger(ContentService.class);
	private final ContentRepository contentRepository;

	private final UserRepository userRepository;

	private final SpaceRepository spaceRepository;

	private final ContentUserRepository contentUserRepository;

	private final SpaceService spaceService;
	private final JoinedRepository joinedRepository;
	private final MongoTemplate mongoTemplate;
	private final AnnotationService annotationService;

	@Autowired
	public ContentService(ContentRepository contentRepository, UserRepository userRepository,
			SpaceRepository spaceRepository, ContentUserRepository contentUserRepository,
			JoinedRepository joinedRepository, MongoTemplate mongoTemplate, AnnotationService annotationService,
			SpaceService service) {
		super();
		this.contentRepository = contentRepository;
		this.userRepository = userRepository;
		this.spaceRepository = spaceRepository;
		this.contentUserRepository = contentUserRepository;
		this.joinedRepository = joinedRepository;
		this.mongoTemplate = mongoTemplate;
		this.annotationService = annotationService;
		this.spaceService = service;
	}

	@Transactional
	@Auditable(EntityAction.CONTENT_CREATE)
	@PreAuthorize("hasAuthority('CONTENT_CREATE')")
	public ResponseModel createContent(ContentCreateModel contentCreateModel) {
		log.debug("Create Content {}", contentCreateModel);
		return userRepository.findOneByUserNameAndDeletedFalse(SecurityUtils.getCurrentUserLogin()).map(user -> {
			Space space = spaceRepository.findById(contentCreateModel.getSpaceId()).orElseThrow(NotFoundException::new);

			if (contentCreateModel.getType() != ContentType.WORKSHEET) {
				if (contentCreateModel.getShelf() != null
						&& contentRepository.findOneByNameAndShelfNameAndOwnerAndSpaceAndDeletedFalse(
								contentCreateModel.getName(), contentCreateModel.getShelf(), user, space).isPresent()) {
					log.warn("content {} already exist", contentCreateModel.getName());
					throw new ExistException("name");
				} else if (contentCreateModel.getShelf() == null && contentRepository
						.findOneByNameAndOwnerAndSpaceAndDeletedFalse(contentCreateModel.getName(), user, space)
						.isPresent()) {
					log.warn("content {} already exist", contentCreateModel.getName());
					throw new ExistException("name");
				}
			} else {
				contentCreateModel.setShelf(ContentType.WORKSHEET.name());
			}

			Content content = new Content();
			content.setName(contentCreateModel.getName());
			content.setShelfName(contentCreateModel.getShelf());
			content.setSpace(space);
			content.setOwner(user);
			if (contentCreateModel.getExt() != null) {
				content.setExt(contentCreateModel.getExt().toLowerCase());
			}
			content.setSize(contentCreateModel.getContentLength());
			content.setTags(contentCreateModel.getTags() != null ? String.join(",", contentCreateModel.getTags()) : "");
			content.setCheckSum(contentCreateModel.getCheckSum());

			content.setType(contentCreateModel.getType());

			if (content.getType().equals(ContentType.URL)) {
				content.setFileName(null);
				content.setFolderName(null);
				content.setContentUrl(contentCreateModel.getContentUrl());
				content.setStatus(ContentStatus.READY);
			}

			content.setThumbnail(contentCreateModel.getThumbnail());
			content.setAllowUseOrginal(contentCreateModel.getAllowUseOriginal());
			contentRepository.save(content);
			spaceService.updateSpaceModificationDate(space);
			log.debug("content saved: {}", contentCreateModel);
			return ResponseModel.done(content.getId(),
					new ContentInfoMessage(content.getId(), content.getName(), content.getType(), space.getId(),
							space.getName(), space.getCategory().getName(), new From(new UserInfoMessage(user))));
		}).orElseThrow(NotPermittedException::new);
	}

	@Transactional(readOnly = true)
	@PreAuthorize("hasAuthority('CONTENT_READ')")
	public ResponseModel getSpaceContents(Long spaceId, PageRequest pageRequest, String shelf) {
		log.debug("get space {} contents", spaceId);
		return userRepository.findOneByUserNameAndDeletedFalse(SecurityUtils.getCurrentUserLogin()).map(user -> {
			// specifications

			Space space = spaceRepository.findById(spaceId).orElseThrow(NotFoundException::new);

			Specification<Content> statusSpec = (root, criteriaQuery, criteriaBuilder) -> root.get("status")
					.in(ContentStatus.READY, ContentStatus.UPLOADED);
			Specification<Content> typeSpec = (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder
					.notEqual(root.get("type"), ContentType.WORKSHEET);
			Specification<Content> spaceSpec = (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder
					.equal(root.get("space").get("id"), spaceId);
			Specification<Content> notDeleted = (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder
					.equal(root.get("deleted"), false);
			Specification<Content> shelfContentSpecification = shelf == null ? null
					: (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get("shelfName"), shelf);

			Specification<Content> searchSpec;
			if (user.getType() == UserType.USER) {
				searchSpec = Specification.where(spaceSpec).and(statusSpec).and(typeSpec).and(notDeleted)
						.and(shelfContentSpecification);
			} else {
				searchSpec = Specification.where(spaceSpec).and(typeSpec).and(notDeleted);
			}
			Page<Content> contentPage = contentRepository.findAll(searchSpec, pageRequest);
			return PageResponseModel.done(
					contentPage.getContent().stream().map(this::getContentModel).collect(Collectors.toList()),
					contentPage.getTotalPages(), pageRequest.getPageNumber(), contentPage.getContent().size());
		}).orElseThrow(NotPermittedException::new);
	}

	@Transactional(readOnly = true)
	@PreAuthorize("hasAuthority('CONTENT_READ')")
	@Deprecated
	public ResponseModel getSpaceContentsUpdates(Long id, DateModel dateModel) {
		log.debug("get space {} updates since {}", id, dateModel.getDate());
		final ContentUpdateModel contentUpdateModel = new ContentUpdateModel();

		Date date = DateConverter.convertZonedDateTimeToDate(dateModel.getDate());
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MINUTE, 0);
		date = calendar.getTime();

		log.debug("date after remove ==> {}", date);

		contentUpdateModel.getNewContents()
				.addAll(contentRepository.findBySpaceIdAndDeletedFalseAndCreationDateAfter(id, date)
						.filter(content -> content.getStatus() == ContentStatus.UPLOADED
								&& content.getType() != ContentType.WORKSHEET)
						.map(this::getContentModel).collect(Collectors.toList()));
		log.debug("Updates: new contents");
		contentUpdateModel.getUpdatedContents()
				.addAll(contentRepository.findBySpaceIdAndDeletedFalseAndLastModifiedDateAfter(id, date)
						.filter(content -> content.getStatus() == ContentStatus.UPLOADED
								&& content.getType() != ContentType.WORKSHEET)
						.map(this::getContentModel).collect(Collectors.toList()));
		log.debug("Updates: updated contents");
		contentUpdateModel.getDeletedContents()
				.addAll(contentRepository.findBySpaceIdAndDeletedTrueAndDeletedDateAfter(id, date).map(Content::getId)
						.collect(Collectors.toList()));
		log.debug("Updates: Deleted contents");

		log.debug("Updates:{}", contentUpdateModel);
		return ResponseModel.done(contentUpdateModel);
	}

	@Transactional()
	@Auditable(EntityAction.CONTENT_UPDATE)
	@PreAuthorize("hasAuthority('CONTENT_UPDATE')")
	@Message(services = Services.NOTIFICATIONS, entityAction = EntityAction.CONTENT_UPDATE)
	public ResponseModel update(Long contentId, ContentCreateModel contentCreateModel) {
		log.debug("update content {} with data {} ", contentId, contentCreateModel);
		return userRepository.findOneByUserNameAndDeletedFalse(SecurityUtils.getCurrentUserLogin())
				.map(user -> contentRepository.findOneByIdAndDeletedFalse(contentId).map(content -> {
					if (content.getType() == ContentType.WORKSHEET) {
						throw new MintException(Code.INVALID, "type");
					}
					if (!content.getOwner().equals(user) && !content.getSpace().getUser().equals(user)) {
						throw new NotPermittedException();
					}
					if (!content.getName().equals(contentCreateModel.getName())) {
						if (contentCreateModel.getShelf() != null && contentRepository
								.findOneByNameAndShelfNameAndOwnerAndSpaceAndDeletedFalse(contentCreateModel.getName(),
										contentCreateModel.getShelf(), user, content.getSpace())
								.isPresent()) {
							log.warn("content {} with shelf {} exist", contentCreateModel.getName(),
									content.getShelfName());
							throw new ExistException("name");
						} else if (contentCreateModel.getShelf() == null && contentRepository
								.findOneByNameAndOwnerAndSpaceAndDeletedFalse(contentCreateModel.getName(),
										content.getOwner(), content.getSpace())
								.isPresent()) {
							log.warn("content {} exist", contentCreateModel.getName());
							throw new ExistException("name");
						}
					}
					content.setName(contentCreateModel.getName());
					content.setShelfName(contentCreateModel.getShelf());
					content.setTags(String.join(",", contentCreateModel.getTags()));
					content.setThumbnail(contentCreateModel.getThumbnail());
					content.setAllowUseOrginal(contentCreateModel.getAllowUseOriginal());
					contentRepository.save(content);
					spaceService.updateSpaceModificationDate(content.getSpace());
					log.debug("content {} updated", contentId);
					return ResponseModel.done(content.getId(),
							new ContentInfoMessage(content.getId(), content.getName(), content.getType(),
									content.getSpace().getId(), content.getSpace().getName(),
									content.getSpace().getCategory().getName(), new From(new UserInfoMessage(user))));
				}).orElseThrow(NotFoundException::new)).orElseThrow(NotPermittedException::new);
	}

	@Transactional()
	@Auditable(EntityAction.CONTENT_UPDATE)
	@PreAuthorize("hasAuthority('CONTENT_UPDATE')")
	public ResponseModel updateStatus(Long contentId, ContentStatus status) {
		log.debug("update status for content {} with status {} ", contentId, status);
		return contentRepository.findOneByIdAndDeletedFalse(contentId).map(content -> {
			content.setStatus(status);
			contentRepository.save(content);
			log.debug("content {} status updated", contentId);
			return ResponseModel.done();
		}).orElseThrow(NotFoundException::new);
	}

	@Transactional()
	@PreAuthorize("hasAuthority('CONTENT_DELETE')")
	@Auditable(EntityAction.CONTENT_DELETE)
	@Message(services = Services.NOTIFICATIONS, entityAction = EntityAction.CONTENT_DELETE)
	public ResponseModel delete(Long id) {
		log.debug("deleting content {}", id);
		return userRepository.findOneByUserNameAndDeletedFalse(SecurityUtils.getCurrentUserLogin())
				.map(user -> contentRepository.findOneByIdAndDeletedFalse(id).map(content -> {
					if (content.getSpace().getCategory().getFoundation() != null
							&& user.getType() == UserType.FOUNDATION_ADMIN
							&& user.getFoundation().equals(content.getSpace().getCategory().getFoundation())) {
						annotationService.deletebyContentId(id);
						contentRepository.delete(content);
						spaceService.updateSpaceModificationDate(content.getSpace());
						return ResponseModel.done(null, new ContentInfoMessage(content.getId(), content.getName(),
								content.getType(), content.getSpace().getId(), content.getSpace().getName(),
								content.getSpace().getCategory().getName(), new From(new UserInfoMessage(user))));
					}
					return joinedRepository
							.findOneByUserIdAndSpaceIdAndDeletedFalse(user.getId(), content.getSpace().getId())
							.map(joined -> {
								if (content.getOwner().equals(joined.getUser())
										|| joined.getSpaceRole().equals(SpaceRole.OWNER)) {
									annotationService.deletebyContentId(id);
									contentRepository.delete(content);
									spaceService.updateSpaceModificationDate(content.getSpace());
									return ResponseModel.done(null, new ContentInfoMessage(content.getId(),
											content.getName(), content.getType(), content.getSpace().getId(),
											content.getSpace().getName(), content.getSpace().getCategory().getName(),
											new From(new UserInfoMessage(user))));
								} else {
									throw new NotPermittedException();
								}
							}).orElseThrow(NotPermittedException::new);
				}).orElseThrow(NotFoundException::new)).orElseThrow(NotPermittedException::new);
	}

	@Transactional(readOnly = true)
	@PreAuthorize("hasAuthority('CONTENT_READ')")
	public ResponseModel getShelves(Long id) {
		log.debug("get shelves on space {}", id);
		return ResponseModel.done(contentRepository.findDistinctShelfNameBySpaceIdAndDeletedFalse(id).distinct()
				.sorted(Comparator.reverseOrder()).collect(Collectors.toSet()));
	}

	private ContentModel getContentModel(Content content) {
		log.debug("get content model from content domain");
		ContentModel contentModel = new ContentModel();
		mapper.map(content, contentModel);
		contentModel.getTags()
				.addAll(Arrays.asList(content.getTags() == null ? new String[] { "" } : content.getTags().split(",")));

		contentModel.setLastModifiedDate(DateConverter.convertDateToZonedDateTime(content.getLastModifiedDate()));
		contentModel.setCreationDate(DateConverter.convertDateToZonedDateTime(content.getCreationDate()));
		contentModel.setOwner(content.getOwner().getUserName().equalsIgnoreCase(SecurityUtils.getCurrentUserLogin()));
		contentUserRepository.findByUserIdAndContentId(SecurityUtils.getCurrentUser().getId(), content.getId())
				.ifPresent(contentUser -> {
					contentModel.setFavorite(contentUser.getFavorite());
					contentModel.setFavoriteDate(contentUser.getFavoriteDate());
					contentModel
							.setLastAccess(DateConverter.convertDateToZonedDateTime(contentUser.getLastAccessDate()));
					contentModel.setNumberOfViews(contentModel.getNumberOfViews() + contentUser.getViews());
				});
		Query query = new Query(Criteria.where("contentId").is(content.getId()).and("type").is("VEIW"));
		contentModel.setNumberOfViews(
				contentModel.getNumberOfViews() + Long.valueOf(mongoTemplate.count(query, "tasks")).intValue());
		contentModel.setAllowUseOriginal(content.getAllowUseOrginal());
		contentModel.setNumberOfAnnotation(content.getNumberOfAnnotation());
		log.debug("content model got:{}", contentModel);
		return contentModel;
	}

	@Transactional(readOnly = true)
	@PreAuthorize("hasAuthority('CONTENT_READ')")
	public ResponseModel getById(Long id) {
		log.debug("get content by id {}", id);
		return userRepository.findOneByUserNameAndDeletedFalse(SecurityUtils.getCurrentUserLogin())
				.map(user -> contentRepository.findOneByIdAndDeletedFalse(id).map(content -> {
					if (user.getType() == UserType.USER && !joinedRepository
							.findOneBySpaceIdAndUserIdAndDeletedFalse(content.getSpace().getId(), user.getId())
							.isPresent()) {
						throw new NotPermittedException();
					}

					if (content.getFileName() == null) {
						content.setFileName(content.getName());
						contentRepository.save(content);
					}
					return ResponseModel.done(getContentModel(content));
				}).orElseThrow(NotFoundException::new)).orElseThrow(NotPermittedException::new);
	}

	@Transactional(readOnly = true)
	@PreAuthorize("hasAuthority('CONTENT_READ')")
	public ResponseModel getParentSpaceById(Long id) {
		ContentModel content = (ContentModel) getById(id).getData();
		if (content != null && content.getSpaceId() != null) {
			return spaceService.getSpaceById(content.getSpaceId(), "en");
		} else {
			throw new NotFoundException();
		}
	}

	@Transactional
	@Auditable(EntityAction.CONTENT_CREATE)
	@PreAuthorize("hasAuthority('CONTENT_CREATE')")
	public ResponseModel copyContentToSpace(Long id, Long spaceId) {
		log.debug("copy content {} to space {}", id, spaceId);
		return userRepository.findOneByUserNameAndDeletedFalse(SecurityUtils.getCurrentUserLogin()).map(user -> {
			if (user.getType() == UserType.USER) {
				Optional<Joined> joinedSpace = joinedRepository.findOneBySpaceIdAndUserIdAndDeletedFalse(spaceId,
						user.getId());
				if (!joinedSpace.isPresent()) {
					throw new NotPermittedException();
				} else if (joinedSpace.get().getSpaceRole() != SpaceRole.OWNER
						&& joinedSpace.get().getSpaceRole() != SpaceRole.CO_OWNER
						&& joinedSpace.get().getSpaceRole() != SpaceRole.EDITOR) {
					throw new NotPermittedException();
				}
			}

			return contentRepository.findOneByIdAndDeletedFalse(id).map(content -> {
				if (user.getType() != UserType.USER) {
					Long orgId = content.getSpace().getCategory().getOrganization() == null ? null
							: content.getSpace().getCategory().getOrganization().getId();
					Long foundId = content.getSpace().getCategory().getFoundation() == null ? null
							: content.getSpace().getCategory().getFoundation().getId();
					PermissionCheck.checkUserForFoundationAndOrgOperation(user, orgId, foundId);
				}

				Space space = spaceRepository.findById(spaceId).orElseThrow(NotFoundException::new);

				if (space == null) {
					log.warn("space {} not found", spaceId);
					throw new NotFoundException("space");
				}
				if (space.equals(content.getSpace())) {
					log.warn("invalid , copy to the same space {}", spaceId);
					throw new MintException(Code.INVALID, "error.content.space.same");
				}

				if (content.getStatus() != ContentStatus.UPLOADED && content.getStatus() != ContentStatus.READY) {
					throw new MintException(Code.INVALID, "error.content.status");
				}

				if (content.getShelfName() != null
						&& contentRepository.findOneByNameAndShelfNameAndOwnerAndSpaceAndDeletedFalse(content.getName(),
								content.getShelfName(), content.getOwner(), space).isPresent()) {
					log.warn("content {} exist", content.getName());
					throw new ExistException("name");
				} else if (content.getShelfName() == null && contentRepository
						.findOneByNameAndOwnerAndSpaceAndDeletedFalse(content.getName(), content.getOwner(), space)
						.isPresent()) {
					log.warn("content {} exist", content.getName());
					throw new ExistException("name");
				}

				Content copyContent = new Content();
				copyContent.setName(content.getName());
				copyContent.setFileName(content.getFileName());
				copyContent.setShelfName(content.getShelfName());
				copyContent.setSpace(space);
				copyContent.setExt(content.getExt());
				copyContent.setSize(content.getSize());
				copyContent.setTags(content.getTags());
				copyContent.setCheckSum(content.getCheckSum());
				copyContent.setType(content.getType());
				copyContent.setThumbnail(content.getThumbnail());
				copyContent.setOwner(content.getOwner());
				copyContent.setFolderName(content.getFolderName());
				copyContent.setStatus(content.getStatus());
				copyContent.setContentUrl(content.getContentUrl());
				copyContent.setAllowUseOrginal(content.getAllowUseOrginal());

				contentRepository.save(copyContent);
				log.debug("content {} copied to space {}", id, spaceId);
				spaceService.updateSpaceModificationDate(space);
				return ResponseModel.done(copyContent.getId());
			}).orElseThrow(NotFoundException::new);
		}).orElseThrow(NotFoundException::new);
	}

	@Transactional
	@Auditable(EntityAction.CONTENT_UPDATE)
	public ResponseModel updateContentUserData(Long id, ContentUserData contentUserData) {
		log.debug("update content {} with data {}", id, contentUserData);
		Content content = contentRepository.findById(id).orElseThrow(NotFoundException::new);

		CurrentUserDetail user = SecurityUtils.getCurrentUser();
		Joined joined = joinedRepository
				.findOneBySpaceIdAndUserIdAndDeletedFalse(content.getSpace().getId(), user.getId())
				.orElseThrow(() -> new InvalidException("error.space.notjoined"));

		return contentUserRepository.findByUserIdAndContentId(SecurityUtils.getCurrentUser().getId(), id)
				.map(contentUser -> {
					if (contentUser.getUserId() == null) {
						contentUser.setUserId(user.getId());
					}
					if (contentUserData.getFavorite() != null) {
						contentUser.setFavorite(contentUserData.getFavorite());
						contentUser.setFavoriteDate(new Date());
					}
					if (contentUserData.getTimeSpent() != null) {
						contentUser.getTimeSpent().add(contentUserData.getTimeSpent());
					}
					contentUser.setLastAccessDate(new Date());
					contentUser.setViews(contentUser.getViews() + 1);
					contentUserRepository.save(contentUser);
					log.debug("content {} updated", id);
					return ResponseModel.done();
				}).orElseGet(() -> {
					log.debug("new contnet user {}", SecurityUtils.getCurrentUserLogin());
					ContentUser contentUser = new ContentUser();
					contentUser.setContentId(id);
					contentUser.setUserId(user.getId());
					contentUser.setUserName(user.getUsername());
					if (contentUserData.getFavorite() != null) {
						contentUser.setFavorite(contentUserData.getFavorite());
						contentUser.setFavoriteDate(new Date());
					}
					if (contentUserData.getTimeSpent() != null) {
						contentUser.getTimeSpent().add(contentUserData.getTimeSpent());
					}
					contentUser.setViews(1);
					contentUser.setLastAccessDate(new Date());
					contentUserRepository.save(contentUser);
					log.debug("content {} updated", id);
					return ResponseModel.done();
				});
	}

	@Transactional(readOnly = true)
	public Content getContentInformation(Long id) {
		Objects.requireNonNull(id);
		return userRepository
				.findOneByUserNameAndDeletedFalse(SecurityUtils.getCurrentUserLogin())
				.map(
						user -> {

							return contentRepository.findById(id)
									.map(
											content -> {
												if (user.getType() != UserType.USER) {
													Long orgId = content.getSpace().getCategory()
															.getOrganization() == null
																	? null
																	: content.getSpace().getCategory().getOrganization()
																			.getId();
													Long foundId = content.getSpace().getCategory()
															.getFoundation() == null
																	? null
																	: content.getSpace().getCategory().getFoundation()
																			.getId();
													PermissionCheck.checkUserForFoundationAndOrgOperation(
															user, orgId, foundId);
													return content;
												}
												return joinedRepository
														.findOneBySpaceIdAndUserIdAndDeletedFalse(
																content.getSpace().getId(), user.getId())
														.map(joined -> content)
														.orElseThrow(NotPermittedException::new);
											})
									.orElseThrow(NotFoundException::new);
						})
				.orElseThrow(NotPermittedException::new);
	}

	@Transactional
	public void updateContentStatus(Long id, ContentStatus status) {
		updateContentStatus(id, status, null, null, null, null, null);
	}

	@Transactional(readOnly = true)
	public List<Content> getContentListByType(ContentType contentType) {
		return contentRepository.findByTypeAndDeletedFalseAndStatusIn(
				contentType, ContentStatus.READY, ContentStatus.UPLOADED);
	}

	@Transactional
	public void updateContentStatus(
			Long id,
			ContentStatus status,
			String key,
			String keyId,
			String ext,
			ContentType type,
			String originalPath) {
		Objects.requireNonNull(id);
		Objects.requireNonNull(status);

		contentRepository
				.findOneByIdAndDeletedFalse(id)
				.ifPresent(
						content -> {
							content.setStatus(status);
							if (key != null && keyId != null) {
								content.setKey(key);
								content.setKeyId(keyId);
							}
							if (ext != null && !content.getExt().equalsIgnoreCase(ext)) {
								content.setExt(ext);
							}
							if (type != null && content.getType() != type) {
								content.setType(type);
							}

							if (originalPath != null) {
								content.setOriginalPath(originalPath);
							}

							contentRepository.save(content);
						});
	}

	@Transactional
	public void deleteContent(Long id) {
		Objects.requireNonNull(id);
		contentRepository.deleteById(id);
	}
}
