package co.edu.unbosque.marketplace.booking.dto;

import java.time.LocalDate;

public record CheckoutRequest(Long alojamientoId, LocalDate checkIn, LocalDate checkOut,
        String numeroTarjeta, String vencimiento, String cvv, String titular) { }
