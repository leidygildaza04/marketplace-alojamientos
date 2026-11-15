package co.edu.unbosque.marketplace.booking.dto;

import java.util.List;

public record ResenasAlojamientoResponse(double calificacionPromedio, long cantidad, List<ResenaResponse> resenas) {}
