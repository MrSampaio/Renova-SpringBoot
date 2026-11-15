package com.renova.renova.features.cart;

import com.renova.renova.features.cart.dto.CartResponse;
import com.renova.renova.features.products.Product;
import com.renova.renova.features.products.ProductService;
import com.renova.renova.features.products.ProductStatus;
import com.renova.renova.features.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductService productService;

    public Cart getOrCreateCart(User user) {
        return cartRepository.findByUser(user)
                .orElseGet(() -> cartRepository.save(new Cart(user)));
    }

    public CartResponse getCart(User user) {
        return CartResponse.from(getOrCreateCart(user));
    }

    public CartResponse addProduct(User user, String productId) {
        Cart cart = getOrCreateCart(user);
        Product product = productService.findProductById(productId);

        if (product.getStatus() != ProductStatus.AVAILABLE) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Esta peça não está mais disponível para compra."
            );
        }

        boolean alreadyInCart = cart.getProducts().stream()
                .anyMatch(item -> item.getProductId().equals(product.getProductId()));

        if (alreadyInCart) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Esta peça já está no seu carrinho."
            );
        }

        cart.getProducts().add(product);
        return CartResponse.from(cartRepository.save(cart));
    }

    public CartResponse removeProduct(User user, String productId) {
        Cart cart = getOrCreateCart(user);
        cart.getProducts().removeIf(item -> item.getProductId().equals(productId));
        return CartResponse.from(cartRepository.save(cart));
    }

    public CartResponse clearCart(User user) {
        Cart cart = getOrCreateCart(user);
        cart.getProducts().clear();
        return CartResponse.from(cartRepository.save(cart));
    }
}