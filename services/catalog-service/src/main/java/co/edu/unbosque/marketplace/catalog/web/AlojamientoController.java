
package co.edu.unbosque.marketplace.catalog.web;

import co.edu.unbosque.marketplace.catalog.domain.Alojamiento;
import co.edu.unbosque.marketplace.catalog.dto.BloquearAlojamientoRequest;
import co.edu.unbosque.marketplace.catalog.dto.CrearAlojamientoRequest;
import co.edu.unbosque.marketplace.catalog.dto.DetalleAlojamientoResponse;
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

    // SCRUM-24: CONSULTAR DETALLE DE PUBLICACION

    @GetMapping("/{id}")
    public ResponseEntity<DetalleAlojamientoResponse> consultarDetalle(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(service.consultarDetalle(id));
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
        boolean esAdministrador = esAdministrador(authentication);

        Alojamiento alojamiento =
                service.activar(usuarioId, esAdministrador, id);

        return ResponseEntity.ok(alojamiento);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Alojamiento> editar(
            @PathVariable Long id,
            @Valid @RequestBody CrearAlojamientoRequest request,
            Authentication authentication
    ) {
        Long usuarioId = Long.valueOf(authentication.getName());
        boolean esAdministrador = esAdministrador(authentication);

        Alojamiento alojamiento =
                service.editar(usuarioId, esAdministrador, id, request);

        return ResponseEntity.ok(alojamiento);
    }

    @PatchMapping("/{id}/pausar")
    public ResponseEntity<Alojamiento> pausar(
            @PathVariable Long id,
            Authentication authentication
    ) {
        Long usuarioId = Long.valueOf(authentication.getName());
        boolean esAdministrador = esAdministrador(authentication);

        Alojamiento alojamiento =
                service.pausar(usuarioId, esAdministrador, id);

        return ResponseEntity.ok(alojamiento);
    }

    @PatchMapping("/{id}/bloquear")
    public ResponseEntity<Alojamiento> bloquear(
            @PathVariable Long id,
            @Valid @RequestBody BloquearAlojamientoRequest request,
            Authentication authentication
    ) {
        Long administradorId = Long.valueOf(authentication.getName());
        boolean administrador = esAdministrador(authentication);

        return ResponseEntity.ok(
                service.bloquear(
                        administradorId,
                        administrador,
                        id,
                        request.motivo()
                )
        );
    }

    private boolean esAdministrador(Authentication authentication) {
        return authentication.getAuthorities()
                .stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_ADMIN")
                );
    }
}
