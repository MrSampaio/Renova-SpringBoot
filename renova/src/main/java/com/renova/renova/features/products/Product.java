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

import java.util.ArrayList;
import java.util.List;

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

    private String description;

    @Positive(message = "Insira um preço válido maior que zero.")
    private double price;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "O status do produto é obrigatório.")
    private ProductStatus status;

    @Enumerated(EnumType.STRING)
    private ProductCategories category;

    @Enumerated(EnumType.STRING)
    @Column(name = "product_condition")
    private ProductConditions condition;

    private String sizeType;

    @NotBlank(message = "O tamanho do produto é obrigatório.")
    private String size;

    @ElementCollection
    @CollectionTable(name = "product_images", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "image_url")
    private List<String> imageUrls = new ArrayList<>();
}