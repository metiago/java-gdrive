package io.zbx.exceptions;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.PrintWriter;
import java.io.StringWriter;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> exception(Exception e) {

        ErrorDetails errorDetails = new ErrorDetails();

        if(e instanceof GoogleJsonResponseException) {
            errorDetails.setStatusCode(((GoogleJsonResponseException) e).getStatusCode());
            errorDetails.setErrorMessage("File " + ((GoogleJsonResponseException) e).getStatusMessage());
            return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
        }
        else {
            return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private String getStackTraceAsString(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }
}
