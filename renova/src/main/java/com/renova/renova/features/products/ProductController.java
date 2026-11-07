package com.renova.renova.features.products;

import com.renova.renova.features.products.dto.CreateProductRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/products")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class ProductController {

    @Autowired
    private ProductService productService;

    // POST /products
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Product> createProduct(
            @Valid @ModelAttribute CreateProductRequest request,
            @RequestParam("images") List<MultipartFile> images
    ) {
        Product savedProduct = productService.registerProduct(request, images);
        return ResponseEntity.ok(savedProduct);
    }

    @GetMapping
    public ResponseEntity<List<Product>> listProducts(@RequestParam(value = "nome", required = false) String name) {
        if (name != null && !name.isBlank()) {
            return ResponseEntity.ok(productService.findProductsByName(name));
        }
        return ResponseEntity.ok(productService.listProducts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> findProductById(@PathVariable String id) {
        return ResponseEntity.ok(productService.findProductById(id));
    }
}