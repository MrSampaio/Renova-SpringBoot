package com.renova.renova.features.cart;

import com.renova.renova.features.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, String> {
    Optional<Cart> findByUser(User user);
}