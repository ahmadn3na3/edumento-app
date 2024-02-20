package com.edumento.core.controller.advisor;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import com.edumento.core.constants.Code;
import com.edumento.core.exception.ExistException;
import com.edumento.core.exception.InvalidException;
import com.edumento.core.exception.MintException;
import com.edumento.core.exception.NotFoundException;
import com.edumento.core.exception.NotPermittedException;
import com.edumento.core.exception.NotReadyException;
import com.edumento.core.model.ResponseModel;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Created by ahmad on 2/18/16. Controller Advisor to handel validation
 * exceptions
 */
@ControllerAdvice
public class ControllerValidationHandler {

	@Autowired
	private MessageSource msgSource;

	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	public ResponseModel processValidationError(MethodArgumentNotValidException ex) {
		var result = ex.getBindingResult();
		var error = result.getFieldError();
		return processFieldError(error);
	}

	@ExceptionHandler(ExistException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	@ResponseBody
	public ResponseModel processExistException(HttpServletRequest request, ExistException e) {
		var responseModel = handleMintException(request, e);
		responseModel.setData(e.getData());
		return responseModel;
	}

	@ExceptionHandler(NotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ResponseBody
	public ResponseModel processNotFoundException(HttpServletRequest request, NotFoundException e) {
		return handleMintException(request, e);
	}

	@ExceptionHandler({ MintException.class, InvalidException.class })
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	public ResponseModel processMintException(HttpServletRequest request, MintException e) {
		return handleMintException(request, e);
	}

	@ExceptionHandler(NotPermittedException.class)
	@ResponseStatus(HttpStatus.FORBIDDEN)
	@ResponseBody
	public ResponseModel processMintException(HttpServletRequest request, NotPermittedException e) {
		if (e.getErrorMessage() != null && !e.getErrorMessage().isEmpty()) {
			e.setErrorMessage("unauthorized");
		}
		return handleMintException(request, e);
	}

	@ExceptionHandler(NotReadyException.class)
	@ResponseStatus(HttpStatus.PRECONDITION_FAILED)
	@ResponseBody
	public ResponseModel processMintException(HttpServletRequest request, NotReadyException e) {

		return handleMintException(request, e);
	}

	@ExceptionHandler(ConcurrencyFailureException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	@ResponseBody
	public ResponseModel processConcurencyError(ConcurrencyFailureException ex) {
		return ResponseModel.error(Code.UNKNOWN, ex.getLocalizedMessage());
	}

	@ExceptionHandler(AccessDeniedException.class)
	@ResponseStatus(HttpStatus.FORBIDDEN)
	@ResponseBody
	public ResponseModel processAccessDeniedException(AccessDeniedException e) {
		return ResponseModel.error(Code.NOT_PERMITTED, e.getMessage());
	}

	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	@ResponseBody
	@ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
	public ResponseModel processMethodNotSupportedException(HttpRequestMethodNotSupportedException exception) {
		return ResponseModel.error(Code.NOT_PERMITTED, exception.getMessage());
	}

	@ExceptionHandler(MaxUploadSizeExceededException.class)
	public ResponseModel handleSizeExceededException(HttpServletRequest request, Exception ex) {
		return ResponseModel.error(Code.UPLOAD_EXCEED);
	}

	private ResponseModel handleMintException(HttpServletRequest request, MintException e) {
		var msg = "";
		var langKey = "en";
		if (request != null && request.getHeader("lang") != null && !request.getHeader("lang").isEmpty()) {
			langKey = request.getHeader("lang");
		}
		var currentLocale = Locale.forLanguageTag(langKey);
		if (e.getErrorMessage() != null) {
			msg = msgSource.getMessage(e.getErrorMessage(), new Object[0], e.getErrorMessage(), currentLocale);
		}
		msg = msgSource.getMessage(e.getCode().getMessage(), new Object[] { msg }, currentLocale);
		return ResponseModel.error(e.getCode(), msg);
	}

	private ResponseModel processFieldError(FieldError error) {
		ResponseModel message = null;
		String msg;
		if (error != null) {
			var currentLocale = Locale.forLanguageTag("en");
			message = ResponseModel.error(Code.INVALID, null);
			if (error.getCode().contains("null")) {
				message.setCodeType(Code.MISSING);
			} else if (error.getCode().contains("email")) {
				message.setCodeType(Code.INVALID_EMAIL);
			}
			try {
				msg = msgSource.getMessage(error.getDefaultMessage(), new Object[0], currentLocale);
				msg = msgSource.getMessage(Code.INVALID.getMessage(), new Object[] { msg }, currentLocale);
			} catch (NoSuchMessageException ex) {
				msg = error.getDefaultMessage();
			}
			message.setMessage(msg);
		}
		return message;
	}
}
