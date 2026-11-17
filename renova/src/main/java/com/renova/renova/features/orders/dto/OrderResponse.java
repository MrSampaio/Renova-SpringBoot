package com.renova.renova.features.orders.dto;

import com.renova.renova.features.orders.Order;
import com.renova.renova.features.orders.OrderStatus;
import com.renova.renova.features.orders.PaymentMethod;
import com.renova.renova.features.products.Product;

import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        String orderId,
        Long orderNumber,
        String customerName,
        String customerEmail,
        String customerPhone,
        List<Product> items,
        double totalPrice,
        PaymentMethod paymentMethod,
        OrderStatus status,
        LocalDateTime createdAt,
        LocalDateTime expiresAt
) {
    public static OrderResponse from(Order order) {
        return new OrderResponse(
                order.getOrderId(),
                order.getOrderNumber(),
                order.getUser().getName(),
                order.getUser().getEmail(),
                order.getUser().getPhone(),
                order.getProducts() != null ? order.getProducts() : List.of(),
                order.getTotalPrice(),
                order.getPaymentMethod(),
                order.getStatus(),
                order.getCreatedAt(),
                order.getExpiresAt()
        );
    }
}