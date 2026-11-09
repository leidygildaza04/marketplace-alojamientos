package co.edu.unbosque.marketplace.backoffice.notification.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Entity
@Table(name = "notificaciones")
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(nullable = false, length = 160)
    private String titulo;

    @Column(nullable = false, length = 2000)
    private String mensaje;

    @Column(nullable = false, length = 60)
    private String tipo;

    @Column(name = "entidad_id")
    private Long entidadId;

    @Column(nullable = false)
    private boolean leida = false;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    protected Notificacion() {
    }

    public Notificacion(Long usuarioId, String titulo, String mensaje,
                        String tipo, Long entidadId) {
        this.usuarioId = usuarioId;
        this.titulo = titulo;
        this.mensaje = mensaje;
        this.tipo = tipo;
        this.entidadId = entidadId;
        this.leida = false;
        this.fechaCreacion = LocalDateTime.now(ZoneId.of("America/Bogota"));
    }

    public Long getId() {
        return id;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public String getTipo() {
        return tipo;
    }

    public Long getEntidadId() {
        return entidadId;
    }

    public boolean isLeida() {
        return leida;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void marcarComoLeida() {
        this.leida = true;
    }
}