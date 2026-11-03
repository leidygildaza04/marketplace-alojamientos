package co.edu.unbosque.marketplace.catalog.dto;

import co.edu.unbosque.marketplace.catalog.domain.PeriodoDisponibilidad;
import java.time.LocalDate;

public record PeriodoDisponibilidadResponse(
        Long id,
        Long alojamientoId,
        LocalDate fechaInicio,
        LocalDate fechaFin,
        String estado) {

    public static PeriodoDisponibilidadResponse desde(PeriodoDisponibilidad periodo) {
        return new PeriodoDisponibilidadResponse(
                periodo.getId(),
                periodo.getAlojamiento().getId(),
                periodo.getFechaInicio(),
                periodo.getFechaFin(),
                periodo.getEstado().name());
    }
}
