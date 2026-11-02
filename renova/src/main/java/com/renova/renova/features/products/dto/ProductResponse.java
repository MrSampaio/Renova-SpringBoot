package com.renova.renova.features.products.dto;

import com.renova.renova.features.products.ProductStatus;
import java.math.BigDecimal;

public record ProductResponse(
        String productId,
        String productName,
        String description,
        String category,
        BigDecimal price,
        String size,
        ProductStatus status,
        String imageUrl
) {
}