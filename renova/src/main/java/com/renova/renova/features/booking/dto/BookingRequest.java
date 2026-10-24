package com.renova.renova.features.booking.dto;

import com.renova.renova.features.booking.Booking;
import com.renova.renova.features.products.Product;

import java.util.ArrayList;

public class BookingRequest {
    private Booking booking;
    private ArrayList<Product> basket;

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    public ArrayList<Product> getBasket() {
        return basket;
    }

    public void setBasket(ArrayList<Product> basket) {
        this.basket = basket;
    }
}
