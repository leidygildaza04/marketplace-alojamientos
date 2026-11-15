package co.edu.unbosque.marketplace.booking.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;

@Entity
@Table(name = "resenas", uniqueConstraints = @UniqueConstraint(name = "uq_resena_reserva", columnNames = "id_reserva"))
public class Resena {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_reserva", nullable = false)
    private Long reservaId;

    @Column(name = "id_usuario", nullable = false)
    private Long userId;

    @Column(name = "id_alojamiento", nullable = false)
    private Long alojamientoId;

    @Column(nullable = false)
    private int calificacion;

    @Column(length = 1000)
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

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private Instant fechaCreacion;

    protected Resena() {}

    public Resena(Long reservaId, Long userId, Long alojamientoId, int calificacion, String comentario) {
        this.reservaId = reservaId;
        this.userId = userId;
        this.alojamientoId = alojamientoId;
        this.calificacion = calificacion;
        this.comentario = comentario;
    }

    @PrePersist
    void asignarFechaCreacion() {
        if (fechaCreacion == null) fechaCreacion = Instant.now();
    }

    public Long getId() { return id; }
    public Long getReservaId() { return reservaId; }
    public Long getUserId() { return userId; }
    public Long getAlojamientoId() { return alojamientoId; }
    public int getCalificacion() { return calificacion; }
    public String getComentario() { return comentario; }
    public boolean isVisible() { return visible; }
    public void setVisible(boolean visible) { this.visible = visible; }
    public Instant getFechaCreacion() { return fechaCreacion; }
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
