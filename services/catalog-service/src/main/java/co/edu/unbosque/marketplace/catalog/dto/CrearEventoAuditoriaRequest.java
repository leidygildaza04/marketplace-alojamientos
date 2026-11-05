package co.edu.unbosque.marketplace.catalog.dto;

public record CrearEventoAuditoriaRequest(
        Long idUsuario,
        String accion,
        String entidad,
        Long idEntidad,
        String detalle,
        String resultado
) {
}
