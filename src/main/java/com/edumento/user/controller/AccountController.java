package com.edumento.user.controller;

import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.edumento.category.repos.CategoryRepository;
import com.edumento.core.model.ResponseModel;
import com.edumento.user.model.account.ChangePasswordModel;
import com.edumento.user.model.account.FoundationRegesiterAccountModel;
import com.edumento.user.model.account.FoundationRegesiterAccountWithEncodePasswordModel;
import com.edumento.user.model.account.KeyAndPasswordDTO;
import com.edumento.user.model.account.RegesiterAccountModel;
import com.edumento.user.model.account.ResetPasswordModel;
import com.edumento.user.model.user.UserCreateModel;
import com.edumento.user.services.AccountService;
import com.edumento.user.services.UserUploadService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

/** REST controller for managing the current user's account. */
@RestController
@RequestMapping("/api")
public class AccountController {

	private final Logger log = LoggerFactory.getLogger(AccountController.class);

	@Autowired
	private AccountService accountService;

	@Autowired
	private UserUploadService uploadService;

	@Autowired
	private CategoryRepository categoryRepository;

	@Value("${mint.version}")
	private String version;

	/** POST /register -> register the user. */
	@RequestMapping(path = "/register", method = RequestMethod.POST)
	//@ApiOperation(value = "Register new account", notes = "this method is used to register for a new account", response = ResponseModel.class)
	public ResponseModel registerAccount(@Valid @RequestBody RegesiterAccountModel userDTO,
			HttpServletRequest request) {
		String baseUrl = request.getScheme() + // "http"
				"://" + // "://"
				request.getServerName() + // "myhost"
				":" + // ":"
				request.getServerPort() + // "80"
				request.getContextPath();
		String lang = request.getHeader("lang") != null ? request.getHeader("lang") : "en"; // "/myContextPath"
		// or "" if
		// deployed
		// in root
		// context

		return accountService.createUserInformation(userDTO, baseUrl, lang);
	}

//	@ApiOperation(value = "Register new B2B account", notes = "this method is used to register for a new B2B account", response = ResponseModel.class)
	@RequestMapping(path = "/b2bregister", method = RequestMethod.POST)
	public ResponseModel registerB2bAccount(@Valid @RequestBody FoundationRegesiterAccountModel userDTO,
			HttpServletRequest request) {
		return accountService.createUser(userDTO);
	}
	
//	@ApiOperation(value = "Register new B2B account with ", notes = "this method is used to register for a new B2B account", response = ResponseModel.class)
	@RequestMapping(path = "/b2bregisterWithEncodePassword", method = RequestMethod.POST)
	public ResponseModel registerB2bAccountWithEncodePassword(@Valid @RequestBody FoundationRegesiterAccountWithEncodePasswordModel userDTO,
			HttpServletRequest request) {
		return accountService.createUser(userDTO);
	}

	@RequestMapping(path = "/reactivate", method = RequestMethod.POST)
//	@ApiOperation(value = "Resend Activtion Code", notes = "This method is used to resend activation code to registered users", response = ResponseModel.class)
	public ResponseModel resendActivationMail(@RequestBody @Validated ResetPasswordModel model,
			@RequestHeader(required = false, defaultValue = "en") String lang) {
		return accountService.resendActivationCode(model.getMail(), lang);
	}

	/** GET /activate -> activate the registered user. */
	@GetMapping(value = "/activate/{key}", produces = MediaType.APPLICATION_JSON_VALUE)
//	@ApiOperation(value = "Activate Account", notes = "This method is used to activate account with activation key")
	public ResponseModel activateAccount(@PathVariable String key) {
		return accountService.activateRegistration(key);
	}

	/** GET /account -> get the current user. */
	@GetMapping(value = "/account", produces = MediaType.APPLICATION_JSON_VALUE)
//	@ApiOperaton(value = "Get Account", notes = "this method is used to get user authorities")
	public ResponseModel getAccount(HttpServletRequest request) {
		return ResponseModel.done(accountService.getUserWithAuthorities());
	}

	/** POST /account -> update the current user information. */
	@RequestMapping(path = "/account", method = RequestMethod.POST)
//	@ApiOperation(value = "Save Account", notes = "this method is used to save user's information")
	public ResponseModel saveAccount(@RequestBody UserCreateModel userDTO, HttpServletRequest request) {
		return accountService.updateUserInformation(userDTO);
	}

	/** POST /change_password -> changes the current user's password */
	@RequestMapping(path = "/account/change_password", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
//	@ApiOperation(value = "Change password", notes = "this method is used to change user's password")
	public ResponseModel changePassword(@RequestBody @Validated ChangePasswordModel model, HttpServletRequest request) {

		return accountService.changePassword(model);
	}

	@RequestMapping(path = "/account/forget_password/init", method = RequestMethod.POST)
//	@ApiOperation(value = "Request Reset password", notes = "this method is used to request resetting user's password")
	public ResponseModel requestPasswordReset(@RequestBody @Validated ResetPasswordModel model,
			@RequestHeader(defaultValue = "en", required = false) String lang) {
		return accountService.requestPasswordReset(model.getMail(), "", lang);
	}

	@GetMapping(value = "/account/forget_password/checkcode/{key}", produces = MediaType.APPLICATION_JSON_VALUE)
//	@ApiOperation(value = "Validate Reset Password key", notes = "this method is used to validate reset password code")
	public ResponseModel requestPasswordReset(@PathVariable String key, HttpServletRequest request) {
		return accountService.checkResetCode(key);
	}

	@RequestMapping(path = "/account/forget_password/finish", method = RequestMethod.POST)
//	@ApiOperation(value = "Reset password", notes = "this method is used to complete reset password process")
	public ResponseModel finishPasswordReset(@Valid @RequestBody KeyAndPasswordDTO keyAndPassword,
			HttpServletRequest request) {

		return accountService.completePasswordReset(keyAndPassword.getNewPassword(), keyAndPassword.getKey());
	}

	@GetMapping(value = "/interests", produces = MediaType.APPLICATION_JSON_VALUE)
	@Transactional(readOnly = true)
//	@ApiOperation(value = "Get interest", notes = "this method is used to list categories of interests")
	public ResponseModel getInterests(
			@RequestHeader(name = "lang", required = false, defaultValue = "en") String lang) {
		Set<String> categories = categoryRepository.findByOrganizationIsNullAndFoundationIsNullAndDeletedFalse()
				.map(category -> lang.equals("ar") ? category.getNameAr() : category.getName())
				.collect(Collectors.toSet());
		return ResponseModel.done(categories);
	}

	@GetMapping(value = "/get_groups/{foundationId}", produces = MediaType.APPLICATION_JSON_VALUE)
	@Transactional(readOnly = true)
//	@ApiOperation(value = "Get interest", notes = "this method is used to list categories of interests")
	public ResponseModel getGroups(@PathVariable(name = "foundationId") Long foundationId,
			@RequestHeader(name = "lang", required = false, defaultValue = "en") String lang) {

		return accountService.getFoundationGroups(foundationId);
	}

	@GetMapping(value = "/time", produces = MediaType.APPLICATION_JSON_VALUE)
//	@ApiOperation(value = "Get Time", notes = "this method is used to return server time to caller")
	public ResponseModel getTime() {
		ResponseModel responseModel = ResponseModel.done(new Date());
		responseModel.setMessage("mint version = " + version);
		return responseModel;
	}

	@GetMapping(value = "/images_thumbnail", produces = MediaType.APPLICATION_JSON_VALUE)
//	@ApiOperation(value = "Get default images", notes = "this method is used to get default images and thumbnails")
	public ResponseModel getDefaultImagesAndThumbnails() {
		return uploadService.getDefaultImagesAndThumbnails();
	}

	@RequestMapping(path = "/account/changegrade/{fromId}/to/{toId}", method = RequestMethod.PUT)
	public ResponseModel changeGrade(@PathVariable("fromId") Long fromId, @PathVariable("toId") Long toId) {
		return accountService.changeGrade(fromId, toId);

	}
	
	/** created by A.Alsayed 16-01-2019 */
	/** this method is used for returning sum of user spaces score and user level */
	@RequestMapping(path = "/leaderboard/getUserLevelAndPoints", method = RequestMethod.GET)
//	@ApiOperation(value = "Get user level", notes = "this method is used to Get sum of user spaces score and user level ")
	public ResponseModel getUserLevelAndPoints() {
		return null;
		
//		return accountService.getUserLevelAndPoints();
	}
	
	/** created by A.Alsayed 21-01-2019 */
	/** this method is used for returning User's global ranking */
	@RequestMapping(path = "/leaderboard/getUserGlobalRanking", method = RequestMethod.GET)
//	@ApiOperation(value = "Get User Global Ranking", notes = "this method is used to return User's global ranking")
	public ResponseModel getUserGlobalRanking() {
		return null;
//		return accountService.getUserGlobalRanking();
	}
	
	/** created by A.Alsayed 21-01-2019 */
	/** this method is used for returning User's rank per each space */
	@RequestMapping(path = "/leaderboard/getUserSpaceRanking", method = RequestMethod.GET)
//	@ApiOperation(value = "Get User Space Ranking", notes = "this method is used to return User's rank per each space")
	public ResponseModel getUserSpaceRanking() {
		return null;
//		return accountService.getUserSpaceRanking();
	}
	
	/** Created by A.Alsayed on 14/03/2019. */
	/** this method is used for Getting Top Users Ranking */
	@RequestMapping(path = "/leaderboard/getTopUsersRanking", method = RequestMethod.GET)
//	@ApiOperation(value = "Get Top Users Ranking", notes = "this method is used to return Top Users Ranking")
	public ResponseModel getTopUsersRanking() {
		return null;
//		return accountService.getTopUsersRanking();
	}
	
//	@ApiOperation(value = "encode Password", notes = "this method is used to encode password if want to send encoded password", response = ResponseModel.class)
	@RequestMapping(path = "/encodePassword", method = RequestMethod.POST)
	public ResponseModel encodePassword(@RequestBody String password) {
		return accountService.encodePasseword(password);
	}
}
