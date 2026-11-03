package co.edu.unbosque.marketplace.catalog.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record CrearAlojamientoRequest(

    @NotBlank(message = "El título es obligatorio")
    @Size(max = 180, message = "El título no puede superar 180 caracteres")
    String titulo,

    String descripcion,

    @Size(max = 255, message = "La dirección no puede superar 255 caracteres")
    String direccion,

    @Size(max = 100, message = "La ciudad no puede superar 100 caracteres")
    String ciudad,

    @NotNull(message = "El precio por noche es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor que cero")
    BigDecimal precioNoche,

    @NotNull(message = "La capacidad es obligatoria")
    @Min(value = 1, message = "La capacidad debe ser mayor que cero")
    Integer capacidad

) {
}
