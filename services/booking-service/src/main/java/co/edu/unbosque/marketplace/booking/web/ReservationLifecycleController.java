
package co.edu.unbosque.marketplace.booking.web;

import co.edu.unbosque.marketplace.booking.dto.CotizacionRequest;
import co.edu.unbosque.marketplace.booking.dto.CotizacionResponse;
import co.edu.unbosque.marketplace.booking.dto.CheckoutRequest;
import co.edu.unbosque.marketplace.booking.dto.CheckoutResponse;
import co.edu.unbosque.marketplace.booking.dto.ReservaResponse;
import co.edu.unbosque.marketplace.booking.service.CotizacionService;
import co.edu.unbosque.marketplace.booking.service.CheckoutService;
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
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/v1/reservas")
public class ReservationLifecycleController {

    private final ReservaService reservas;
    private final CotizacionService cotizaciones;
    private final CheckoutService checkout;

    public ReservationLifecycleController(ReservaService reservas, CotizacionService cotizaciones,
            CheckoutService checkout) {
        this.reservas = reservas;
        this.cotizaciones = cotizaciones;
        this.checkout = checkout;
    }

    @PostMapping("/cotizar")
    public CotizacionResponse cotizar(@RequestBody CotizacionRequest request) {
        return cotizaciones.cotizar(request);
    }

    @PostMapping("/checkout")
    public ResponseEntity<CheckoutResponse> pagar(@RequestBody CheckoutRequest request,
            Authentication authentication) {
        return ResponseEntity.status(201).body(checkout.pagar(Long.valueOf(authentication.getName()), request));
    }

    @PostMapping("/{reservaId}/cancelar")
    public ResponseEntity<ReservaResponse> cancelar(@PathVariable Long reservaId, Authentication authentication) {
        return ResponseEntity.ok(reservas.cancelar(reservaId, Long.valueOf(authentication.getName())));
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
