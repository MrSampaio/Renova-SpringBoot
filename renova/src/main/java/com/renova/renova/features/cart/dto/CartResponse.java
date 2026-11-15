package com.renova.renova.features.cart.dto;

import com.renova.renova.features.cart.Cart;
import com.renova.renova.features.products.Product;

import java.util.List;

public record CartResponse(
        String cartId,
        List<Product> items,
        int itemCount,
        double totalPrice
) {
    public static CartResponse from(Cart cart) {
        List<Product> items = cart.getProducts() != null ? cart.getProducts() : List.of();
        double total = items.stream()
                .mapToDouble(Product::getPrice)
                .sum();

        return new CartResponse(
                cart.getCartId(),
                items,
                items.size(),
                total
        );
    }
}