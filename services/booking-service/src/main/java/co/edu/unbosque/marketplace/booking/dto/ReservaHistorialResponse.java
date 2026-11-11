package co.edu.unbosque.marketplace.booking.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ReservaHistorialResponse(
        Long id,
        Long userId,
        Long alojamientoId,
        String tituloAlojamiento,
        String ciudad,
        LocalDate checkIn,
        LocalDate checkOut,
        BigDecimal total,
        String estado
) {
}