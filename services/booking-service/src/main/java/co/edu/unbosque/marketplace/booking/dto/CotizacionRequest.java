package co.edu.unbosque.marketplace.booking.dto;

import java.time.LocalDate;

public record CotizacionRequest(Long alojamientoId, LocalDate checkIn, LocalDate checkOut) { }
