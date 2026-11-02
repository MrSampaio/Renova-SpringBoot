package com.renova.renova.features.products.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record ProductCreateRequest(

        @NotBlank(message = "O nome é obrigatório.")
        String productName,

        @NotBlank(message = "A descrição é obrigatória.")
        String description,

        @NotBlank(message = "A categoria é obrigatória.")
        String category,

        @NotNull(message = "O preço é obrigatório.")
        @Positive(message = "O preço deve ser maior que zero.")
        BigDecimal price,

        @NotBlank(message = "O tamanho é obrigatório.")
        String size
) {}