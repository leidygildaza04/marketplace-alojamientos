package co.edu.unbosque.marketplace.backoffice.audit.web;

import co.edu.unbosque.marketplace.backoffice.audit.dto.CrearEventoAuditoriaRequest;
import co.edu.unbosque.marketplace.backoffice.audit.entity.EventoAuditoria;
import co.edu.unbosque.marketplace.backoffice.audit.repository.EventoAuditoriaRepository;
import co.edu.unbosque.marketplace.backoffice.audit.service.AuditoriaService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/auditoria")
@PreAuthorize("hasRole('ADMIN')")
public class AdminAuditoriaController {
    private final EventoAuditoriaRepository repository;
    private final AuditoriaService service;

    public AdminAuditoriaController(EventoAuditoriaRepository repository, AuditoriaService service) {
        this.repository = repository;
        this.service = service;
    }

    @GetMapping
    public List<EventoAuditoria> listar() {
        return repository.findAllByOrderByFechaEventoDesc();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventoAuditoria registrar(@Valid @RequestBody AdminAuditRequest request, Authentication authentication) {
        return service.registrar(new CrearEventoAuditoriaRequest(Long.valueOf(authentication.getName()),
                request.accion(), request.entidad(), request.idEntidad(), request.detalle(), request.resultado()));
    }

    public record AdminAuditRequest(@NotBlank @Size(max = 80) String accion,
            @NotBlank @Size(max = 500) String entidad, @NotNull @Positive Long idEntidad,
            @Size(max = 2000) String detalle, @NotBlank @Size(max = 120) String resultado) {}
}
