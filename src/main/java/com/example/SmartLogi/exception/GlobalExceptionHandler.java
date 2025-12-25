package com.example.SmartLogi.exception;

import com.example.SmartLogi.dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
    public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFound(ResourceNotFoundException ex, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "error", "Resource Not Found",
                "message", ex.getMessage(),
                "timestamp", LocalDateTime.now(),
                "status", HttpStatus.NOT_FOUND,
                "path", req.getRequestURI()
        ));
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Map<String, Object>> handleBusiness(BusinessException ex, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "error", "BAD_REQUEST",
                "message", ex.getMessage(),
                "timestamp", LocalDateTime.now(),
                "status", HttpStatus.BAD_REQUEST,
                "path", req.getRequestURI()
        ));

    }

    @ExceptionHandler(InsufficientInventoryException.class)
    public ResponseEntity<ApiResponse> handleInsufficientInventory(InsufficientInventoryException ex) {
        ApiResponse response = ApiResponse.builder()
                .message(ex.getMessage())
                .status(HttpStatus.CONFLICT.value())
                .build();
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneralException(Exception ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<Map<String, Object>> handleUnauthorizedException(UnauthorizedException ex, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                "error", "Not authenticated",
                "message" , ex.getMessage(),
                "timestamp", LocalDateTime.now(),
                "status", HttpStatus.UNAUTHORIZED.value(),
                "path", req.getRequestURI()

        ));
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<Map<String, Object>> handleForbiddenException(ForbiddenException ex, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                "error", "You don’t have permission",
                "message",ex.getMessage(),
                "timestamp",LocalDateTime.now(),
                "status", HttpStatus.FORBIDDEN.value(),
                "path", req.getRequestURI()
        ));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> handleBadCredentials(BadCredentialsException ex,HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                Map.of(
                        "error", "Bad credentials",
                        "message",ex.getMessage(),
                        "timestamp",LocalDateTime.now(),
                        "status", HttpStatus.UNAUTHORIZED.value(),
                        "path", req.getRequestURI()
                ));
    }



}
