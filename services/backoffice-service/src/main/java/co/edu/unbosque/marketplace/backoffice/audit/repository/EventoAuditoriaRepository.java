package co.edu.unbosque.marketplace.backoffice.audit.repository;

import co.edu.unbosque.marketplace.backoffice.audit.entity.EventoAuditoria;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EventoAuditoriaRepository
        extends JpaRepository<EventoAuditoria, Long> {
    List<EventoAuditoria> findAllByOrderByFechaEventoDesc();
}
