package co.edu.unbosque.marketplace.catalog.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;

public record CrearAlojamientoRequest(
    @NotBlank(message = "El titulo es obligatorio")
    @Size(max = 180, message = "El titulo no puede superar 180 caracteres")
    String titulo,
    String descripcion,
    @Size(max = 255, message = "La direccion no puede superar 255 caracteres")
    String direccion,
    @Size(max = 100, message = "La ciudad no puede superar 100 caracteres")
    String ciudad,
    @NotNull(message = "El precio por noche es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor que cero")
    BigDecimal precioNoche,
    @NotNull(message = "La capacidad es obligatoria")
    @Min(value = 1, message = "La capacidad debe ser mayor que cero")
    Integer capacidad,
    @Size(max = 50, message = "El tipo de alojamiento no puede superar 50 caracteres")
    String tipoAlojamiento,
    String reglas,
    String politicaCancelacion,
    List<String> servicios,
    List<String> imagenes,
    @DecimalMin(value = "0.00", message = "La tarifa de limpieza no puede ser negativa")
    BigDecimal tarifaLimpieza,
    @Valid
    List<ServicioAdicionalRequest> serviciosAdicionales
) {
}