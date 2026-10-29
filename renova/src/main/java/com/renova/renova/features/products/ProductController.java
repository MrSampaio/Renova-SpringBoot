package com.renova.renova.features.products;

import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/catalog")
public class ProductController {

    // POST /produtos
    @PostMapping("")
    public void createProduct(@RequestBody Product requestProduct) {

        Product product = new Product();
        product.setProductName(requestProduct.getProductName());
        product.setDescription(requestProduct.getDescription());
        product.setPrice(requestProduct.getPrice());
        product.setStatus(requestProduct.getStatus());

        ProductService productService = new ProductService();
        productService.registerProduct(product);
    }

    // GET /produtos  (lists all)
    // GET /produtos?nome=Camisa (filters by name)
    @GetMapping("")
    public ArrayList<Product> listProducts(@RequestParam(value = "nome", required = false) String name) throws Exception {
        ProductService productService = new ProductService();

        // if the user sent a name in the URL, run the filtered search
        if (name != null && !name.isEmpty()) {
            return productService.findProductsByName(name);
        }

        // if nothing was sent, list everything
        return productService.listProducts();
    }

    // GET /produtos/1 (filters by id)
    @GetMapping("/{id}")
    public Product findProductById(@PathVariable String id) throws Exception {
        ProductService productService = new ProductService();
        return productService.findProductById(id);
    }

//    @GetMapping("")
//    public ArrayList<Product> findProductsByName(String searchTerm) throws Exception{
//        ProductService productService = new ProductService();
//
//        return productService.findProductsByName(searchTerm);
//    }
}