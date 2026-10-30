package co.edu.unbosque.marketplace.booking.domain;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity @Table(name="reservas")
public class Reserva {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) @Column(name="id_reserva") private Long id;
 @Column(name="id_usuario",nullable=false) private Long userId;
 @Column(name="fecha_entrada",nullable=false) private LocalDate checkIn;
 @Column(name="fecha_salida",nullable=false) private LocalDate checkOut;
 @Column(nullable=false) private String estado;
 protected Reserva(){}
 public Long getId(){return id;} public Long getUserId(){return userId;} public LocalDate getCheckIn(){return checkIn;} public LocalDate getCheckOut(){return checkOut;} public String getEstado(){return estado;}
}
