package co.edu.unbosque.marketplace.catalog.web;

import co.edu.unbosque.marketplace.catalog.domain.Alojamiento;
import co.edu.unbosque.marketplace.catalog.dto.AlojamientoPreciosResponse;
import co.edu.unbosque.marketplace.catalog.dto.ServicioAdicionalPrecioResponse;
import co.edu.unbosque.marketplace.catalog.repository.AlojamientoRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/internal/alojamientos")
public class AlojamientoPreciosInternalController {

    private final AlojamientoRepository alojamientos;

    public AlojamientoPreciosInternalController(
            AlojamientoRepository alojamientos) {
        this.alojamientos = alojamientos;
    }

    @GetMapping("/{alojamientoId}/precios")
    @Transactional(readOnly = true)
    public AlojamientoPreciosResponse consultarPrecios(
            @PathVariable Long alojamientoId) {

        Alojamiento alojamiento = alojamientos.findById(alojamientoId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "El alojamiento no existe"));

        if (alojamiento.getPrecioNoche() == null
                || alojamiento.getPrecioNoche().signum() <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El alojamiento no tiene un precio por noche válido");
        }

        List<ServicioAdicionalPrecioResponse> servicios =
                alojamiento.getServiciosAdicionales().stream()
                        .map(servicio -> new ServicioAdicionalPrecioResponse(
                                servicio.getId(),
                                servicio.getNombre(),
                                servicio.getPrecio()))
                        .toList();

        return new AlojamientoPreciosResponse(
                alojamiento.getId(),
                alojamiento.getPrecioNoche(),
                alojamiento.getTarifaLimpieza(),
                servicios);
    }
}