package com.renova.renova.features.orders;

import com.renova.renova.features.cart.Cart;
import com.renova.renova.features.cart.CartRepository;
import com.renova.renova.features.cart.CartService;
import com.renova.renova.features.orders.dto.CreateOrderRequest;
import com.renova.renova.features.orders.dto.OrderResponse;
import com.renova.renova.features.products.Product;
import com.renova.renova.features.products.ProductRepository;
import com.renova.renova.features.products.ProductService;
import com.renova.renova.features.products.ProductStatus;
import com.renova.renova.features.user.User;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartService cartService;

    @Autowired
    private CartRepository cartRepository;

    @Transactional
    public OrderResponse createOrderFromCart(User user, CreateOrderRequest request) {
        Set<String> uniqueProductIds = new HashSet<>(request.productIds());
        List<Product> selectedProducts = new ArrayList<>();

        for (String productId : uniqueProductIds) {
            Product product = productService.findProductById(productId);

            if (product.getStatus() != ProductStatus.AVAILABLE) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "A peça \"" + product.getProductName() + "\" não está mais disponível para reserva."
                );
            }

            product.setStatus(ProductStatus.RESERVED);
            selectedProducts.add(product);
        }

        productRepository.saveAll(selectedProducts);

        // remove do carrinho só peças selecionadas neste pedido
        Cart cart = cartService.getOrCreateCart(user);
        cart.getProducts().removeIf(item -> uniqueProductIds.contains(item.getProductId()));
        cartRepository.save(cart);

        double total = selectedProducts.stream()
                .mapToDouble(Product::getPrice)
                .sum();

        LocalDateTime now = LocalDateTime.now();

        Order order = new Order();
        order.setOrderNumber(orderRepository.findMaxOrderNumber() + 1);
        order.setUser(user);
        order.setProducts(selectedProducts);
        order.setTotalPrice(total);
        order.setPaymentMethod(request.paymentMethod());
        order.setStatus(OrderStatus.RESERVED);
        order.setCreatedAt(now);
        order.setExpiresAt(now.plusDays(7));

        return OrderResponse.from(orderRepository.save(order));
    }

    public List<OrderResponse> listMyOrders(User user) {
        return orderRepository.findByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(OrderResponse::from)
                .toList();
    }
}