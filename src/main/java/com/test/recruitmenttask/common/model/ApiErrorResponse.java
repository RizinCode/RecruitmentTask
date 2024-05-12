package com.test.recruitmenttask.common.model;

import com.test.recruitmenttask.common.helper.ErrorType;

public record ApiErrorResponse(
    int status,
    String error,
    String description,
    String message) {

  public static ApiErrorResponse from(ErrorType errorType, String message) {
    return new ApiErrorResponse(errorType.getStatusCode(), errorType.getKey(), errorType.getDescription(), message);
  }

  public static ApiErrorResponse from(ErrorType errorType) {
    return new ApiErrorResponse(errorType.getStatusCode(), errorType.getKey(), errorType.getDescription(), null);
  }
}
