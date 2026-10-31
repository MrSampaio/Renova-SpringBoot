package com.renova.renova.features.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserUpdateDTO(
        @NotBlank(message = "O nome não pode estar vazio.")
        String name,

        @Size(min = 11, max = 11, message = "O telefone deve ter 11 dígitos.")
        String phone
) {}