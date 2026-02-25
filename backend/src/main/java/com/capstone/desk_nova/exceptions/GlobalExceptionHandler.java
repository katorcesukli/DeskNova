package com.capstone.desk_nova.exceptions;

import com.capstone.desk_nova.dto.error.ErrorResponse;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse<String>> handleEntityNotFoundException(EntityNotFoundException e, WebRequest req) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ErrorResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.NOT_FOUND.value(),
                        HttpStatus.NOT_FOUND.getReasonPhrase(),
                        e.getMessage(),
                        req.getDescription(false).replace("uri=", "")
                )
        );
    }
    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<ErrorResponse<String>> handleSecurityException(SecurityException e, WebRequest req) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                new ErrorResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.UNAUTHORIZED.value(),
                        HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                        e.getMessage(),
                        req.getDescription(false).replace("uri=", "")
                )
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse<String>> handleAccessDeniedException(AccessDeniedException e, WebRequest req) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                new ErrorResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.UNAUTHORIZED.value(),
                        HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                        e.getMessage(),
                        req.getDescription(false).replace("uri=", "")
                )
        );
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<ErrorResponse<String>> handleDuplicateKeyException(DuplicateKeyException e, WebRequest req) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                new ErrorResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.CONFLICT.value(),
                        HttpStatus.CONFLICT.getReasonPhrase(),
                        e.getMessage(),
                        req.getDescription(false).replace("uri=", "")
                )
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse<Map<String, String>>> handleValidationException(MethodArgumentNotValidException ex, WebRequest req) {
        Map<String, String> messages = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        e -> Optional.ofNullable(e.getDefaultMessage()).orElse("Validation failed"),
                        // a merge function to keep the first error found
                        (existingValue, newValue) -> existingValue
                ));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ErrorResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.BAD_REQUEST.value(),
                        HttpStatus.BAD_REQUEST.getReasonPhrase(),
                        messages,
                        req.getDescription(false).replace("uri=", "")
                )
        );
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse<String>> handleArgsMismatch(MethodArgumentTypeMismatchException ex, WebRequest req) {
        String message = "";

        // handle enum type
        if (ex.getRequiredType() != null && ex.getRequiredType().isEnum()) {
            String[] options = Arrays
                    .stream(ex.getRequiredType().getEnumConstants())
                    .map(e -> ((Enum<?>) e).name())
                    .toArray(String[]::new);

            if (ex.getValue() != null) {
                message = String.format("'%s' not part of available options: %s",
                        ex.getValue().toString(),
                        Arrays.toString(options));
            } else {
                message = "Null values not allowed";
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(
                            new ErrorResponse<>(
                                    LocalDateTime.now(),
                                    HttpStatus.BAD_REQUEST.value(),
                                    "ENUM ARG MISMATCH",
                                    message,
                                    req.getDescription(false).replace("uri=", "")
                            )
                    );
        }

        // handle generic type mismatch error

        String paramName = ex.getName();
        Class<?> required = ex.getRequiredType();
        Object value = ex.getValue();
        Class<?> argument = (value != null) ? value.getClass() : null;

        message = String.format("Parameter '%s' should be of type %s, but value '%s' is of type %s",
                paramName,
                required != null ? required.getSimpleName() : "?",
                value != null ? value.toString() : "?",
                argument != null ? argument.getSimpleName() : "?"
        );

        String error = String.format("MISMATCH: %s IS NOT %s",
                required != null ? required.getSimpleName().toUpperCase() : "?",
                argument != null ? argument.getSimpleName().toUpperCase() : "?");

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(
                        new ErrorResponse<>(
                                LocalDateTime.now(),
                                HttpStatus.BAD_REQUEST.value(),
                                error,
                                message,
                                req.getDescription(false).replace("uri=", "")
                        )
                );
    }
}
