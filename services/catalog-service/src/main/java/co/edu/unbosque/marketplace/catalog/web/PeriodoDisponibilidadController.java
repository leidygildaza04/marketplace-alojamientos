package co.edu.unbosque.marketplace.catalog.web;

import co.edu.unbosque.marketplace.catalog.dto.PeriodoDisponibilidadRequest;
import co.edu.unbosque.marketplace.catalog.dto.PeriodoDisponibilidadResponse;
import co.edu.unbosque.marketplace.catalog.service.PeriodoDisponibilidadService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/alojamientos/{alojamientoId}/periodos-disponibilidad")
public class PeriodoDisponibilidadController {

    private final PeriodoDisponibilidadService servicio;

    public PeriodoDisponibilidadController(PeriodoDisponibilidadService servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    public List<PeriodoDisponibilidadResponse> listar(@PathVariable Long alojamientoId) {
        return servicio.listar(alojamientoId);
    }

    @PostMapping
    public ResponseEntity<PeriodoDisponibilidadResponse> crear(@PathVariable Long alojamientoId,
            @RequestBody PeriodoDisponibilidadRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(servicio.crear(alojamientoId, request));
    }

    @PutMapping("/{periodoId}")
    public PeriodoDisponibilidadResponse editar(@PathVariable Long alojamientoId,
            @PathVariable Long periodoId, @RequestBody PeriodoDisponibilidadRequest request) {
        return servicio.editar(alojamientoId, periodoId, request);
    }

    @DeleteMapping("/{periodoId}")
    public ResponseEntity<Void> eliminar(@PathVariable Long alojamientoId,
            @PathVariable Long periodoId) {
        servicio.eliminar(alojamientoId, periodoId);
        return ResponseEntity.noContent().build();
    }
}
