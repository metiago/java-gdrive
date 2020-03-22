package io.zbx.exceptions;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> exception(Exception e) {

        ErrorDetails errorDetails = new ErrorDetails();

        if (e instanceof GoogleJsonResponseException) {
            errorDetails.setStatusCode(((GoogleJsonResponseException) e).getStatusCode());
            errorDetails.setErrorMessage(((GoogleJsonResponseException) e).getStatusMessage());
            return new ResponseEntity<>(errorDetails, HttpStatus.valueOf(((GoogleJsonResponseException) e).getStatusCode()));
        }

        if (e instanceof BadCredentialsException) {
            errorDetails.setStatusCode(401);
            errorDetails.setErrorMessage(e.getMessage());
            return new ResponseEntity<>(((MethodArgumentNotValidException) e).getBindingResult(), HttpStatus.UNAUTHORIZED);
        }

        if (e instanceof MethodArgumentNotValidException) {
            errorDetails.setStatusCode(400);
            errorDetails.setErrorMessage(e.getMessage());
            return new ResponseEntity<>(((MethodArgumentNotValidException) e).getBindingResult(), HttpStatus.BAD_REQUEST);
        }

        if (e instanceof MissingServletRequestParameterException) {
            errorDetails.setStatusCode(400);
            errorDetails.setErrorMessage(e.getMessage());
            return new ResponseEntity<>(((MethodArgumentNotValidException) e).getBindingResult(), HttpStatus.BAD_REQUEST);
        } else {
            errorDetails.setStatusCode(500);
            errorDetails.setErrorMessage("Sorry. Something went wrong. Try again later.");
            return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
