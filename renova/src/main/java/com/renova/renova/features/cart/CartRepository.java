package com.renova.renova.features.cart;

import com.renova.renova.features.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, String> {
    Optional<Cart> findByUser(User user);

    @Modifying
    @Query(value = "DELETE FROM cart_products WHERE product_id = :productId", nativeQuery = true)
    void removeProductFromAllCarts(@Param("productId") String productId);
}