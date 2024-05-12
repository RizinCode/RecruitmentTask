package com.test.recruitmenttask.common.exception;

import com.test.recruitmenttask.common.helper.ErrorType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ReadableException extends RuntimeException {
  private final transient ErrorType errorType;

}
