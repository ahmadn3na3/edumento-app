package com.edumento.assessment.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.edumento.assessment.model.AssessmentCreateModel;
import com.edumento.assessment.model.AssessmentGetAllModel;
import com.edumento.assessment.model.PracticeGenerateModel;
import com.edumento.assessment.model.UserAssessmentModel;
import com.edumento.assessment.model.UserPracticeModel;
import com.edumento.assessment.model.challenge.ChallengeCreateModel;
import com.edumento.assessment.services.AssessmentService;
import com.edumento.core.constants.AssessmentStatus;
import com.edumento.core.constants.AssessmentType;
import com.edumento.core.constants.SortField;
import com.edumento.core.controller.abstractcontroller.AbstractController;
import com.edumento.core.model.PageRequestModel;
import com.edumento.core.model.ResponseModel;

import jakarta.servlet.http.HttpServletRequest;

/** Created by ayman on 13/06/16. */
@RestController
@RequestMapping("/api/assessment")
public class AssessmentController extends AbstractController<AssessmentCreateModel, Long> {
	@Autowired
	AssessmentService assessmentService;

	@Override
	@RequestMapping(method = RequestMethod.POST)
//	@ApiOperation(value = "Create Assessment ", notes = "this method is used to create assessment assignment , quize , worksheet and practice")
	public ResponseModel create(@RequestBody @Validated AssessmentCreateModel createModel) {
		return assessmentService.create(createModel, null);
	}

	@RequestMapping(method = RequestMethod.POST, path = "/practice")
//	@ApiOperation(value = "Generate practice", notes = "this method is used to generate practice from question bank")
	public ResponseModel generatePratcice(@RequestBody @Validated PracticeGenerateModel practiceGenerateModel,
			HttpServletRequest request) {
		return assessmentService.generatePractice(practiceGenerateModel, request);
	}

	@RequestMapping(method = RequestMethod.POST, path = "/challenge")
//	@ApiOperation(value = "Generate challenge", notes = "this method is used to generate practice from question bank")
	public ResponseModel generateChanllenge(@RequestBody @Validated ChallengeCreateModel challengeCreateModel,
			HttpServletRequest request) {
		return assessmentService.createChallenge(challengeCreateModel, request);
	}

	@Override
	@RequestMapping(path = "/{id}", method = RequestMethod.PUT)
//	@ApiOperation(value = "Update Assessment", notes = "this method is used to update assessment")
	public ResponseModel update(@PathVariable Long id, @RequestBody @Validated AssessmentCreateModel updateModel) {

		return assessmentService.update(id, updateModel);
	}

	/** Created by A.Alsayed on 05/01/19. */
	@RequestMapping(path = "/challenge/{spaceId}", method = RequestMethod.GET)
//	@ApiOperation(value = "Get user challenges", notes = "this method is used to get user challenges ")
	public ResponseModel getUserChallenges(@PathVariable Long spaceId, @RequestHeader(required = false) Integer page,
			@RequestHeader(required = false) Integer size) {
		return assessmentService.getUserChallenges(spaceId, PageRequestModel.getPageRequestModel(page, size));
	}

	/** Created by A.Alsayed on 05/01/19. */
	@RequestMapping(path = "/challenge/{challengeId}/start", method = RequestMethod.GET)
//	@ApiOperation(value = "start user challenges", notes = "this method is used to start user challenge")
	public ResponseModel startChallenge(@PathVariable Long challengeId) {
		return assessmentService.startChallenge(challengeId);
	}

	/** Created by A.Alsayed on 18/02/19. */
	@RequestMapping(path = "/challengeOpponents/{challengeId}", method = RequestMethod.GET)
//	@ApiOperation(value = "Get challenge opponent", notes = "this method is used to get challenge opponents")
	public ResponseModel getChallengeOpponents(@PathVariable Long challengeId) {
		return assessmentService.getChallengeOpponents(challengeId);
	}

//	@RequestMapping(path = "/findAssessment/{assessmentId}", method = RequestMethod.GET)
//	@ApiOperation(value = "testing check assessment", notes = "this method is testing check assessment")
//	public ResponseModel checkAssessmentByOwnerOrChallengee(@PathVariable Long assessmentId) {
//		return assessmentService.checkAssessmentByOwnerOrChallengee(assessmentId);
//	}

	@Override
	@RequestMapping(path = "/{id}", method = RequestMethod.GET)
//	@ApiOperation(value = "Get an assessment", notes = "this method is used to get an assessment by id")
	public ResponseModel get(@PathVariable Long id) {
		return assessmentService.get(id);
	}

	@RequestMapping(path = "/getAll", method = RequestMethod.POST)
//	@ApiOperation(value = "List all assessment", notes = "this method is used to get all assessments for user")
	public ResponseModel getAll(@RequestBody @Validated AssessmentGetAllModel assessmentGetAllModel,
			@RequestHeader(required = false) Integer page, @RequestHeader(required = false) Integer size,
			@RequestHeader(required = false) SortField field,
			@RequestHeader(required = false) Sort.Direction direction) {
		Sort sort = null;
		if (field != null && direction != null) {
			sort = Sort.by(direction, field.getFieldName());
		}
		return assessmentService.get(assessmentGetAllModel, PageRequestModel.getPageRequestModel(page, size, sort));
	}

	@RequestMapping(path = "/assessmentOverview/{spaceId}", method = RequestMethod.GET)
//	@ApiOperation(value = "Get assessment overview", notes = "this method is used to get an overview of assessments in specific space")
	public ResponseModel getAssessmentOverview(@PathVariable Long spaceId) {
		return assessmentService.getAssessmentOverview(spaceId);
	}

	@RequestMapping(method = RequestMethod.GET)
//	@ApiOperation(value = "Get All Assessments", notes = "this method is used to get all Assessments", hidden = true)
	public ResponseModel getAll(@RequestHeader(required = false) Long spaceId,
			@RequestHeader(required = false) AssessmentType assessmentType,
			@RequestHeader(required = false) Integer page, @RequestHeader(required = false) Integer size) {
		return assessmentService.getAll(PageRequestModel.getPageRequestModel(page, size), spaceId, assessmentType);
	}

	@Override
	@DeleteMapping(path = "/{id}")
//	@ApiOperation(value = "Delete assessment", notes = "this method is used to delete assessment by id")
	public ResponseModel delete(@PathVariable Long id) {
		return assessmentService.delete(id);
	}

//	@ApiOperation(value = "Submit assessment", notes = "this method is used to submit user answers for an assessment")
	@RequestMapping(path = "/submit", method = RequestMethod.POST)
	public ResponseModel submit(@RequestBody @Validated UserAssessmentModel userAssessmentModel,
			@RequestHeader(name = "lang", required = false, defaultValue = "eng") String lang) {
		return assessmentService.submit(userAssessmentModel, lang);
	}

	@RequestMapping(path = "/getUpdates", method = RequestMethod.POST)
	@Deprecated
	public ResponseModel getUpdates(@RequestBody @Validated AssessmentGetAllModel assessmentGetAllModel) {
		return assessmentService.getUpdates(assessmentGetAllModel);
	}

	@RequestMapping(path = "/getComunityList/{id}", method = RequestMethod.GET)
//	@ApiOperation(value = "Get assessment's community", notes = "this method is used to list users who answer an assessment")
	public ResponseModel getCommunityList(@PathVariable Long id) {
		return assessmentService.getCommunityList(id);
	}

	@RequestMapping(path = "/publish/{id}", method = RequestMethod.PUT)
//	@ApiOperation(value = "Publish assessment", notes = "this method is used to publish an assessment")
	public ResponseModel publish(@PathVariable Long id) {
		return assessmentService.togglePublish(id);
	}

	@RequestMapping(path = "/{id}/user/{userId}", method = RequestMethod.GET)
//	@ApiOperation(value = "Get User's assessment", notes = "this method is used to get specific assessment of specific user")
	public ResponseModel getUserAssessment(@PathVariable Long id, @PathVariable Long userId) {
		return assessmentService.getUserAssessment(id, userId);
	}

	@RequestMapping(path = "/set/{id}/{status}", method = RequestMethod.GET)
//	@ApiOperation(value = "Update assessment status", notes = "this method is used to update assessment status", hidden = true)
	public ResponseModel updateAssessmentStatus(@PathVariable Long id, @PathVariable AssessmentStatus status) {
		return assessmentService.updateAssessmentStatus(id, status);
	}

	@RequestMapping(path = "/reset/{id}", method = RequestMethod.GET)
//	@ApiOperation(value = "Reset practice", notes = "this method is used to reset practice")
	public ResponseModel reset(@PathVariable Long id) {
		return assessmentService.reset(id);
	}

	@RequestMapping(path = "/submit/question", method = RequestMethod.POST)
//	@ApiOperation(value = "Submit question", notes = "this method is used to submit question", hidden = true)
	public ResponseModel submitQuestion(@RequestBody @Validated UserPracticeModel userPracticeModel) {
		return assessmentService.submitQuestion(userPracticeModel);
	}
}
