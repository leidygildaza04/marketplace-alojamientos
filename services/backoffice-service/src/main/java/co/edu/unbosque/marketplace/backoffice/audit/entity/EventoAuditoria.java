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
@Table(name = "evento_auditoria")
public class EventoAuditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_evento")
    private Long idEvento;

    @Column(name = "id_usuario")
    private Long idUsuario;

    @Column(nullable = false, length = 100)
    private String accion;

    @Column(nullable = false, length = 100)
    private String entidad;

    @Column(name = "id_entidad", nullable = false)
    private Long idEntidad;

    @Column(columnDefinition = "TEXT")
    private String detalle;

    @Column(nullable = false, length = 50)
    private String resultado;

    @Column(name = "fecha_evento", nullable = false)
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
