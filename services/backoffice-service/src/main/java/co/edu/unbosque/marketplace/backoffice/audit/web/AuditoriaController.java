package co.edu.unbosque.marketplace.backoffice.audit.web;

import co.edu.unbosque.marketplace.backoffice.audit.dto.CrearEventoAuditoriaRequest;
import co.edu.unbosque.marketplace.backoffice.audit.entity.EventoAuditoria;
import co.edu.unbosque.marketplace.backoffice.audit.service.AuditoriaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/auditoria/eventos")
public class AuditoriaController {

    private final AuditoriaService auditoriaService;

    public AuditoriaController(AuditoriaService auditoriaService) {
        this.auditoriaService = auditoriaService;
    }

    @PostMapping
    public ResponseEntity<EventoAuditoria> registrar(
            @Valid @RequestBody CrearEventoAuditoriaRequest request) {
        EventoAuditoria evento = auditoriaService.registrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(evento);
    }
}
