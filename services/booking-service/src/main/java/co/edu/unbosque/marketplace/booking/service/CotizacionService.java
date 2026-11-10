package co.edu.unbosque.marketplace.booking.service;

import co.edu.unbosque.marketplace.booking.client.AlojamientoPreciosResponse;
import co.edu.unbosque.marketplace.booking.client.CatalogPricingClient;
import co.edu.unbosque.marketplace.booking.dto.CrearCotizacionRequest;
import co.edu.unbosque.marketplace.booking.dto.CotizacionResponse;
import co.edu.unbosque.marketplace.booking.dto.ServicioAdicionalCotizadoResponse;
import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CotizacionService {

    private final CatalogPricingClient catalog;

    public CotizacionService(CatalogPricingClient catalog) {
        this.catalog = catalog;
    }

    public CotizacionResponse calcular(CrearCotizacionRequest request) {

        if (request == null
                || request.alojamientoId() == null
                || request.alojamientoId() <= 0
                || request.checkIn() == null
                || request.checkOut() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El alojamiento y las fechas son obligatorios");
        }

        if (!request.checkOut().isAfter(request.checkIn())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La fecha de salida debe ser posterior a la fecha de entrada");
        }

        long noches = ChronoUnit.DAYS.between(
                request.checkIn(),
                request.checkOut());

        AlojamientoPreciosResponse precios =
                catalog.consultarPrecios(request.alojamientoId());

        if (precios == null || precios.precioNoche() == null
                || precios.precioNoche().signum() <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El alojamiento no tiene precios válidos");
        }

        BigDecimal tarifaLimpieza = precios.tarifaLimpieza() != null
                ? precios.tarifaLimpieza()
                : BigDecimal.ZERO;

        if (tarifaLimpieza.signum() < 0) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "La tarifa de limpieza no puede ser negativa");
        }

        BigDecimal subtotalAlojamiento =
                precios.precioNoche().multiply(BigDecimal.valueOf(noches));

        List<AlojamientoPreciosResponse.ServicioAdicionalPrecioResponse>
                serviciosDisponibles = precios.serviciosAdicionales() != null
                ? precios.serviciosAdicionales()
                : List.of();

        List<Long> seleccionados = request.serviciosAdicionalesIds() != null
                ? request.serviciosAdicionalesIds()
                : List.of();

        Set<Long> idsSeleccionados = new HashSet<>(seleccionados);

        if (idsSeleccionados.size() != seleccionados.size()
                || seleccionados.stream().anyMatch(id -> id == null)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Los servicios adicionales seleccionados no son válidos o están repetidos");
        }

        List<ServicioAdicionalCotizadoResponse> serviciosCotizados =
                new ArrayList<>();

        BigDecimal totalServicios = BigDecimal.ZERO;

        for (Long idSeleccionado : seleccionados) {
            var servicio = serviciosDisponibles.stream()
                    .filter(item -> item.id() != null
                            && item.id().equals(idSeleccionado))
                    .findFirst()
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "Uno de los servicios seleccionados no pertenece al alojamiento"));

            if (servicio.precio() == null || servicio.precio().signum() < 0) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "El precio de un servicio adicional no es válido");
            }

            serviciosCotizados.add(new ServicioAdicionalCotizadoResponse(
                    servicio.id(),
                    servicio.nombre(),
                    servicio.precio()));

            totalServicios = totalServicios.add(servicio.precio());
        }

        BigDecimal total = subtotalAlojamiento
                .add(tarifaLimpieza)
                .add(totalServicios);

        return new CotizacionResponse(
                request.alojamientoId(),
                request.checkIn(),
                request.checkOut(),
                noches,
                precios.precioNoche(),
                subtotalAlojamiento,
                tarifaLimpieza,
                serviciosCotizados,
                totalServicios,
                total);
    }
}