package co.edu.unbosque.marketplace.booking.web;

import co.edu.unbosque.marketplace.booking.repository.ReservaRepository;
import co.edu.unbosque.marketplace.booking.service.ReservaService;
import java.time.LocalDate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** Endpoints internos para coordinación entre Catalog y Booking. */
@RestController
@RequestMapping("/internal/alojamientos")
public class AccommodationReservationsController {

    private final ReservaRepository reservas;
    private final ReservaService reservaService;

    public AccommodationReservationsController(
            ReservaRepository reservas,
            ReservaService reservaService) {
        this.reservas = reservas;
        this.reservaService = reservaService;
    }

    @GetMapping("/{alojamientoId}/reservas-confirmadas/solapamiento")
    public ResponseEntity<Boolean> existeReservaConfirmadaEnPeriodo(
            @PathVariable Long alojamientoId,
            @RequestParam LocalDate fechaInicio,
            @RequestParam LocalDate fechaFin) {

        boolean existe = reservas
                .existsByAlojamientoIdAndEstadoAndCheckInLessThanEqualAndCheckOutGreaterThanEqual(
                        alojamientoId, "CONFIRMADA", fechaFin, fechaInicio);
        return ResponseEntity.ok(existe);
    }

    @PostMapping("/{alojamientoId}/reservas/revision-manual")
    public ResponseEntity<Integer> marcarReservasEnRevision(
            @PathVariable Long alojamientoId) {

        int cantidad = reservaService.marcarReservasFuturasEnRevision(alojamientoId);
        return ResponseEntity.ok(cantidad);
    }
}
