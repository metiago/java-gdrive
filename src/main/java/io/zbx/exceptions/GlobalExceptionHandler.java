package io.zbx.exceptions;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> exception(Exception e) {

        ErrorDetails errorDetails = new ErrorDetails();
        // TODO Add exception for missing request params a nd validation
        if (e instanceof GoogleJsonResponseException) {
            errorDetails.setStatusCode(((GoogleJsonResponseException) e).getStatusCode());
            errorDetails.setErrorMessage(((GoogleJsonResponseException) e).getStatusMessage());
            return new ResponseEntity<>(errorDetails, HttpStatus.valueOf(((GoogleJsonResponseException) e).getStatusCode()));
        } else {
            errorDetails.setStatusCode(500);
            errorDetails.setErrorMessage("Sorry. Something went wrong. Try again later.");
            return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
