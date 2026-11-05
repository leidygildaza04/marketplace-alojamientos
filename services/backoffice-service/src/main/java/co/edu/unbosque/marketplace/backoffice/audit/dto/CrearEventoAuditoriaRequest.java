package co.edu.unbosque.marketplace.backoffice.audit.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CrearEventoAuditoriaRequest(
        Long idUsuario,
        @NotBlank String accion,
        @NotBlank String entidad,
        @NotNull @Positive Long idEntidad,
        String detalle,
        @NotBlank String resultado
) {
}
