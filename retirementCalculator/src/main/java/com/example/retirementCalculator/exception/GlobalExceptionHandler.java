package com.example.retirementCalculator.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.ObjectError;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Centralized exception handler for the Retirement Calculator application.
 * <p>
 * This class uses Spring's {@link ControllerAdvice} to intercept exceptions
 * thrown by controllers and return appropriate HTTP responses with
 * standardized error messages and status codes.
 * </p>
 * <p>
 * Handles:
 * <ul>
 *   <li>Custom exceptions extending {@link RetirementCalculatorException} with detailed error codes.</li>
 *   <li>Validation errors for method arguments, returning a list of validation messages.</li>
 *   <li>Generic exceptions, returning a 500 Internal Server Error response.</li>
 * </ul>
 * </p>
 *
 * @author Priscilla Masunyane
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles custom exceptions specific to the retirement calculator domain.
     *
     * @param ex the RetirementCalculatorException thrown by the application
     * @return ResponseEntity with error details and HTTP 400 Bad Request status
     */
    @ExceptionHandler(RetirementCalculatorException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(RetirementCalculatorException ex) {
        ErrorResponse response = new ErrorResponse(ex.getMessage(), ex.getErrorCode());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles all missing resource exceptions.
     *
     * @param ex the no resource found exception thrown
     * @return ResponseEntity with a generic error message and HTTP 404 Resource not found Error status
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(NoResourceFoundException ex) {
        ErrorResponse response = new ErrorResponse("Resource not found", "RC-404");
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles all other uncaught exceptions.
     *
     * @param ex the generic exception thrown
     * @return ResponseEntity with a generic error message and HTTP 500 Internal Server Error status
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        ErrorResponse response = new ErrorResponse("Internal server error", "RC-500");
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handles validation errors triggered by @Valid annotated controller method arguments.
     *
     * @param ex the MethodArgumentNotValidException containing validation errors
     * @return ResponseEntity with a list of validation error messages and HTTP 400 Bad Request status
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<String> errorMessages = ex.getBindingResult().getAllErrors().stream()
                .map(ObjectError::getDefaultMessage)
                .collect(Collectors.toList());

        return new ResponseEntity<>(errorMessages, HttpStatus.BAD_REQUEST);
    }

    /**
     * Inner class representing a standardized error response body.
     */
    public static class ErrorResponse {
        private final String message;
        private final String code;

        /**
         * Constructs an ErrorResponse with a message and error code.
         *
         * @param message human-readable error message
         * @param code    application-specific error code
         */
        public ErrorResponse(String message, String code) {
            this.message = message;
            this.code = code;
        }

        /**
         * Gets the error message.
         *
         * @return the error message string
         */
        public String getMessage() {
            return message;
        }

        /**
         * Gets the error code.
         *
         * @return the error code string
         */
        public String getCode() {
            return code;
        }
    }
}
