package co.edu.unbosque.marketplace.backoffice.notification.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CrearNotificacionRequest {

    @NotNull
    private Long usuarioId;

    @NotBlank
    @Size(max = 160)
    private String titulo;

    @NotBlank
    @Size(max = 2000)
    private String mensaje;

    @NotBlank
    @Size(max = 60)
    private String tipo;

    private Long entidadId;

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public Long getEntidadId() { return entidadId; }
    public void setEntidadId(Long entidadId) { this.entidadId = entidadId; }
}
