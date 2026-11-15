package co.edu.unbosque.marketplace.booking.client;

import java.math.BigDecimal;
import java.util.List;

public record AlojamientoPreciosResponse(
        Long alojamientoId,
        BigDecimal precioNoche,
        BigDecimal tarifaLimpieza,
        List<ServicioAdicionalPrecioResponse> serviciosAdicionales
) {
    public record ServicioAdicionalPrecioResponse(
            Long id,
            String nombre,
            BigDecimal precio
    ) {
    }
}