package co.edu.unbosque.marketplace.catalog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record BloquearAlojamientoRequest(
        @NotBlank(message = "El motivo del bloqueo es obligatorio")
        @Size(max = 1000, message = "El motivo no puede superar los 1000 caracteres")
        String motivo
) {
}
