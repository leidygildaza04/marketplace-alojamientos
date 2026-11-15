package co.edu.unbosque.marketplace.booking.dto;

import java.math.BigDecimal;

/** Resumen de cotización que consume el frontend en /api/v1/reservas/cotizar. */
public record CotizacionResumenResponse(int noches, BigDecimal subtotalNoches, BigDecimal cargoLimpieza,
        BigDecimal cargoServicio, BigDecimal descuentoEstancia, BigDecimal total, String moneda) { }
