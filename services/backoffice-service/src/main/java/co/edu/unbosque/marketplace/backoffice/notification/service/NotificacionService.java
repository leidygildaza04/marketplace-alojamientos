package co.edu.unbosque.marketplace.backoffice.notification.service;

import co.edu.unbosque.marketplace.backoffice.notification.dto.CrearNotificacionRequest;
import co.edu.unbosque.marketplace.backoffice.notification.dto.NotificacionResponse;
import co.edu.unbosque.marketplace.backoffice.notification.entity.Notificacion;
import co.edu.unbosque.marketplace.backoffice.notification.repository.NotificacionRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class NotificacionService {

    private final NotificacionRepository repository;

    public NotificacionService(NotificacionRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public NotificacionResponse crear(CrearNotificacionRequest request) {
        Notificacion notificacion = new Notificacion(
                request.getUsuarioId(),
                request.getTitulo(),
                request.getMensaje(),
                request.getTipo(),
                request.getEntidadId());
        return new NotificacionResponse(repository.save(notificacion));
    }

    @Transactional(readOnly = true)
    public List<NotificacionResponse> listarPorUsuario(Long usuarioId) {
        return repository.findByUsuarioIdOrderByFechaCreacionDesc(usuarioId)
                .stream()
                .map(NotificacionResponse::new)
                .toList();
    }

    @Transactional
    public NotificacionResponse marcarComoLeida(Long usuarioId, Long notificacionId) {
        Notificacion notificacion = repository.findByIdAndUsuarioId(notificacionId, usuarioId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Notificaci├│n no encontrada"));
        notificacion.marcarComoLeida();
        return new NotificacionResponse(repository.save(notificacion));
    }
}
