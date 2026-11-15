package co.edu.unbosque.marketplace.booking.service;

import co.edu.unbosque.marketplace.booking.client.CatalogPeriodClient;
import co.edu.unbosque.marketplace.booking.domain.Pago;
import co.edu.unbosque.marketplace.booking.domain.Reserva;
import co.edu.unbosque.marketplace.booking.dto.CheckoutRequest;
import co.edu.unbosque.marketplace.booking.dto.CheckoutResponse;
import co.edu.unbosque.marketplace.booking.dto.CotizacionRequest;
import co.edu.unbosque.marketplace.booking.repository.PagoRepository;
import co.edu.unbosque.marketplace.booking.repository.ReservaRepository;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CheckoutService {
    private final CotizacionService cotizaciones;
    private final SimuladorPagoService simulador;
    private final CatalogPeriodClient catalog;
    private final ReservaRepository reservas;
    private final PagoRepository pagos;
    private final TransactionTemplate transactions;

    public CheckoutService(CotizacionService cotizaciones, SimuladorPagoService simulador,
            CatalogPeriodClient catalog, ReservaRepository reservas, PagoRepository pagos,
            PlatformTransactionManager manager) {
        this.cotizaciones = cotizaciones; this.simulador = simulador; this.catalog = catalog;
        this.reservas = reservas; this.pagos = pagos; this.transactions = new TransactionTemplate(manager);
    }

    public CheckoutResponse pagar(Long userId, CheckoutRequest request) {
        var quote = cotizaciones.cotizar(new CotizacionRequest(request.alojamientoId(), request.checkIn(), request.checkOut()));
        SimuladorPagoService.Resultado resultado;
        try { resultado = simulador.procesar(request.numeroTarjeta(), request.vencimiento(), request.cvv(), request.titular()); }
        catch (IllegalArgumentException ex) { throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage()); }

        String estado = resultado.aprobado() ? "CONFIRMADA" : "RECHAZADA";
        Reserva reserva = transactions.execute(s -> reservas.save(new Reserva(userId, request.alojamientoId(),
                request.checkIn(), request.checkOut(), "PENDIENTE", quote.total())));
        if (reserva == null || reserva.getId() == null) throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No fue posible crear la reserva");
        String referencia = "SIM-" + UUID.randomUUID();

        if (!resultado.aprobado()) {
            finalizar(reserva.getId(), "RECHAZADA", resultado, quote.total(), referencia);
            return respuesta(reserva, quote.total(), "RECHAZADO", referencia,
                    "Pago de prueba rechazado. No se realizó ningún cobro.");
        }
        try {
            catalog.reservar(request.alojamientoId(), reserva.getId(), request.checkIn(), request.checkOut());
        } catch (RestClientException ex) {
            finalizar(reserva.getId(), "RECHAZADA",
                    new SimuladorPagoService.Resultado(false, resultado.marca(), resultado.ultimosCuatro(), "NO_DISPONIBLE"),
                    quote.total(), referencia);
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Las fechas dejaron de estar disponibles. La simulación no generó un cobro.", ex);
        }
        try {
            finalizar(reserva.getId(), estado, resultado, quote.total(), referencia);
        } catch (RuntimeException ex) {
            try {
                catalog.liberar(request.alojamientoId(), reserva.getId());
                cambiarEstado(reserva.getId(), "RECHAZADA");
            } catch (RestClientException compensacion) {
                ex.addSuppressed(compensacion);
                cambiarEstado(reserva.getId(), "COMPENSACION_PENDIENTE");
            }
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "No fue posible guardar el resultado del checkout; las fechas fueron liberadas o requieren revisión.", ex);
        }
        return respuesta(reserva, quote.total(), "APROBADO", referencia,
                "Pago de prueba aprobado. No se realizó ningún cobro real.");
    }

    private void finalizar(Long id, String estado, SimuladorPagoService.Resultado resultado,
            java.math.BigDecimal total, String referencia) {
        transactions.execute(s -> {
            Reserva actual = reservas.findById(id).orElseThrow();
            actual.cambiarEstado(estado);
            reservas.save(actual);
            pagos.save(new Pago(id, total, resultado.aprobado() ? "APROBADO" : "RECHAZADO",
                    resultado.marca(), resultado.ultimosCuatro(), referencia, resultado.codigo()));
            return null;
        });
    }

    private void cambiarEstado(Long id, String estado) {
        transactions.execute(s -> {
            reservas.findById(id).ifPresent(actual -> {
                actual.cambiarEstado(estado);
                reservas.save(actual);
            });
            return null;
        });
    }

    private CheckoutResponse respuesta(Reserva reserva, java.math.BigDecimal total, String pago,
            String referencia, String mensaje) {
        return new CheckoutResponse(reserva.getId(), "R-" + reserva.getId(),
                pago.equals("APROBADO") ? "CONFIRMADA" : "RECHAZADA", total, pago, referencia, mensaje);
    }
}
