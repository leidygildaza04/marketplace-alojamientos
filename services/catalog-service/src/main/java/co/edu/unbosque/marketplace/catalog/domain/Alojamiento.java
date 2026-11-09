package co.edu.unbosque.marketplace.catalog.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "alojamientos")
public class Alojamiento {

    public static final String ESTADO_BLOQUEADA = "BLOQUEADA";

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

    @Column(name = "motivo_bloqueo", length = 1000)
    private String motivoBloqueo;

    @Column(name = "tipo_alojamiento", length = 50)
    private String tipoAlojamiento;

    @Column(columnDefinition = "TEXT")
    private String reglas;

    @Column(name = "politica_cancelacion", columnDefinition = "TEXT")
    private String politicaCancelacion;

    @ElementCollection
    @CollectionTable(
            name = "alojamiento_servicios",
            joinColumns = @JoinColumn(name = "id_alojamiento")
    )
    @Column(name = "servicio", nullable = false, length = 100)
    private List<String> servicios = new ArrayList<>();

    @ElementCollection
    @CollectionTable(
            name = "alojamiento_imagenes",
            joinColumns = @JoinColumn(name = "id_alojamiento")
    )
    @Column(name = "imagen_url", nullable = false, length = 1000)
    private List<String> imagenes = new ArrayList<>();

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

    public String getMotivoBloqueo() {
        return motivoBloqueo;
    }

    public String getTipoAlojamiento() {
        return tipoAlojamiento;
    }

    public String getReglas() {
        return reglas;
    }

    public String getPoliticaCancelacion() {
        return politicaCancelacion;
    }

    public List<String> getServicios() {
        return servicios;
    }

    public List<String> getImagenes() {
        return imagenes;
    }

    public void actualizarDatosComplementarios(
            String tipoAlojamiento,
            String reglas,
            String politicaCancelacion,
            List<String> servicios,
            List<String> imagenes
    ) {
        this.tipoAlojamiento = tipoAlojamiento;
        this.reglas = reglas;
        this.politicaCancelacion = politicaCancelacion;

        this.servicios.clear();
        if (servicios != null) {
            this.servicios.addAll(servicios);
        }

        this.imagenes.clear();
        if (imagenes != null) {
            this.imagenes.addAll(imagenes);
        }
    }

    public void bloquear(String motivo) {
        if (motivo == null || motivo.isBlank()) {
            throw new IllegalArgumentException(
                    "El motivo del bloqueo es obligatorio"
            );
        }

        if (ESTADO_BLOQUEADA.equals(estado)) {
            throw new IllegalStateException(
                    "El alojamiento ya está bloqueado"
            );
        }

        this.motivoBloqueo = motivo.trim();
        this.estado = ESTADO_BLOQUEADA;
    }

    public void actualizarDatos(
            String titulo,
            String descripcion,
            String direccion,
            String ciudad,
            BigDecimal precioNoche,
            Integer capacidad
    ) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.direccion = direccion;
        this.ciudad = ciudad;
        this.precioNoche = precioNoche;
        this.capacidad = capacidad;
    }

    public void cambiarEstadoA(String nuevoEstado) {

        if (nuevoEstado == null) {
            throw new IllegalArgumentException(
                    "El nuevo estado no puede ser nulo"
            );
        }

        boolean estadoReconocido =
                nuevoEstado.equals("BORRADOR")
                || nuevoEstado.equals("PENDIENTE_REVISION")
                || nuevoEstado.equals("ACTIVA")
                || nuevoEstado.equals("PAUSADA")
                || nuevoEstado.equals(ESTADO_BLOQUEADA);

        if (!estadoReconocido) {
            throw new IllegalArgumentException(
                    "Estado de publicacion no reconocido: " + nuevoEstado
            );
        }

        boolean transicionPermitida =
                ("BORRADOR".equals(estado) && "ACTIVA".equals(nuevoEstado))
                || ("ACTIVA".equals(estado) && "PAUSADA".equals(nuevoEstado))
                || ("PAUSADA".equals(estado) && "ACTIVA".equals(nuevoEstado));

        if (!transicionPermitida) {
            throw new IllegalStateException(
                    "No se permite cambiar el estado de "
                            + estado + " a " + nuevoEstado
            );
        }

        this.estado = nuevoEstado;
    }
}
