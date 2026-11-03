package co.edu.unbosque.marketplace.booking.web;

import co.edu.unbosque.marketplace.booking.dto.CrearReservaRequest;
import co.edu.unbosque.marketplace.booking.dto.ReservaResponse;
import co.edu.unbosque.marketplace.booking.service.ReservaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/reservas")
public class ReservationLifecycleController {

    private final ReservaService reservas;

    public ReservationLifecycleController(ReservaService reservas) {
        this.reservas = reservas;
    }

    @PostMapping
    public ResponseEntity<ReservaResponse> confirmar(@RequestBody CrearReservaRequest request) {
        return ResponseEntity.status(201).body(reservas.confirmar(request));
    }

    @PostMapping("/{reservaId}/cancelar")
    public ResponseEntity<ReservaResponse> cancelar(@PathVariable Long reservaId) {
        return ResponseEntity.ok(reservas.cancelar(reservaId));
    }
}
