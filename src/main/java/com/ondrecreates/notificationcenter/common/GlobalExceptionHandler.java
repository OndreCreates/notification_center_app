package com.ondrecreates.notificationcenter.common;

import com.ondrecreates.notificationcenter.notification.NotificationNotFoundException;
import com.ondrecreates.notificationcenter.notification.NotificationNotReprocessableException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(error.getField(), error.getDefaultMessage());
        }

        return Map.of(
                "error", "Neplatný požadavek",
                "fields", fieldErrors
        );
    }

    @ExceptionHandler(NotificationNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Object> handleNotFound(NotificationNotFoundException ex) {
        return Map.of("error", ex.getMessage());
    }

    @ExceptionHandler(NotificationNotReprocessableException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, Object> handleConflict(NotificationNotReprocessableException ex) {
        return Map.of("error", ex.getMessage());
    }
}
