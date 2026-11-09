package co.edu.unbosque.marketplace.booking.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "resenas")
public class Resena {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_resena")
    private Long id;

    @Column(name = "id_alojamiento", nullable = false)
    private Long alojamientoId;

    @Column(name = "id_usuario", nullable = false)
    private Long usuarioId;

    @Column(nullable = false)
    private Integer calificacion;

    @Column(columnDefinition = "TEXT")
    private String comentario;

    @Column(nullable = false)
    private boolean visible = true;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    protected Resena() {
    }

    public Resena(Long alojamientoId, Long usuarioId,
            Integer calificacion, String comentario) {
        this.alojamientoId = alojamientoId;
        this.usuarioId = usuarioId;
        this.calificacion = calificacion;
        this.comentario = comentario;
        this.visible = true;
    }

    @PrePersist
    void establecerFechaCreacion() {
        if (fechaCreacion == null) {
            fechaCreacion = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public Long getAlojamientoId() {
        return alojamientoId;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public Integer getCalificacion() {
        return calificacion;
    }

    public String getComentario() {
        return comentario;
    }

    public boolean isVisible() {
        return visible;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }
}
