package co.edu.unbosque.marketplace.booking.web;

import co.edu.unbosque.marketplace.booking.dto.CotizacionRequest;
import co.edu.unbosque.marketplace.booking.dto.CotizacionResponse;
import co.edu.unbosque.marketplace.booking.dto.CheckoutRequest;
import co.edu.unbosque.marketplace.booking.dto.CheckoutResponse;
import co.edu.unbosque.marketplace.booking.dto.ReservaResponse;
import co.edu.unbosque.marketplace.booking.service.CotizacionService;
import co.edu.unbosque.marketplace.booking.service.CheckoutService;
import co.edu.unbosque.marketplace.booking.service.ReservaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
    }
}
