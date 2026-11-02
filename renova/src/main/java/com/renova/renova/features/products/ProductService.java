package com.renova.renova.features.products;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository repository;

    @Autowired
    private ProductImageService productImageService;

    @Transactional
    public Product registerProduct(Product product, MultipartFile image) {
        String imageUrl = productImageService.save(image);

        try {
            product.setImageUrl(imageUrl);

            return repository.save(product);
        } catch (RuntimeException exception) {
            productImageService.delete(imageUrl);
            throw exception;
        }
    }

    public Product registerProduct(Product product) {
        return repository.save(product);
    }

    public List<Product> listProducts() {
        return repository.findAll();
    }

    public List<Product> findProductsByName(String name) {
        return repository.findByProductNameContainingIgnoreCase(name);
    }

    public Product findProductById(String id) {
        // se não encontrar, lança exceção
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto com o ID " + id + "não encontrado."));
    }
}