package com.vishnu.authplatform.shared.error;

import com.vishnu.authplatform.application.application.UpdateApplicationStatusUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleBadRequest(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiError.of("BAD_REQUEST", ex.getMessage()));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiError> handleConflict(IllegalStateException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiError.of("CONFLICT", ex.getMessage()));
    }

    @ExceptionHandler(UpdateApplicationStatusUseCase.ApplicationNotFoundException.class)
    public ResponseEntity<ApiError> handleApplicationNotFound(
            UpdateApplicationStatusUseCase.ApplicationNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiError.of("NOT_FOUND", ex.getMessage()));
    }

    public record ApiError(String code, String message, Instant timestamp) {
        public static ApiError of(String code, String message) {
            return new ApiError(code, message, Instant.now());
        }
    }
}
