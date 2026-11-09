
package co.edu.unbosque.marketplace.backoffice.notification.dto;

import co.edu.unbosque.marketplace.backoffice.notification.entity.Notificacion;
import java.time.LocalDateTime;

public record NotificacionResponse(
        Long id,
        Long usuarioId,
        String titulo,
        String mensaje,
        String tipo,
        Long entidadId,
        boolean leida,
        LocalDateTime fechaCreacion
) {
    public NotificacionResponse(Notificacion n) {
        this(
                n.getId(),
                n.getUsuarioId(),
                n.getTitulo(),
                n.getMensaje(),
                n.getTipo(),
                n.getEntidadId(),
                n.isLeida(),
                n.getFechaCreacion()
        );
    }
}
