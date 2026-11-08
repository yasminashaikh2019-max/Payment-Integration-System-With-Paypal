package com.hulkhiretech.payments.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.hulkhiretech.payments.constant.ErrorCodeEnum;
import com.hulkhiretech.payments.pojo.ErrorResponse;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(PaypalProviderException.class)
    public ResponseEntity<ErrorResponse> handlePaypalProviderException(
            PaypalProviderException exception) {
    	log.error(
    		    "PaypalProviderException occurred. errorCode={}, errorMessage={}, httpStatus={}",
    		    exception.getErrorCode(),
    		    exception.getErrorMessage(),
    		    exception.getHttpStatus());

        ErrorResponse errorResponse = new ErrorResponse();

        errorResponse.setErrorCode(exception.getErrorCode());
        errorResponse.setErrorMessage(exception.getErrorMessage());

        return new ResponseEntity<>
                (errorResponse,exception.getHttpStatus());
    }


@ExceptionHandler(Exception.class)
public ResponseEntity<ErrorResponse> handleGenericException(
        Exception exception) {
	log.error(
		    "Unhandled Exception occured:", exception.getMessage(),
		    exception);

    ErrorResponse errorResponse = new ErrorResponse();

    errorResponse.setErrorCode(ErrorCodeEnum.GENERIC_ERROR.getErrorCode());
    errorResponse.setErrorMessage(ErrorCodeEnum.GENERIC_ERROR.getErrorMessage());

    return new ResponseEntity<>
            (errorResponse,HttpStatus.INTERNAL_SERVER_ERROR);
}
}