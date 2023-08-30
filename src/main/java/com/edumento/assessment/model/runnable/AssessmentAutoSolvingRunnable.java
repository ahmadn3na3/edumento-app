package com.edumento.assessment.model.runnable;

import com.edumento.assessment.domain.QuestionAnswer;
import com.edumento.assessment.domain.UserAssessment;
import com.edumento.assessment.model.QuestionAnswerModel;
import com.edumento.assessment.model.UserAssessmentModel;
import com.edumento.assessment.repos.UserAssessmentRepository;
import com.edumento.assessment.services.AssessmentService;
import com.edumento.core.constants.AssessmentStatus;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AssessmentAutoSolvingRunnable implements Runnable {

	private final Logger log = LoggerFactory.getLogger(AssessmentAutoSolvingRunnable.class);

	private final UserAssessmentRepository userAssessmentRepository;

	private final AssessmentService assessmentService;
	//FIXME: set as finals
	private long assessmentId;
	private long userId;
	private long limitDuration;

	public AssessmentAutoSolvingRunnable(long assessmentId, long userId, long limitDuration,
			UserAssessmentRepository userAssessmentRepository, AssessmentService assessmentService) {
		this.assessmentId = assessmentId;
		this.userId = userId;
		this.limitDuration = limitDuration;
		this.userAssessmentRepository = userAssessmentRepository;
		this.assessmentService = assessmentService;
	}

	@Override
	//FIXME: Do not use string concat in logger
	//FIXME: Use DEBUG/TRACE Level in detail
	
	public void run() {
		log.info("AssessmentAutoSolvingRunnable ::: Running Thread......");
		log.info("AssessmentAutoSolvingRunnable ::: userId = " + userId);
		log.info("AssessmentAutoSolvingRunnable ::: assessmentId = " + assessmentId);
		log.info("AssessmentAutoSolvingRunnable ::: limitDuration = " + limitDuration);
		Optional<UserAssessment> userAssessment = userAssessmentRepository
				.findOneByUserIdAndAssessmentIdAndDeletedFalse(userId, assessmentId);
		if (userAssessment.isPresent()) {
			if (!Arrays.asList(AssessmentStatus.FINISHED, AssessmentStatus.NOT_EVALUATED, AssessmentStatus.EVALUATED)
					.contains(userAssessment.get().getAssessmentStatus())) {
				autoSolvingAssessment(assessmentId, userId, limitDuration);
			}
		}
	}

	/** created by A.Alsayed 03-03-2019 */
	private void autoSolvingAssessment(long assessmentId, long userId, long limitDuration) {
		log.info("AssessmentAutoSolvingRunnable ::: Running autoSolvingAssessment......");
		UserAssessmentModel userAssessmentModel = new UserAssessmentModel();

		userAssessmentModel.setAssessmentId(assessmentId);
		userAssessmentModel.setUserId(userId);
		userAssessmentModel.setDuration(limitDuration);

		// getting submitted questions until time duration:
		// ================================================
		userAssessmentRepository.findOneByUserIdAndAssessmentIdAndDeletedFalse(userId, assessmentId)
				.ifPresent(userAssessment -> {
					userAssessmentModel.setAssessmentStatus(userAssessment.getAssessmentStatus());
					userAssessmentModel
							.setQuestionAnswerModels(questionListMapping(userAssessment.getQuestionAnswerList()));
				});

		// calling the submit function:
		assessmentService.autoSubmitChallenge(userAssessmentModel);
		log.info("AssessmentAutoSolvingRunnable ::: Finishing autoSolvingAssessment process......");
	}

	/** created by A.Alsayed 03-03-2019 */
	//TODO: Use lamba experision
	private List<QuestionAnswerModel> questionListMapping(List<QuestionAnswer> questionAnswerList) {
		if (questionAnswerList != null && questionAnswerList.size() > 0) {
			List<QuestionAnswerModel> questionAnswerModels = new ArrayList<QuestionAnswerModel>();
			for (QuestionAnswer questionAnswer : questionAnswerList) {
				QuestionAnswerModel questionAnswerModel = new QuestionAnswerModel();

				questionAnswerModel.setGrade(questionAnswer.getGrade());
				questionAnswerModel.setId(questionAnswer.getId());
				questionAnswerModel.setQuestionId(questionAnswer.getQuestionId());
				questionAnswerModel.setSkipped(questionAnswer.getSkipped());
				questionAnswerModel.setUserAnswer(questionAnswer.getUserAnswer());
				questionAnswerModel.setUserId(questionAnswer.getUserId());

				questionAnswerModels.add(questionAnswerModel);
			}
			return questionAnswerModels;
		}
		return null;
	}
}
