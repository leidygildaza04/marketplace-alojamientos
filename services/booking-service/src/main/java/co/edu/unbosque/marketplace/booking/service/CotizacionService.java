
package co.edu.unbosque.marketplace.booking.service;

import co.edu.unbosque.marketplace.booking.client.AlojamientoPreciosResponse;
import co.edu.unbosque.marketplace.booking.client.CatalogPricingClient;
import co.edu.unbosque.marketplace.booking.dto.CrearCotizacionRequest;
import co.edu.unbosque.marketplace.booking.dto.CotizacionResponse;
import co.edu.unbosque.marketplace.booking.dto.ServicioAdicionalCotizadoResponse;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CotizacionService {

    private static final BigDecimal CIEN = new BigDecimal("100");

    private final CatalogPricingClient catalog;
    private final BigDecimal porcentajeServicio;
    private final BigDecimal porcentajeDescuento;

    public CotizacionService(
            CatalogPricingClient catalog,
            @Value("${app.cotizacion.porcentaje-servicio:10}")
            BigDecimal porcentajeServicio,
            @Value("${app.cotizacion.porcentaje-descuento:0}")
            BigDecimal porcentajeDescuento) {

        this.catalog = catalog;
        this.porcentajeServicio = validarPorcentaje(
                porcentajeServicio, "servicio");
        this.porcentajeDescuento = validarPorcentaje(
                porcentajeDescuento, "descuento");
    }

    private BigDecimal validarPorcentaje(
            BigDecimal porcentaje, String concepto) {

        if (porcentaje == null
                || porcentaje.signum() < 0
                || porcentaje.compareTo(CIEN) > 0) {
            throw new IllegalArgumentException(
                    "El porcentaje de " + concepto
                            + " debe estar entre 0 y 100");
        }

        return porcentaje;
    }

    private BigDecimal calcularImporte(
            BigDecimal base, BigDecimal porcentaje) {

        return base.multiply(porcentaje)
                .divide(CIEN, 2, RoundingMode.HALF_UP);
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

        // Base para calcular el servicio y el descuento.
        BigDecimal subtotal = subtotalAlojamiento
                .add(tarifaLimpieza)
                .add(totalServicios);

        BigDecimal tarifaServicio = calcularImporte(
                subtotal, porcentajeServicio);

        BigDecimal descuento = calcularImporte(
                subtotal, porcentajeDescuento);

        BigDecimal total = subtotal
                .add(tarifaServicio)
                .subtract(descuento);

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
                porcentajeServicio,
                tarifaServicio,
                porcentajeDescuento,
                descuento,
                total);
    }
}
