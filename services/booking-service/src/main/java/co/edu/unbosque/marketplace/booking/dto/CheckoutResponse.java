package co.edu.unbosque.marketplace.booking.dto;

import java.math.BigDecimal;

public record CheckoutResponse(Long reservaId, String codigoReserva, String estadoReserva,
        BigDecimal total, String estadoPago, String referenciaPago, String mensaje) { }
