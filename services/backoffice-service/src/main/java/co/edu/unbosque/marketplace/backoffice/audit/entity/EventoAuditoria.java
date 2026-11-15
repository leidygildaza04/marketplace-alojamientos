package co.edu.unbosque.marketplace.backoffice.audit.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_events")
public class EventoAuditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long idEvento;

    @Column(name = "actor_id")
    private Long idUsuario;

    @Column(name = "action", nullable = false, length = 80)
    private String accion;

    @Column(name = "entity", nullable = false, length = 500)
    private String entidad;

    @Column(name = "id_entidad", nullable = false)
    private Long idEntidad;

    @Column(columnDefinition = "TEXT")
    private String detalle;

    @Column(name = "result", nullable = false, length = 120)
    private String resultado;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime fechaEvento;

    protected EventoAuditoria() {
    }

    public EventoAuditoria(
            Long idUsuario,
            String accion,
            String entidad,
            Long idEntidad,
            String detalle,
            String resultado
    ) {
        this.idUsuario = idUsuario;
        this.accion = accion;
        this.entidad = entidad;
        this.idEntidad = idEntidad;
        this.detalle = detalle;
        this.resultado = resultado;
    }

    @PrePersist
    void asignarFechaEvento() {
        if (fechaEvento == null) {
            fechaEvento = LocalDateTime.now();
        }
    }

    public Long getIdEvento() { return idEvento; }
    public Long getIdUsuario() { return idUsuario; }
    public String getAccion() { return accion; }
    public String getEntidad() { return entidad; }
    public Long getIdEntidad() { return idEntidad; }
    public String getDetalle() { return detalle; }
    public String getResultado() { return resultado; }
    public LocalDateTime getFechaEvento() { return fechaEvento; }
}
