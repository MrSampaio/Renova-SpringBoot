package com.renova.renova.features.booking.dto;

import com.renova.renova.features.booking.Booking;
import com.renova.renova.features.products.Product;

import java.util.ArrayList;

public class BookingRequest {
    private Booking reserva;
    private ArrayList<Product> carrinho;

    public Booking getReserva() {
        return reserva;
    }

    public void setReserva(Booking reserva) {
        this.reserva = reserva;
    }

    public ArrayList<Product> getCarrinho() {
        return carrinho;
    }

    public void setCarrinho(ArrayList<Product> carrinho) {
        this.carrinho = carrinho;
    }
}
