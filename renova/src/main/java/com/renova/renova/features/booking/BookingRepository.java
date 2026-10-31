package com.renova.renova.features.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, String> {

    // lista todas as reservas de um usuário específico
    List<Booking> findByUserId(String userId);

    @Query(value = "SELECT b.*, u.name as customer_name FROM bookings b JOIN users u ON b.user_id = u.user_id WHERE u.name LIKE %:customerName%", nativeQuery = true)
    List<Booking> findByCustomerNameContaining(@Param("customerName") String customerName);
}
