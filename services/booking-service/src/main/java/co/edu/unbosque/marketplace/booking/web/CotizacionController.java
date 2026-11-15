package co.edu.unbosque.marketplace.booking.web;

import co.edu.unbosque.marketplace.booking.dto.CrearCotizacionRequest;
import co.edu.unbosque.marketplace.booking.dto.CotizacionResponse;
import co.edu.unbosque.marketplace.booking.service.CotizacionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/cotizaciones")
public class CotizacionController {

    private final CotizacionService cotizaciones;

    public CotizacionController(CotizacionService cotizaciones) {
        this.cotizaciones = cotizaciones;
    }

    @PostMapping
    public ResponseEntity<CotizacionResponse> calcular(
            @RequestBody CrearCotizacionRequest request) {
        return ResponseEntity.ok(cotizaciones.calcular(request));
    }
}