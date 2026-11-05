
package com.lab.concurrencypalallelism.marketInformation.infra.exception;

import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.TransientDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice(
    basePackages = "com.lab.concurrencypalallelism.marketInformation"
)
public class GlobalExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(CompletionException.class)
  public ResponseEntity<ErrorResponse> handleCompletion(
      CompletionException exception,
      HttpServletRequest request) {

    Throwable cause = exception.getCause();

    if (cause instanceof TimeoutException timeoutException) {
      return handleTimeout(timeoutException, request);
    }

    if (cause instanceof TransientDataAccessException databaseException) {
      return handleDatabaseError(databaseException, request);
    }

    if (cause instanceof IllegalArgumentException illegalArgumentException) {
      return handleIllegalArgument(illegalArgumentException, request);
    }

    log.error("Error durante la ejecución de CompletableFuture", exception);

    return buildResponse(
        HttpStatus.INTERNAL_SERVER_ERROR,
        "Ocurrió un error al obtener información de mercado",
        request
    );
  }

  @ExceptionHandler(TimeoutException.class)
  public ResponseEntity<ErrorResponse> handleTimeout(
      TimeoutException exception,
      HttpServletRequest request) {

    log.error("Tiempo de espera agotado al obtener información de mercado", exception);

    return buildResponse(
        HttpStatus.GATEWAY_TIMEOUT,
        "Tiempo de espera agotado al obtener información de mercado",
        request
    );
  }

  @ExceptionHandler(TransientDataAccessException.class)
  public ResponseEntity<ErrorResponse> handleDatabaseError(
      TransientDataAccessException exception,
      HttpServletRequest request) {

    log.error("Error de PostgreSQL después de agotar los reintentos", exception);

    return buildResponse(
        HttpStatus.SERVICE_UNAVAILABLE,
        "No se pudo obtener la información de PostgreSQL",
        request
    );
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponse> handleIllegalArgument(
      IllegalArgumentException exception,
      HttpServletRequest request) {

    return buildResponse(
        HttpStatus.BAD_REQUEST,
        exception.getMessage(),
        request
    );
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

    return buildResponse(
        HttpStatus.BAD_REQUEST,
        message,
        request
    );
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleUnexpected(
      Exception exception,
      HttpServletRequest request) {

    log.error("Error inesperado al obtener información de mercado", exception);

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
