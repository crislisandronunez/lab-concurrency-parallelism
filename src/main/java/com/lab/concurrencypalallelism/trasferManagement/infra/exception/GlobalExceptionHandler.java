package com.lab.concurrencypalallelism.trasferManagement.infra.exception;

import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
  public ResponseEntity<ErrorResponse> handleOptimisticLock(
      ObjectOptimisticLockingFailureException exception,
      HttpServletRequest request) {

    return buildResponse(
        HttpStatus.CONFLICT,
        "La cuenta fue modificada por otra operación. Se agotaron los reintentos.",
        request
    );
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponse> handleIllegalArgument(
      IllegalArgumentException exception,
      HttpServletRequest request) {

    return buildResponse(HttpStatus.BAD_REQUEST, exception.getMessage(), request);
  }

  @ExceptionHandler(IllegalStateException.class)
  public ResponseEntity<ErrorResponse> handleIllegalState(
      IllegalStateException exception,
      HttpServletRequest request) {

    return buildResponse(HttpStatus.CONFLICT, exception.getMessage(), request);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidation(
      MethodArgumentNotValidException exception,
      HttpServletRequest request) {

    String message = exception.getBindingResult()
        .getFieldErrors()
        .stream()
        .findFirst()
        .map(error -> error.getField() + ": " + error.getDefaultMessage())
        .orElse("Los datos de la solicitud no son válidos");

    return buildResponse(HttpStatus.BAD_REQUEST, message, request);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleUnexpected(
      Exception exception,
      HttpServletRequest request) {

    return buildResponse(
        HttpStatus.INTERNAL_SERVER_ERROR,
        "Ocurrió un error interno al procesar la operación",
        request
    );
  }

  private ResponseEntity<ErrorResponse> buildResponse(
      HttpStatus status,
      String message,
      HttpServletRequest request) {

    ErrorResponse response = new ErrorResponse(
        Instant.now(),
        status.value(),
        status.getReasonPhrase(),
        message,
        request.getRequestURI()
    );

    return ResponseEntity.status(status).body(response);
  }
}