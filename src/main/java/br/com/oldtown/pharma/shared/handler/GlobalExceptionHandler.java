package br.com.oldtown.pharma.shared.handler;

import br.com.oldtown.pharma.shared.exception.BadRequestException;
import br.com.oldtown.pharma.shared.exception.ConflictException;
import br.com.oldtown.pharma.shared.exception.InvalidPriceException;
import br.com.oldtown.pharma.shared.exception.NotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<HttpErrorResponse> handleBadRequest(BadRequestException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        HttpErrorResponse response = new HttpErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI(),
                null
        );
        ex.printStackTrace();

        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<HttpErrorResponse> handleNotFound(NotFoundException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;

        HttpErrorResponse response = new HttpErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI(),
                null
        );
        ex.printStackTrace();
        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<HttpErrorResponse> handleConflict(ConflictException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.CONFLICT;

        HttpErrorResponse response = new HttpErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI(),
                null
        );
        ex.printStackTrace();
        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<HttpErrorResponse> handleValidation(MethodArgumentNotValidException ex,
                                                              HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        List<String> details = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::formatFieldError)
                .toList();

        HttpErrorResponse response = new HttpErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                "Validation error.",
                request.getRequestURI(),
                details
        );
        ex.printStackTrace();
        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<HttpErrorResponse> handleConstraintViolation(ConstraintViolationException ex,
                                                                       HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        List<String> details = ex.getConstraintViolations()
                .stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .toList();

        HttpErrorResponse response = new HttpErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                "Validation error.",
                request.getRequestURI(),
                details
        );
        ex.printStackTrace();
        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<HttpErrorResponse> handleUnreadableMessage(HttpMessageNotReadableException ex,
                                                                     HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        HttpErrorResponse response = new HttpErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                "Malformed request body.",
                request.getRequestURI(),
                null
        );
        ex.printStackTrace();
        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<HttpErrorResponse> handleGenericException(
            Exception ex,
            HttpServletRequest request) {

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        HttpErrorResponse response = new HttpErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                "An unexpected internal error occurred.",
                request.getRequestURI(),
                null
        );
        ex.printStackTrace();
        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(InvalidPriceException.class)
    public ResponseEntity<HttpErrorResponse> handleBadRequest(InvalidPriceException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        HttpErrorResponse response = new HttpErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI(),
                null
        );
        ex.printStackTrace();

        return ResponseEntity.status(status).body(response);
    }

    private String formatFieldError(FieldError error) {
        return error.getField() + ": " + error.getDefaultMessage();
    }
}
