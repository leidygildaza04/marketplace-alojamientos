package co.edu.unbosque.marketplace.catalog.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record BusquedaAlojamientoRequest(
        String ciudad,
        Integer capacidad,
        BigDecimal precioMinimo,
        BigDecimal precioMaximo,
        String tipoAlojamiento,
        List<String> servicios,
        LocalDate fechaInicio,
        LocalDate fechaFin
) {
}
