package com.renova.renova.features.products.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.List;

public record CreateProductRequest(

        @NotBlank(message = "O nome é obrigatório.")
        String productName,

        String description,

        @NotBlank(message = "A categoria é obrigatória.")
        String category,

        @NotNull(message = "O preço é obrigatório.")
        @Positive(message = "O preço deve ser maior que zero.")
        BigDecimal price,

        @NotBlank(message = "O tipo de tamanho é obrigatório.")
        String sizeType,

        @NotBlank(message = "O tamanho é obrigatório.")
        String size,

        @NotBlank(message = "O estado (condição) da roupa é obrigatório.")
        String condition


) {}