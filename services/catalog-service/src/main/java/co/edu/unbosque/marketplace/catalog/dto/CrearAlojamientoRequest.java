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
    Integer capacidad,

    @Size(max = 60, message = "El tipo no puede superar 60 caracteres")
    String tipo,

    @Size(max = 1024, message = "La URL de imagen no puede superar 1024 caracteres")
    String imagen,

    String servicios,
    String reglas,

    @DecimalMin(value = "0.00", message = "El cargo de limpieza no puede ser negativo")
    BigDecimal cargoLimpieza,

    @Size(max = 30, message = "La política de cancelación no puede superar 30 caracteres")
    String politicaCancelacion

) {
    public CrearAlojamientoRequest(String titulo, String descripcion, String direccion, String ciudad,
            BigDecimal precioNoche, Integer capacidad) {
        this(titulo, descripcion, direccion, ciudad, precioNoche, capacidad, null, null, null, null, null, null);
    }
}
