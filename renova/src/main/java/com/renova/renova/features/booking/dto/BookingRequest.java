package com.renova.renova.features.booking.dto;

import com.renova.renova.features.booking.Booking;
import com.renova.renova.features.products.Product;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record BookingRequest(

        @NotNull(message = "Os dados da reserva são obrigatórios.")
        @Valid // adiciona @NotNull e @Positive
        Booking booking,

        @NotEmpty(message = "O carrinho não pode estar vazio.")
        @Valid // valida cada produto na lista
        List<Product> basket

) {}