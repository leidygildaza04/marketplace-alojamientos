package co.edu.unbosque.marketplace.booking.dto;

import java.time.LocalDate;
import java.util.List;

public record CrearCotizacionRequest(
        Long alojamientoId,
        LocalDate checkIn,
        LocalDate checkOut,
        List<Long> serviciosAdicionalesIds
) {
}