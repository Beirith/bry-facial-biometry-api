package com.isaquebeirith.bry_facial_biometry_api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class AppExceptionHandler {
    @ExceptionHandler(DuplicateCpfException.class)
    public ResponseEntity<Map<String, Object>> handleDuplicateCpf(DuplicateCpfException e) {
        return generateResponseEntity(HttpStatus.CONFLICT, e.getMessage());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleUserNotFound(UserNotFoundException e) {
        return generateResponseEntity(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(InvalidPictureException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidPicture(InvalidPictureException e) {
        return generateResponseEntity(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        List<String> errorsList = e.getBindingResult().getFieldErrors().stream().
                map(FieldError::getDefaultMessage).toList();

        String errorsMessages = String.join(", ", errorsList);

        return generateResponseEntity(HttpStatus.BAD_REQUEST, errorsMessages);
    }

    @ExceptionHandler(InvalidUpdateException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidUpdate(InvalidUpdateException e) {
        return generateResponseEntity(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    private ResponseEntity<Map<String, Object>> generateResponseEntity(HttpStatus status, Object message) {
        Map<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("status", status.value());
        bodyMap.put("error", status.getReasonPhrase());
        bodyMap.put("message", message);

        return new ResponseEntity<>(bodyMap, status);
    }
}
