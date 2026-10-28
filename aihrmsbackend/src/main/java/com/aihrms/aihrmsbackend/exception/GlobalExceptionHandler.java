package com.aihrms.aihrmsbackend.exception;

import com.aihrms.aihrmsbackend.dto.response.CustomResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<CustomResponse> handleNotFound(ResourceNotFoundException ex) {
        CustomResponse body = new CustomResponse(HttpStatus.NOT_FOUND.value(), null, ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<CustomResponse> handleBadRequest(IllegalArgumentException ex) {
        CustomResponse body = new CustomResponse(HttpStatus.BAD_REQUEST.value(), null, ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<CustomResponse> handleIllegalState(IllegalStateException ex) {
        CustomResponse body = new CustomResponse(HttpStatus.BAD_REQUEST.value(), null, ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CustomResponse> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .orElse("Validation failed");
        CustomResponse body = new CustomResponse(HttpStatus.BAD_REQUEST.value(), null, message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CustomResponse> handleGeneric(Exception ex) {
        CustomResponse body = new CustomResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                null,
                ex.getMessage() != null ? ex.getMessage() : "Unexpected error"
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}