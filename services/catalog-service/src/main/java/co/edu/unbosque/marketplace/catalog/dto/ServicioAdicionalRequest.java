package co.edu.unbosque.marketplace.catalog.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record ServicioAdicionalRequest(

        @NotBlank(message = "El nombre del servicio adicional es obligatorio")
        @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
        String nombre,

        @NotNull(message = "El precio del servicio adicional es obligatorio")
        @DecimalMin(value = "0.00", message = "El precio no puede ser negativo")
        BigDecimal precio

) {
}
