package co.edu.unbosque.marketplace.catalog.dto;

import java.math.BigDecimal;

public record ServicioAdicionalPrecioResponse(
        Long id,
        String nombre,
        BigDecimal precio
) {
}