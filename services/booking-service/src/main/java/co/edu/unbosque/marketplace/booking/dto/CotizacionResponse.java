package co.edu.unbosque.marketplace.booking.dto;

import java.math.BigDecimal;

public record CotizacionResponse(int noches, BigDecimal subtotalNoches, BigDecimal cargoLimpieza,
        BigDecimal cargoServicio, BigDecimal descuentoEstancia, BigDecimal total, String moneda) { }
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
        BigDecimal porcentajeServicio,
        BigDecimal tarifaServicio,
        BigDecimal porcentajeDescuento,
        BigDecimal descuento,
        BigDecimal total
) {
}
