package co.edu.unbosque.marketplace.catalog.web;

import co.edu.unbosque.marketplace.catalog.dto.PeriodoDisponibilidadRequest;
import co.edu.unbosque.marketplace.catalog.dto.PeriodoDisponibilidadResponse;
import co.edu.unbosque.marketplace.catalog.repository.AlojamientoRepository;
import co.edu.unbosque.marketplace.catalog.service.PeriodoDisponibilidadService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/alojamientos/{alojamientoId}/periodos-disponibilidad")
public class PeriodoDisponibilidadController {

    private final PeriodoDisponibilidadService servicio;
    private final AlojamientoRepository alojamientos;

    public PeriodoDisponibilidadController(PeriodoDisponibilidadService servicio, AlojamientoRepository alojamientos) {
        this.servicio = servicio;
        this.alojamientos = alojamientos;
    }

    @GetMapping
    public List<PeriodoDisponibilidadResponse> listar(@PathVariable Long alojamientoId, Authentication authentication) {
        validarPropietario(alojamientoId, authentication);
        return servicio.listar(alojamientoId);
    }

    @PostMapping
    public ResponseEntity<PeriodoDisponibilidadResponse> crear(@PathVariable Long alojamientoId,
            @RequestBody PeriodoDisponibilidadRequest request, Authentication authentication) {
        validarPropietario(alojamientoId, authentication);
        return ResponseEntity.status(HttpStatus.CREATED).body(servicio.crear(alojamientoId, request));
    }

    @PutMapping("/{periodoId}")
    public PeriodoDisponibilidadResponse editar(@PathVariable Long alojamientoId,
            @PathVariable Long periodoId, @RequestBody PeriodoDisponibilidadRequest request, Authentication authentication) {
        validarPropietario(alojamientoId, authentication);
        return servicio.editar(alojamientoId, periodoId, request);
    }

    @DeleteMapping("/{periodoId}")
    public ResponseEntity<Void> eliminar(@PathVariable Long alojamientoId,
            @PathVariable Long periodoId, Authentication authentication) {
        validarPropietario(alojamientoId, authentication);
        servicio.eliminar(alojamientoId, periodoId);
        return ResponseEntity.noContent().build();
    }

    private void validarPropietario(Long alojamientoId, Authentication authentication) {
        boolean administrador = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
        var alojamiento = alojamientos.findById(alojamientoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "El alojamiento no existe"));
        boolean propietario = alojamiento.getHostId().equals(Long.valueOf(authentication.getName()));
        if (!administrador && !propietario) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "No tienes permiso para administrar este calendario");
        }
    }
}
