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

    // edição de produtos
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Product> updateProduct(
            @PathVariable String id,
            @Valid @ModelAttribute CreateProductRequest request,
            @RequestParam(value = "existingImages", required = false) List<String> existingImages,
            @RequestParam(value = "images", required = false) List<MultipartFile> images
    ) {
        Product updatedProduct = productService.updateProduct(id, request, existingImages, images);
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable String id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}