package com.renova.renova.features.products;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String productId;

    @NotBlank(message = "O nome do produto é obrigatório.")
    private String productName;

    @NotBlank(message = "A descrição do produto é obrigatória.")
    private String description;

    @JdbcTypeCode(SqlTypes.DECIMAL)
    @Positive(message = "Insira um preço válido maior que zero.")
    private double price;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "O status do produto é obrigatório.")
    private ProductStatus status;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

}