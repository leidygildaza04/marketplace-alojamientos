package co.edu.unbosque.marketplace.booking.dto;

import java.math.BigDecimal;

public record ServicioAdicionalCotizadoResponse(
        Long id,
        String nombre,
        BigDecimal precio
) {
}