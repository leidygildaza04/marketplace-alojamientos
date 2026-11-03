package co.edu.unbosque.marketplace.catalog.web;

import co.edu.unbosque.marketplace.catalog.domain.PeriodoDisponibilidad;
import co.edu.unbosque.marketplace.catalog.repository.PeriodoDisponibilidadRepository;
import java.time.LocalDate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/** Endpoints internos llamados por Booking para reclamar/liberar disponibilidad. */
@RestController
@RequestMapping("/internal/alojamientos/{alojamientoId}/periodos-disponibilidad")
public class PeriodosReservaInternalController {

    private final PeriodoDisponibilidadRepository periodos;

    public PeriodosReservaInternalController(PeriodoDisponibilidadRepository periodos) {
        this.periodos = periodos;
    }

    @PostMapping("/reservar")
    @Transactional
    public ResponseEntity<Void> reservar(@PathVariable Long alojamientoId,
            @RequestParam Long reservaId,
            @RequestParam LocalDate fechaInicio,
            @RequestParam LocalDate fechaFin) {
        if (reservaId == null || fechaInicio == null || fechaFin == null
                || !fechaInicio.isBefore(fechaFin)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Reserva y rango de fechas válidos son obligatorios");
        }
        int actualizados = periodos.reservarAtomico(alojamientoId, reservaId, fechaInicio, fechaFin,
                PeriodoDisponibilidad.Estado.DISPONIBLE, PeriodoDisponibilidad.Estado.RESERVADO);
        if (actualizados != 1) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "El periodo no existe, no contiene las fechas solicitadas o ya está reservado");
        }
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/liberar")
    @Transactional
    public ResponseEntity<Void> liberar(@PathVariable Long alojamientoId,
            @RequestParam Long reservaId) {
        // Idempotente: una repetición de la compensación/liberación no libera otro periodo.
        periodos.liberarAtomico(alojamientoId, reservaId,
                PeriodoDisponibilidad.Estado.DISPONIBLE, PeriodoDisponibilidad.Estado.RESERVADO);
        return ResponseEntity.noContent().build();
    }
}
