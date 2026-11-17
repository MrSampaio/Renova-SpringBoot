package com.renova.renova.features.orders;

import com.renova.renova.features.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, String> {

    List<Order> findByUserOrderByCreatedAtDesc(User user);

    List<Order> findAllByOrderByCreatedAtDesc();

    @Query("SELECT COALESCE(MAX(o.orderNumber), 1000) FROM Order o")
    Long findMaxOrderNumber();
}