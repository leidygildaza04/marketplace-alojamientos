package co.edu.unbosque.marketplace.booking.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record CotizacionResponse(
        Long alojamientoId,
        LocalDate checkIn,
        LocalDate checkOut,
        long noches,
        BigDecimal precioPorNoche,
        BigDecimal subtotalAlojamiento,
        BigDecimal tarifaLimpieza,
        List<ServicioAdicionalCotizadoResponse> serviciosAdicionales,
        BigDecimal totalServiciosAdicionales,
        BigDecimal total
) {
}