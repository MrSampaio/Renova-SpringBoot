package com.renova.renova.features.products;

import com.renova.renova.features.products.dto.CreateProductRequest;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository repository;

    @Autowired
    private ProductImageService productImageService;

    public Product registerProduct(CreateProductRequest request, List<MultipartFile> images) {
        Product product = new Product();
        product.setProductName(request.productName());
        product.setDescription(request.description());
        product.setPrice(request.price().doubleValue());
        product.setCategory(ProductCategories.valueOf(request.category().toUpperCase()));
        product.setCondition(ProductConditions.valueOf(request.condition().toUpperCase()));
        product.setSizeType(request.sizeType());
        product.setSize(request.size());
        product.setStatus(ProductStatus.AVAILABLE);

        List<String> savedImageUrls = new ArrayList<>();

        // processa as imagens se existirem
        if (images != null && !images.isEmpty()) {
            for (MultipartFile img : images) {
                savedImageUrls.add(productImageService.save(img));
            }
        }
        product.setImageUrls(savedImageUrls);

        try {
            return repository.save(product);
        } catch (RuntimeException exception) {
            // se o banco falhar, apaga todas as imagens que já tinha feito upload
            savedImageUrls.forEach(url -> productImageService.delete(url));
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

    public Product updateProduct(
            String id,
            CreateProductRequest request,
            List<String> existingImages,
            List<MultipartFile> newImages
    ) {
        Product product = findProductById(id);

        product.setProductName(request.productName());
        product.setDescription(request.description());
        product.setPrice(request.price().doubleValue());
        product.setCategory(ProductCategories.valueOf(request.category().toUpperCase()));
        product.setCondition(ProductConditions.valueOf(request.condition().toUpperCase()));
        product.setSizeType(request.sizeType());
        product.setSize(request.size());

        List<String> keptImages = existingImages != null ? new ArrayList<>(existingImages) : new ArrayList<>();
        List<String> newlySavedUrls = new ArrayList<>();

        if (newImages != null && !newImages.isEmpty()) {
            for (MultipartFile img : newImages) {
                if (!img.isEmpty()) {
                    newlySavedUrls.add(productImageService.save(img));
                }
            }
        }

        List<String> finalImageUrls = new ArrayList<>(keptImages);
        finalImageUrls.addAll(newlySavedUrls);

        if (finalImageUrls.isEmpty()) {
            newlySavedUrls.forEach(url -> productImageService.delete(url));
            throw new IllegalArgumentException("The product must have at least one image.");
        }

        // quais imagens velhas foram tiradas pelo usuário
        List<String> oldImages = product.getImageUrls() != null ? new ArrayList<>(product.getImageUrls()) : List.of();
        List<String> imagesToDelete = oldImages.stream()
                .filter(oldUrl -> !keptImages.contains(oldUrl))
                .toList();

        product.setImageUrls(finalImageUrls);

        try {
            Product updatedProduct = repository.save(product);
            // só apaga depois que o banco confirmar
            imagesToDelete.forEach(url -> productImageService.delete(url));
            return updatedProduct;
        } catch (RuntimeException exception) {
            // rollback dos arquivos novos se o banco falhar
            newlySavedUrls.forEach(url -> productImageService.delete(url));
            throw exception;
        }
    }

    public void deleteProduct(String id) {
        Product product = findProductById(id);

        List<String> imagesToDelete = product.getImageUrls() != null
                ? new ArrayList<>(product.getImageUrls())
                : List.of();

        repository.delete(product);
        repository.flush();

        imagesToDelete.forEach(url -> productImageService.delete(url));
    }
}