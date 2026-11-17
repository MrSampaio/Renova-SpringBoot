package com.renova.renova.features.orders.dto;

import com.renova.renova.features.orders.PaymentMethod;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CreateOrderRequest(
        @NotEmpty(message = "Selecione ao menos um produto para finalizar o pedido.")
        List<String> productIds,

        @NotNull(message = "Selecione uma forma de pagamento.")
        PaymentMethod paymentMethod
) {}