package co.edu.unbosque.marketplace.booking.dto;

import java.time.LocalDate;

public record CrearReservaRequest(
        Long userId,
        Long alojamientoId,
        LocalDate checkIn,
        LocalDate checkOut) {
}
