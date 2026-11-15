package co.edu.unbosque.marketplace.booking.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "pagos")
public class Pago {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "id_pago")
    private Long id;
    @Column(name = "id_reserva", nullable = false) private Long reservaId;
    @Column(nullable = false, precision = 12, scale = 2) private BigDecimal monto;
    @Column(nullable = false, length = 3) private String moneda = "COP";
    @Column(nullable = false, length = 20) private String estado;
    @Column(name = "marca_tarjeta", nullable = false, length = 30) private String marcaTarjeta;
    @Column(name = "ultimos_cuatro", nullable = false, length = 4) private String ultimosCuatro;
    @Column(nullable = false, length = 80) private String referencia;
    @Column(name = "codigo_respuesta", nullable = false, length = 40) private String codigoRespuesta;
    @Column(name = "fecha_creacion", insertable = false, updatable = false) private Instant fechaCreacion;

    protected Pago() { }
    public Pago(Long reservaId, BigDecimal monto, String estado, String marcaTarjeta,
            String ultimosCuatro, String referencia, String codigoRespuesta) {
        this.reservaId = reservaId; this.monto = monto; this.estado = estado;
        this.marcaTarjeta = marcaTarjeta; this.ultimosCuatro = ultimosCuatro;
        this.referencia = referencia; this.codigoRespuesta = codigoRespuesta;
    }
}
