
package co.edu.unbosque.marketplace.catalog.web;

import co.edu.unbosque.marketplace.catalog.domain.Alojamiento;
import co.edu.unbosque.marketplace.catalog.dto.CrearAlojamientoRequest;
import co.edu.unbosque.marketplace.catalog.service.AlojamientoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/alojamientos")
public class AlojamientoController {

    private final AlojamientoService service;

    public AlojamientoController(AlojamientoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Alojamiento> crear(
            @Valid @RequestBody CrearAlojamientoRequest request,
            Authentication authentication
    ) {
        Long hostId = Long.valueOf(authentication.getName());

        Alojamiento alojamiento = service.crear(hostId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(alojamiento);
    }

    @PostMapping("/{id}/activar")
    public ResponseEntity<Alojamiento> activar(
            @PathVariable Long id,
            Authentication authentication
    ) {
        Long usuarioId = Long.valueOf(authentication.getName());

        boolean esAdministrador = authentication.getAuthorities()
                .stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_ADMIN")
                );

        Alojamiento alojamiento =
                service.activar(usuarioId, esAdministrador, id);

        return ResponseEntity.ok(alojamiento);
    }
}
