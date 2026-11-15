package co.edu.unbosque.marketplace.booking.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ReservaDetalleResponse(
        Long id,
        Long userId,
        Long alojamientoId,
        String tituloAlojamiento,
        String ciudad,
        String direccion,
        LocalDate checkIn,
        LocalDate checkOut,
        String estado,
        BigDecimal total,
        CotizacionResponse cotizacion
) {
}