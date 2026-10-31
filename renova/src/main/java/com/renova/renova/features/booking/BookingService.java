package com.renova.renova.features.booking;

import com.renova.renova.features.booking.dto.BookingRequest;
import com.renova.renova.features.products.Product;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    // garante que tudo salva ou tudo cancela na hora
    @Transactional
    public boolean finalizeBooking(BookingRequest request) {
        Booking booking = request.getBooking();
        List<Product> cart = request.getBasket();

        if (booking == null || cart == null || cart.isEmpty()) {
            throw new IllegalArgumentException("Dados da reserva ou carrinho estão vazios.");
        }

        // define os dados de criação
        booking.setBookingDate(LocalDateTime.now());
        booking.setStatus(BookingStatus.AWAITING_PAYMENT);

        // JPA vai automaticamente pegar esses produtos e fazer os INSERTS na tabela booking_items
        booking.setProducts(cart);

        bookingRepository.save(booking);
        return true;
    }

    public List<Booking> listAllBookings() {
        return bookingRepository.findAll();
    }

    public List<Booking> listBookingsByUser(String userId) {
        return bookingRepository.findByUserId(userId);
    }

    public List<Booking> findBookingsByCustomerName(String customerName) {
        return bookingRepository.findByCustomerNameContaining(customerName);
    }

    public Booking findBookingById(String bookingId) {
        return bookingRepository.findById(bookingId).orElse(null);
    }

    public List<Product> listProductsByBooking(String bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Reserva não encontrada."));
        return booking.getProducts();
    }

    public void updateBookingStatus(String bookingId, BookingStatus newStatus) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Reserva não encontrada."));

        booking.setStatus(newStatus);
        bookingRepository.save(booking);
    }
}