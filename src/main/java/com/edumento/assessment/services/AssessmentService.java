package com.edumento.assessment.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.edumento.assessment.domain.Assessment;
import com.edumento.assessment.domain.AssessmentQuestion;
import com.edumento.assessment.domain.AssessmentQuestionChoice;
import com.edumento.assessment.domain.QuestionAnswer;
import com.edumento.assessment.domain.UserAssessment;
import com.edumento.assessment.mappers.AssessmentsMapper;
import com.edumento.assessment.model.AssessmentCreateModel;
import com.edumento.assessment.model.AssessmentGetAllModel;
import com.edumento.assessment.model.AssessmentListModel;
import com.edumento.assessment.model.AssessmentModel;
import com.edumento.assessment.model.AssessmentQuestionCreateModel;
import com.edumento.assessment.model.AssessmentUserModel;
import com.edumento.assessment.model.AssessmentsUpdatesModel;
import com.edumento.assessment.model.ChoicesModel;
import com.edumento.assessment.model.MongoQuestionModel;
import com.edumento.assessment.model.PracticeGenerateModel;
import com.edumento.assessment.model.QuestionAnswerGetModel;
import com.edumento.assessment.model.QuestionAnswerModel;
import com.edumento.assessment.model.QuestionBankResponseModel;
import com.edumento.assessment.model.QuestionSearchModel;
import com.edumento.assessment.model.UserAssessmentModel;
import com.edumento.assessment.model.UserPracticeModel;
import com.edumento.assessment.model.challenge.ChallengeCreateModel;
import com.edumento.assessment.model.challenge.ChallengeSummaryModel;
import com.edumento.assessment.model.challenge.ChallengeesGrade;
import com.edumento.assessment.model.runnable.AssessmentAutoSolvingRunnable;
import com.edumento.assessment.repos.AssessmentQuestionChoicesRepository;
import com.edumento.assessment.repos.AssessmentQuestionRepository;
import com.edumento.assessment.repos.AssessmentRepository;
import com.edumento.assessment.repos.QuestionAnswerRepository;
import com.edumento.assessment.repos.UserAssessmentRepository;
import com.edumento.content.models.ContentUserModel;
import com.edumento.content.repos.ContentRepository;
import com.edumento.core.configuration.MintProperties;
import com.edumento.core.configuration.auditing.Auditable;
import com.edumento.core.configuration.notifications.Message;
import com.edumento.core.constants.AssessmentStatus;
import com.edumento.core.constants.AssessmentType;
import com.edumento.core.constants.Code;
import com.edumento.core.constants.ContentStatus;
import com.edumento.core.constants.ContentType;
import com.edumento.core.constants.Services;
import com.edumento.core.constants.SortField;
import com.edumento.core.constants.SpaceRole;
import com.edumento.core.constants.notification.EntityAction;
import com.edumento.core.exception.InvalidException;
import com.edumento.core.exception.MintException;
import com.edumento.core.exception.NotFoundException;
import com.edumento.core.exception.NotPermittedException;
import com.edumento.core.model.PageRequestModel;
import com.edumento.core.model.PageResponseModel;
import com.edumento.core.model.ResponseModel;
import com.edumento.core.model.messages.From;
import com.edumento.core.model.messages.assessment.AssessementsInfoMessage;
import com.edumento.core.model.messages.assessment.AssessmentSubmitMessage;
import com.edumento.core.model.messages.user.UserInfoMessage;
import com.edumento.core.security.SecurityUtils;
import com.edumento.core.util.DateConverter;
import com.edumento.core.util.RandomUtils;
import com.edumento.space.domain.Joined;
import com.edumento.space.repos.JoinedRepository;
import com.edumento.space.repos.SpaceRepository;
import com.edumento.user.domain.User;
import com.edumento.user.services.AccountService;
import com.edumento.user.services.InnerUserService;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

/** Created by ayman on 13/06/16. */
@Service
@Slf4j
public class AssessmentService {
	private final QuestionAnswerRepository questionAnswerRepository;
	private final AssessmentRepository assessmentRepository;
	private final AssessmentQuestionRepository assessmentQuestionRepository;
	private final AssessmentQuestionChoicesRepository assessmentQuestionChoicesRepository;
	private final RestTemplate restTemplate = new RestTemplate();
	private final MintProperties mintProperties;

	private static final int MAX_USER_IN_COMMUNITY = 4;

	private final SpaceRepository spaceRepository;
	private final ContentRepository contentRepository;

	private final JoinedRepository joinedRepository;

	private final UserAssessmentRepository userAssessmentRepository;
	private final MessageSource messageSource;
	private final ThreadPoolTaskScheduler taskScheduler;
	private final InnerUserService innerUserService;

	public AssessmentService(AssessmentQuestionChoicesRepository assessmentQuestionChoicesRepository,
			AssessmentQuestionRepository assessmentQuestionRepository,
			UserAssessmentRepository userAssessmentRepository, AccountService userService,
			AssessmentRepository assessmentRepository, QuestionAnswerRepository questionAnswerRepository,
			MintProperties mintProperties, SpaceRepository spaceRepository, JoinedRepository joinedRepository,
			ContentRepository contentRepository, MessageSource messageSource, InnerUserService innerUserService,
			ThreadPoolTaskScheduler taskScheduler) {
		this.assessmentQuestionChoicesRepository = assessmentQuestionChoicesRepository;
		this.assessmentQuestionRepository = assessmentQuestionRepository;
		this.userAssessmentRepository = userAssessmentRepository;
		this.assessmentRepository = assessmentRepository;
		this.mintProperties = mintProperties;
		this.spaceRepository = spaceRepository;
		this.joinedRepository = joinedRepository;
		this.questionAnswerRepository = questionAnswerRepository;
		this.contentRepository = contentRepository;
		this.messageSource = messageSource;
		this.innerUserService = innerUserService;
		this.taskScheduler = taskScheduler;
	}

	@Transactional
	@Auditable(EntityAction.ASSESSMENT_CREATE)
	@PreAuthorize("hasAnyAuthority('ASSESSMENT_CREATE','CHALLENGE_CREATE')")
	@Message(entityAction = EntityAction.ASSESSMENT_CREATE, services = Services.NOTIFICATIONS)
	public ResponseModel createChallenge(ChallengeCreateModel challengeCreateModel, HttpServletRequest request) {
		if (challengeCreateModel.getQuestionSearchModel().getSpaceId() == null) {
			challengeCreateModel.getQuestionSearchModel().setSpaceId(challengeCreateModel.getSpaceId());
		}
		challengeCreateModel.getQuestionSearchModel().setLimit(10);
		var responseModel = searchQuestionBank(challengeCreateModel.getQuestionSearchModel(),
				request);
		if (responseModel.getCode() == 10) {
			if (responseModel.getData() != null && !responseModel.getData().isEmpty()) {
				/*
				 * modified by A.Alsayed 7-2-2019 Add new filter for getting challengees with
				 * school name not equal that of the challenge creator.
				 */
				// UserInfoModel loggedInUser = userService.getUserWithAuthorities();
				// String ownerSchool =
				// loggedInUser.getSchool() != null ? loggedInUser.getSchool() : "";
				var userOptional = joinedRepository.findBySpaceIdAndDeletedFalse(challengeCreateModel.getSpaceId())
						.filter(new Predicate<Joined>() {
							@Override
							public boolean test(Joined joined) {
								return joined.getSpaceRole() == SpaceRole.EDITOR
										|| joined.getSpaceRole() == SpaceRole.COLLABORATOR;
							}
						})
						.map(Joined::getUser)
						.filter(new Predicate<User>() {
							@Override
							public boolean test(User user) {
								return !user.getId().equals(SecurityUtils.getCurrentUser().getId());
							}
						})
						// .filter(user -> user.getSchool() == null ||
						// !user.getSchool().equals(ownerSchool))
						.sorted(new Comparator<User>() {
							@Override
							public int compare(User user1, User user2) {
								return RandomUtils.generateResetKey()
										.compareTo(RandomUtils.generateResetKey());
							}
						})
						.findAny().orElseThrow(new Supplier<NotFoundException>() {
							@Override
							public NotFoundException get() {
								return new NotFoundException("error.assessment.challngeenotfound");
							}
						});

				List<AssessmentQuestionCreateModel> assessmentQuestionCreateModels = responseModel.getData().stream()
						.limit(10).map(new Function<MongoQuestionModel, AssessmentQuestionCreateModel>() {
							@Override
							public AssessmentQuestionCreateModel apply(MongoQuestionModel questionModel) {
								var assessmentQuestionCreateModel = new AssessmentQuestionCreateModel(
										questionModel);
								assessmentQuestionCreateModel.setQuestionWeight(1);
								return assessmentQuestionCreateModel;
							}
						}).collect(Collectors.toList());
				log.debug("assessmentQuestionCreateModels == > {}", assessmentQuestionCreateModels.size());
				var assessmentCreateModel = new AssessmentCreateModel(challengeCreateModel.getTitle(),
						AssessmentType.CHALLENGE, true, challengeCreateModel.getSpaceId(),
						assessmentQuestionCreateModels);

				assessmentCreateModel.setLockMint(false);
				assessmentCreateModel.setLimitDuration(challengeCreateModel.getLimitDuration());
				assessmentCreateModel.setDueDate(ZonedDateTime.now().plusDays(5));
				var model = create(assessmentCreateModel, userOptional);
				var assessmentId = (Long) model.getData();
				var assessment = assessmentRepository.getReferenceById(assessmentId);
				assessment.getChallengees().add(userOptional);
				assessment.setAssessmentStatus(AssessmentStatus.NOT_STARTED);
				assessmentRepository.save(assessment);
				var creator = new UserAssessment();
				creator.setAssessmentId(assessment.getId());
				creator.setUserId(assessment.getOwner().getId());
				creator.setAssessmentStatus(AssessmentStatus.NOT_STARTED);
				userAssessmentRepository.save(creator);
				var opponent = new UserAssessment();
				opponent.setAssessmentId(assessment.getId());
				opponent.setUserId(userOptional.getId());
				opponent.setAssessmentStatus(AssessmentStatus.NOT_STARTED);
				userAssessmentRepository.save(opponent);
				return model;
			} else {
				throw new NotFoundException("error.assessment.noenoughquestion");
			}
		} else {
			throw new MintException(responseModel.getCodeType(), responseModel.getMessage());
		}
	}

	@Transactional
	@Auditable(EntityAction.ASSESSMENT_CREATE)
	@PreAuthorize("hasAuthority('ASSESSMENT_CREATE')")
	public ResponseModel generatePractice(PracticeGenerateModel practiceGenerateModel, HttpServletRequest request) {
		if (practiceGenerateModel.getQuestionSearchModel().getSpaceId() == null) {
			practiceGenerateModel.getQuestionSearchModel().setSpaceId(practiceGenerateModel.getSpaceId());
		}
		var responseModel = searchQuestionBank(practiceGenerateModel.getQuestionSearchModel(),
				request);
		if (responseModel.getCode() == 10) {
			if (responseModel.getData() != null && !responseModel.getData().isEmpty()
					&& responseModel.getData().size() >= practiceGenerateModel.getQuestionSearchModel().getLimit()) {
				var maximum = practiceGenerateModel.getQuestionSearchModel().getQuestionType().length
						* practiceGenerateModel.getMinimum();
				log.debug("maximum == > {}", maximum);
				List<AssessmentQuestionCreateModel> assessmentQuestionCreateModels = responseModel.getData().stream()
						.map(new Function<MongoQuestionModel, AssessmentQuestionCreateModel>() {
							@Override
							public AssessmentQuestionCreateModel apply(MongoQuestionModel questionModel) {
								var assessmentQuestionCreateModel = new AssessmentQuestionCreateModel(
										questionModel);
								assessmentQuestionCreateModel.setQuestionWeight(1);
								return assessmentQuestionCreateModel;
							}
						}).collect(Collectors.toList());
				Collections.shuffle(assessmentQuestionCreateModels);
				log.debug("assessmentQuestionCreateModels == > {}", assessmentQuestionCreateModels.size());

				var assessmentCreateModel = new AssessmentCreateModel(
						practiceGenerateModel.getPracticeName(), AssessmentType.PRACTICE, true,
						practiceGenerateModel.getSpaceId(),
						assessmentQuestionCreateModels.stream().limit(maximum).collect(Collectors.toList()));

				assessmentCreateModel.setLockMint(false);

				return create(assessmentCreateModel, null);
			} else {
				throw new NotFoundException("error.assessment.noenoughquestion");
			}
		} else {
			throw new MintException(responseModel.getCodeType(), responseModel.getMessage());
		}
	}

	@Transactional
	@Auditable(EntityAction.ASSESSMENT_CREATE)
	@PreAuthorize("hasAuthority('ASSESSMENT_CREATE')")
	@Message(entityAction = EntityAction.ASSESSMENT_CREATE, services = Services.NOTIFICATIONS)
	public ResponseModel create(AssessmentCreateModel assesmentCreateModel, User challengee) {
		var user = User.of(SecurityUtils.getCurrentUser().getId());

		if (assesmentCreateModel.getSpaceId() == null) {
			throw new MintException(Code.INVALID_KEY, "spaceId");
		}
		var space = spaceRepository.findOneByIdAndDeletedFalse(assesmentCreateModel.getSpaceId())
				.orElseThrow(NotFoundException::new);

		var joined = joinedRepository
				.findOneBySpaceIdAndUserIdAndSpaceRoleInAndDeletedFalse(space.getId(), user.getId(),
						SpaceRole.COLLABORATOR, SpaceRole.CO_OWNER, SpaceRole.EDITOR, SpaceRole.OWNER)
				.orElseThrow(NotPermittedException::new);

		if (assesmentCreateModel.isPublish() && assesmentCreateModel.getAssessmentQuestionCreateModels().isEmpty()
				&& (assesmentCreateModel.getAssessmentType() == AssessmentType.ASSIGNMENT
						|| assesmentCreateModel.getAssessmentType() == AssessmentType.QUIZ)) {
			throw new MintException(Code.INVALID, "error.assessment.question.empty");
		}
		var assessment = new Assessment();
		AssessmentsMapper.INSTANCE.createModelToEntity(assesmentCreateModel, assessment);

		assessment.setOwner(user);
		assessment.setSpace(space);
		if (assesmentCreateModel.getStartDate() != null) {
			if (!validateDate(assesmentCreateModel.getStartDate())) {
				throw new InvalidException("error.invalid.date");
			}
			assessment.setStartDateTime(DateConverter.convertZonedDateTimeToDate(assesmentCreateModel.getStartDate()));
		}
		if (assesmentCreateModel.getDueDate() != null) {
			if (!validateDate(assesmentCreateModel.getDueDate())) {
				throw new InvalidException("error.invalid.date");
			}
			assessment.setDueDate(DateConverter.convertZonedDateTimeToDate(assesmentCreateModel.getDueDate()));
		}

		if (assessment.getPublish().booleanValue()) {
			assessment.setPublishDate(new Date());
		}

		assessmentRepository.save(assessment);

		switch (assesmentCreateModel.getAssessmentType()) {
		case ASSIGNMENT, QUIZ, PRACTICE, CHALLENGE -> {
			if (assesmentCreateModel.getAssessmentType() == AssessmentType.PRACTICE
					|| assesmentCreateModel.getAssessmentType() == AssessmentType.CHALLENGE) {
				assessment.setPublishDate(assessment.getCreationDate());
			}
			mapAndSaveQuestion(assesmentCreateModel, assessment);
		}
		case WORKSHEET -> {
			if (assesmentCreateModel.getWorkSheetContentId() != null
					&& assesmentCreateModel.getWorkSheetContentId() != 0) {
				var content = contentRepository.getReferenceById(assesmentCreateModel.getWorkSheetContentId());
				if (content.getType() != ContentType.WORKSHEET
						|| content.getType() == ContentType.WORKSHEET && content.getStatus() != ContentStatus.READY) {

					return ResponseModel.error(Code.INVALID, "error.worksheet.content.invalid");
				}
				assessment.setContent(content);
				if (assesmentCreateModel.getTotalAssessmentPoints() == null
						|| assesmentCreateModel.getTotalAssessmentPoints() == 0) {
					return ResponseModel.error(Code.INVALID, "error.assessment.totalpoints");
				}
				assessment.setTotalPoints(assesmentCreateModel.getTotalAssessmentPoints());
			}
		}
		default -> {
		}
		}

		assessmentRepository.save(assessment);
		joined.setAssessmentCount(
				assessmentRepository.countByOwnerIdAndSpaceIdAndDeletedFalse(user.getId(), space.getId()));
		joinedRepository.save(joined);
		return ResponseModel.done(assessment.getId(),
				new AssessementsInfoMessage(assessment.getId(), assessment.getTitle(), assessment.getAssessmentType(),
						new From(user.getId(), user.getFullName(), user.getThumbnail(), null),
						assessment.getSpace().getId(), assessment.getStartDateTime(), assessment.getDueDate(),
						space.getName(), space.getCategory().getName(),
						challengee != null ? challengee.getId() : null));

	}

	private void extractChoices(AssessmentQuestionCreateModel assessmentQuestionCreateModel,
			AssessmentQuestion assessmentQuestion) {
		if (assessmentQuestionCreateModel.getChoicesList() != null) {
			Set<AssessmentQuestionChoice> temp = new HashSet<>();
			for (ChoicesModel choicesModel : assessmentQuestionCreateModel.getChoicesList()) {
				var choice = new AssessmentQuestionChoice();
				choice.setCorrectAnswer(choicesModel.getCorrectAnswer());
				choice.setCorrectOrder(choicesModel.getCorrectOrder());
				choice.setPairCol(choicesModel.getPairColumn());
				choice.setLabel(choicesModel.getLabel());
				choice.setCorrectAnswerResourceUrl(choicesModel.getCorrectAnswerResourceUrl());
				choice.setAssessmentQuestion(assessmentQuestion);
				temp.add(choice);
			}
			assessmentQuestion.setAssessmentQuestionChoices(temp);
		}
	}

	@Transactional
	@Auditable(EntityAction.ASSESSMENT_UPDATE)
	@PreAuthorize("hasAuthority('ASSESSMENT_UPDATE')")
	@Message(entityAction = EntityAction.ASSESSMENT_UPDATE, services = Services.NOTIFICATIONS)
	public ResponseModel update(Long id, AssessmentCreateModel assessmentModel) {
		return Optional.of(assessmentRepository.getReferenceById(id)).map(new Function<Assessment, ResponseModel>() {
			@Override
			public ResponseModel apply(Assessment assessment) {
				if (assessment.getPublish() == Boolean.TRUE) {
					throw new MintException(Code.INVALID, "error.assessment.status");
				}

				AssessmentsMapper.INSTANCE.createModelToEntity(assessmentModel, assessment);
				assessment.setStartDateTime(DateConverter.convertZonedDateTimeToDate(assessmentModel.getStartDate()));
				assessment.setDueDate(DateConverter.convertZonedDateTimeToDate(assessmentModel.getDueDate()));
				assessmentRepository.save(assessment);

				var assessmentQuestions = assessmentQuestionRepository
						.findByAssessmentAndDeletedFalse(assessment);

				if (assessmentQuestions != null && !assessmentQuestions.isEmpty()) {
					assessmentQuestions.stream()
							.filter(new Predicate<AssessmentQuestion>() {
								@Override
								public boolean test(AssessmentQuestion assessmentQuestion) {
									return assessmentQuestion.getAssessmentQuestionChoices() != null
											&& !assessmentQuestion.getAssessmentQuestionChoices().isEmpty();
								}
							})
							.forEach(new Consumer<AssessmentQuestion>() {
								@Override
								public void accept(AssessmentQuestion assessmentQuestion) {
									assessmentQuestionChoicesRepository
											.deleteAll(assessmentQuestion.getAssessmentQuestionChoices());
								}
							});
					assessmentQuestionRepository.deleteAll(assessmentQuestions);
				}
				assessment.setTotalPoints(0);
				mapAndSaveQuestion(assessmentModel, assessment);
				assessmentRepository.save(assessment);
				return ResponseModel.done();
			}
		}).orElseThrow(NotFoundException::new);
	}

	private void mapAndSaveQuestion(AssessmentCreateModel assessmentModel, Assessment assessment) {
		if (assessmentModel.getAssessmentQuestionCreateModels() != null
				&& !assessmentModel.getAssessmentQuestionCreateModels().isEmpty()) {
			assessmentModel.getAssessmentQuestionCreateModels().forEach(new Consumer<AssessmentQuestionCreateModel>() {
				@Override
				public void accept(AssessmentQuestionCreateModel assessmentQuestionCreateModel) {
					var assessmentQuestion = new AssessmentQuestion();
					AssessmentsMapper.INSTANCE.mapAssessmentQuestionModelToDomain(assessmentQuestionCreateModel,
							assessmentQuestion);
					assessmentQuestion.setId(null);
					extractChoices(assessmentQuestionCreateModel, assessmentQuestion);
					assessmentQuestion.setAssessment(assessment);
					assessmentQuestionRepository.save(assessmentQuestion);
					assessment.setTotalPoints(assessment.getTotalPoints() + assessmentQuestion.getQuestionWeight());
					log.debug("assessmentQuestionCreateModel == > {}", assessmentQuestionCreateModel.getBody());
				}
			});
		}
	}

	@Transactional
	@PreAuthorize("hasAuthority('ASSESSMENT_READ')")
	public ResponseModel get(Long id) {
		var user = User.of(SecurityUtils.getCurrentUser().getId());
		return assessmentRepository.findOneByIdAndDeletedFalse(id).map(new Function<Assessment, ResponseModel>() {
			@Override
			public ResponseModel apply(Assessment assesment) {
				var assessmentModel = assessmentMapping(assesment, user.getId());
				if (assesment.getLimitDuration() != null) {
					assessmentModel.setLimitedByTime(true);
				}

				return ResponseModel.done(assessmentModel);
			}
		}).orElseThrow(NotFoundException::new);
	}

	/** Created by A.Alsayed on 05/01/19. */
	@Transactional
	public ResponseModel getUserChallenges(Long spaceId, PageRequest pageRequest) {

		// 1. get current logged-in user:
		// ==============================
		var userDetail = SecurityUtils.getCurrentUser();
		if (userDetail != null) {
			/*
			 * 2. get user assessments by space id from My SQL where: assessment type =
			 * challenge assessment assessment is not deleted assessment is not expired
			 * status is finished.
			 */
			var currentDate = LocalDate.now().atTime(23, 59, 59, 999999999);
			var allAssessmentsPage = assessmentRepository.getUserChallenges(AssessmentType.CHALLENGE,
					spaceId, userDetail.getId(), AssessmentStatus.FINISHED,
					Date.from(currentDate.toInstant(ZoneOffset.UTC)), pageRequest);
			/*
			 * 3. transfer data to Challenge Summary Model
			 */
			List<ChallengeSummaryModel> challengeSummaryModels = new ArrayList<>();
			allAssessmentsPage.forEach(new Consumer<Assessment>() {
				@Override
				public void accept(Assessment assessment) {
					var challengeSummaryModel = mapChallengeAssessment(assessment);
					challengeSummaryModels.add(challengeSummaryModel);
				}
			});
			return PageResponseModel.done(challengeSummaryModels, allAssessmentsPage.getTotalPages(),
					pageRequest.getPageNumber(), challengeSummaryModels.size());
		}
		return null;
	}

	/** Created by A.Alsayed on 18/02/19. */
	@Transactional
	public ResponseModel getChallengeOpponents(Long challengeId) {
		// 1. get current logged-in user:
		// ==============================
		var userDetail = SecurityUtils.getCurrentUser();
		log.info("getting user details...");
		if (userDetail != null) {
			/*
			 * 2. get Challenge by challenge id from My SQL where: assessment type =
			 * challenge assessment is not deleted.
			 */
			log.info("calling assessmentRepository.findOneByIdAndDeletedFalseAndAssessmentType ........");
			List<ChallengeesGrade> opponents = new ArrayList<>();

			var assessment = assessmentRepository
					.findOneByIdAndDeletedFalseAndAssessmentType(challengeId, AssessmentType.CHALLENGE);
			if (assessment.isPresent()) {
				log.info("assessment.isPresent() .......");
				opponents = getChallengeOppenentGrades(assessment.get());
			}
			log.info("ResponseModel.done(opponents) = " + ResponseModel.done(opponents));
			return ResponseModel.done(opponents);
		}
		return null;
	}

	private ChallengeSummaryModel mapChallengeAssessment(Assessment assessment) {
		var challengeSummaryModel = new ChallengeSummaryModel();
		challengeSummaryModel.setId(assessment.getId());
		challengeSummaryModel.setCreationDate(assessment.getCreationDate());
		challengeSummaryModel.setDueDate(assessment.getDueDate());
		challengeSummaryModel.setTitle(assessment.getTitle());
		challengeSummaryModel.setOverallChallengeStatus(assessment.getAssessmentStatus());

		// getting total grades:
		getChallengeUserAssessments(assessment, challengeSummaryModel);

		return challengeSummaryModel;
	}

	private void getChallengeUserAssessments(Assessment assessment, ChallengeSummaryModel challengeSummaryModel) {
		List<ChallengeesGrade> opponents = userAssessmentRepository
				.findByAssessmentIdAndDeletedFalseOrderByTotalGradeAsc(assessment.getId()).map(new Function<UserAssessment, ChallengeesGrade>() {
					@Override
					public ChallengeesGrade apply(UserAssessment userAssessment) {
						var grade = new ChallengeesGrade();
						grade.setTotalGrade(userAssessment.getTotalGrade());
						grade.setStatus(userAssessment.getAssessmentStatus());
						if (userAssessment.getUserId().equals(assessment.getOwner().getId())) {
							grade.setId(assessment.getOwner().getId());
							grade.setName(assessment.getOwner().getFullName());
							grade.setCreator(true);
							grade.setSchool(assessment.getOwner().getSchool());
							grade.setThumbnail(assessment.getOwner().getThumbnail());

						} else {
							var user = assessment.getChallengees().stream()
									.filter(new Predicate<User>() {
										@Override
										public boolean test(User u) {
											return userAssessment.getUserId().equals(u.getId());
										}
									}).findFirst().get();
							grade.setId(user.getId());
							grade.setName(user.getFullName());
							grade.setSchool(user.getSchool());
							grade.setThumbnail(user.getThumbnail());
						}
						return grade;
					}
				}).collect(Collectors.toList());

		challengeSummaryModel.setOpponents(opponents);
	}

	/** Created by A.Alsayed on 18/02/19. */
	private List<ChallengeesGrade> getChallengeOppenentGrades(Assessment assessment) {
		log.info("calling getChallengeOppenentGrades inside function........" + assessment.getId());
		List<ChallengeesGrade> opponents = userAssessmentRepository
				.findByAssessmentIdAndDeletedFalseOrderByTotalGradeAsc(assessment.getId()).map(new Function<UserAssessment, ChallengeesGrade>() {
					@Override
					public ChallengeesGrade apply(UserAssessment userAssessment) {
						log.info("calling userAssessment........" + userAssessment);
						var grade = new ChallengeesGrade();
						grade.setTotalGrade(userAssessment.getTotalGrade());
						grade.setStatus(userAssessment.getAssessmentStatus());
						if (userAssessment.getUserId().equals(assessment.getOwner().getId())) {
							grade.setId(assessment.getOwner().getId());
							grade.setName(assessment.getOwner().getFullName());
							grade.setCreator(true);
							grade.setSchool(assessment.getOwner().getSchool());
							grade.setThumbnail(assessment.getOwner().getThumbnail());
						} else {
							var user = assessment.getChallengees().stream()
									.filter(new Predicate<User>() {
										@Override
										public boolean test(User u) {
											return userAssessment.getUserId().equals(u.getId());
										}
									}).findFirst().get();
							grade.setId(user.getId());
							grade.setName(user.getFullName());
							grade.setSchool(user.getSchool());
							grade.setThumbnail(user.getThumbnail());
						}
						return grade;
					}
				}).collect(Collectors.toList());
		log.info("opponents size ........" + opponents.size());
		return opponents;
	}

	@Transactional
	@PreAuthorize("hasAuthority('ASSESSMENT_READ')")
	public ResponseModel get(AssessmentGetAllModel assessmentGetAllModel, PageRequest pageRequest) {
		var user = User.of(SecurityUtils.getCurrentUser().getId());
		if (spaceRepository.existsById(assessmentGetAllModel.getSpaceId())) {
			var pageRequestWithDefaultSort = pageRequest;
			if (pageRequest.getSort().isUnsorted()) {
				PageRequestModel.getPageRequestModel(pageRequest.getPageNumber(), pageRequest.getPageSize(),
						Sort.by(Sort.Direction.DESC, SortField.PUBLISH_DATE.getFieldName()));
			}
			if (assessmentGetAllModel.getAssessmentType() != null) {
				Page<Assessment> allAssessmentsPage = null;
				if (assessmentGetAllModel.getAssessmentType() == AssessmentType.PRACTICE) {
					allAssessmentsPage = assessmentRepository.findAllByAssessmentTypeandOwnerId(
							assessmentGetAllModel.getAssessmentType(), assessmentGetAllModel.getSpaceId(), user.getId(),
							pageRequestWithDefaultSort);
				} else {
					allAssessmentsPage = assessmentRepository.findAllByAssessmentType(
							assessmentGetAllModel.getAssessmentType(), assessmentGetAllModel.getSpaceId(), user.getId(),
							pageRequestWithDefaultSort);
				}
				return PageResponseModel.done(
						allAssessmentsPage.getContent().stream()
								.map(new Function<Assessment, AssessmentListModel>() {
									@Override
									public AssessmentListModel apply(Assessment assessment) {
										return assessmentListMapping(assessment, user.getId());
									}
								})
								.collect(Collectors.toList()),
						allAssessmentsPage.getTotalPages(), pageRequestWithDefaultSort.getPageNumber(),
						allAssessmentsPage.getTotalElements());
			}
			var ownedPage = assessmentRepository.findBySpaceIdAndDeletedFalseAndOwnerOrPublishTrue(
					assessmentGetAllModel.getSpaceId(), user, pageRequest);

			Set<AssessmentListModel> assessmentModels = new HashSet<>(ownedPage.getContent().stream()
					.map(new Function<Assessment, AssessmentListModel>() {
						@Override
						public AssessmentListModel apply(Assessment assessment) {
							return assessmentListMapping(assessment, user.getId());
						}
					})
					.filter(new Predicate<AssessmentListModel>() {
						@Override
						public boolean test(AssessmentListModel assessmentListModel) {
							return ((assessmentListModel.getAssessmentType() != AssessmentType.PRACTICE) || Objects.equals(assessmentListModel.getOwner(), user.getId()));
						}
					})
					.collect(Collectors.toSet()));
			return PageResponseModel.done(assessmentModels, ownedPage.getTotalPages(), pageRequest.getPageNumber(),
					assessmentModels.size());
		} else {
			throw new NotFoundException("error.space.notfound");
		}

	}

	@Transactional
	@PreAuthorize("hasAuthority('ASSESSMENT_READ')")
	public ResponseModel getAssessmentOverview(Long spaceId) {
		var assessmentGroups = assessmentRepository
				.findBySpaceIdAndPublishTrueAndPublishDateNotNullAndDeletedFalseOrderByPublishDateDesc(spaceId).collect(
						Collectors.groupingBy(Assessment::getAssessmentType, HashMap::new, Collectors.collectingAndThen(
								Collectors.toSet(), new Function<Set<Assessment>, List<AssessmentListModel>>() {
									@Override
									public List<AssessmentListModel> apply(Set<Assessment> list) {
										return mapList(list, SecurityUtils.getCurrentUser().getId());
									}
								})));
		return ResponseModel.done(assessmentGroups);

	}

	private List<AssessmentListModel> mapList(Set<Assessment> assessmentList, Long currentUserId) {
		return assessmentList.stream().limit(3).map(new Function<Assessment, AssessmentListModel>() {
			@Override
			public AssessmentListModel apply(Assessment assessment) {
				var assessmentListModel = new AssessmentListModel(assessment, userAssessmentRepository
						.findByAssessmentIdAndDeletedFalse(assessment.getId()).collect(Collectors.toList()), currentUserId);
				assessmentListModel.setNumberOfQuestions(
						assessmentQuestionRepository.countByAssessmentIdAndDeletedFalse(assessmentListModel.getId()));
				return assessmentListModel;
			}
		}).collect(Collectors.toList());
	}

	@Transactional
	@PreAuthorize("hasAuthority('ASSESSMENT_READ') and hasAuthority('ADMIN')")
	public ResponseModel getAll(PageRequest pageRequest, Long spaceId, AssessmentType assessmentType) {
		Specification<Assessment> spaceIdSpec = null;
		Specification<Assessment> typeSpec = null;
		if (spaceId != null) {
			var space = spaceRepository.findOneByIdAndDeletedFalse(spaceId).orElseThrow(NotFoundException::new);
			spaceIdSpec = new Specification<Assessment>() {
				@Override
				@Nullable
				public jakarta.persistence.criteria.Predicate toPredicate(Root<Assessment> root, CriteriaQuery<?> query,
						CriteriaBuilder cb) {
					return cb.equal(root.get("space"), space);
				}
			};
		}

		if (assessmentType != null) {
			typeSpec = new Specification<Assessment>() {
				@Override
				@Nullable
				public jakarta.persistence.criteria.Predicate toPredicate(Root<Assessment> root, CriteriaQuery<?> query,
						CriteriaBuilder cb) {
					return cb.equal(root.get("assessmentType"), assessmentType);
				}
			};
		}

		Page<AssessmentListModel> allAssessmentsPage = assessmentRepository
				.findAll(Specification.where(spaceIdSpec).and(typeSpec)
						.and(new Specification<Assessment>() {
							@Override
							@Nullable
							public jakarta.persistence.criteria.Predicate toPredicate(Root<Assessment> root,
									CriteriaQuery<?> query, CriteriaBuilder cb) {
								return cb.isFalse(root.get("deleted"));
							}
						}), pageRequest)
				.map(new Function<Assessment, AssessmentListModel>() {
					@Override
					public AssessmentListModel apply(Assessment assessment) {
						return assessmentListMapping(assessment, SecurityUtils.getCurrentUser().getId());
					}
				});

		return PageResponseModel.done(allAssessmentsPage.getContent(), allAssessmentsPage.getTotalPages(),
				pageRequest.getPageNumber(), allAssessmentsPage.getTotalElements());

	}

	@Transactional
	@Auditable(EntityAction.ASSESSMENT_DELETE)
	@PreAuthorize("hasAuthority('ASSESSMENT_DELETE')")
	public ResponseModel delete(Long id) {
		if (null != id) {
			return assessmentRepository.findOneByIdAndDeletedFalse(id).map(new Function<Assessment, ResponseModel>() {
				@Override
				public ResponseModel apply(Assessment assessment) {
					if (assessment.getAssessmentType() != AssessmentType.PRACTICE
							&& userAssessmentRepository.findByAssessmentIdAndDeletedFalse(id).count() >= 1) {
						throw new MintException(Code.INVALID, "error.assessment.status");
					}

					var joined = joinedRepository.findOneBySpaceIdAndUserIdAndSpaceRoleInAndDeletedFalse(
							assessment.getSpace().getId(), SecurityUtils.getCurrentUser().getId(), SpaceRole.COLLABORATOR,
							SpaceRole.CO_OWNER, SpaceRole.EDITOR, SpaceRole.OWNER).orElseThrow(NotPermittedException::new);

					var assessmentQuestions = assessmentQuestionRepository
							.findByAssessmentAndDeletedFalse(assessment);

					if (assessmentQuestions != null && !assessmentQuestions.isEmpty()) {
						for (var iterator = assessmentQuestions.iterator(); iterator.hasNext();) {
							if (iterator.hasNext()) {
								var assessmentQuestion = iterator.next();
								if (assessmentQuestion.getAssessmentQuestionChoices() != null
										&& !assessmentQuestion.getAssessmentQuestionChoices().isEmpty()) {
									for (var iteratorChoices = assessmentQuestion
											.getAssessmentQuestionChoices().iterator(); iterator.hasNext();) {
										if (iteratorChoices.hasNext()) {
											var choice = iteratorChoices.next();
											assessmentQuestionChoicesRepository.delete(choice);
										} else {
											break;
										}
									}
								}
							} else {
								break;
							}
						}
						assessmentQuestionRepository.deleteAll(assessmentQuestions);
					}
					assessmentRepository.delete(assessment);
					joined.setAssessmentCount(assessmentRepository.countByOwnerIdAndSpaceIdAndDeletedFalse(
							SecurityUtils.getCurrentUser().getId(), assessment.getSpace().getId()));
					joinedRepository.save(joined);
					return ResponseModel.done();
				}
			}).orElseThrow(NotFoundException::new);
		}
		throw new MintException(Code.INVALID);
	}

	/** created by A.Alsayed 07-03-2019 */
	public void autoSubmitChallenge(UserAssessmentModel userAssessmentModel) {
		log.info("autoSubmitChallenge ::: Start ...........................");
		var assessment = assessmentRepository
				.findOneByIdAndDeletedFalse(userAssessmentModel.getAssessmentId());
		if (assessment.isPresent()) {
			log.info("autoSubmitChallenge ::: assessment [" + assessment.get().getId() + "] exists");
			var userAssessment = userAssessmentRepository
					.findOneByUserIdAndAssessmentIdAndDeletedFalse(userAssessmentModel.getUserId(),
							userAssessmentModel.getAssessmentId());
			if (userAssessment.isPresent()) {
				log.info("autoSubmitChallenge ::: userAssessment [" + userAssessment.get().getId() + "] exists");
				if (Arrays.asList(AssessmentStatus.FINISHED, AssessmentStatus.NOT_EVALUATED, AssessmentStatus.EVALUATED)
						.contains(userAssessment.get().getAssessmentStatus())) {
					throw new MintException(Code.INVALID, "error.assessment.taken");
				}
				userAssessment.get().setAssessmentStatus(AssessmentStatus.FINISHED);
				if (userAssessmentModel.getDuration() != null) {
					userAssessment.get().setDuration(userAssessmentModel.getDuration());
				}

				var assessmentQuestions = assessmentQuestionRepository
						.findByAssessmentAndDeletedFalse(assessment.get());
				assessmentQuestions.forEach(new Consumer<AssessmentQuestion>() {
					@Override
					public void accept(AssessmentQuestion assessmentQuestion) {
						assessmentQuestion.setQuestionWeight(1);
					}
				});

				var isChallengeFinished = checkChallengeStatus(assessment.get().getId(),
						userAssessmentModel.getUserId());
				if (isChallengeFinished) {
					assessment.get().setAssessmentStatus(AssessmentStatus.FINISHED);
				}

				assessmentQuestionRepository.saveAll(assessmentQuestions);
				assessmentRepository.save(assessment.get());

				log.info("autoSubmitChallenge ::: calculate grade method");
				grade(userAssessmentModel, userAssessment.get(), assessmentQuestions, assessment.get(),
						User.of(userAssessmentModel.getUserId()));

				userAssessmentRepository.save(userAssessment.get());

				if (isChallengeFinished) {
					updateUserChallengeScores(assessment.get().getId(), assessment.get().getSpace().getId());
				}
			}
		}
	}

	@Transactional
	@Auditable(EntityAction.ASSESSMENT_DELETE)
	@PreAuthorize("hasAuthority('ASSESSMENT_SOLVE_CREATE')")
	@Message(entityAction = EntityAction.ASSESSMENT_SUBMIT, services = Services.NOTIFICATIONS)
	public ResponseModel submit(UserAssessmentModel userAssessmentModel, String lang) {
		var user = User.of(SecurityUtils.getCurrentUser().getId());
		return assessmentRepository.findOneByIdAndDeletedFalse(userAssessmentModel.getAssessmentId())
				.map(new Function<Assessment, ResponseModel>() {
					@Override
					public ResponseModel apply(Assessment assessment) {
						var userAssessment = userAssessmentRepository
								.findOneByUserIdAndAssessmentIdAndDeletedFalse(user.getId(),
										userAssessmentModel.getAssessmentId())
								.orElseGet(new Supplier<UserAssessment>() {
									@Override
									public UserAssessment get() {
										UserAssessment userAssessment2 = new UserAssessment();
										userAssessment2.setUserId(user.getId());
										userAssessment2.setAssessmentId(assessment.getId());
										userAssessment2.setFullGrade(assessment.getTotalPoints());
										return userAssessment2;
									}
								});

						if (Arrays.asList(AssessmentStatus.FINISHED, AssessmentStatus.NOT_EVALUATED,
								AssessmentStatus.EVALUATED).contains(userAssessment.getAssessmentStatus())) {
							throw new MintException(Code.INVALID, "error.assessment.taken");
						}
						if (assessment.getAssessmentType() != AssessmentType.PRACTICE
								&& assessment.getAssessmentType() != AssessmentType.CHALLENGE
								&& userAssessmentModel.getUserId() != null
								&& !userAssessmentModel.getUserId().equals(user.getId())
								&& user.equals(assessment.getOwner())) {
							userAssessment = userAssessmentRepository
									.findOneByUserIdAndAssessmentIdAndDeletedFalse(userAssessmentModel.getUserId(),
											assessment.getId())
									.orElseThrow(new Supplier<MintException>() {
										@Override
										public MintException get() {
											return new MintException(Code.INVALID, "error.assessment.nottaken");
										}
									});
						}

						userAssessment.setAssessmentStatus(userAssessmentModel.getAssessmentStatus());

						if (userAssessmentModel.getDuration() != null) {
							userAssessment.setDuration(userAssessmentModel.getDuration());
						}

						switch (assessment.getAssessmentType()) {
						case ASSIGNMENT:
						case QUIZ:
						case PRACTICE:
						case CHALLENGE:
							var isChallengeFinished = false;
							var assessmentQuestions = assessmentQuestionRepository
									.findByAssessmentAndDeletedFalse(assessment);
							if (AssessmentType.PRACTICE.equals(assessment.getAssessmentType())
									|| AssessmentType.CHALLENGE.equals(assessment.getAssessmentType())) {
								assessmentQuestions.forEach(new Consumer<AssessmentQuestion>() {
									@Override
									public void accept(AssessmentQuestion assessmentQuestion) {
										assessmentQuestion.setQuestionWeight(1);
									}
								});
								if (AssessmentType.CHALLENGE.equals(assessment.getAssessmentType())) {
									/** changes by A.Alsayed 20-02-2019 */
									// check challenge status using owner and opponent.
									isChallengeFinished = checkChallengeStatus(assessment.getId(), user.getId());
									if (isChallengeFinished) {
										assessment.setAssessmentStatus(AssessmentStatus.FINISHED);
									}
								} else {
									assessment.setAssessmentStatus(userAssessmentModel.getAssessmentStatus());
								}
								assessmentQuestionRepository.saveAll(assessmentQuestions);
								assessmentRepository.save(assessment);
							}

							grade(userAssessmentModel, userAssessment, assessmentQuestions, assessment, user);

							break;
						case WORKSHEET:
							if (!assessment.getOwner().getId().equals(user.getId())
									&& userAssessmentModel.getUserWorkSheetAnswerModel() != null) {
								userAssessment.setWorkSheetAnswerModel(AssessmentsMapper.INSTANCE
										.cloneToNewModel(userAssessmentModel.getUserWorkSheetAnswerModel()));
								userAssessmentModel.setFullGrade(assessment.getTotalPoints());
								userAssessment.setAssessmentStatus(AssessmentStatus.NOT_EVALUATED);
							} else if (assessment.getOwner().getId().equals(user.getId())
									&& userAssessmentModel.getOwnerWorkSheetAnswerModel() != null) {
								userAssessment.setOwnerWorkSheetAnswerModel(AssessmentsMapper.INSTANCE
										.cloneToNewModel(userAssessmentModel.getOwnerWorkSheetAnswerModel()));
								userAssessment.setTotalGrade(userAssessmentModel.getTotalGrade());
								if (assessment.getTotalPoints() > 0 && null != assessment.getTotalPoints()) {
									userAssessment.setPercentage(userAssessment.getTotalGrade()
											/ assessment.getTotalPoints().floatValue() * 100);
								}
								userAssessment.setAssessmentStatus(AssessmentStatus.EVALUATED);
							} else {
								throw new MintException(Code.INVALID);
							}
							break;
						default:
							throw new InvalidException("error.assessment.type");
						}
						userAssessmentRepository.save(userAssessment);

						/** changes by A.Alsayed 15-01-2019 */
						// update user space score with the new grade of assessment after
						// evaluation:
						if (AssessmentType.PRACTICE.equals(assessment.getAssessmentType())) {
							updateUserSpaceScore(user.getId(), assessment.getSpace().getId(),
									userAssessment.getTotalGrade() * 10);
						} else if (AssessmentType.CHALLENGE.equals(assessment.getAssessmentType())
								&& checkChallengeStatus(assessment.getId(), user.getId())) {
							// user space score with challenge results.
							updateUserChallengeScores(assessment.getId(), assessment.getSpace().getId());
						}

						final var contentUserModel = new AssessmentUserModel();
						var user1 = user;
						if (!user.getId().equals(userAssessment.getUserId())) {
							user1 = User.of(userAssessment.getUserId());
						}
						contentUserModel.setId(user1.getId());
						contentUserModel.setName(user1.getFullName());
						contentUserModel.setUserName(user1.getUserName());
						contentUserModel.setImage(user1.getThumbnail());
						contentUserModel.setFullGrade(userAssessment.getFullGrade());
						contentUserModel.setTotalGrade(userAssessment.getTotalGrade());
						contentUserModel.setAssessmentStatus(userAssessment.getAssessmentStatus());
						contentUserModel.setPercentage(userAssessment.getPercentage());
						contentUserModel.setMessage(getReportMessage(userAssessment.getPercentage(),
								Locale.forLanguageTag(lang == null ? "en" : lang)));
						return ResponseModel.done(contentUserModel,
								new AssessmentSubmitMessage(assessment.getId(), assessment.getTitle(),
										assessment.getAssessmentType(), new From(SecurityUtils.getCurrentUser()),
										assessment.getSpace().getId(), assessment.getStartDateTime(),
										assessment.getDueDate(), assessment.getSpace().getName(),
										assessment.getSpace().getCategory().getName(), userAssessment.getAssessmentStatus(),
										user.equals(assessment.getOwner()), new UserInfoMessage(user1),
										new UserInfoMessage(assessment.getOwner())));
					}
				}).orElseThrow(NotFoundException::new);
	}

	/**
	 * created by A.Alsayed 20-02-2019 Check if all users complete their challenge.
	 */
	private boolean checkChallengeStatus(Long assessmentId, Long userId) {
		List<UserAssessment> userAssessmentsList = userAssessmentRepository
				.findByAssessmentIdAndDeletedFalse(assessmentId).collect(Collectors.toList());
		if (userAssessmentsList != null && !userAssessmentsList.isEmpty()) {
			for (UserAssessment userAssessment : userAssessmentsList) {
				if (userAssessment.getAssessmentStatus() != AssessmentStatus.EVALUATED
						&& userAssessment.getUserId().longValue() != userId.longValue()) {
					return false;
				}
			}
		}
		return true;
	}

	/** created by A.Alsayed 15-01-2019 */
	// update user space score with the new grade of assessment after evaluation
	private void updateUserSpaceScore(Long userId, Long spaceId, float totalGrade) {
		// 1. get joined record by user id and space id:
		joinedRepository.findOneByUserIdAndSpaceIdAndDeletedFalse(userId, spaceId).ifPresent(new Consumer<Joined>() {
			@Override
			public void accept(Joined joined) {
				// 2. update total score by adding user assessment total grade:
				joined.setSpaceScorePoints(joined.getSpaceScorePoints() + totalGrade);

				// 3. save joined record in DB:
				joinedRepository.save(joined);
			}
		});
		/**
		 * modified by A.Alsayed 23-01-2019 Update total user score, this attribute to
		 * be used for getting user global ranking.
		 */
		// TODO: restructre this code to gamification
		// userRepository
		// .findOneByIdAndDeletedFalse(userId)
		// .ifPresent(
		// user -> {
		// user.setTotalScore(user.getTotalScore() + totalGrade);
		// userRepository.save(user);
		// });
	}

	private void updateUserChallengeScores(Long assessmentId, Long spaceId) {
		List<UserAssessment> userAssessmentsList = userAssessmentRepository
				.findByAssessmentIdAndDeletedFalseOrderByTotalGradeDesc(assessmentId).collect(Collectors.toList());
		if (userAssessmentsList != null && userAssessmentsList.size() == 2) {
			if (userAssessmentsList.get(0).getTotalGrade().floatValue() > userAssessmentsList.get(1).getTotalGrade()
					.floatValue()) {
				// winner found
				updateUserSpaceScore(userAssessmentsList.get(0).getUserId(), spaceId,
						userAssessmentsList.get(0).getTotalGrade() * 20);
				updateUserSpaceScore(userAssessmentsList.get(1).getUserId(), spaceId,
						userAssessmentsList.get(1).getTotalGrade() * 10);
			} else {
				// result is draw.
				for (UserAssessment userAssessment : userAssessmentsList) {
					updateUserSpaceScore(userAssessment.getUserId(), spaceId, userAssessment.getTotalGrade() * 10);
				}
			}
		}
	}

	// TODO: use lamda experssion
	@Transactional
	public ResponseModel startChallenge(long assessmentId) {

		log.info("startChallenge ::: user exists");
		var assessment = checkAssessmentByOwnerOrChallengee(assessmentId,
				SecurityUtils.getCurrentUser().getId());
		if (assessment != null) {
			log.info("startChallenge ::: assessment [" + assessment.getId() + "] exists");
			// mark assessment as started:
			if (assessment.getAssessmentStatus() != null && (assessment.getAssessmentStatus() == AssessmentStatus.NEW
					|| assessment.getAssessmentStatus() == AssessmentStatus.NOT_STARTED)) {
				assessment.setAssessmentStatus(AssessmentStatus.STARTED);
				assessmentRepository.save(assessment);
				log.info("startChallenge ::: mark assessment as started");
			}

			var userAssessment = userAssessmentRepository
					.findOneByUserIdAndAssessmentIdAndDeletedFalse(SecurityUtils.getCurrentUser().getId(),
							assessmentId);
			if (userAssessment.isPresent()) {
				log.info("startChallenge ::: userAssessment [" + userAssessment.get().getId() + "] exists");
				// mark user assessment as started
				userAssessment.get().setAssessmentStatus(AssessmentStatus.STARTED);
				userAssessmentRepository.save(userAssessment.get());
				log.info("startChallenge ::: mark userAssessment as started");

				// start the auto solving process:
				// ===============================
				var userPracticeModel = new UserPracticeModel();

				userPracticeModel.setAssessmentId(assessmentId);
				userPracticeModel.setAssessmentStatus(AssessmentStatus.STARTED);
				userPracticeModel.setUserId(SecurityUtils.getCurrentUser().getId());

				log.info("startChallenge ::: start the auto solving process");

				taskScheduler.schedule(
						new AssessmentAutoSolvingRunnable(assessmentId, SecurityUtils.getCurrentUser().getId(),
								assessment.getLimitDuration(), userAssessmentRepository, this),
						LocalDateTime.now().plusSeconds(120 + assessment.getLimitDuration() / 1000)
								.toInstant(ZoneOffset.UTC));

				return ResponseModel.done(userPracticeModel);
			} else {
				throw new NotFoundException();
			}
		} else {
			throw new NotFoundException();
		}

	}

	@Transactional
	@Auditable(EntityAction.ASSESSMENT_SUBMIT)
	public ResponseModel submitQuestion(UserPracticeModel userPracticeModel) {

		var assessment = checkAssessmentByOwnerOrChallengee(userPracticeModel.getAssessmentId(),
				SecurityUtils.getCurrentUser().getId());
		if (assessment != null) {
			// change assessment status to Started:
			if (assessment.getAssessmentStatus() != null && (assessment.getAssessmentStatus() == AssessmentStatus.NEW
					|| assessment.getAssessmentStatus() == AssessmentStatus.NOT_STARTED)) {
				assessment.setAssessmentStatus(AssessmentStatus.STARTED);
				assessmentRepository.save(assessment);

				// NEW: Start counting based on the limit duration (automatic solving):
				// ====================================================================
				// if (assessment.getAssessmentType() == AssessmentType.CHALLENGE) {
				// taskScheduler.schedule(
				// new AssessmentAutoSolvingRunnable(userPracticeModel,
				// assessment.getLimitDuration()),
				// new Date(System.currentTimeMillis() + assessment.getLimitDuration()));
				// }
			}

			var userAssessment = userAssessmentRepository
					.findOneByUserIdAndAssessmentIdAndDeletedFalse(SecurityUtils.getCurrentUser().getId(),
							userPracticeModel.getAssessmentId());
			if (userAssessment.isPresent()) {
				var questionAnswer = questionAnswerRepository
						.findOneByUserIdAndQuestionIdAndDeletedFalse(SecurityUtils.getCurrentUser().getId(),
								userPracticeModel.getQuestionAnswerModels().getQuestionId())
						.map(new Function<QuestionAnswer, QuestionAnswer>() {
							@Override
							public QuestionAnswer apply(QuestionAnswer questionAnswer1) {
								questionAnswer1.setUserAnswer(userPracticeModel.getQuestionAnswerModels().getUserAnswer());
								questionAnswer1.setGrade(userPracticeModel.getQuestionAnswerModels().getGrade());
								return questionAnswer1;
							}
						}).orElseGet(new Supplier<QuestionAnswer>() {
							@Override
							public QuestionAnswer get() {
								QuestionAnswer questionAnswer1 = new QuestionAnswer();
								questionAnswer1.setUserAnswer(userPracticeModel.getQuestionAnswerModels().getUserAnswer());
								questionAnswer1.setQuestionId(userPracticeModel.getQuestionAnswerModels().getQuestionId());
								questionAnswer1.setUserId(SecurityUtils.getCurrentUser().getId());
								questionAnswer1.setGrade(userPracticeModel.getQuestionAnswerModels().getGrade());
								return questionAnswer1;
							}
						});
				questionAnswerRepository.save(questionAnswer);
				if (!userAssessment.get().getQuestionAnswerList().contains(questionAnswer)) {
					userAssessment.get().getQuestionAnswerList().add(questionAnswer);
				}
				userAssessment.get()
						.setAssessmentStatus(userPracticeModel.getAssessmentStatus() == AssessmentStatus.NEW
								|| userPracticeModel.getAssessmentStatus() == AssessmentStatus.NOT_STARTED
										? AssessmentStatus.STARTED
										: userPracticeModel.getAssessmentStatus());
				userAssessmentRepository.save(userAssessment.get());
				return ResponseModel.done((Object) userAssessment.get().getId());
			} else {
				var newUserAssessment = new UserAssessment();
				newUserAssessment.setAssessmentId(userPracticeModel.getAssessmentId());
				newUserAssessment.setUserId(SecurityUtils.getCurrentUser().getId());
				newUserAssessment.setAssessmentStatus(userPracticeModel.getAssessmentStatus() == AssessmentStatus.NEW
						|| userPracticeModel.getAssessmentStatus() == AssessmentStatus.NOT_STARTED
								? AssessmentStatus.STARTED
								: userPracticeModel.getAssessmentStatus());
				newUserAssessment.setQuestionAnswerList(new ArrayList<>());
				var questionAnswer = new QuestionAnswer();
				questionAnswer.setUserAnswer(userPracticeModel.getQuestionAnswerModels().getUserAnswer());
				questionAnswer.setQuestionId(userPracticeModel.getQuestionAnswerModels().getQuestionId());
				questionAnswer.setUserId(SecurityUtils.getCurrentUser().getId());
				questionAnswer.setGrade(userPracticeModel.getQuestionAnswerModels().getGrade());
				questionAnswerRepository.save(questionAnswer);
				newUserAssessment.getQuestionAnswerList().add(questionAnswer);
				userAssessmentRepository.save(newUserAssessment);
				return ResponseModel.done((Object) newUserAssessment.getId());
			}
		} else {
			throw new NotFoundException();
		}

	}

	private Assessment checkAssessmentByOwnerOrChallengee(Long assessmentId, Long userId) {
		log.info("checkAssessmentByOwnerOrChallengee user exist with id = " + userId);
		// 1. check if assessment type is challenge or practice and the user is owner:
		var assessment = assessmentRepository.findOneByIdAndDeletedFalseAndOwnerIdAndAssessmentTypeIn(
				assessmentId, userId, AssessmentType.PRACTICE, AssessmentType.CHALLENGE);
		if (assessment.isPresent()) {
			log.info("checkAssessmentByOwnerOrChallengee OWNER assessment exist with id = " + assessment.get().getId());
			return assessment.get();
		} else {
			// 2. check if assessment type is challenge or practice and the user is
			// challengee:
			log.info("checkAssessmentByOwnerOrChallengee trying to get using challengee id....");
			var assessments = assessmentRepository.findAssessmentByChallengee(userId, assessmentId);
			if (assessments != null && assessments.size() > 0) {
				log.info("checkAssessmentByOwnerOrChallengee CHALLENGEE assessment exist with id = "
						+ assessments.get(0).getId());
				return assessments.get(0);
			}
		}
		return null;
	}

	@Transactional
	@PreAuthorize("hasAuthority('ASSESSMENT_READ')")
	public ResponseModel getUpdates(AssessmentGetAllModel assessmentGetAllModel) {

		var assessmentsUpdatesModel = new AssessmentsUpdatesModel();
		var userId = SecurityUtils.getCurrentUser().getId();
		assessmentsUpdatesModel.setDeletedAssessments(assessmentRepository
				.findBySpaceIdAndDeletedDateAfterAndDeletedTrue(assessmentGetAllModel.getSpaceId(),
						DateConverter.convertZonedDateTimeToDate(assessmentGetAllModel.getDate()))
				.map(new Function<Assessment, AssessmentModel>() {
					@Override
					public AssessmentModel apply(Assessment assessment) {
						return assessmentMapping(assessment, userId);
					}
				}).collect(Collectors.toList()));
		assessmentsUpdatesModel.setUpdatedAssessments(assessmentRepository
				.findBySpaceIdAndLastModifiedDateAfterAndDeletedFalse(assessmentGetAllModel.getSpaceId(),
						DateConverter.convertZonedDateTimeToDate(assessmentGetAllModel.getDate()))
				.map(new Function<Assessment, AssessmentModel>() {
					@Override
					public AssessmentModel apply(Assessment assessment) {
						return assessmentMapping(assessment, userId);
					}
				}).collect(Collectors.toList()));
		assessmentsUpdatesModel.setNewAssessments(assessmentRepository
				.findBySpaceIdAndLastModifiedDateIsNullAndCreationDateAfterAndDeletedFalse(
						assessmentGetAllModel.getSpaceId(),
						DateConverter.convertZonedDateTimeToDate(assessmentGetAllModel.getDate()))
				.map(new Function<Assessment, AssessmentModel>() {
					@Override
					public AssessmentModel apply(Assessment assessment) {
						return assessmentMapping(assessment, userId);
					}
				}).collect(Collectors.toList()));

		return ResponseModel.done(assessmentsUpdatesModel);

	}

	@Transactional
	@Auditable(EntityAction.ASSESSMENT_PUBLISH)
	@PreAuthorize("hasAuthority('ASSESSMENT_UPDATE')")
	public ResponseModel togglePublish(Long id) {
		return assessmentRepository.findOneByIdAndDeletedFalse(id).map(new Function<Assessment, ResponseModel>() {
			@Override
			public ResponseModel apply(Assessment assessment) {
				if (!assessment.getPublish()) {
					if ((assessment.getAssessmentType() == AssessmentType.QUIZ
							|| assessment.getAssessmentType() == AssessmentType.ASSIGNMENT)
							&& assessmentQuestionRepository.findByAssessmentAndDeletedFalse(assessment).isEmpty()) {
						return ResponseModel.error(Code.INVALID, "error.assessment.question.empty");
					}
					assessment.setPublish(Boolean.TRUE);
					assessment.setPublishDate(new Date());
				} else {
					assessment.setPublish(Boolean.FALSE);
				}
				assessmentRepository.save(assessment);
				return ResponseModel.done();
			}
		}).orElseThrow(NotFoundException::new);
	}

	@Transactional
	@PreAuthorize("hasAuthority('ASSESSMENT_READ')")
	public ResponseModel getCommunityList(Long id) {
		return assessmentRepository.findOneByIdAndDeletedFalse(id).map(new Function<Assessment, ResponseModel>() {
			@Override
			public ResponseModel apply(Assessment assessment) {
				Set<AssessmentUserModel> communityList = new HashSet<>();
				Supplier<Stream<UserAssessment>> userAssessmentSupplier = new Supplier<Stream<UserAssessment>>() {
					@Override
					public Stream<UserAssessment> get() {
						return userAssessmentRepository
								.findByAssessmentIdAndDeletedFalse(id);
					}
				};
				var userList = joinedRepository.findBySpaceIdAndDeletedFalse(assessment.getSpace().getId())
						.filter(new Predicate<Joined>() {
							@Override
							public boolean test(Joined joined) {
								return !joined.getUser().equals(assessment.getOwner());
							}
						}).map(Joined::getUser)
						.collect(Collectors.toSet());
				for (User user : userList) {
					final var contentUserModel = new AssessmentUserModel();
					contentUserModel.setId(user.getId());
					contentUserModel.setName(user.getFullName());
					contentUserModel.setUserName(user.getUserName());
					contentUserModel.setImage(user.getThumbnail());
					userAssessmentSupplier.get().filter(new Predicate<UserAssessment>() {
						@Override
						public boolean test(UserAssessment userAssessment) {
							return userAssessment.getUserId().equals(user.getId());
						}
					})
							.findFirst().ifPresent(new Consumer<UserAssessment>() {
								@Override
								public void accept(UserAssessment userAssessment) {
									contentUserModel.setFullGrade(userAssessment.getFullGrade());
									contentUserModel.setTotalGrade(userAssessment.getTotalGrade());
									contentUserModel.setAssessmentStatus(userAssessment.getAssessmentStatus());
								}
							});

					communityList.add(contentUserModel);
				}
				return ResponseModel.done(communityList);
			}
		}).orElseThrow(NotFoundException::new);
	}

	@Transactional
	@PreAuthorize("hasAuthority('ASSESSMENT_READ')")
	public ResponseModel getUserAssessment(Long assessmentId, Long userId) {
		var assessment = assessmentRepository.findById(assessmentId).orElseThrow(NotFoundException::new);

		return userAssessmentRepository.findOneByUserIdAndAssessmentIdAndDeletedFalse(userId, assessmentId)
				.map(new Function<UserAssessment, ResponseModel>() {
					@Override
					public ResponseModel apply(UserAssessment userAssessment) {
						var userAssessmentGetModel = new AssessmentModel(assessment,
								Collections.singletonList(userAssessment), userId);
						userAssessmentGetModel.getAssessmentQuestionCreateModels().clear();
						switch (assessment.getAssessmentType()) {
						case ASSIGNMENT:
						case QUIZ:
						case CHALLENGE:
							assessment.getAssessmentQuestions().stream()
									.filter(new Predicate<AssessmentQuestion>() {
										@Override
										public boolean test(AssessmentQuestion assessmentQuestion) {
											return !assessmentQuestion.isDeleted();
										}
									})
									.forEach(new Consumer<AssessmentQuestion>() {
										@Override
										public void accept(AssessmentQuestion assessmentQuestion) {
											var questionAnswerGetModel = mapQuestionAnswerGetModel(
													assessmentQuestion);
											userAssessment.getQuestionAnswerList().stream()
													.filter(new Predicate<QuestionAnswer>() {
														@Override
														public boolean test(QuestionAnswer questionAnswer) {
															return questionAnswer.getQuestionId()
																	.equals(questionAnswerGetModel.getId());
														}
													})
													.findFirst().ifPresent(new Consumer<QuestionAnswer>() {
														@Override
														public void accept(QuestionAnswer questionAnswer) {
															questionAnswerGetModel.setUserAnswer(questionAnswer.getUserAnswer());
															questionAnswerGetModel.setGrade(questionAnswer.getGrade());
														}
													});
											userAssessmentGetModel.getAssessmentQuestionCreateModels()
													.add(questionAnswerGetModel);
										}
									});
							break;
						case WORKSHEET:
							if (userAssessment.getWorkSheetAnswerModel() != null) {
								userAssessmentGetModel.setUserWorkSheetAnswerModel(AssessmentsMapper.INSTANCE
										.cloneToNewModel(userAssessment.getWorkSheetAnswerModel()));
							}
							if (userAssessment.getOwnerWorkSheetAnswerModel() != null) {
								userAssessmentGetModel.setOwnerWorkSheetAnswerModel(AssessmentsMapper.INSTANCE
										.cloneToNewModel(userAssessment.getOwnerWorkSheetAnswerModel()));
							}
							userAssessmentGetModel.setTotalGrade(userAssessment.getTotalGrade());
							break;
						case PRACTICE:
							if (assessment.getOwner().getId().equals(SecurityUtils.getCurrentUser().getId())) {
								assessment.getAssessmentQuestions().stream()
										.filter(new Predicate<AssessmentQuestion>() {
											@Override
											public boolean test(AssessmentQuestion assessmentQuestion) {
												return !assessmentQuestion.isDeleted();
											}
										})
										.forEach(new Consumer<AssessmentQuestion>() {
											@Override
											public void accept(AssessmentQuestion assessmentQuestion) {
												var questionAnswerGetModel = mapQuestionAnswerGetModel(
														assessmentQuestion);
												userAssessment.getQuestionAnswerList().stream()
														.filter(new Predicate<QuestionAnswer>() {
															@Override
															public boolean test(QuestionAnswer questionAnswer) {
																return questionAnswer.getQuestionId()
																		.equals(questionAnswerGetModel.getId());
															}
														})
														.findFirst().ifPresent(new Consumer<QuestionAnswer>() {
															@Override
															public void accept(QuestionAnswer questionAnswer) {
																questionAnswerGetModel
																		.setUserAnswer(questionAnswer.getUserAnswer());
																questionAnswerGetModel.setGrade(0.0f);
															}
														});
												userAssessmentGetModel.getAssessmentQuestionCreateModels()
														.add(questionAnswerGetModel);
												userAssessmentGetModel.setLimitDuration(userAssessment.getDuration());
											}
										});
							} else {
								throw new NotPermittedException();
							}
							break;
						}
						return ResponseModel.done(userAssessmentGetModel);
					}
				}).orElseThrow(new Supplier<MintException>() {
					@Override
					public MintException get() {
						return new MintException(Code.INVALID_CODE, "error.assessment.nottaken");
					}
				});
	}

	// TODO: Review
	// Status are STARTED , FINISHED OR PAUSED
	@Transactional
	@Auditable(EntityAction.ASSESSMENT_UPDATE)
	public ResponseModel updateAssessmentStatus(Long assessmentId, AssessmentStatus assessmentStatus) {
		return assessmentRepository.findOneByIdAndDeletedFalseAndOwnerIdAndAssessmentTypeIn(assessmentId,
				SecurityUtils.getCurrentUser().getId(), AssessmentType.PRACTICE).map(new Function<Assessment, ResponseModel>() {
					@Override
					public ResponseModel apply(Assessment assessment) {
						assessment.setAssessmentStatus(assessmentStatus);
						if (AssessmentStatus.STARTED.equals(assessmentStatus)) {
							assessment.setStartDateTime(new Date());
						}
						assessmentRepository.save(assessment);
						return ResponseModel.done();
					}
				}).orElseThrow(NotFoundException::new);
	}

	// TODO: Review
	@Transactional
	@Auditable(EntityAction.ASSESSMENT_UPDATE)
	public ResponseModel reset(Long id) {
		userAssessmentRepository
				.findOneByUserIdAndAssessmentIdAndDeletedFalse(SecurityUtils.getCurrentUser().getId(), id)
				.ifPresent(new Consumer<UserAssessment>() {
					@Override
					public void accept(UserAssessment userAssessment) {
						questionAnswerRepository.deleteAll(userAssessment.getQuestionAnswerList());
						userAssessmentRepository.delete(userAssessment);
						assessmentRepository.findById(id).ifPresent(new Consumer<Assessment>() {
							@Override
							public void accept(Assessment assessment) {
								assessment.setAssessmentStatus(AssessmentStatus.NEW);
								assessment.setStartDateTime(null);
								assessment.setLimitDuration(0L);
								assessmentRepository.save(assessment);
							}
						});
					}
				});
		return ResponseModel.done();
	}

	private AssessmentListModel assessmentListMapping(Assessment assessment, Long currentUserId) {

		List<UserAssessment> userAssessmentsList = userAssessmentRepository
				.findByAssessmentIdAndDeletedFalseOrderByTotalGradeAsc(assessment.getId()).collect(Collectors.toList());
		var assessmentListModel = new AssessmentListModel(assessment, userAssessmentsList,
				currentUserId);

		List<Long> userIdList = userAssessmentsList.stream()
				.filter(new Predicate<UserAssessment>() {
					@Override
					public boolean test(UserAssessment userAssessment1) {
						return userAssessment1.getAssessmentStatus() == AssessmentStatus.FINISHED
								|| userAssessment1.getAssessmentStatus() == AssessmentStatus.NOT_EVALUATED;
					}
				})
				.limit(MAX_USER_IN_COMMUNITY).map(UserAssessment::getUserId).collect(Collectors.toList());
		assessmentListModel.setUserCommunity(getCommunityList(userIdList));
		assessmentListModel.setNumberOfQuestions(
				assessmentQuestionRepository.countByAssessmentIdAndDeletedFalse(assessment.getId()));
		return assessmentListModel;
	}

	private AssessmentModel assessmentMapping(Assessment assessment, Long currentUserId) {

		List<UserAssessment> userAssessmentsList = userAssessmentRepository
				.findByAssessmentIdAndDeletedFalseOrderByTotalGradeAsc(assessment.getId()).collect(Collectors.toList());

		var assessmentModel = new AssessmentModel(assessment, userAssessmentsList, currentUserId);

		List<Long> userIdList = userAssessmentsList.stream()
				.filter(new Predicate<UserAssessment>() {
					@Override
					public boolean test(UserAssessment userAssessment1) {
						return userAssessment1.getAssessmentStatus() == AssessmentStatus.FINISHED
								|| userAssessment1.getAssessmentStatus() == AssessmentStatus.NOT_EVALUATED;
					}
				})
				.limit(MAX_USER_IN_COMMUNITY).map(UserAssessment::getUserId).collect(Collectors.toList());
		if (!userIdList.isEmpty()) {
			assessmentModel.setUserCommunity(getCommunityList(userIdList));
		}

		return assessmentModel;
	}

	private List<ContentUserModel> getCommunityList(List<Long> userIdList) {
		List<ContentUserModel> communityList = new ArrayList<>();
		if (!userIdList.isEmpty()) {
			innerUserService.findAllById(userIdList, ContentUserModel::new)
					.forEach(new Consumer<ContentUserModel>() {
						@Override
						public void accept(ContentUserModel user) {
							communityList.add(user);
						}
					});
		}
		return communityList;
	}

	private void grade(UserAssessmentModel userAssessmentModel, UserAssessment userAssessment,
			List<AssessmentQuestion> assessmentQuestions, Assessment assessment, User user) {
		var totalGrade = 0f;
		var questionAnswerList = userAssessment.getQuestionAnswerList();
		if (AssessmentStatus.FINISHED.equals(userAssessment.getAssessmentStatus())) {
			userAssessment.setAssessmentStatus(AssessmentStatus.EVALUATED);
		}
		if (userAssessmentModel.getQuestionAnswerModels() != null
				&& !userAssessmentModel.getQuestionAnswerModels().isEmpty()) {
			for (AssessmentQuestion assessmentQuestion : assessmentQuestions) {
				var questionAnswerModelOptional = userAssessmentModel
						.getQuestionAnswerModels().stream().filter(new Predicate<QuestionAnswerModel>() {
							@Override
							public boolean test(QuestionAnswerModel questionAnswerModel1) {
								return questionAnswerModel1
										.getQuestionId().equals(assessmentQuestion.getId());
							}
						})
						.findFirst();

				if (!questionAnswerModelOptional.isPresent()) {
					continue;
				}
				var questionAnswerModel = questionAnswerModelOptional.get();

				if (questionAnswerModel.getUserAnswer() != null
						&& !questionAnswerModel.getUserAnswer().trim().isEmpty()) {
					if (questionAnswerModel.getGrade() != null && questionAnswerModel.getGrade() != 0.0f
							&& user.equals(assessment.getOwner())
							&& assessment.getAssessmentType() != AssessmentType.PRACTICE
							&& assessment.getAssessmentType() != AssessmentType.CHALLENGE) {
						var questionAnswer = new QuestionAnswer();
						AssessmentsMapper.INSTANCE.mapQuestionAnswerModelToDomain(questionAnswerModel, questionAnswer);
						questionAnswer.setUserId(userAssessment.getUserId());
						var index = questionAnswerList.indexOf(questionAnswer);
						if (index == -1) {
							questionAnswerList.add(questionAnswer);
						} else {
							questionAnswerList.get(index).setGrade(questionAnswerModel.getGrade());
						}

						totalGrade += questionAnswerModel.getGrade();
						continue;
					}
					questionAnswerModel.setGrade(0f);
					switch (assessmentQuestion.getQuestionType()) {
					case TRUE_FALSE -> gradeTrueFalseQuestion(questionAnswerModel, assessmentQuestion);
					case SINGLE_CHOICE -> gradeSingleChoiceQuestion(questionAnswerModel, assessmentQuestion);
					case MATCHING -> gradeMatchingQuestion(questionAnswerModel, assessmentQuestion);
					case MULTIPLE_CHOICES -> gradeMultipleChoicesQuestion(questionAnswerModel, assessmentQuestion);
					case SEQUENCE -> gradeSequenceQuestion(questionAnswerModel, assessmentQuestion);
					case COMPLETE -> gradeCompleteQuestion(questionAnswerModel, assessmentQuestion);
					case ESSAY -> {
						if (!user.getId().equals(assessment.getOwner().getId())) {
							userAssessment.setAssessmentStatus(AssessmentStatus.NOT_EVALUATED);
						}
					}
					}

					totalGrade += questionAnswerModel.getGrade();
				}

				var questionAnswer = new QuestionAnswer();
				AssessmentsMapper.INSTANCE.mapQuestionAnswerModelToDomain(questionAnswerModel, questionAnswer);
				questionAnswer.setUserId(userAssessment.getUserId());
				questionAnswerList.add(questionAnswer);
			}
		}

		userAssessment.setTotalGrade(totalGrade);

		if (assessment.getTotalPoints() > 0 && null != assessment.getTotalPoints()) {
			var temp = Double.valueOf(totalGrade) / Double.valueOf(assessment.getTotalPoints().floatValue()) * 100;
			userAssessment.setPercentage((float) temp);
		}

		questionAnswerRepository.saveAll(questionAnswerList);
		userAssessment.setQuestionAnswerList(questionAnswerList);
	}

	private void gradeTrueFalseQuestion(QuestionAnswerModel questionAnswerModel,
			AssessmentQuestion assessmentQuestion) {

		if (assessmentQuestion.getCorrectAnswer() == null
				&& "false".equalsIgnoreCase(questionAnswerModel.getUserAnswer())) {
			questionAnswerModel.setGrade(assessmentQuestion.getQuestionWeight().floatValue());
		} else if (assessmentQuestion.getCorrectAnswer() != null && assessmentQuestion.getCorrectAnswer().trim()
				.equalsIgnoreCase(questionAnswerModel.getUserAnswer().trim())) {
			questionAnswerModel.setGrade(assessmentQuestion.getQuestionWeight().floatValue());
		}
	}

	private void gradeSingleChoiceQuestion(QuestionAnswerModel questionAnswerModel,
			AssessmentQuestion assessmentQuestion) {
		var userAnswers = new ArrayList<String>(Arrays.asList(questionAnswerModel.getUserAnswer().split(",")));
		if (userAnswers.size() == 1) {
			var assessmentQuestionChoices = assessmentQuestion.getAssessmentQuestionChoices();
			for (AssessmentQuestionChoice assessmentQuestionChoice : assessmentQuestionChoices) {
				if (assessmentQuestionChoice.getCorrectAnswer()) {
					if (assessmentQuestionChoice.getId() == Long.parseLong(questionAnswerModel.getUserAnswer())) {
						questionAnswerModel.setGrade(assessmentQuestion.getQuestionWeight().floatValue());
					}
				}
			}
		}
	}

	private void gradeMultipleChoicesQuestion(QuestionAnswerModel questionAnswerModel,
			AssessmentQuestion assessmentQuestion) {
		var assessmentQuestionChoices = assessmentQuestion.getAssessmentQuestionChoices();
		var userAnswers = new ArrayList<String>(Arrays.asList(questionAnswerModel.getUserAnswer().split(",")));
		var correctAnswers = new ArrayList<String>();
		for (AssessmentQuestionChoice assessmentQuestionChoice : assessmentQuestionChoices) {
			if (Optional.ofNullable(assessmentQuestionChoice.getCorrectAnswer()).orElse(Boolean.FALSE)) {
				correctAnswers.add(String.valueOf(assessmentQuestionChoice.getId()));
			}
		}
		Collections.sort(userAnswers);
		Collections.sort(correctAnswers);
		if (correctAnswers.equals(userAnswers)) {
			questionAnswerModel.setGrade(assessmentQuestion.getQuestionWeight().floatValue());
		}
	}

	private void gradeSequenceQuestion(QuestionAnswerModel questionAnswerModel, AssessmentQuestion assessmentQuestion) {

		List<String> correctAnswers = assessmentQuestion.getAssessmentQuestionChoices().stream()
				.sorted(Comparator.comparingInt(AssessmentQuestionChoice::getCorrectOrder))
				.map(new Function<AssessmentQuestionChoice, String>() {
					@Override
					public String apply(AssessmentQuestionChoice assessmentQuestionChoice) {
						return assessmentQuestionChoice.getId().toString();
					}
				})
				.collect(Collectors.toList());
		var userAnswers = new ArrayList<String>(Arrays.asList(questionAnswerModel.getUserAnswer().split(",")));

		if (correctAnswers.equals(userAnswers)) {
			questionAnswerModel.setGrade(assessmentQuestion.getQuestionWeight().floatValue());
		}
	}

	// TODO: Need more review
	private void gradeMatchingQuestion(QuestionAnswerModel questionAnswerModel, AssessmentQuestion assessmentQuestion) {
		var point = 0;

		var userAnswers = new ArrayList<Set<String>>();
		var correctAnswers = new ArrayList<Set<String>>();

		var assessmentQuestionChoices = assessmentQuestion.getAssessmentQuestionChoices()
				.stream().sorted(Comparator.comparingInt(AssessmentQuestionChoice::getCorrectOrder)
						.thenComparing(AssessmentQuestionChoice::getPairCol))
				.toList();

		Arrays.stream(questionAnswerModel.getUserAnswer().split(",")).forEach(new Consumer<String>() {
			@Override
			public void accept(String s) {
				Set<String> pair = new HashSet<>(Arrays.asList(s.split("\\|")));
				userAnswers.add(pair);
			}
		});

		for (var i = 0; i < assessmentQuestionChoices.size() - 1; i += 2) {
			Set<String> cpair = new HashSet<>();
			cpair.add(String.valueOf(assessmentQuestionChoices.get(i).getId()));
			cpair.add(String.valueOf(assessmentQuestionChoices.get(i + 1).getId()));
			correctAnswers.add(cpair);
		}

		for (Set<String> userAnswer : userAnswers) {
			if (correctAnswers.contains(userAnswer)) {
				point++;
			}
		}

		questionAnswerModel.setGrade(point / (float) correctAnswers.size() * assessmentQuestion.getQuestionWeight());
	}

	private void gradeCompleteQuestion(QuestionAnswerModel questionAnswerModel, AssessmentQuestion assessmentQuestion) {

		var correctAnswers = new ArrayList<String>();
		assessmentQuestion.getAssessmentQuestionChoices().stream()
				.sorted(Comparator.comparingInt(AssessmentQuestionChoice::getCorrectOrder))
				.forEach(new Consumer<AssessmentQuestionChoice>() {
					@Override
					public void accept(AssessmentQuestionChoice assessmentQuestionChoice) {
						correctAnswers.add(assessmentQuestionChoice.getLabel());
					}
				});
		var userAnswers = new ArrayList<String>(Arrays.asList(questionAnswerModel.getUserAnswer().split(",")));
		if (correctAnswers.equals(userAnswers)) {
			questionAnswerModel.setGrade(assessmentQuestion.getQuestionWeight().floatValue());
		}
	}

	// TODO: Review
	private QuestionAnswerGetModel mapQuestionAnswerGetModel(AssessmentQuestion assessmentQuestion) {
		var questionAnswerGetModel = new QuestionAnswerGetModel();
		questionAnswerGetModel.setBody(assessmentQuestion.getBody());
		questionAnswerGetModel.setBodyResourceUrl(assessmentQuestion.getBodyResourceUrl());
		questionAnswerGetModel.setCorrectAnswer(assessmentQuestion.getCorrectAnswer());
		questionAnswerGetModel.setId(assessmentQuestion.getId());
		questionAnswerGetModel.setQuestionType(assessmentQuestion.getQuestionType());
		questionAnswerGetModel.setQuestionWeight(assessmentQuestion.getQuestionWeight());

		assessmentQuestion.getAssessmentQuestionChoices().forEach(new Consumer<AssessmentQuestionChoice>() {
			@Override
			public void accept(AssessmentQuestionChoice assessmentQuestionChoice) {
				var assessmentQuestionChoiceModel = new ChoicesModel();
				assessmentQuestionChoiceModel.setId(assessmentQuestionChoice.getId());
				assessmentQuestionChoiceModel.setCorrectAnswer(assessmentQuestionChoice.getCorrectAnswer());
				assessmentQuestionChoiceModel.setCorrectOrder(assessmentQuestionChoice.getCorrectOrder());
				assessmentQuestionChoiceModel.setLabel(assessmentQuestionChoice.getLabel());
				assessmentQuestionChoiceModel.setPairColumn(assessmentQuestionChoice.getPairCol());
				assessmentQuestionChoiceModel
						.setCorrectAnswerResourceUrl(assessmentQuestionChoice.getCorrectAnswerResourceUrl());
				assessmentQuestionChoiceModel
						.setCorrectAnswerDescription(assessmentQuestionChoice.getCorrectAnswerDescription());
				questionAnswerGetModel.getChoicesList().add(assessmentQuestionChoiceModel);
			}
		});

		return questionAnswerGetModel;
	}

	private QuestionBankResponseModel searchQuestionBank(QuestionSearchModel questionSearchModel,
			HttpServletRequest request) {
		var headers = new HttpHeaders();
		var questionBankUrl = "http://" + mintProperties.getApiDomain();
		log.info("url to connect {}", questionBankUrl);
		headers.setContentType(MediaType.APPLICATION_JSON);
		if (questionSearchModel.getLimit() != null && !questionSearchModel.getLimit().equals(0)) {
			headers.add("size", questionSearchModel.getLimit().toString());
		}
		headers.add("Authorization", request.getHeader("Authorization"));
		var httpRequest = new HttpEntity<QuestionSearchModel>(questionSearchModel, headers);
		try {
			log.info("request {}", httpRequest);

			return restTemplate.postForObject(questionBankUrl + "/api/question/search", httpRequest,
					QuestionBankResponseModel.class);
		} catch (RestClientException e) {
			log.error("error in search question bank", e);
			return (QuestionBankResponseModel) ResponseModel.error(Code.UNKNOWN, e.getMessage());
		}
	}

	private String getReportMessage(Float percentage, Locale locale) {
		String msg;

		if (percentage.intValue() <= 49) {
			msg = "mint.practice.morepractice";
		} else if (percentage <= 69) {
			msg = "mint.practice.redo";
		} else if (percentage <= 85) {
			msg = "mint.practice.good";
		} else if (percentage <= 95) {
			msg = "mint.practice.vgood";
		} else {
			msg = "mint.practice.excellent";
		}
		return messageSource.getMessage(msg, null, "", locale);
	}

	private boolean validateDate(ZonedDateTime date) {
		return date.isAfter(ZonedDateTime.now());
	}
}
