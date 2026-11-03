package co.edu.unbosque.marketplace.booking.web;

import co.edu.unbosque.marketplace.booking.repository.ReservaRepository;
import java.time.LocalDate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** Endpoint interno para que Catalog consulte si un periodo coincide con reservas confirmadas. */
@RestController
@RequestMapping("/internal/alojamientos")
public class AccommodationReservationsController {

    private final ReservaRepository reservas;

    public AccommodationReservationsController(ReservaRepository reservas) {
        this.reservas = reservas;
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
}
