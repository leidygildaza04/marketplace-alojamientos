package co.edu.unbosque.marketplace.catalog.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "alojamientos")
public class Alojamiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_alojamiento")
    private Long id;

    @Column(name = "id_anfitrion", nullable = false)
    private Long hostId;

    @Column(nullable = false, length = 180)
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(length = 255)
    private String direccion;

    @Column(length = 100)
    private String ciudad;

    @Column(name = "precio_noche", precision = 12, scale = 2)
    private BigDecimal precioNoche;

    private Integer capacidad;

    @Column(name = "estado_publicacion", nullable = false, length = 30)
    private String estado;

    protected Alojamiento() {
    }

    public Alojamiento(
            Long hostId,
            String titulo,
            String descripcion,
            String direccion,
            String ciudad,
            BigDecimal precioNoche,
            Integer capacidad
    ) {
        this.hostId = hostId;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.direccion = direccion;
        this.ciudad = ciudad;
        this.precioNoche = precioNoche;
        this.capacidad = capacidad;
        this.estado = "BORRADOR";
    }

    public Alojamiento(Long hostId, String titulo, String estado) {
        this.hostId = hostId;
        this.titulo = titulo;
        this.estado = estado;
    }

    public Long getId() {
        return id;
    }

    public Long getHostId() {
        return hostId;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getDireccion() {
        return direccion;
    }

    public String getCiudad() {
        return ciudad;
    }

    public BigDecimal getPrecioNoche() {
        return precioNoche;
    }

    public Integer getCapacidad() {
        return capacidad;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
