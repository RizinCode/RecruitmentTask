package com.test.recruitmenttask.product.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import org.springframework.lang.NonNull;

public record ProductDto(
    Integer id,
    @NotBlank
    String name,
    @NotNull
    BigDecimal price
) {
}
