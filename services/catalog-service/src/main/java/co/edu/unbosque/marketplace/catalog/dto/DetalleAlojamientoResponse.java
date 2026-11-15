package co.edu.unbosque.marketplace.catalog.dto;

import java.math.BigDecimal;
import java.util.List;

public record DetalleAlojamientoResponse(
        Long id,
        String titulo,
        String descripcion,
        String direccion,
        String ciudad,
        BigDecimal precioNoche,
        Integer capacidad,
        String estado,
        String tipoAlojamiento,
        String reglas,
        String politicaCancelacion,
        List<String> servicios,
        List<String> imagenes,
        List<PeriodoDisponibilidadResponse> periodosDisponibilidad,
        BigDecimal promedioCalificacion,
        long cantidadResenas,
        List<ResenaResponse> resenas
) {
}