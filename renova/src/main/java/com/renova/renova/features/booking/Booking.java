package com.renova.renova.features.booking;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String bookingId;

    private String userId;

    @JdbcTypeCode(SqlTypes.DECIMAL) // column is DECIMAL(10,2) in MySQL
    private double totalAmount;

    private String paymentMethod;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime bookingDate;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate expirationDate;

    private String status;

    @Transient
    private String customerName;

    public String getBookingId() {
        return bookingId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        if (userId == null || userId.isBlank()) throw new IllegalArgumentException("Error assigning the user ID.");
        this.userId = userId;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public LocalDateTime getBookingDate() {
        return bookingDate;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public String getStatus() {
        return status;
    }

    public void setBookingId(String bookingId) {
        if (bookingId == null || bookingId.isBlank()) {
            throw new IllegalArgumentException("Error assigning the booking ID.");
        } else {
            this.bookingId = bookingId;
        }
    }

    public void setTotalAmount(double totalAmount) {
        if (totalAmount <= 0) {
            throw new IllegalArgumentException("Error assigning the booking amount.");
        } else {
            this.totalAmount = totalAmount;
        }
    }

    public void setPaymentMethod(String paymentMethod) {
        if (!paymentMethod.toUpperCase().equals("DEBIT") && !paymentMethod.toUpperCase().equals("CASH") && !paymentMethod.toUpperCase().equals("PIX")) {
            throw new IllegalArgumentException("Error assigning the booking payment method.");
        } else {
            this.paymentMethod = paymentMethod;
        }
    }

    public void setBookingDate(LocalDateTime bookingDate) {
        if (bookingDate == null) {
            throw new IllegalArgumentException("Error assigning the booking date.");
        } else {
            this.bookingDate = bookingDate;
        }
    }

    public void setExpirationDate(LocalDate expirationDate) {
        if (expirationDate == null) {
            throw new IllegalArgumentException("Error assigning the expiration date.");
        } else {
            this.expirationDate = expirationDate;
        }
    }

    public void setStatus(String status) {
        // CHANGED: values now match the English ENUM of the database
        // (they were AGUARDANDO PAGAMENTO / CONCLUÍDA / CANCELADA).
        if (!status.toUpperCase().equals("AWAITING_PAYMENT") && !status.toUpperCase().equals("COMPLETED") && !status.toUpperCase().equals("CANCELLED")) {
            throw new IllegalArgumentException("Error assigning the booking status.");
        } else {
            this.status = status;
        }
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
}