package co.edu.unbosque.marketplace.catalog.dto;

import co.edu.unbosque.marketplace.catalog.domain.Alojamiento;
import java.math.BigDecimal;
import java.util.List;

public record ResultadoBusquedaAlojamientoResponse(
        Long id,
        String titulo,
        String descripcion,
        String ciudad,
        BigDecimal precioNoche,
        Integer capacidad,
        String tipoAlojamiento,
        List<String> servicios,
        List<String> imagenes
) {
    public static ResultadoBusquedaAlojamientoResponse desde(Alojamiento alojamiento) {
        return new ResultadoBusquedaAlojamientoResponse(
                alojamiento.getId(),
                alojamiento.getTitulo(),
                alojamiento.getDescripcion(),
                alojamiento.getCiudad(),
                alojamiento.getPrecioNoche(),
                alojamiento.getCapacidad(),
                alojamiento.getTipoAlojamiento(),
                alojamiento.getServicios(),
                alojamiento.getImagenes()
        );
    }
}
