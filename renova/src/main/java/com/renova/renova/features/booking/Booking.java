package com.renova.renova.features.booking;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.renova.renova.features.products.Product;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "bookings")
@Getter
@Setter
@NoArgsConstructor
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String bookingId;

    @NotBlank(message = "O ID do usuário é obrigatório.")
    @Column(name = "user_id")
    private String userId;

    @JdbcTypeCode(SqlTypes.DECIMAL)
    @Positive(message = "O valor total da reserva deve ser maior que zero.")
    private double totalAmount;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "O método de pagamento é obrigatório.")
    private PaymentMethod paymentMethod;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime bookingDate;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate expirationDate;

    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    @Transient
    private String customerName;

    @ManyToMany
    @JoinTable(
            name = "booking_items", // Nome da sua tabela de junção no banco
            joinColumns = @JoinColumn(name = "booking_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    private List<Product> products = new ArrayList<>();
}