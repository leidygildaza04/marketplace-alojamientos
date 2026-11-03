package co.edu.unbosque.marketplace.catalog.domain;

import jakarta.persistence.*;

@Entity @Table(name="alojamientos")
public class Alojamiento {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) @Column(name="id_alojamiento") private Long id;
 @Column(name="id_anfitrion", nullable=false) private Long hostId;
 @Column(nullable=false) private String titulo;
 @Column(name="estado_publicacion", nullable=false) private String estado;
 protected Alojamiento() {}
 public Alojamiento(Long hostId, String titulo, String estado) {
  this.hostId = hostId;
  this.titulo = titulo;
  this.estado = estado;
 }
 public Long getId(){return id;}
 public Long getHostId(){return hostId;}
 public String getTitulo(){return titulo;}
 public String getEstado(){return estado;}
 public void setEstado(String estado){this.estado=estado;}
}
