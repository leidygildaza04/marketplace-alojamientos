package co.edu.unbosque.marketplace.booking.service;

import co.edu.unbosque.marketplace.booking.client.CatalogPeriodClient;
import co.edu.unbosque.marketplace.booking.dto.CotizacionRequest;
import co.edu.unbosque.marketplace.booking.dto.CotizacionResponse;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.temporal.ChronoUnit;
import java.time.LocalDate;
import java.time.ZoneId;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CotizacionService {
    private static final BigDecimal TARIFA_SERVICIO = new BigDecimal("0.10");
    private static final BigDecimal DESCUENTO_ESTANCIA = new BigDecimal("0.10");
    private final CatalogPeriodClient catalog;

    public CotizacionService(CatalogPeriodClient catalog) { this.catalog = catalog; }

    public CotizacionResponse cotizar(CotizacionRequest request) {
        if (request == null || request.alojamientoId() == null || request.checkIn() == null
                || request.checkOut() == null || !request.checkIn().isBefore(request.checkOut())
                || request.checkIn().isBefore(LocalDate.now(ZoneId.of("America/Bogota")))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Selecciona fechas válidas para cotizar");
        }
        var alojamiento = catalog.obtenerPrecio(request.alojamientoId());
        if (!"ACTIVA".equals(alojamiento.estado()) || alojamiento.precioNoche() == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "El alojamiento no está disponible");
        }
        int noches = Math.toIntExact(ChronoUnit.DAYS.between(request.checkIn(), request.checkOut()));
        BigDecimal nochesSubtotal = alojamiento.precioNoche().multiply(BigDecimal.valueOf(noches));
        BigDecimal aseo = alojamiento.cargoLimpieza() == null ? BigDecimal.ZERO : alojamiento.cargoLimpieza();
        BigDecimal servicio = nochesSubtotal.multiply(TARIFA_SERVICIO).setScale(0, RoundingMode.HALF_UP);
        BigDecimal descuento = noches >= 7
                ? nochesSubtotal.multiply(DESCUENTO_ESTANCIA).setScale(0, RoundingMode.HALF_UP) : BigDecimal.ZERO;
        BigDecimal total = nochesSubtotal.add(aseo).add(servicio).subtract(descuento);
        return new CotizacionResponse(noches, nochesSubtotal, aseo, servicio, descuento, total, "COP");
    }
}
