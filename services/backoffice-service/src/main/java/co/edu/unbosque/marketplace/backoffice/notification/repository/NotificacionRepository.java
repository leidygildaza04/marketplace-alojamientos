package co.edu.unbosque.marketplace.backoffice.notification.repository;

import co.edu.unbosque.marketplace.backoffice.notification.entity.Notificacion;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {

    List<Notificacion> findByUsuarioIdOrderByFechaCreacionDesc(Long usuarioId);

    Optional<Notificacion> findByIdAndUsuarioId(Long id, Long usuarioId);
}
