package com.renova.renova.features.products;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository repository;

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