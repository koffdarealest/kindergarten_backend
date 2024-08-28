package com.fsoft.fsa.kindergarten.config.exception;

import com.fsoft.fsa.kindergarten.config.Translator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Log4j2
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    //dinh nghia cac exception se dc xu ly: MethodArgumentNotValidException va ConstraintViolationException
    @ExceptionHandler({Exception.class})
    public ResponseEntity<Object> handleAll(Exception exception) {
        String message = "Unexpected error";
        String detailMessage = exception.getLocalizedMessage();
        int code = 1;
        System.out.println("======================> handleValidationException 1");
        ErrorResponse response = new ErrorResponse(
                message,
                detailMessage,
                code,
                exception);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(
            NoHandlerFoundException exception, HttpHeaders headers, HttpStatusCode status, WebRequest request
    ) {
        String message = "No handler found for " + exception.getHttpMethod() + " " + exception.getRequestURL();
        String detailMessage = exception.getLocalizedMessage();
        int code = 2;
        System.out.println("======================> handleValidationException 2");
        ErrorResponse response = new ErrorResponse(
                message,
                detailMessage,
                code,
                exception);
        log.error(detailMessage, exception);
        return new ResponseEntity<>(response, status);
    }

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
            HttpRequestMethodNotSupportedException exception,
            HttpHeaders headers, HttpStatusCode status, WebRequest request
    ) {
        String message = getMessageFromHttpRequestMethodNotSuppoertException(exception);
        String detailMessage = exception.getLocalizedMessage();
        int code = 3;
        System.out.println("======================> handleValidationException 3");
        ErrorResponse response = new ErrorResponse(
                message,
                detailMessage,
                code,
                exception);
        log.error(detailMessage, exception);
        return new ResponseEntity<>(response, status);
    }


    private String getMessageFromHttpRequestMethodNotSuppoertException(
            HttpRequestMethodNotSupportedException exception
    ) {
        String message = exception.getMethod() + " method is not supported for this request. Support methods are";
        for (HttpMethod method : exception.getSupportedHttpMethods()) {
            message += " " + method;
        }
        return message;
    }

    //not support media type
    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(
            HttpMediaTypeNotSupportedException exception,
            HttpHeaders headers, HttpStatusCode status, WebRequest request
    ) {
        String message = getMessageFromHttpMediaTypeNotSupportedException(exception);
        String detailMessage = exception.getLocalizedMessage();
        int code = 4;
        System.out.println("======================> handleValidationException 4");
        ErrorResponse response = new ErrorResponse(
                message,
                detailMessage,
                code,
                exception);
        log.error(detailMessage, exception);
        return new ResponseEntity<>(response, status);
    }

    private String getMessageFromHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException exception) {
        String message = exception.getContentType() + " media type is not supported. Supported media types are ";
        for (MediaType method : exception.getSupportedMediaTypes()) {
            message += method + ", ";
        }
        return message.substring(0, message.length() - 2);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException exception,
            HttpHeaders headers, HttpStatusCode status, WebRequest request
    ) {
        String message = exception.getParameterName() + " parameter is missing";
        String detailMessage = exception.getLocalizedMessage();
        int code = 5;
        System.out.println("======================> handleValidationException 5");
        ErrorResponse response = new ErrorResponse(
                message,
                detailMessage,
                code,
                exception);
        log.error(detailMessage, exception);

        return new ResponseEntity<>(response, status);
    }

    //Wrong parameter type
    @ExceptionHandler({MethodArgumentTypeMismatchException.class})
    protected ResponseEntity<Object> handleMethodArgumentTypeMismatch(
            MethodArgumentTypeMismatchException exception
    ) {
        String message = exception.getName() + " should be of type " + exception.getRequiredType().getName();
        String detailMessage = exception.getLocalizedMessage();
        int code = 6;
        System.out.println("======================> handleValidationException 6");
        ErrorResponse response = new ErrorResponse(
                message,
                detailMessage,
                code,
                new Exception());
        log.error(detailMessage, exception);

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    //Bean Validation
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException exception,
            HttpHeaders headers, HttpStatusCode status, WebRequest request
    ) {
        String message = "Validation: Parameters is error!";
        String detailMessage = exception.getLocalizedMessage();
        Map<String, String> errors = new HashMap<>();
        for (ObjectError error : exception.getBindingResult().getAllErrors()) {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        }
        int code = 7;
        System.out.println("======================> handleValidationException 7");
        ErrorResponse response = new ErrorResponse(
                message,
                detailMessage,
                code,
                errors);
        log.error(detailMessage, exception);

        return new ResponseEntity<>(response, status);
    }

    //Bean validation
    @SuppressWarnings("rawtypes")
    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<Object> handleConstrainViolationException(
            ConstraintViolationException exception
    ) {
        String message = "Validation: Parameters is error!";
        String detailMessage = exception.getLocalizedMessage();
        Map<String, String> errors = new HashMap<>();
        for (ConstraintViolation violation : exception.getConstraintViolations()) {
            String fieldName = violation.getPropertyPath().toString();
            String errorMessage = violation.getMessage();
            errors.put(fieldName, errorMessage);
        }
        int code = 8;
        System.out.println("======================> handleValidationException 8");
        ErrorResponse response = new ErrorResponse(
                message,
                detailMessage,
                code,
                errors);
        log.error(detailMessage, exception);

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    //401 unauthrized
    @ExceptionHandler(BadCredentialsException.class)
    protected ResponseEntity<Object> handleBadCredentialsException(
            BadCredentialsException exception
    ) {
        String message = Translator.toLocale("auth.login.fail");
        String detailMessage = exception.getLocalizedMessage();
        int code = 9;
        System.out.println("======================> handleValidationException 9");
        ErrorResponse response = new ErrorResponse(
                message,
                detailMessage,
                code,
                exception);
        log.error(detailMessage, exception);

        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    //404 Resource not found
    @ExceptionHandler(ResourceNotFoundException.class)
    protected ResponseEntity<Object> handleResourceNotFoundException(
            ResourceNotFoundException exception
    ) {
        String message = "Resource not found exception";
        String detailMessage = exception.getLocalizedMessage();
        int code = 10;
        System.out.println("======================> handleValidationException 10");
        ErrorResponse response = new ErrorResponse(
                message,
                detailMessage,
                code,
                exception);
        log.error(detailMessage, exception);

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    //404 Resource not found
    @ExceptionHandler(UserEmailExistException.class)
    protected ResponseEntity<Object> handleUserEmailExistException(
            UserEmailExistException exception
    ) {
        String message = "User email have exist";
        String detailMessage = exception.getLocalizedMessage();
        int code = 11;
        Map<String, String> error = new HashMap<>();
        error.put("email", "Email already exists");
        System.out.println("======================> handleValidationException 11");
        ErrorResponse response = new ErrorResponse(
                message,
                detailMessage,
                code,
                error);
        log.error(detailMessage, exception);

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(PasswordsNotMatchException.class)
    protected ResponseEntity<Object> handlePasswordsNotMatchException(
            PasswordsNotMatchException exception
    ) {
        String message = "Password and Confirm password don?t match. Please try again.";
        String detailMessage = exception.getLocalizedMessage();
        int code = 12;
        System.out.println("======================> handleValidationException 12");
        ErrorResponse response = new ErrorResponse(
                message,
                detailMessage,
                code,
                exception
        );
        log.error(detailMessage, exception);

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoAuthorityException.class)
    protected ResponseEntity<Object> handleNoAuthorityException(
            NoAuthorityException exception
    ) {
        String message = "Don't have permission to access this resource";
        String detailMessage = exception.getLocalizedMessage();
        int code = 13;
        System.out.println("======================> handleValidationException 13");
        ErrorResponse response = new ErrorResponse(
                message,
                detailMessage,
                code,
                exception
        );
        log.error(detailMessage, exception);

        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(StatusChangeException.class)
    protected ResponseEntity<Object> handleStatusChangeException(
            StatusChangeException exception
    ) {
        String message = "Change Status Failed!";
        String detailMessage = exception.getLocalizedMessage();
        int code = 15;
        System.out.println("======================> handleValidationException 15");
        ErrorResponse response = new ErrorResponse(
                message,
                detailMessage,
                code,
                exception
        );
        log.error(detailMessage, exception);

        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(FeeInvalidException.class)
    protected ResponseEntity<Object> handleFeeInvalidException(
            FeeInvalidException exception
    ) {
        String message = "Fee is invalid";
        String detailMessage = exception.getLocalizedMessage();
        int code = 14;
        System.out.println("======================> handleValidationException 14");
        ErrorResponse response = new ErrorResponse(
                message,
                detailMessage,
                code,
                exception
        );
        log.error(detailMessage, exception);

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Object> handleAuthenticationException(AuthenticationException ex) {
        String message = "Authentication failed";
        String detailMessage = ex.getLocalizedMessage();
        int code = 16;
        System.out.println("======================> handleValidationException 16");
        ErrorResponse response = new ErrorResponse(
                message,
                detailMessage,
                code,
                ex);
        log.error(detailMessage, ex);
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<Object> handleAccessDeniedException(AuthorizationDeniedException ex) {
        String message = "Access denied controlled";
        String detailMessage = ex.getLocalizedMessage();
        int code = 17;
        System.out.println("======================> handleValidationException 17");
        ErrorResponse response = new ErrorResponse(
                message,
                detailMessage,
                code,
                ex);
        log.error(detailMessage, ex);
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(ResourceAlreadyExistException.class)
    public ResponseEntity<Object> handleResourceAlreadyExistException(ResourceAlreadyExistException ex) {
        String message = "Resource Already Existed";
        String detailMessage = ex.getLocalizedMessage();
        int code = 18;
        System.out.println("======================> handleValidationException 18");
        ErrorResponse response = new ErrorResponse(
                message,
                detailMessage,
                code,
                ex);
        log.error(detailMessage, ex);
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(EnrollException.class)
    public ResponseEntity<Object> handleEnrollException(EnrollException ex) {
        String message = "Can't enroll or un-enroll";
        String detailMessage = ex.getLocalizedMessage();
        int code = 19;
        System.out.println("======================> handleValidationException 19");
        ErrorResponse response = new ErrorResponse(
                message,
                detailMessage,
                code,
                ex);
        log.error(detailMessage, ex);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }



    //Default exception
//    @ExceptionHandler({ AccountBlockException.class })
//    protected ResponseEntity<Object> handleAccountBlockException(
//            AccountBlockException exception
//    ) {
//        String detailMessage = exception.getLocalizedMessage();
//        int code = 11;
//
//        ErrorResponse response = new ErrorResponse(
//                detailMessage,
//                detailMessage,
//                code,
//                exception);
//
//        log.error(detailMessage, exception);
//
//        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
//    }
}
