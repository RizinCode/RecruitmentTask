package com.test.recruitmenttask.common;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.test.recruitmenttask.common.exception.ReadableException;
import com.test.recruitmenttask.common.model.ApiErrorResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionControllerAdvice {

  @ExceptionHandler(ReadableException.class)
  public ResponseEntity<ApiErrorResponse> handleReadableException(ReadableException e) {
    return ResponseEntity.status(HttpStatus.valueOf(e.getErrorType().getStatusCode()))
        .header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE)
        .body(ApiErrorResponse.from(e.getErrorType()));
  }

}
