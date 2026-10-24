package com.renova.renova.features.products;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String productId;

    private String productName;

    private String description;

    @JdbcTypeCode(SqlTypes.DECIMAL)
    private double price;

    @JsonProperty("status")
    private String status;

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        if (productId == null || productId.isBlank()) {
            throw new IllegalArgumentException("Error assigning the product ID.");
        } else {
            this.productId = productId;
        }
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        if (productName == null || productName.isEmpty() || productName.length() <= 0) {
            throw new IllegalArgumentException("The 'product name' field is required!");
        } else {
            this.productName = productName;
        }
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        if (description == null || description.isEmpty() || description.length() <= 0) {
            throw new IllegalArgumentException("The 'description' field is required!");
        } else {
            this.description = description;
        }
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        if (price <= 0.0) {
            throw new IllegalArgumentException("Enter a valid price for the product.");
        } else {
            this.price = price;
        }
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        if (!status.toUpperCase().equals("AVAILABLE") && !status.toUpperCase().equals("RESERVED") && !status.toUpperCase().equals("SOLD")) {
            throw new IllegalArgumentException("Error assigning the product status.");
        } else {
            this.status = status;
        }
    }
}