package com.test.recruitmenttask.product.model.dto;

import java.util.List;

public record ProductsListDto(
    Integer pageNumber,
    Integer pageSize,
    List<ProductDto> products) {
}
