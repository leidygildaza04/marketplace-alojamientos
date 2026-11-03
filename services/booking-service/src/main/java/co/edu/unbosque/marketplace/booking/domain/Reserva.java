package co.edu.unbosque.marketplace.booking.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "reservas")
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reserva")
    private Long id;

    @Column(name = "id_usuario", nullable = false)
    private Long userId;

    @Column(name = "id_alojamiento", nullable = false)
    private Long alojamientoId;

    @Column(name = "fecha_entrada", nullable = false)
    private LocalDate checkIn;

    @Column(name = "fecha_salida", nullable = false)
    private LocalDate checkOut;

    @Column(nullable = false)
    private String estado;

    protected Reserva() {
    }

    public Reserva(Long userId, Long alojamientoId, LocalDate checkIn,
            LocalDate checkOut, String estado) {
        this.userId = userId;
        this.alojamientoId = alojamientoId;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.estado = estado;
    }

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public Long getAlojamientoId() { return alojamientoId; }
    public LocalDate getCheckIn() { return checkIn; }
    public LocalDate getCheckOut() { return checkOut; }
    public String getEstado() { return estado; }

    public void cambiarEstado(String estado) {
        this.estado = estado;
    }
}
