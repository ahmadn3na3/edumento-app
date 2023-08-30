package com.edumento.assessment.services;

import com.edumento.assessment.domain.*;
import com.edumento.assessment.mappers.AssessmentsMapper;
import com.edumento.assessment.model.*;
import com.edumento.assessment.model.challenge.ChallengeCreateModel;
import com.edumento.assessment.model.challenge.ChallengeSummaryModel;
import com.edumento.assessment.model.challenge.ChallengeesGrade;
import com.edumento.assessment.model.runnable.AssessmentAutoSolvingRunnable;
import com.edumento.assessment.repos.*;
import com.edumento.content.domain.Content;
import com.edumento.content.models.ContentUserModel;
import com.edumento.content.repos.ContentRepository;
import com.edumento.core.configuration.MintProperties;
import com.edumento.core.configuration.auditing.Auditable;
import com.edumento.core.configuration.notifications.Message;
import com.edumento.core.constants.*;
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
import com.edumento.core.security.CurrentUserDetail;
import com.edumento.core.security.SecurityUtils;
import com.edumento.core.util.DateConverter;
import com.edumento.core.util.RandomUtils;
import com.edumento.space.domain.Joined;
import com.edumento.space.domain.Space;
import com.edumento.space.repos.JoinedRepository;
import com.edumento.space.repos.SpaceRepository;
import com.edumento.user.domain.User;
import com.edumento.user.repo.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
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
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/** Created by ayman on 13/06/16. */
@Service
public class AssessmentService {
  private final Logger log = LoggerFactory.getLogger(AssessmentService.class);
  private final QuestionAnswerRepository questionAnswerRepository;
  private final AssessmentRepository assessmentRepository;
  private final AssessmentQuestionRepository assessmentQuestionRepository;
  private final AssessmentQuestionChoicesRepository assessmentQuestionChoicesRepository;
  private final RestTemplate restTemplate = new RestTemplate();
  private final MintProperties mintProperties;

  private static final int MAX_USER_IN_COMMUNITY = 4;

  private final SpaceRepository spaceRepository;
  private final ContentRepository contentRepository;

  private final UserRepository userRepository;

  private final JoinedRepository joinedRepository;

  private final UserAssessmentRepository userAssessmentRepository;
  private final MessageSource messageSource;

  @Autowired private ThreadPoolTaskScheduler taskScheduler;

  @Autowired
  public AssessmentService(
      AssessmentQuestionChoicesRepository assessmentQuestionChoicesRepository,
      AssessmentQuestionRepository assessmentQuestionRepository,
      UserAssessmentRepository userAssessmentRepository,
      UserRepository userRepository,
      AssessmentRepository assessmentRepository,
      QuestionAnswerRepository questionAnswerRepository,
      MintProperties mintProperties,
      SpaceRepository spaceRepository,
      JoinedRepository joinedRepository,
      ContentRepository contentRepository,
      MessageSource messageSource) {
    this.assessmentQuestionChoicesRepository = assessmentQuestionChoicesRepository;
    this.assessmentQuestionRepository = assessmentQuestionRepository;
    this.userAssessmentRepository = userAssessmentRepository;
    this.userRepository = userRepository;
    this.assessmentRepository = assessmentRepository;
    this.mintProperties = mintProperties;
    this.spaceRepository = spaceRepository;
    this.joinedRepository = joinedRepository;
    this.questionAnswerRepository = questionAnswerRepository;
    this.contentRepository = contentRepository;
    this.messageSource = messageSource;
  }

  @Transactional
  @Auditable(EntityAction.ASSESSMENT_CREATE)
  @PreAuthorize("hasAnyAuthority('ASSESSMENT_CREATE','CHALLENGE_CREATE')")
  @Message(entityAction = EntityAction.ASSESSMENT_CREATE, services = Services.NOTIFICATIONS)
  public ResponseModel createChallenge(
      ChallengeCreateModel challengeCreateModel, HttpServletRequest request) {
    if (challengeCreateModel.getQuestionSearchModel().getSpaceId() == null) {
      challengeCreateModel.getQuestionSearchModel().setSpaceId(challengeCreateModel.getSpaceId());
    }
    challengeCreateModel.getQuestionSearchModel().setLimit(10);
    QuestionBankResponseModel responseModel =
        searchQuestionBank(challengeCreateModel.getQuestionSearchModel(), request);
    if (responseModel.getCode() == 10) {
      if (responseModel.getData() != null && !responseModel.getData().isEmpty()) {
        /*
         * modified by A.Alsayed 7-2-2019 Add new filter for getting challengees with
         * school name not equal that of the challenge creator.
         */
        Optional<User> loggedInUser =
            userRepository.findOneByUserNameAndDeletedFalse(SecurityUtils.getCurrentUserLogin());
        String ownerSchool =
            loggedInUser.get().getSchool() != null ? loggedInUser.get().getSchool() : "";
        User userOptional =
            joinedRepository
                .findBySpaceIdAndDeletedFalse(challengeCreateModel.getSpaceId())
                .filter(
                    joined ->
                        joined.getSpaceRole() == SpaceRole.EDITOR
                            || joined.getSpaceRole() == SpaceRole.COLLABORATOR)
                .map(Joined::getUser)
                .filter(user -> !user.getId().equals(SecurityUtils.getCurrentUser().getId()))
                .filter(user -> user.getSchool() == null || !user.getSchool().equals(ownerSchool))
                .sorted(
                    (user1, user2) ->
                        RandomUtils.generateResetKey().compareTo(RandomUtils.generateResetKey()))
                .findAny()
                .orElseThrow(() -> new NotFoundException("error.assessment.challngeenotfound"));

        List<AssessmentQuestionCreateModel> assessmentQuestionCreateModels =
            responseModel.getData().stream()
                .limit(10)
                .map(
                    questionModel -> {
                      AssessmentQuestionCreateModel assessmentQuestionCreateModel =
                          new AssessmentQuestionCreateModel(questionModel);
                      assessmentQuestionCreateModel.setQuestionWeight(1);
                      return assessmentQuestionCreateModel;
                    })
                .collect(Collectors.toList());
        log.debug("assessmentQuestionCreateModels == > {}", assessmentQuestionCreateModels.size());
        AssessmentCreateModel assessmentCreateModel =
            new AssessmentCreateModel(
                challengeCreateModel.getTitle(),
                AssessmentType.CHALLENGE,
                true,
                challengeCreateModel.getSpaceId(),
                assessmentQuestionCreateModels);

        assessmentCreateModel.setLockMint(false);
        assessmentCreateModel.setLimitDuration(challengeCreateModel.getLimitDuration());
        assessmentCreateModel.setDueDate(ZonedDateTime.now().plusDays(5));
        ResponseModel model = create(assessmentCreateModel, userOptional);
        Long assessmentId = (Long) model.getData();
        Assessment assessment = assessmentRepository.getReferenceById(assessmentId);
        assessment.getChallengees().add(userOptional);
        assessment.setAssessmentStatus(AssessmentStatus.NOT_STARTED);
        assessmentRepository.save(assessment);
        UserAssessment creator = new UserAssessment();
        creator.setAssessmentId(assessment.getId());
        creator.setUserId(assessment.getOwner().getId());
        creator.setAssessmentStatus(AssessmentStatus.NOT_STARTED);
        userAssessmentRepository.save(creator);
        UserAssessment opponent = new UserAssessment();
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
  public ResponseModel generatePractice(
      PracticeGenerateModel practiceGenerateModel, HttpServletRequest request) {
    if (practiceGenerateModel.getQuestionSearchModel().getSpaceId() == null) {
      practiceGenerateModel.getQuestionSearchModel().setSpaceId(practiceGenerateModel.getSpaceId());
    }
    QuestionBankResponseModel responseModel =
        searchQuestionBank(practiceGenerateModel.getQuestionSearchModel(), request);
    if (responseModel.getCode() == 10) {
      if (responseModel.getData() != null
          && !responseModel.getData().isEmpty()
          && responseModel.getData().size()
              >= practiceGenerateModel.getQuestionSearchModel().getLimit()) {
        int maximum =
            practiceGenerateModel.getQuestionSearchModel().getQuestionType().length
                * practiceGenerateModel.getMinimum();
        log.debug("maximum == > {}", maximum);
        List<AssessmentQuestionCreateModel> assessmentQuestionCreateModels =
            responseModel.getData().stream()
                .map(
                    questionModel -> {
                      AssessmentQuestionCreateModel assessmentQuestionCreateModel =
                          new AssessmentQuestionCreateModel(questionModel);
                      assessmentQuestionCreateModel.setQuestionWeight(1);
                      return assessmentQuestionCreateModel;
                    })
                .collect(Collectors.toList());
        Collections.shuffle(assessmentQuestionCreateModels);
        log.debug("assessmentQuestionCreateModels == > {}", assessmentQuestionCreateModels.size());

        AssessmentCreateModel assessmentCreateModel =
            new AssessmentCreateModel(
                practiceGenerateModel.getPracticeName(),
                AssessmentType.PRACTICE,
                true,
                practiceGenerateModel.getSpaceId(),
                assessmentQuestionCreateModels.stream()
                    .limit(maximum)
                    .collect(Collectors.toList()));

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
    return userRepository
        .findOneByUserNameAndDeletedFalse(SecurityUtils.getCurrentUserLogin())
        .map(
            user -> {
              if (assesmentCreateModel.getSpaceId() == null) {
                throw new MintException(Code.INVALID_KEY, "spaceId");
              }
              Space space =
                  spaceRepository
                      .findOneByIdAndDeletedFalse(assesmentCreateModel.getSpaceId())
                      .orElseThrow(NotFoundException::new);

              Joined joined =
                  joinedRepository
                      .findOneBySpaceIdAndUserIdAndSpaceRoleInAndDeletedFalse(
                          space.getId(),
                          user.getId(),
                          SpaceRole.COLLABORATOR,
                          SpaceRole.CO_OWNER,
                          SpaceRole.EDITOR,
                          SpaceRole.OWNER)
                      .orElseThrow(NotPermittedException::new);

              if (assesmentCreateModel.isPublish()
                  && assesmentCreateModel.getAssessmentQuestionCreateModels().isEmpty()
                  && (assesmentCreateModel.getAssessmentType() == AssessmentType.ASSIGNMENT
                      || assesmentCreateModel.getAssessmentType() == AssessmentType.QUIZ)) {
                throw new MintException(Code.INVALID, "error.assessment.question.empty");
              }
              Assessment assessment = new Assessment();
              AssessmentsMapper.INSTANCE.createModelToEntity(assesmentCreateModel, assessment);

              assessment.setOwner(user);
              assessment.setSpace(space);
              if (assesmentCreateModel.getStartDate() != null) {
                if (!validateDate((assesmentCreateModel.getStartDate()))) {
                  throw new InvalidException("error.invalid.date");
                }
                assessment.setStartDateTime(
                    DateConverter.convertZonedDateTimeToDate(assesmentCreateModel.getStartDate()));
              }
              if (assesmentCreateModel.getDueDate() != null) {
                if (!validateDate(assesmentCreateModel.getDueDate())) {
                  throw new InvalidException("error.invalid.date");
                }
                assessment.setDueDate(
                    DateConverter.convertZonedDateTimeToDate(assesmentCreateModel.getDueDate()));
              }

              if (assessment.getPublish()) {
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
                            Content content =
                                    this.contentRepository.getReferenceById(
                                            assesmentCreateModel.getWorkSheetContentId());
                            if ( content.getType() != ContentType.WORKSHEET
                                    || (content.getType() == ContentType.WORKSHEET
                                    && content.getStatus() != ContentStatus.READY)) {

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
                  assessmentRepository.countByOwnerIdAndSpaceIdAndDeletedFalse(
                      user.getId(), space.getId()));
              joinedRepository.save(joined);
              return ResponseModel.done(
                  assessment.getId(),
                  new AssessementsInfoMessage(
                      assessment.getId(),
                      assessment.getTitle(),
                      assessment.getAssessmentType(),
                      new From(user.getId(), user.getFullName(), user.getThumbnail(), null),
                      assessment.getSpace().getId(),
                      assessment.getStartDateTime(),
                      assessment.getDueDate(),
                      space.getName(),
                      space.getCategory().getName(),
                      challengee != null ? challengee.getId() : null));
            })
        .orElseThrow(NotPermittedException::new);
  }

  private void extractChoices(
      AssessmentQuestionCreateModel assessmentQuestionCreateModel,
      AssessmentQuestion assessmentQuestion) {
    if (assessmentQuestionCreateModel.getChoicesList() != null) {
      Set<AssessmentQuestionChoice> temp = new HashSet<>();
      for (ChoicesModel choicesModel : assessmentQuestionCreateModel.getChoicesList()) {
        AssessmentQuestionChoice choice = new AssessmentQuestionChoice();
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
    return userRepository
        .findOneByUserNameAndDeletedFalse(SecurityUtils.getCurrentUserLogin())
        .map(
            user ->
                Optional.of(assessmentRepository.getReferenceById(id))
                    .map(
                        assessment -> {
                          if (assessment.getPublish() == Boolean.TRUE) {
                            throw new MintException(Code.INVALID, "error.assessment.status");
                          }

                          AssessmentsMapper.INSTANCE.createModelToEntity(
                              assessmentModel, assessment);
                          assessment.setStartDateTime(
                              DateConverter.convertZonedDateTimeToDate(
                                  assessmentModel.getStartDate()));
                          assessment.setDueDate(
                              DateConverter.convertZonedDateTimeToDate(
                                  assessmentModel.getDueDate()));
                          assessmentRepository.save(assessment);

                          List<AssessmentQuestion> assessmentQuestions =
                              assessmentQuestionRepository.findByAssessmentAndDeletedFalse(
                                  assessment);

                          if (assessmentQuestions != null && !assessmentQuestions.isEmpty()) {
                            assessmentQuestions.stream()
                                .filter(
                                    assessmentQuestion ->
                                        assessmentQuestion.getAssessmentQuestionChoices() != null
                                            && !assessmentQuestion
                                                .getAssessmentQuestionChoices()
                                                .isEmpty())
                                .forEach(
                                    assessmentQuestion ->
                                        assessmentQuestionChoicesRepository.deleteAll(
                                            assessmentQuestion.getAssessmentQuestionChoices()));
                            assessmentQuestionRepository.deleteAll(assessmentQuestions);
                          }
                          assessment.setTotalPoints(0);
                          mapAndSaveQuestion(assessmentModel, assessment);
                          assessmentRepository.save(assessment);
                          return ResponseModel.done();
                        })
                    .orElseThrow(NotFoundException::new))
        .orElseThrow(NotPermittedException::new);
  }

  private void mapAndSaveQuestion(AssessmentCreateModel assessmentModel, Assessment assessment) {
    if (assessmentModel.getAssessmentQuestionCreateModels() != null
        && !assessmentModel.getAssessmentQuestionCreateModels().isEmpty()) {
      assessmentModel
          .getAssessmentQuestionCreateModels()
          .forEach(
              assessmentQuestionCreateModel -> {
                AssessmentQuestion assessmentQuestion = new AssessmentQuestion();
                AssessmentsMapper.INSTANCE.mapAssessmentQuestionModelToDomain(
                    assessmentQuestionCreateModel, assessmentQuestion);
                assessmentQuestion.setId(null);
                extractChoices(assessmentQuestionCreateModel, assessmentQuestion);
                assessmentQuestion.setAssessment(assessment);
                assessmentQuestionRepository.save(assessmentQuestion);
                assessment.setTotalPoints(
                    assessment.getTotalPoints() + assessmentQuestion.getQuestionWeight());
                log.debug(
                    "assessmentQuestionCreateModel == > {}",
                    assessmentQuestionCreateModel.getBody());
              });
    }
  }

  @Transactional
  @PreAuthorize("hasAuthority('ASSESSMENT_READ')")
  public ResponseModel get(Long id) {
    return userRepository
        .findOneByUserNameAndDeletedFalse(SecurityUtils.getCurrentUserLogin())
        .map(
            user ->
                assessmentRepository
                    .findOneByIdAndDeletedFalse(id)
                    .map(
                        assesment -> {
                          AssessmentModel assessmentModel =
                              assessmentMapping(assesment, user.getId());
                          if (assesment.getLimitDuration() != null) {
                            assessmentModel.setLimitedByTime(true);
                          }

                          return ResponseModel.done(assessmentModel);
                        })
                    .orElseThrow(NotFoundException::new))
        .orElseThrow(NotPermittedException::new);
  }

  /** Created by A.Alsayed on 05/01/19. */
  @Transactional
  public ResponseModel getUserChallenges(Long spaceId, PageRequest pageRequest) {

    // 1. get current logged-in user:
    // ==============================
    CurrentUserDetail userDetail = SecurityUtils.getCurrentUser();
    if (userDetail != null) {
      /*
       * 2. get user assessments by space id from My SQL where: assessment type =
       * challenge assessment assessment is not deleted assessment is not expired
       * status is finished.
       */
      LocalDateTime currentDate = LocalDate.now().atTime(23, 59, 59, 999999999);
      Page<Assessment> allAssessmentsPage =
          assessmentRepository.getUserChallenges(
              AssessmentType.CHALLENGE,
              spaceId,
              userDetail.getId(),
              AssessmentStatus.FINISHED,
              Date.from(currentDate.toInstant(ZoneOffset.UTC)),
              pageRequest);
      /*
       * 3. transfer data to Challenge Summary Model
       */
      List<ChallengeSummaryModel> challengeSummaryModels = new ArrayList<ChallengeSummaryModel>();
      allAssessmentsPage.forEach(
          assessment -> {
            ChallengeSummaryModel challengeSummaryModel = mapChallengeAssessment(assessment);
            challengeSummaryModels.add(challengeSummaryModel);
          });
      return PageResponseModel.done(
          challengeSummaryModels,
          allAssessmentsPage.getTotalPages(),
          pageRequest.getPageNumber(),
          challengeSummaryModels.size());
    }
    return null;
  }

  /** Created by A.Alsayed on 18/02/19. */
  @Transactional
  public ResponseModel getChallengeOpponents(Long challengeId) {
    // 1. get current logged-in user:
    // ==============================
    CurrentUserDetail userDetail = SecurityUtils.getCurrentUser();
    log.info("getting user details...");
    if (userDetail != null) {
      /*
       * 2. get Challenge by challenge id from My SQL where: assessment type =
       * challenge assessment is not deleted.
       */
      log.info("calling assessmentRepository.findOneByIdAndDeletedFalseAndAssessmentType ........");
      List<ChallengeesGrade> opponents = new ArrayList<ChallengeesGrade>();

      Optional<Assessment> assessment =
          assessmentRepository.findOneByIdAndDeletedFalseAndAssessmentType(
              challengeId, AssessmentType.CHALLENGE);
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
    ChallengeSummaryModel challengeSummaryModel = new ChallengeSummaryModel();
    challengeSummaryModel.setId(assessment.getId());
    challengeSummaryModel.setCreationDate(assessment.getCreationDate());
    challengeSummaryModel.setDueDate(assessment.getDueDate());
    challengeSummaryModel.setTitle(assessment.getTitle());
    challengeSummaryModel.setOverallChallengeStatus(assessment.getAssessmentStatus());

    // getting total grades:
    getChallengeUserAssessments(assessment, assessment.getOwner().getId(), challengeSummaryModel);

    return challengeSummaryModel;
  }

  private void getChallengeUserAssessments(
      Assessment assessment, Long currentUserId, ChallengeSummaryModel challengeSummaryModel) {
    List<ChallengeesGrade> opponents =
        userAssessmentRepository
            .findByAssessmentIdAndDeletedFalseOrderByTotalGradeAsc(assessment.getId())
            .map(
                userAssessment -> {
                  ChallengeesGrade grade = new ChallengeesGrade();
                  grade.setTotalGrade(userAssessment.getTotalGrade());
                  grade.setStatus(userAssessment.getAssessmentStatus());
                  if (userAssessment.getUserId().equals(assessment.getOwner().getId())) {
                    grade.setId(assessment.getOwner().getId());
                    grade.setName(assessment.getOwner().getFullName());
                    grade.setCreator(true);
                    grade.setSchool(assessment.getOwner().getSchool());
                    grade.setThumbnail(assessment.getOwner().getThumbnail());

                  } else {
                    User user =
                        assessment.getChallengees().stream()
                            .filter(u -> userAssessment.getUserId().equals(u.getId()))
                            .findFirst()
                            .get();
                    grade.setId(user.getId());
                    grade.setName(user.getFullName());
                    grade.setSchool(user.getSchool());
                    grade.setThumbnail(user.getThumbnail());
                  }
                  return grade;
                })
            .collect(Collectors.toList());

    challengeSummaryModel.setOpponents(opponents);
  }

  /** Created by A.Alsayed on 18/02/19. */
  private List<ChallengeesGrade> getChallengeOppenentGrades(Assessment assessment) {
    log.info("calling getChallengeOppenentGrades inside function........" + assessment.getId());
    List<ChallengeesGrade> opponents =
        userAssessmentRepository
            .findByAssessmentIdAndDeletedFalseOrderByTotalGradeAsc(assessment.getId())
            .map(
                userAssessment -> {
                  log.info("calling userAssessment........" + userAssessment);
                  ChallengeesGrade grade = new ChallengeesGrade();
                  grade.setTotalGrade(userAssessment.getTotalGrade());
                  grade.setStatus(userAssessment.getAssessmentStatus());
                  if (userAssessment.getUserId().equals(assessment.getOwner().getId())) {
                    grade.setId(assessment.getOwner().getId());
                    grade.setName(assessment.getOwner().getFullName());
                    grade.setCreator(true);
                    grade.setSchool(assessment.getOwner().getSchool());
                    grade.setThumbnail(assessment.getOwner().getThumbnail());
                  } else {
                    User user =
                        assessment.getChallengees().stream()
                            .filter(u -> userAssessment.getUserId().equals(u.getId()))
                            .findFirst()
                            .get();
                    grade.setId(user.getId());
                    grade.setName(user.getFullName());
                    grade.setSchool(user.getSchool());
                    grade.setThumbnail(user.getThumbnail());
                  }
                  return grade;
                })
            .collect(Collectors.toList());
    log.info("opponents size ........" + opponents.size());
    return opponents;
  }

  @Transactional
  @PreAuthorize("hasAuthority('ASSESSMENT_READ')")
  public ResponseModel get(AssessmentGetAllModel assessmentGetAllModel, PageRequest pageRequest) {
    return userRepository
        .findOneByUserNameAndDeletedFalse(SecurityUtils.getCurrentUserLogin())
        .map(
            user -> {
              if (spaceRepository.getReferenceById(assessmentGetAllModel.getSpaceId()) != null) {
                PageRequest pageRequestWithDefaultSort = pageRequest;
                if (pageRequest.getSort() == null) {
                  PageRequestModel.getPageRequestModel(
                      pageRequest.getPageNumber(),
                      pageRequest.getPageSize(),
                      Sort.by(Sort.Direction.DESC, SortField.PUBLISH_DATE.getFieldName()));
                }
                if (assessmentGetAllModel.getAssessmentType() != null) {
                  Page<Assessment> allAssessmentsPage = null;
                  if (assessmentGetAllModel.getAssessmentType() == AssessmentType.PRACTICE) {
                    allAssessmentsPage =
                        assessmentRepository.findAllByAssessmentTypeandOwnerId(
                            assessmentGetAllModel.getAssessmentType(),
                            assessmentGetAllModel.getSpaceId(),
                            user.getId(),
                            pageRequestWithDefaultSort);
                  } else {
                    allAssessmentsPage =
                        assessmentRepository.findAllByAssessmentType(
                            assessmentGetAllModel.getAssessmentType(),
                            assessmentGetAllModel.getSpaceId(),
                            user.getId(),
                            pageRequestWithDefaultSort);
                  }
                  return PageResponseModel.done(
                      allAssessmentsPage.getContent().stream()
                          .map(assessment -> assessmentListMapping(assessment, user.getId()))
                          .collect(Collectors.toList()),
                      allAssessmentsPage.getTotalPages(),
                      pageRequestWithDefaultSort.getPageNumber(),
                      allAssessmentsPage.getTotalElements());
                }
                Page<Assessment> ownedPage =
                    assessmentRepository.findBySpaceIdAndDeletedFalseAndOwnerOrPublishTrue(
                        assessmentGetAllModel.getSpaceId(), user, pageRequest);

                Set<AssessmentListModel> assessmentModels = new HashSet<>();
                assessmentModels.addAll(
                    ownedPage.getContent().stream()
                        .map(assessment -> assessmentListMapping(assessment, user.getId()))
                        .filter(
                            assessmentListModel ->
                                !(assessmentListModel.getAssessmentType() == AssessmentType.PRACTICE
                                    && !Objects.equals(
                                        assessmentListModel.getOwner(), user.getId())))
                        .collect(Collectors.toSet()));
                return PageResponseModel.done(
                    assessmentModels,
                    ownedPage.getTotalPages(),
                    pageRequest.getPageNumber(),
                    assessmentModels.size());

              } else {
                throw new NotFoundException("space");
              }
            })
        .orElseThrow(NotPermittedException::new);
  }

  @Transactional
  @PreAuthorize("hasAuthority('ASSESSMENT_READ')")
  public ResponseModel getAssessmentOverview(Long spaceId) {
    return userRepository
        .findOneByUserNameAndDeletedFalse(SecurityUtils.getCurrentUserLogin())
        .map(
            user -> {
              Map<AssessmentType, List<AssessmentListModel>> assessmentGroups =
                  assessmentRepository
                      .findBySpaceIdAndPublishTrueAndPublishDateNotNullAndDeletedFalseOrderByPublishDateDesc(
                          spaceId)
                      .collect(
                          Collectors.groupingBy(
                              Assessment::getAssessmentType,
                              HashMap::new,
                              Collectors.collectingAndThen(
                                  Collectors.toSet(), list -> mapList(list, user.getId()))));
              return ResponseModel.done(assessmentGroups);
            })
        .orElseThrow(NotPermittedException::new);
  }

  private List<AssessmentListModel> mapList(Set<Assessment> assessmentList, Long currentUserId) {
    return assessmentList.stream()
        .limit(3)
        .map(
            assessment -> {
              AssessmentListModel assessmentListModel =
                  new AssessmentListModel(
                      assessment,
                      userAssessmentRepository
                          .findByAssessmentIdAndDeletedFalse(assessment.getId())
                          .collect(Collectors.toList()),
                      currentUserId);
              assessmentListModel.setNumberOfQuestions(
                  assessmentQuestionRepository.countByAssessmentIdAndDeletedFalse(
                      assessmentListModel.getId()));
              return assessmentListModel;
            })
        .collect(Collectors.toList());
  }

  @Transactional
  @PreAuthorize("hasAuthority('ASSESSMENT_READ') and hasAuthority('ADMIN')")
  public ResponseModel getAll(
      PageRequest pageRequest, Long spaceId, AssessmentType assessmentType) {
    return userRepository
        .findOneByUserNameAndDeletedFalse(SecurityUtils.getCurrentUserLogin())
        .map(
            user -> {
              Specification<Assessment> spaceIdSpec = null;
              Specification<Assessment> typeSpec = null;
              if (spaceId != null) {
                Space space =
                    spaceRepository
                        .findOneByIdAndDeletedFalse(spaceId)
                        .orElseThrow(NotFoundException::new);
                spaceIdSpec = (root, query, cb) -> cb.equal(root.get("space"), space);
              }

              if (assessmentType != null) {
                typeSpec =
                    (root, query, cb) -> cb.equal(root.get("assessmentType"), assessmentType);
              }

              Page<AssessmentListModel> allAssessmentsPage =
                  assessmentRepository
                      .findAll(
                          Specification.where(spaceIdSpec)
                              .and(typeSpec)
                              .and((root, query, cb) -> cb.isFalse(root.get("deleted"))),
                          pageRequest)
                      .map(assessment -> assessmentListMapping(assessment, user.getId()));

              return PageResponseModel.done(
                  allAssessmentsPage.getContent(),
                  allAssessmentsPage.getTotalPages(),
                  pageRequest.getPageNumber(),
                  allAssessmentsPage.getTotalElements());
            })
        .orElseThrow(NotPermittedException::new);
  }

  @Transactional
  @Auditable(EntityAction.ASSESSMENT_DELETE)
  @PreAuthorize("hasAuthority('ASSESSMENT_DELETE')")
  public ResponseModel delete(Long id) {
    if (null != id) {
      return assessmentRepository
          .findOneByIdAndDeletedFalse(id)
          .map(
              assessment -> {
                if (assessment.getAssessmentType() != AssessmentType.PRACTICE
                    && userAssessmentRepository.findByAssessmentIdAndDeletedFalse(id).count()
                        >= 1) {
                  throw new MintException(Code.INVALID, "error.assessment.status");
                }

                Joined joined =
                    joinedRepository
                        .findOneBySpaceIdAndUserIdAndSpaceRoleInAndDeletedFalse(
                            assessment.getSpace().getId(),
                            SecurityUtils.getCurrentUser().getId(),
                            SpaceRole.COLLABORATOR,
                            SpaceRole.CO_OWNER,
                            SpaceRole.EDITOR,
                            SpaceRole.OWNER)
                        .orElseThrow(NotPermittedException::new);

                List<AssessmentQuestion> assessmentQuestions =
                    assessmentQuestionRepository.findByAssessmentAndDeletedFalse(assessment);

                if (assessmentQuestions != null && !assessmentQuestions.isEmpty()) {
                  for (Iterator<AssessmentQuestion> iterator = assessmentQuestions.iterator();
                      iterator.hasNext(); ) {
                    if (iterator.hasNext()) {
                      AssessmentQuestion assessmentQuestion = iterator.next();
                      if (assessmentQuestion.getAssessmentQuestionChoices() != null
                          && !assessmentQuestion.getAssessmentQuestionChoices().isEmpty()) {
                        for (Iterator<AssessmentQuestionChoice> iteratorChoices =
                                assessmentQuestion.getAssessmentQuestionChoices().iterator();
                            iterator.hasNext(); ) {
                          if (iteratorChoices.hasNext()) {
                            AssessmentQuestionChoice choice = iteratorChoices.next();
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
                joined.setAssessmentCount(
                    assessmentRepository.countByOwnerIdAndSpaceIdAndDeletedFalse(
                        SecurityUtils.getCurrentUser().getId(), assessment.getSpace().getId()));
                joinedRepository.save(joined);
                return ResponseModel.done();
              })
          .orElseThrow(NotFoundException::new);
    }
    throw new MintException(Code.INVALID);
  }

  /** created by A.Alsayed 07-03-2019 */
  public void autoSubmitChallenge(UserAssessmentModel userAssessmentModel) {
    log.info("autoSubmitChallenge ::: Start ...........................");
    Optional<Assessment> assessment =
        assessmentRepository.findOneByIdAndDeletedFalse(userAssessmentModel.getAssessmentId());
    if (assessment.isPresent()) {
      log.info("autoSubmitChallenge ::: assessment [" + assessment.get().getId() + "] exists");
      Optional<UserAssessment> userAssessment =
          userAssessmentRepository.findOneByUserIdAndAssessmentIdAndDeletedFalse(
              userAssessmentModel.getUserId(), userAssessmentModel.getAssessmentId());
      if (userAssessment.isPresent()) {
        log.info(
            "autoSubmitChallenge ::: userAssessment [" + userAssessment.get().getId() + "] exists");
        if (Arrays.asList(
                AssessmentStatus.FINISHED,
                AssessmentStatus.NOT_EVALUATED,
                AssessmentStatus.EVALUATED)
            .contains(userAssessment.get().getAssessmentStatus())) {
          throw new MintException(Code.INVALID, "error.assessment.taken");
        }
        userAssessment.get().setAssessmentStatus(AssessmentStatus.FINISHED);
        if (userAssessmentModel.getDuration() != null) {
          userAssessment.get().setDuration(userAssessmentModel.getDuration());
        }

        List<AssessmentQuestion> assessmentQuestions =
            assessmentQuestionRepository.findByAssessmentAndDeletedFalse(assessment.get());
        assessmentQuestions.forEach(assessmentQuestion -> assessmentQuestion.setQuestionWeight(1));

        boolean isChallengeFinished =
            checkChallengeStatus(assessment.get().getId(), userAssessmentModel.getUserId());
        if (isChallengeFinished) {
          assessment.get().setAssessmentStatus(AssessmentStatus.FINISHED);
        }

        assessmentQuestionRepository.saveAll(assessmentQuestions);
        assessmentRepository.save(assessment.get());

        log.info("autoSubmitChallenge ::: calculate grade method");
        grade(
            userAssessmentModel,
            userAssessment.get(),
            assessmentQuestions,
            assessment.get(),
            userRepository.findOneByIdAndDeletedFalse(userAssessmentModel.getUserId()).get());

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
    return userRepository
        .findOneByUserNameAndDeletedFalse(SecurityUtils.getCurrentUserLogin())
        .map(
            user ->
                assessmentRepository
                    .findOneByIdAndDeletedFalse(userAssessmentModel.getAssessmentId())
                    .map(
                        assessment -> {
                          UserAssessment userAssessment =
                              userAssessmentRepository
                                  .findOneByUserIdAndAssessmentIdAndDeletedFalse(
                                      user.getId(), userAssessmentModel.getAssessmentId())
                                  .orElseGet(
                                      () -> {
                                        UserAssessment userAssessment2 = new UserAssessment();
                                        userAssessment2.setUserId(user.getId());
                                        userAssessment2.setAssessmentId(assessment.getId());
                                        userAssessment2.setFullGrade(assessment.getTotalPoints());
                                        return userAssessment2;
                                      });

                          if (Arrays.asList(
                                  AssessmentStatus.FINISHED,
                                  AssessmentStatus.NOT_EVALUATED,
                                  AssessmentStatus.EVALUATED)
                              .contains(userAssessment.getAssessmentStatus())) {
                            throw new MintException(Code.INVALID, "error.assessment.taken");
                          }
                          if (assessment.getAssessmentType() != AssessmentType.PRACTICE
                              && assessment.getAssessmentType() != AssessmentType.CHALLENGE
                              && userAssessmentModel.getUserId() != null
                              && !userAssessmentModel.getUserId().equals(user.getId())
                              && user.equals(assessment.getOwner())) {
                            userAssessment =
                                userAssessmentRepository
                                    .findOneByUserIdAndAssessmentIdAndDeletedFalse(
                                        userAssessmentModel.getUserId(), assessment.getId())
                                    .orElseThrow(
                                        () ->
                                            new MintException(
                                                Code.INVALID, "error.assessment.nottaken"));
                          }

                          userAssessment.setAssessmentStatus(
                              userAssessmentModel.getAssessmentStatus());

                          if (userAssessmentModel.getDuration() != null) {
                            userAssessment.setDuration(userAssessmentModel.getDuration());
                          }

                          switch (assessment.getAssessmentType()) {
                            case ASSIGNMENT:
                            case QUIZ:
                            case PRACTICE:
                            case CHALLENGE:
                              boolean isChallengeFinished = false;
                              List<AssessmentQuestion> assessmentQuestions =
                                  assessmentQuestionRepository.findByAssessmentAndDeletedFalse(
                                      assessment);
                              if (assessment.getAssessmentType().equals(AssessmentType.PRACTICE)
                                  || assessment
                                      .getAssessmentType()
                                      .equals(AssessmentType.CHALLENGE)) {
                                assessmentQuestions.forEach(
                                    assessmentQuestion -> assessmentQuestion.setQuestionWeight(1));
                                if (assessment
                                    .getAssessmentType()
                                    .equals(AssessmentType.CHALLENGE)) {
                                  /** changes by A.Alsayed 20-02-2019 */
                                  // check challenge status using owner and opponent.
                                  isChallengeFinished =
                                      checkChallengeStatus(assessment.getId(), user.getId());
                                  if (isChallengeFinished) {
                                    assessment.setAssessmentStatus(AssessmentStatus.FINISHED);
                                  }
                                } else {
                                  assessment.setAssessmentStatus(
                                      userAssessmentModel.getAssessmentStatus());
                                }
                                assessmentQuestionRepository.saveAll(assessmentQuestions);
                                assessmentRepository.save(assessment);
                              }

                              grade(
                                  userAssessmentModel,
                                  userAssessment,
                                  assessmentQuestions,
                                  assessment,
                                  user);

                              break;
                            case WORKSHEET:
                              if (!assessment.getOwner().getId().equals(user.getId())
                                  && userAssessmentModel.getUserWorkSheetAnswerModel() != null) {
                                userAssessment.setWorkSheetAnswerModel(
                                    AssessmentsMapper.INSTANCE.cloneToNewModel(
                                        userAssessmentModel.getUserWorkSheetAnswerModel()));
                                userAssessmentModel.setFullGrade(assessment.getTotalPoints());
                                userAssessment.setAssessmentStatus(AssessmentStatus.NOT_EVALUATED);
                              } else if (assessment.getOwner().getId().equals(user.getId())
                                  && userAssessmentModel.getOwnerWorkSheetAnswerModel() != null) {
                                userAssessment.setOwnerWorkSheetAnswerModel(
                                    AssessmentsMapper.INSTANCE.cloneToNewModel(
                                        userAssessmentModel.getOwnerWorkSheetAnswerModel()));
                                userAssessment.setTotalGrade(userAssessmentModel.getTotalGrade());
                                if (assessment.getTotalPoints() > 0
                                    && null != assessment.getTotalPoints()) {
                                  userAssessment.setPercentage(
                                      (userAssessment.getTotalGrade()
                                              / assessment.getTotalPoints().floatValue())
                                          * 100);
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
                          if (assessment.getAssessmentType().equals(AssessmentType.PRACTICE)) {
                            updateUserSpaceScore(
                                user.getId(),
                                assessment.getSpace().getId(),
                                userAssessment.getTotalGrade() * 10);
                          } else if (assessment.getAssessmentType().equals(AssessmentType.CHALLENGE)
                              && checkChallengeStatus(assessment.getId(), user.getId())) {
                            // user space score with challenge results.
                            updateUserChallengeScores(
                                assessment.getId(), assessment.getSpace().getId());
                          }

                          final AssessmentUserModel contentUserModel = new AssessmentUserModel();
                          User user1 = user;
                          if (!user.getId().equals(userAssessment.getUserId())) {
                            user1 =
                                userRepository
                                    .findById(userAssessment.getUserId())
                                    .orElseThrow(NotFoundException::new);
                          }
                          contentUserModel.setId(user1.getId());
                          contentUserModel.setName(user1.getFullName());
                          contentUserModel.setUserName(user1.getUserName());
                          contentUserModel.setImage(user1.getThumbnail());
                          contentUserModel.setFullGrade(userAssessment.getFullGrade());
                          contentUserModel.setTotalGrade(userAssessment.getTotalGrade());
                          contentUserModel.setAssessmentStatus(
                              userAssessment.getAssessmentStatus());
                          contentUserModel.setPercentage(userAssessment.getPercentage());
                          contentUserModel.setMessage(
                              getReportMessage(
                                  userAssessment.getPercentage(),
                                  Locale.forLanguageTag(lang == null ? "en" : lang)));
                          return ResponseModel.done(
                              contentUserModel,
                              new AssessmentSubmitMessage(
                                  assessment.getId(),
                                  assessment.getTitle(),
                                  assessment.getAssessmentType(),
                                  new From(SecurityUtils.getCurrentUser()),
                                  assessment.getSpace().getId(),
                                  assessment.getStartDateTime(),
                                  assessment.getDueDate(),
                                  assessment.getSpace().getName(),
                                  assessment.getSpace().getCategory().getName(),
                                  userAssessment.getAssessmentStatus(),
                                  user.equals(assessment.getOwner()),
                                  new UserInfoMessage(user1),
                                  new UserInfoMessage(assessment.getOwner())));
                        })
                    .orElseThrow(NotFoundException::new))
        .orElseThrow(NotPermittedException::new);
  }

  /** created by A.Alsayed 20-02-2019 Check if all users complete their challenge. */
  private boolean checkChallengeStatus(Long assessmentId, Long userId) {
    List<UserAssessment> userAssessmentsList =
        userAssessmentRepository
            .findByAssessmentIdAndDeletedFalse(assessmentId)
            .collect(Collectors.toList());
    if (userAssessmentsList != null && userAssessmentsList.size() > 0) {
      for (UserAssessment userAssessment : userAssessmentsList) {
        if (userAssessment.getAssessmentStatus() != AssessmentStatus.EVALUATED
            && userAssessment.getUserId().longValue() != userId.longValue()) return false;
      }
    }
    return true;
  }

  /** created by A.Alsayed 15-01-2019 */
  // update user space score with the new grade of assessment after evaluation
  private void updateUserSpaceScore(Long userId, Long spaceId, float totalGrade) {
    // 1. get joined record by user id and space id:
    joinedRepository
        .findOneByUserIdAndSpaceIdAndDeletedFalse(userId, spaceId)
        .ifPresent(
            joined -> {
              // 2. update total score by adding user assessment total grade:
              joined.setSpaceScorePoints(joined.getSpaceScorePoints() + totalGrade);

              // 3. save joined record in DB:
              joinedRepository.save(joined);
            });
    /**
     * modified by A.Alsayed 23-01-2019 Update total user score, this attribute to be used for
     * getting user global ranking.
     */
    userRepository
        .findOneByIdAndDeletedFalse(userId)
        .ifPresent(
            user -> {
              user.setTotalScore(user.getTotalScore() + totalGrade);
              userRepository.save(user);
            });
  }

  private void updateUserChallengeScores(Long assessmentId, Long spaceId) {
    List<UserAssessment> userAssessmentsList =
        userAssessmentRepository
            .findByAssessmentIdAndDeletedFalseOrderByTotalGradeDesc(assessmentId)
            .collect(Collectors.toList());
    if (userAssessmentsList != null && userAssessmentsList.size() == 2) {
      if (userAssessmentsList.get(0).getTotalGrade().floatValue()
          > userAssessmentsList.get(1).getTotalGrade().floatValue()) {
        // winner found
        updateUserSpaceScore(
            userAssessmentsList.get(0).getUserId(),
            spaceId,
            userAssessmentsList.get(0).getTotalGrade() * 20);
        updateUserSpaceScore(
            userAssessmentsList.get(1).getUserId(),
            spaceId,
            userAssessmentsList.get(1).getTotalGrade() * 10);
      } else {
        // result is draw.
        for (UserAssessment userAssessment : userAssessmentsList) {
          updateUserSpaceScore(
              userAssessment.getUserId(), spaceId, userAssessment.getTotalGrade() * 10);
        }
      }
    }
  }

  // TODO: use lamda experssion
  @Transactional
  public ResponseModel startChallenge(long assessmentId) {
    Optional<User> user =
        userRepository.findOneByUserNameAndDeletedFalse(SecurityUtils.getCurrentUserLogin());
    if (user.isPresent()) {
      log.info("startChallenge ::: user exists");
      Assessment assessment = checkAssessmentByOwnerOrChallengee(assessmentId, user.get());
      if (assessment != null) {
        log.info("startChallenge ::: assessment [" + assessment.getId() + "] exists");
        // mark assessment as started:
        if (assessment.getAssessmentStatus() != null
            && (assessment.getAssessmentStatus() == AssessmentStatus.NEW
                || assessment.getAssessmentStatus() == AssessmentStatus.NOT_STARTED)) {
          assessment.setAssessmentStatus(AssessmentStatus.STARTED);
          assessmentRepository.save(assessment);
          log.info("startChallenge ::: mark assessment as started");
        }

        Optional<UserAssessment> userAssessment =
            userAssessmentRepository.findOneByUserIdAndAssessmentIdAndDeletedFalse(
                user.get().getId(), assessmentId);
        if (userAssessment.isPresent()) {
          log.info(
              "startChallenge ::: userAssessment [" + userAssessment.get().getId() + "] exists");
          // mark user assessment as started
          userAssessment.get().setAssessmentStatus(AssessmentStatus.STARTED);
          userAssessmentRepository.save(userAssessment.get());
          log.info("startChallenge ::: mark userAssessment as started");

          // start the auto solving process:
          // ===============================
          UserPracticeModel userPracticeModel = new UserPracticeModel();

          userPracticeModel.setAssessmentId(assessmentId);
          userPracticeModel.setAssessmentStatus(AssessmentStatus.STARTED);
          userPracticeModel.setUserId(user.get().getId());

          log.info("startChallenge ::: start the auto solving process");
          taskScheduler.schedule(
              new AssessmentAutoSolvingRunnable(
                  assessmentId,
                  user.get().getId(),
                  assessment.getLimitDuration(),
                  userAssessmentRepository,
                  this),
              new Date(System.currentTimeMillis() + assessment.getLimitDuration() + 120000));

          return ResponseModel.done(userPracticeModel);
        } else {
          throw new NotFoundException();
        }
      } else {
        throw new NotFoundException();
      }
    } else {
      throw new NotPermittedException();
    }
  }

  @Transactional
  @Auditable(EntityAction.ASSESSMENT_SUBMIT)
  public ResponseModel submitQuestion(UserPracticeModel userPracticeModel) {
    Optional<User> user =
        userRepository.findOneByUserNameAndDeletedFalse(SecurityUtils.getCurrentUserLogin());
    if (user.isPresent()) {
      Assessment assessment =
          checkAssessmentByOwnerOrChallengee(userPracticeModel.getAssessmentId(), user.get());
      if (assessment != null) {
        // change assessment status to Started:
        if (assessment.getAssessmentStatus() != null
            && (assessment.getAssessmentStatus() == AssessmentStatus.NEW
                || assessment.getAssessmentStatus() == AssessmentStatus.NOT_STARTED)) {
          assessment.setAssessmentStatus(AssessmentStatus.STARTED);
          assessmentRepository.save(assessment);

          // NEW: Start counting based on the limit duration (automatic solving):
          // ====================================================================
          //					if (assessment.getAssessmentType() == AssessmentType.CHALLENGE) {
          //						taskScheduler.schedule(
          //								new AssessmentAutoSolvingRunnable(userPracticeModel,
          // assessment.getLimitDuration()),
          //								new Date(System.currentTimeMillis() + assessment.getLimitDuration()));
          //					}
        }

        Optional<UserAssessment> userAssessment =
            userAssessmentRepository.findOneByUserIdAndAssessmentIdAndDeletedFalse(
                user.get().getId(), userPracticeModel.getAssessmentId());
        if (userAssessment.isPresent()) {
          QuestionAnswer questionAnswer =
              questionAnswerRepository
                  .findOneByUserIdAndQuestionIdAndDeletedFalse(
                      user.get().getId(),
                      userPracticeModel.getQuestionAnswerModels().getQuestionId())
                  .map(
                      questionAnswer1 -> {
                        questionAnswer1.setUserAnswer(
                            userPracticeModel.getQuestionAnswerModels().getUserAnswer());
                        questionAnswer1.setGrade(
                            userPracticeModel.getQuestionAnswerModels().getGrade());
                        return questionAnswer1;
                      })
                  .orElseGet(
                      () -> {
                        QuestionAnswer questionAnswer1 = new QuestionAnswer();
                        questionAnswer1.setUserAnswer(
                            userPracticeModel.getQuestionAnswerModels().getUserAnswer());
                        questionAnswer1.setQuestionId(
                            userPracticeModel.getQuestionAnswerModels().getQuestionId());
                        questionAnswer1.setUserId(user.get().getId());
                        questionAnswer1.setGrade(
                            userPracticeModel.getQuestionAnswerModels().getGrade());
                        return questionAnswer1;
                      });
          questionAnswerRepository.save(questionAnswer);
          if (!userAssessment.get().getQuestionAnswerList().contains(questionAnswer)) {
            userAssessment.get().getQuestionAnswerList().add(questionAnswer);
          }
          userAssessment
              .get()
              .setAssessmentStatus(
                  (userPracticeModel.getAssessmentStatus() == AssessmentStatus.NEW
                          || userPracticeModel.getAssessmentStatus()
                              == AssessmentStatus.NOT_STARTED)
                      ? AssessmentStatus.STARTED
                      : userPracticeModel.getAssessmentStatus());
          userAssessmentRepository.save(userAssessment.get());
          return ResponseModel.done((Object) userAssessment.get().getId());
        } else {
          UserAssessment newUserAssessment = new UserAssessment();
          newUserAssessment.setAssessmentId(userPracticeModel.getAssessmentId());
          newUserAssessment.setUserId(user.get().getId());
          newUserAssessment.setAssessmentStatus(
              (userPracticeModel.getAssessmentStatus() == AssessmentStatus.NEW
                      || userPracticeModel.getAssessmentStatus() == AssessmentStatus.NOT_STARTED)
                  ? AssessmentStatus.STARTED
                  : userPracticeModel.getAssessmentStatus());
          newUserAssessment.setQuestionAnswerList(new ArrayList<>());
          QuestionAnswer questionAnswer = new QuestionAnswer();
          questionAnswer.setUserAnswer(userPracticeModel.getQuestionAnswerModels().getUserAnswer());
          questionAnswer.setQuestionId(userPracticeModel.getQuestionAnswerModels().getQuestionId());
          questionAnswer.setUserId(user.get().getId());
          questionAnswer.setGrade(userPracticeModel.getQuestionAnswerModels().getGrade());
          questionAnswerRepository.save(questionAnswer);
          newUserAssessment.getQuestionAnswerList().add(questionAnswer);
          userAssessmentRepository.save(newUserAssessment);
          return ResponseModel.done((Object) newUserAssessment.getId());
        }
      } else {
        throw new NotFoundException();
      }
    } else {
      throw new NotPermittedException();
    }
  }

  private Assessment checkAssessmentByOwnerOrChallengee(Long assessmentId, User user) {
    log.info("checkAssessmentByOwnerOrChallengee user exist with id = " + user.getId());
    // 1. check if assessment type is challenge or practice and the user is owner:
    Optional<Assessment> assessment =
        assessmentRepository.findOneByIdAndDeletedFalseAndOwnerAndAssessmentTypeIn(
            assessmentId, user, AssessmentType.PRACTICE, AssessmentType.CHALLENGE);
    if (assessment.isPresent()) {
      log.info(
          "checkAssessmentByOwnerOrChallengee OWNER assessment exist with id = "
              + assessment.get().getId());
      return assessment.get();
    } else {
      // 2. check if assessment type is challenge or practice and the user is
      // challengee:
      log.info("checkAssessmentByOwnerOrChallengee trying to get using challengee id....");
      List<Assessment> assessments =
          assessmentRepository.findAssessmentByChallengee(user.getId(), assessmentId);
      if (assessments != null && assessments.size() > 0) {
        log.info(
            "checkAssessmentByOwnerOrChallengee CHALLENGEE assessment exist with id = "
                + assessments.get(0).getId());
        return assessments.get(0);
      }
    }
    return null;
  }

  @Transactional
  @PreAuthorize("hasAuthority('ASSESSMENT_READ')")
  public ResponseModel getUpdates(AssessmentGetAllModel assessmentGetAllModel) {
    return userRepository
        .findOneByUserNameAndDeletedFalse(SecurityUtils.getCurrentUserLogin())
        .map(
            user -> {
              AssessmentsUpdatesModel assessmentsUpdatesModel = new AssessmentsUpdatesModel();
              if (spaceRepository.getOne(assessmentGetAllModel.getSpaceId()) != null) {
                assessmentsUpdatesModel.setDeletedAssessments(
                    assessmentRepository
                        .findBySpaceIdAndDeletedDateAfterAndDeletedTrue(
                            assessmentGetAllModel.getSpaceId(),
                            DateConverter.convertZonedDateTimeToDate(
                                assessmentGetAllModel.getDate()))
                        .map(assessment -> assessmentMapping(assessment, user.getId()))
                        .collect(Collectors.toList()));
                assessmentsUpdatesModel.setUpdatedAssessments(
                    assessmentRepository
                        .findBySpaceIdAndLastModifiedDateAfterAndDeletedFalse(
                            assessmentGetAllModel.getSpaceId(),
                            DateConverter.convertZonedDateTimeToDate(
                                assessmentGetAllModel.getDate()))
                        .map(assessment -> assessmentMapping(assessment, user.getId()))
                        .collect(Collectors.toList()));
                assessmentsUpdatesModel.setNewAssessments(
                    assessmentRepository
                        .findBySpaceIdAndLastModifiedDateIsNullAndCreationDateAfterAndDeletedFalse(
                            assessmentGetAllModel.getSpaceId(),
                            DateConverter.convertZonedDateTimeToDate(
                                assessmentGetAllModel.getDate()))
                        .map(assessment -> assessmentMapping(assessment, user.getId()))
                        .collect(Collectors.toList()));

                return ResponseModel.done(assessmentsUpdatesModel);
              } else {
                throw new NotFoundException("space");
              }
            })
        .orElseThrow(NotPermittedException::new);
  }

  @Transactional
  @Auditable(EntityAction.ASSESSMENT_PUBLISH)
  @PreAuthorize("hasAuthority('ASSESSMENT_UPDATE')")
  public ResponseModel togglePublish(Long id) {
    return assessmentRepository
        .findOneByIdAndDeletedFalse(id)
        .map(
            assessment -> {
              if (assessment.getPublish().equals(Boolean.FALSE)) {
                if ((assessment.getAssessmentType() == AssessmentType.QUIZ
                        || assessment.getAssessmentType() == AssessmentType.ASSIGNMENT)
                    && assessmentQuestionRepository
                        .findByAssessmentAndDeletedFalse(assessment)
                        .isEmpty()) {
                  return ResponseModel.error(Code.INVALID, "error.assessment.question.empty");
                }
                assessment.setPublish(Boolean.TRUE);
                assessment.setPublishDate(new Date());
              } else {
                assessment.setPublish(Boolean.FALSE);
              }
              assessmentRepository.save(assessment);
              return ResponseModel.done();
            })
        .orElseThrow(NotFoundException::new);
  }

  @Transactional
  @PreAuthorize("hasAuthority('ASSESSMENT_READ')")
  public ResponseModel getCommunityList(Long id) {
    return assessmentRepository
        .findOneByIdAndDeletedFalse(id)
        .map(
            assessment -> {
              Set<AssessmentUserModel> communityList = new HashSet<>();
              Supplier<Stream<UserAssessment>> userAssessmentSupplier =
                  () -> userAssessmentRepository.findByAssessmentIdAndDeletedFalse(id);
              Set<Long> userIdList =
                  joinedRepository
                      .findBySpaceIdAndDeletedFalse(assessment.getSpace().getId())
                      .filter(joined -> !joined.getUser().equals(assessment.getOwner()))
                      .map(joined -> joined.getUser().getId())
                      .collect(Collectors.toSet());
              if (userIdList != null && !userIdList.isEmpty()) {
                List<User> userList = new ArrayList<>();
                userRepository.findAllById(userIdList).forEach(userList::add);
                for (User user : userList) {
                  final AssessmentUserModel contentUserModel = new AssessmentUserModel();
                  contentUserModel.setId(user.getId());
                  contentUserModel.setName(user.getFullName());
                  contentUserModel.setUserName(user.getUserName());
                  contentUserModel.setImage(user.getThumbnail());
                  userAssessmentSupplier
                      .get()
                      .filter(userAssessment -> userAssessment.getUserId().equals(user.getId()))
                      .findFirst()
                      .ifPresent(
                          userAssessment -> {
                            contentUserModel.setFullGrade(userAssessment.getFullGrade());
                            contentUserModel.setTotalGrade(userAssessment.getTotalGrade());
                            contentUserModel.setAssessmentStatus(
                                userAssessment.getAssessmentStatus());
                          });

                  communityList.add(contentUserModel);
                }
              }
              return ResponseModel.done(communityList);
            })
        .orElseThrow(NotFoundException::new);
  }

  @Transactional
  @PreAuthorize("hasAuthority('ASSESSMENT_READ')")
  public ResponseModel getUserAssessment(Long assessmentId, Long userId) {
    User user = userRepository.findById(userId).orElseThrow(NotFoundException::new);
    Assessment assessment =
        assessmentRepository.findById(assessmentId).orElseThrow(NotFoundException::new);

    return userAssessmentRepository
        .findOneByUserIdAndAssessmentIdAndDeletedFalse(user.getId(), assessmentId)
        .map(
            userAssessment -> {
              AssessmentModel userAssessmentGetModel =
                  new AssessmentModel(
                      assessment, Collections.singletonList(userAssessment), user.getId());
              userAssessmentGetModel.getAssessmentQuestionCreateModels().clear();
              switch (assessment.getAssessmentType()) {
                case ASSIGNMENT:
                case QUIZ:
                case CHALLENGE:
                  assessment.getAssessmentQuestions().stream()
                      .filter(assessmentQuestion -> !assessmentQuestion.isDeleted())
                      .forEach(
                          assessmentQuestion -> {
                            QuestionAnswerGetModel questionAnswerGetModel =
                                mapQuestionAnswerGetModel(assessmentQuestion);
                            userAssessment.getQuestionAnswerList().stream()
                                .filter(
                                    questionAnswer ->
                                        questionAnswer
                                            .getQuestionId()
                                            .equals(questionAnswerGetModel.getId()))
                                .findFirst()
                                .ifPresent(
                                    questionAnswer -> {
                                      questionAnswerGetModel.setUserAnswer(
                                          questionAnswer.getUserAnswer());
                                      questionAnswerGetModel.setGrade(questionAnswer.getGrade());
                                    });
                            userAssessmentGetModel
                                .getAssessmentQuestionCreateModels()
                                .add(questionAnswerGetModel);
                          });
                  break;
                case WORKSHEET:
                  if (userAssessment.getWorkSheetAnswerModel() != null) {
                    userAssessmentGetModel.setUserWorkSheetAnswerModel(
                        AssessmentsMapper.INSTANCE.cloneToNewModel(
                            userAssessment.getWorkSheetAnswerModel()));
                  }
                  if (userAssessment.getOwnerWorkSheetAnswerModel() != null) {
                    userAssessmentGetModel.setOwnerWorkSheetAnswerModel(
                        AssessmentsMapper.INSTANCE.cloneToNewModel(
                            userAssessment.getOwnerWorkSheetAnswerModel()));
                  }
                  userAssessmentGetModel.setTotalGrade(userAssessment.getTotalGrade());
                  break;
                case PRACTICE:
                  if (assessment
                      .getOwner()
                      .getId()
                      .equals(SecurityUtils.getCurrentUser().getId())) {
                    assessment.getAssessmentQuestions().stream()
                        .filter(assessmentQuestion -> !assessmentQuestion.isDeleted())
                        .forEach(
                            assessmentQuestion -> {
                              QuestionAnswerGetModel questionAnswerGetModel =
                                  mapQuestionAnswerGetModel(assessmentQuestion);
                              userAssessment.getQuestionAnswerList().stream()
                                  .filter(
                                      questionAnswer ->
                                          questionAnswer
                                              .getQuestionId()
                                              .equals(questionAnswerGetModel.getId()))
                                  .findFirst()
                                  .ifPresent(
                                      questionAnswer -> {
                                        questionAnswerGetModel.setUserAnswer(
                                            questionAnswer.getUserAnswer());
                                        questionAnswerGetModel.setGrade(0.0f);
                                      });
                              userAssessmentGetModel
                                  .getAssessmentQuestionCreateModels()
                                  .add(questionAnswerGetModel);
                              userAssessmentGetModel.setLimitDuration(userAssessment.getDuration());
                            });
                  } else {
                    throw new NotPermittedException();
                  }
                  break;
              }
              return ResponseModel.done(userAssessmentGetModel);
            })
        .orElseThrow(() -> new MintException(Code.INVALID_CODE, "error.assessment.nottaken"));
  }

  // TODO: Review
  // Status are STARTED , FINISHED OR PAUSED
  @Transactional
  @Auditable(EntityAction.ASSESSMENT_UPDATE)
  public ResponseModel updateAssessmentStatus(
      Long assessmentId, AssessmentStatus assessmentStatus) {
    return userRepository
        .findOneByUserNameAndDeletedFalse(SecurityUtils.getCurrentUserLogin())
        .map(
            user ->
                assessmentRepository
                    .findOneByIdAndDeletedFalseAndOwnerAndAssessmentTypeIn(
                        assessmentId, user, AssessmentType.PRACTICE)
                    .map(
                        assessment -> {
                          assessment.setAssessmentStatus(assessmentStatus);
                          if (assessmentStatus.equals(AssessmentStatus.STARTED)) {
                            assessment.setStartDateTime(new Date());
                          }
                          assessmentRepository.save(assessment);
                          return ResponseModel.done();
                        })
                    .orElseThrow(NotFoundException::new))
        .orElseThrow(NotPermittedException::new);
  }

  // TODO: Review
  @Transactional
  @Auditable(EntityAction.ASSESSMENT_UPDATE)
  public ResponseModel reset(Long id) {
    return userRepository
        .findOneByUserNameAndDeletedFalse(SecurityUtils.getCurrentUserLogin())
        .map(
            user ->
                assessmentRepository
                    .findOneByIdAndDeletedFalseAndOwnerAndAssessmentTypeIn(
                        id, user, AssessmentType.PRACTICE)
                    .map(
                        assessment ->
                            userAssessmentRepository
                                .findOneByUserIdAndAssessmentIdAndDeletedFalse(
                                    user.getId(), assessment.getId())
                                .map(
                                    userAssessment -> {
                                      questionAnswerRepository.deleteAll(
                                          userAssessment.getQuestionAnswerList());
                                      userAssessmentRepository.delete(userAssessment);
                                      assessment.setStartDateTime(null);
                                      assessment.setAssessmentStatus(AssessmentStatus.NEW);
                                      assessment.setLimitDuration(0l);
                                      assessmentRepository.save(assessment);
                                      return ResponseModel.done(assessment.getId());
                                    })
                                .orElseThrow(NotFoundException::new))
                    .orElseThrow(NotFoundException::new))
        .orElseThrow(NotPermittedException::new);
  }

  private AssessmentListModel assessmentListMapping(Assessment assessment, Long currentUserId) {

    List<UserAssessment> userAssessmentsList =
        userAssessmentRepository
            .findByAssessmentIdAndDeletedFalseOrderByTotalGradeAsc(assessment.getId())
            .collect(Collectors.toList());
    AssessmentListModel assessmentListModel =
        new AssessmentListModel(assessment, userAssessmentsList, currentUserId);

    List<Long> userIdList =
        userAssessmentsList.stream()
            .filter(
                userAssessment1 ->
                    userAssessment1.getAssessmentStatus() == AssessmentStatus.FINISHED
                        || userAssessment1.getAssessmentStatus() == AssessmentStatus.NOT_EVALUATED)
            .limit(MAX_USER_IN_COMMUNITY)
            .map(UserAssessment::getUserId)
            .collect(Collectors.toList());
    assessmentListModel.setUserCommunity(getCommunityList(userIdList));
    assessmentListModel.setNumberOfQuestions(
        assessmentQuestionRepository.countByAssessmentIdAndDeletedFalse(assessment.getId()));
    return assessmentListModel;
  }

  private AssessmentModel assessmentMapping(Assessment assessment, Long currentUserId) {

    List<UserAssessment> userAssessmentsList =
        userAssessmentRepository
            .findByAssessmentIdAndDeletedFalseOrderByTotalGradeAsc(assessment.getId())
            .collect(Collectors.toList());

    AssessmentModel assessmentModel =
        new AssessmentModel(assessment, userAssessmentsList, currentUserId);

    List<Long> userIdList =
        userAssessmentsList.stream()
            .filter(
                userAssessment1 ->
                    userAssessment1.getAssessmentStatus() == AssessmentStatus.FINISHED
                        || userAssessment1.getAssessmentStatus() == AssessmentStatus.NOT_EVALUATED)
            .limit(MAX_USER_IN_COMMUNITY)
            .map(UserAssessment::getUserId)
            .collect(Collectors.toList());
    if (!userIdList.isEmpty()) {
      assessmentModel.setUserCommunity(getCommunityList(userIdList));
    }

    return assessmentModel;
  }

  private List<ContentUserModel> getCommunityList(List<Long> userIdList) {
    List<ContentUserModel> communityList = new ArrayList<>();
    if (!userIdList.isEmpty()) {
      userRepository
          .findAllById(userIdList)
          .forEach(user -> communityList.add(new ContentUserModel(user)));
    }
    return communityList;
  }

  private void grade(
      UserAssessmentModel userAssessmentModel,
      UserAssessment userAssessment,
      List<AssessmentQuestion> assessmentQuestions,
      Assessment assessment,
      User user) {
    float totalGrade = 0f;
    List<QuestionAnswer> questionAnswerList = userAssessment.getQuestionAnswerList();
    if (userAssessment.getAssessmentStatus().equals(AssessmentStatus.FINISHED)) {
      userAssessment.setAssessmentStatus(AssessmentStatus.EVALUATED);
    }
    if (userAssessmentModel.getQuestionAnswerModels() != null
        && !userAssessmentModel.getQuestionAnswerModels().isEmpty()) {
      for (AssessmentQuestion assessmentQuestion : assessmentQuestions) {
        Optional<QuestionAnswerModel> questionAnswerModelOptional =
            userAssessmentModel.getQuestionAnswerModels().stream()
                .filter(
                    questionAnswerModel1 ->
                        questionAnswerModel1.getQuestionId().equals(assessmentQuestion.getId()))
                .findFirst();

        if (!questionAnswerModelOptional.isPresent()) {
          continue;
        }
        QuestionAnswerModel questionAnswerModel = questionAnswerModelOptional.get();

        if (questionAnswerModel.getUserAnswer() != null
            && !questionAnswerModel.getUserAnswer().trim().isEmpty()) {
          if (questionAnswerModel.getGrade() != null
              && questionAnswerModel.getGrade() != 0.0f
              && user.equals(assessment.getOwner())
              && assessment.getAssessmentType() != AssessmentType.PRACTICE
              && assessment.getAssessmentType() != AssessmentType.CHALLENGE) {
            QuestionAnswer questionAnswer = new QuestionAnswer();
            AssessmentsMapper.INSTANCE.mapQuestionAnswerModelToDomain(
                questionAnswerModel, questionAnswer);
            questionAnswer.setUserId(userAssessment.getUserId());
            int index = questionAnswerList.indexOf(questionAnswer);
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

        QuestionAnswer questionAnswer = new QuestionAnswer();
        AssessmentsMapper.INSTANCE.mapQuestionAnswerModelToDomain(
            questionAnswerModel, questionAnswer);
        questionAnswer.setUserId(userAssessment.getUserId());
        questionAnswerList.add(questionAnswer);
      }
    }

    userAssessment.setTotalGrade(totalGrade);

    if (assessment.getTotalPoints() > 0 && null != assessment.getTotalPoints()) {
      double temp =
          Double.valueOf(totalGrade)
              / Double.valueOf(assessment.getTotalPoints().floatValue())
              * 100;
      userAssessment.setPercentage((float) temp);
    }

    questionAnswerRepository.saveAll(questionAnswerList);
    userAssessment.setQuestionAnswerList(questionAnswerList);
  }

  private void gradeTrueFalseQuestion(
      QuestionAnswerModel questionAnswerModel, AssessmentQuestion assessmentQuestion) {

    if ((assessmentQuestion.getCorrectAnswer() == null
        && questionAnswerModel.getUserAnswer().equalsIgnoreCase("false"))) {
      questionAnswerModel.setGrade(assessmentQuestion.getQuestionWeight().floatValue());
    } else if (assessmentQuestion.getCorrectAnswer() != null
        && assessmentQuestion
            .getCorrectAnswer()
            .trim()
            .equalsIgnoreCase(questionAnswerModel.getUserAnswer().trim())) {
      questionAnswerModel.setGrade(assessmentQuestion.getQuestionWeight().floatValue());
    }
  }

  private void gradeSingleChoiceQuestion(
      QuestionAnswerModel questionAnswerModel, AssessmentQuestion assessmentQuestion) {
    ArrayList<String> userAnswers =
        new ArrayList<>(Arrays.asList(questionAnswerModel.getUserAnswer().split(",")));
    if (userAnswers.size() == 1) {
      Set<AssessmentQuestionChoice> assessmentQuestionChoices =
          assessmentQuestion.getAssessmentQuestionChoices();
      for (AssessmentQuestionChoice assessmentQuestionChoice : assessmentQuestionChoices) {
        if (assessmentQuestionChoice.getCorrectAnswer()) {
          if (assessmentQuestionChoice.getId()
              == Long.parseLong(questionAnswerModel.getUserAnswer())) {
            questionAnswerModel.setGrade(assessmentQuestion.getQuestionWeight().floatValue());
          }
        }
      }
    }
  }

  private void gradeMultipleChoicesQuestion(
      QuestionAnswerModel questionAnswerModel, AssessmentQuestion assessmentQuestion) {
    Set<AssessmentQuestionChoice> assessmentQuestionChoices =
        assessmentQuestion.getAssessmentQuestionChoices();
    ArrayList<String> userAnswers =
        new ArrayList<>(Arrays.asList(questionAnswerModel.getUserAnswer().split(",")));
    ArrayList<String> correctAnswers = new ArrayList<>();
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

  private void gradeSequenceQuestion(
      QuestionAnswerModel questionAnswerModel, AssessmentQuestion assessmentQuestion) {

    List<String> correctAnswers =
        assessmentQuestion.getAssessmentQuestionChoices().stream()
            .sorted(Comparator.comparingInt(AssessmentQuestionChoice::getCorrectOrder))
            .map(assessmentQuestionChoice -> assessmentQuestionChoice.getId().toString())
            .collect(Collectors.toList());
    ArrayList<String> userAnswers =
        new ArrayList<>(Arrays.asList(questionAnswerModel.getUserAnswer().split(",")));

    if (correctAnswers.equals(userAnswers)) {
      questionAnswerModel.setGrade(assessmentQuestion.getQuestionWeight().floatValue());
    }
  }

  // TODO: Need more review
  private void gradeMatchingQuestion(
      QuestionAnswerModel questionAnswerModel, AssessmentQuestion assessmentQuestion) {
    int point = 0;

    ArrayList<Set<String>> userAnswers = new ArrayList<>();
    ArrayList<Set<String>> correctAnswers = new ArrayList<>();

    List<AssessmentQuestionChoice> assessmentQuestionChoices =
        assessmentQuestion.getAssessmentQuestionChoices().stream()
            .sorted(
                Comparator.comparingInt(AssessmentQuestionChoice::getCorrectOrder)
                    .thenComparing(AssessmentQuestionChoice::getPairCol))
            .toList();

    Arrays.stream(questionAnswerModel.getUserAnswer().split(","))
        .forEach(
            s -> {
              Set<String> pair = new HashSet<>(Arrays.asList(s.split("\\|")));
              userAnswers.add(pair);
            });

    for (int i = 0; i < assessmentQuestionChoices.size() - 1; i += 2) {
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

    questionAnswerModel.setGrade(
        (point / (float) correctAnswers.size()) * assessmentQuestion.getQuestionWeight());
  }

  private void gradeCompleteQuestion(
      QuestionAnswerModel questionAnswerModel, AssessmentQuestion assessmentQuestion) {

    ArrayList<String> correctAnswers = new ArrayList<>();
    assessmentQuestion.getAssessmentQuestionChoices().stream()
        .sorted(Comparator.comparingInt(AssessmentQuestionChoice::getCorrectOrder))
        .forEach(
            assessmentQuestionChoice -> correctAnswers.add(assessmentQuestionChoice.getLabel()));
    ArrayList<String> userAnswers =
        new ArrayList<>(Arrays.asList(questionAnswerModel.getUserAnswer().split(",")));
    if (correctAnswers.equals(userAnswers)) {
      questionAnswerModel.setGrade(assessmentQuestion.getQuestionWeight().floatValue());
    }
  }

  // TODO: Review
  private QuestionAnswerGetModel mapQuestionAnswerGetModel(AssessmentQuestion assessmentQuestion) {
    QuestionAnswerGetModel questionAnswerGetModel = new QuestionAnswerGetModel();
    questionAnswerGetModel.setBody(assessmentQuestion.getBody());
    questionAnswerGetModel.setBodyResourceUrl(assessmentQuestion.getBodyResourceUrl());
    questionAnswerGetModel.setCorrectAnswer(assessmentQuestion.getCorrectAnswer());
    questionAnswerGetModel.setId(assessmentQuestion.getId());
    questionAnswerGetModel.setQuestionType(assessmentQuestion.getQuestionType());
    questionAnswerGetModel.setQuestionWeight(assessmentQuestion.getQuestionWeight());

    assessmentQuestion
        .getAssessmentQuestionChoices()
        .forEach(
            assessmentQuestionChoice -> {
              ChoicesModel assessmentQuestionChoiceModel = new ChoicesModel();
              assessmentQuestionChoiceModel.setId(assessmentQuestionChoice.getId());
              assessmentQuestionChoiceModel.setCorrectAnswer(
                  assessmentQuestionChoice.getCorrectAnswer());
              assessmentQuestionChoiceModel.setCorrectOrder(
                  assessmentQuestionChoice.getCorrectOrder());
              assessmentQuestionChoiceModel.setLabel(assessmentQuestionChoice.getLabel());
              assessmentQuestionChoiceModel.setPairColumn(assessmentQuestionChoice.getPairCol());
              assessmentQuestionChoiceModel.setCorrectAnswerResourceUrl(
                  assessmentQuestionChoice.getCorrectAnswerResourceUrl());
              assessmentQuestionChoiceModel.setCorrectAnswerDescription(
                  assessmentQuestionChoice.getCorrectAnswerDescription());
              questionAnswerGetModel.getChoicesList().add(assessmentQuestionChoiceModel);
            });

    return questionAnswerGetModel;
  }

  private QuestionBankResponseModel searchQuestionBank(
      QuestionSearchModel questionSearchModel, HttpServletRequest request) {
    HttpHeaders headers = new HttpHeaders();
    String questionBankUrl = "http://" + mintProperties.getApiDomain();
    log.info("url to connect {}", questionBankUrl);
    headers.setContentType(MediaType.APPLICATION_JSON);
    if (questionSearchModel.getLimit() != null && !questionSearchModel.getLimit().equals(0)) {
      headers.add("size", questionSearchModel.getLimit().toString());
    }
    headers.add("Authorization", request.getHeader("Authorization"));
    HttpEntity<QuestionSearchModel> httpRequest = new HttpEntity<>(questionSearchModel, headers);
    try {
      log.info("request {}", httpRequest);

      return restTemplate.postForObject(
          questionBankUrl + "/api/question/search", httpRequest, QuestionBankResponseModel.class);
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
