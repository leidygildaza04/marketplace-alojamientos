package co.edu.unbosque.marketplace.catalog.dto;

import java.time.LocalDate;

public record PeriodoDisponibilidadRequest(LocalDate fechaInicio, LocalDate fechaFin) {
}
