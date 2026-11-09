package co.edu.unbosque.marketplace.backoffice.notification.dto;

import co.edu.unbosque.marketplace.backoffice.notification.entity.Notificacion;
import java.time.LocalDateTime;

public class NotificacionResponse {

    private final Long id;
    private final Long usuarioId;
    private final String titulo;
    private final String mensaje;
    private final String tipo;
    private final Long entidadId;
    private final boolean leida;
    private final LocalDateTime fechaCreacion;

    public NotificacionResponse(Notificacion n) {
        this.id = n.getId();
        this.usuarioId = n.getUsuarioId();
        this.titulo = n.getTitulo();
        this.mensaje = n.getMensaje();
        this.tipo = n.getTipo();
        this.entidadId = n.getEntidadId();
        this.leida = n.isLeida();
        this.fechaCreacion = n.getFechaCreacion();
    }

    public Long getId() { return id; }
    public Long getUsuarioId() { return usuarioId; }
    public String getTitulo() { return titulo; }
    public String getMensaje() { return mensaje; }
    public String getTipo() { return tipo; }
    public Long getEntidadId() { return entidadId; }
    public boolean isLeida() { return leida; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
}
