package com.renova.renova.features.booking;

import com.renova.renova.features.booking.dto.BookingRequest;
import com.renova.renova.features.products.Product;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bookings")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @PostMapping
    public ResponseEntity<Boolean> createBooking(@RequestBody @Valid BookingRequest request) {
        return ResponseEntity.ok(bookingService.finalizeBooking(request));
    }

    @GetMapping
    public ResponseEntity<List<Booking>> listBookings(@RequestParam(name = "customerName", required = false) String customerName) {
        if (customerName != null && !customerName.isBlank()) {
            return ResponseEntity.ok(bookingService.findBookingsByCustomerName(customerName));
        }
        return ResponseEntity.ok(bookingService.listAllBookings());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Booking>> listUserBookings(@PathVariable String userId) {
        return ResponseEntity.ok(bookingService.listBookingsByUser(userId));
    }

    @GetMapping("/{bookingId}/products")
    public ResponseEntity<List<Product>> listProductsByBooking(@PathVariable String bookingId) {
        return ResponseEntity.ok(bookingService.listProductsByBooking(bookingId));
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Booking> findBookingById(@PathVariable String bookingId) {
        Booking booking = bookingService.findBookingById(bookingId);
        return booking != null ? ResponseEntity.ok(booking) : ResponseEntity.notFound().build();
    }

    @PatchMapping("/{bookingId}/status")
    public ResponseEntity<Void> updateBookingStatus(
            @PathVariable String bookingId,
            @RequestParam("newStatus") BookingStatus newStatus) {

        bookingService.updateBookingStatus(bookingId, newStatus);
        return ResponseEntity.ok().build();
    }
}