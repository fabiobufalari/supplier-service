package com.bufalari.supplier.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Global exception handler for the Supplier Service API.
 * Manipulador global de exceções para a API do Serviço de Fornecedores.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /** Standard structure for API error responses. */
    public record ErrorResponse(LocalDateTime timestamp, int status, String error, String message, String path, Object details) {}

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
        String message = "Validation failed. / Falha na validação.";
        log.warn("Validation error: {} - Path: {}", errors, request.getRequestURI());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, message, request.getRequestURI(), errors);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException ex, HttpServletRequest request) {
        String errors = ex.getConstraintViolations().stream().map(cv -> cv.getPropertyPath() + ": " + cv.getMessage()).collect(Collectors.joining("; "));
        String message = "Constraint violation: " + errors + " / Violação de restrição: " + errors;
        log.warn("Constraint violation: {} - Path: {}", errors, request.getRequestURI());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, message, request.getRequestURI(), null);
    }

    @ExceptionHandler({IllegalArgumentException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ErrorResponse> handleBadDataExceptions(Exception ex, HttpServletRequest request) {
         String message = "Invalid request data: " + ex.getMessage() + " / Dados da requisição inválidos: " + ex.getMessage();
         log.warn("Bad request data: {} - Path: {}", ex.getMessage(), request.getRequestURI());
         return buildErrorResponse(HttpStatus.BAD_REQUEST, message, request.getRequestURI(), null);
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleConflictExceptions(ResourceAlreadyExistsException ex, HttpServletRequest request) {
        log.warn("Conflict error: {} - Path: {}", ex.getMessage(), request.getRequestURI());
        return buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage(), request.getRequestURI(), "Resource already exists or violates unique constraint.");
    }

     @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex, HttpServletRequest request) {
        log.warn("Resource not found: {} - Path: {}", ex.getMessage(), request.getRequestURI());
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request.getRequestURI(), "Verify the resource identifier.");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, HttpServletRequest request) {
        String message = "An unexpected internal error occurred. / Ocorreu um erro interno inesperado.";
        log.error("Unexpected internal server error - Path: {}", request.getRequestURI(), ex);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, message, request.getRequestURI(), ex.getClass().getSimpleName());
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(HttpStatus status, String message, String path, Object details) {
        ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now(), status.value(), status.getReasonPhrase(), message, path, details);
        return new ResponseEntity<>(errorResponse, status);
    }
}