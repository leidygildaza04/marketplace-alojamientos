
package co.edu.unbosque.marketplace.booking.web;

import co.edu.unbosque.marketplace.booking.dto.CrearReservaRequest;
import co.edu.unbosque.marketplace.booking.dto.ReservaResponse;
import co.edu.unbosque.marketplace.booking.dto.ReservaHistorialResponse;
import co.edu.unbosque.marketplace.booking.dto.ReservaDetalleResponse;
import java.util.List;
import co.edu.unbosque.marketplace.booking.service.ReservaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/reservas")
public class ReservationLifecycleController {

    private final ReservaService reservas;

    public ReservationLifecycleController(ReservaService reservas) {
        this.reservas = reservas;
    }

    @GetMapping("/huesped/{userId}")
    public ResponseEntity<List<ReservaHistorialResponse>> historialHuesped(
            @PathVariable Long userId) {

        return ResponseEntity.ok(reservas.historialHuesped(userId));
    }

    @GetMapping("/anfitrion/{hostId}")
    public ResponseEntity<List<ReservaHistorialResponse>> historialAnfitrion(
            @PathVariable Long hostId) {

        return ResponseEntity.ok(reservas.historialAnfitrion(hostId));
    }

    @GetMapping("/{reservaId}")
    public ResponseEntity<ReservaDetalleResponse> detalle(
            @PathVariable Long reservaId) {

        return ResponseEntity.ok(reservas.detalle(reservaId));
    }

    @PostMapping
    public ResponseEntity<ReservaResponse> confirmar(
            @RequestHeader("Idempotency-Key") String claveIdempotencia,
            @RequestBody CrearReservaRequest request) {

        return ResponseEntity.status(201)
                .body(reservas.confirmar(request, claveIdempotencia));
    }

    @PostMapping("/{reservaId}/cancelar")
    public ResponseEntity<ReservaResponse> cancelar(
            @PathVariable Long reservaId) {
        return ResponseEntity.ok(reservas.cancelar(reservaId));
    }
}
