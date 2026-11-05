package co.edu.unbosque.marketplace.backoffice.audit.service;

import co.edu.unbosque.marketplace.backoffice.audit.dto.CrearEventoAuditoriaRequest;
import co.edu.unbosque.marketplace.backoffice.audit.entity.EventoAuditoria;
import co.edu.unbosque.marketplace.backoffice.audit.repository.EventoAuditoriaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuditoriaService {

    private final EventoAuditoriaRepository repository;

    public AuditoriaService(EventoAuditoriaRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public EventoAuditoria registrar(CrearEventoAuditoriaRequest request) {
        EventoAuditoria evento = new EventoAuditoria(
                request.idUsuario(),
                request.accion(),
                request.entidad(),
                request.idEntidad(),
                request.detalle(),
                request.resultado()
        );
        return repository.save(evento);
    }
}
