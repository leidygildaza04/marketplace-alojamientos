package co.edu.unbosque.marketplace.booking.dto;

import java.math.BigDecimal;

public record CotizacionResponse(int noches, BigDecimal subtotalNoches, BigDecimal cargoLimpieza,
        BigDecimal cargoServicio, BigDecimal descuentoEstancia, BigDecimal total, String moneda) { }
