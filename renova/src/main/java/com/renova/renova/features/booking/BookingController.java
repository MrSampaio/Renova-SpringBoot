package com.renova.renova.features.booking;

import com.renova.renova.features.booking.dto.BookingRequest;
import com.renova.renova.features.products.Product;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/reservas")
@CrossOrigin(origins = "*")
public class BookingController {

    // POST /reservas
    @PostMapping
    public boolean createBooking(@RequestBody BookingRequest request) {
        BookingService bookingService = new BookingService();
        return bookingService.finalizeBooking(request);
    }

    // GET /reservas
    // GET /reservas?nomeCliente=Sampaio
    @GetMapping
    public ArrayList<Booking> listBookings(@RequestParam(name = "nomeCliente", required = false) String customerName) {
        BookingService bookingService = new BookingService();

        if (customerName != null && !customerName.isEmpty()) {
            return bookingService.findBookingsByCustomerName(customerName);
        }
        return bookingService.listAllBookings();
    }

    // GET /reservas/usuario/1
    @GetMapping("/usuario/{userId}")
    public ArrayList<Booking> listUserBookings(@PathVariable String userId) {
        BookingService bookingService = new BookingService();
        return bookingService.listBookingsByUser(userId);
    }

    // GET /reservas/5/produtos
    @GetMapping("/{bookingId}/produtos")
    public ArrayList<Product> listProductsByBooking(@PathVariable String bookingId) {
        BookingService bookingService = new BookingService();
        return bookingService.listProductsByBooking(bookingId);
    }

    // GET /reservas/5
    // GET /reservas/5?idUsuario=1
    @GetMapping("/{bookingId}")
    public Booking findBookingById(@PathVariable String bookingId, @RequestParam(name = "idUsuario", required = false) String userId) {
        BookingService bookingService = new BookingService();

        if (userId != null) {
            return bookingService.findBookingById(bookingId, userId);
        }
        return bookingService.findAnyBookingById(bookingId);
    }

    // PATCH /reservas/5/status?novoStatus=Concluída
    @PatchMapping("/{bookingId}/status")
    public void updateBookingStatus(@PathVariable String bookingId, @RequestParam("novoStatus") String newStatus) {
        BookingService bookingService = new BookingService();
        bookingService.updateBookingStatus(bookingId, newStatus);
    }
}