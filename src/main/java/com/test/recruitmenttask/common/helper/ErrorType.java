package com.test.recruitmenttask.common.helper;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorType {

  PRODUCT_NOT_FOUND(404, "productNotFound", "Product not found"),
  PRODUCT_NAME_ALREADY_EXISTS(409, "productNameAlreadyExists", "Product name already exists");

  private final int statusCode;
  private final String key;
  private final String description;
}
