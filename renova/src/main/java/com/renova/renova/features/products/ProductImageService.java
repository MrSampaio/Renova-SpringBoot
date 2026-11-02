package com.renova.renova.features.products;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
public class ProductImageService {

    // limite das imagens
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    // tipos de arquivos aceitos
    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/webp"
    );

    // mapeia as extensões
    private static final Map<String, String> EXTENSIONS = Map.of(
            "image/jpeg", ".jpg",
            "image/png", ".png",
            "image/webp", ".webp"
    );

    // diretórios + url da imagem
    private final Path productDirectory;
    private final String productUrl;

    // puxa a url e o diretório pelo app properties
    public ProductImageService(
            @Value("${app.upload.product-directory}") String productDirectory,
            @Value("${app.upload.product-url}") String productUrl
    ) {
        this.productDirectory = Path.of(productDirectory)
                .toAbsolutePath()
                .normalize();

        this.productUrl = productUrl;
    }

    // tenta instanciar o diretório pra adicionar as imagens
    @PostConstruct
    public void initializeDirectory() {
        try {
            Files.createDirectories(productDirectory);
        } catch (IOException exception) {
            throw new IllegalStateException("Error when trying to create the image directory.", exception);
        }
    }

    // função para verificar e salvar as imagens
    public String save(MultipartFile image) {
        validate(image);

        String contentType = image.getContentType();
        String extension = EXTENSIONS.get(contentType);
        String generatedFileName = UUID.randomUUID() + extension;

        Path destination = productDirectory
                .resolve(generatedFileName)
                .normalize();

        if (!destination.startsWith(productDirectory)) {
            throw new IllegalArgumentException("The filepath is invalid.");
        }

        try (InputStream inputStream = image.getInputStream()) {
            Files.copy(
                    inputStream,
                    destination,
                    StandardCopyOption.REPLACE_EXISTING
            );
        } catch (IOException exception) {
            throw new IllegalStateException("Failed when trying to save the image.", exception);
        }

        return productUrl + "/" + generatedFileName;
    }

    // função para deletar as imagens
    public void delete(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) {
            return;
        }

        String fileName = Path.of(imageUrl).getFileName().toString();

        Path filePath = productDirectory
                .resolve(fileName)
                .normalize();

        if (!filePath.startsWith(productDirectory)) {
            throw new IllegalArgumentException("The filepath is invalid.");
        }

        try {
            Files.deleteIfExists(filePath);
        } catch (IOException exception) {
            throw new IllegalStateException("Error when trying to delete image.", exception);
        }
    }

    private void validate(MultipartFile image) {
        if (image == null || image.isEmpty()) {
            throw new IllegalArgumentException("The product image cannot be null");
        }

        if (image.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("The image must be up to 5MB.");
        }

        String contentType = image.getContentType();

        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new IllegalArgumentException("Invalid format. Upload JPG, PNG or WEBP images.");
        }
    }
}