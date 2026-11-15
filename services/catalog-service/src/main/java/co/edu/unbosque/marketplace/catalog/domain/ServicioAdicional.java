package co.edu.unbosque.marketplace.catalog.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "alojamiento_servicios_adicionales")
public class ServicioAdicional {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_servicio_adicional")
    private Long id;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "precio", nullable = false, precision = 12, scale = 2)
    private BigDecimal precio;

    protected ServicioAdicional() {
    }

    public ServicioAdicional(String nombre, BigDecimal precio) {
        this.nombre = nombre;
        this.precio = precio;
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void actualizar(String nombre, BigDecimal precio) {
        this.nombre = nombre;
        this.precio = precio;
    }
}
