package co.edu.unbosque.marketplace.catalog.dto;

import java.math.BigDecimal;
import java.util.List;

public record AlojamientoPreciosResponse(
        Long alojamientoId,
        BigDecimal precioNoche,
        BigDecimal tarifaLimpieza,
        List<ServicioAdicionalPrecioResponse> serviciosAdicionales
) {
}