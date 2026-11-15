package co.edu.unbosque.marketplace.booking.service;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import org.springframework.stereotype.Service;

/** Procesador local de pruebas. Acepta datos ficticios con formato válido; nunca cobra dinero real. */
@Service
public class SimuladorPagoService {
    public record Resultado(boolean aprobado, String marca, String ultimosCuatro, String codigo) { }

    public Resultado procesar(String numero, String vencimiento, String cvv, String titular) {
        String tarjeta = numero == null ? "" : numero.replaceAll("[ -]", "");
        if (tarjeta.length() != 16 || !tarjeta.matches("\\d+")) {
            throw new IllegalArgumentException("Ingresa los 16 dígitos de la tarjeta para la simulación");
        }
        if (cvv == null || !cvv.matches("\\d{3}") || titular == null || titular.isBlank()) {
            throw new IllegalArgumentException("Revisa el titular y el código de seguridad");
        }
        try {
            YearMonth expiry = YearMonth.parse(vencimiento, DateTimeFormatter.ofPattern("MM/yy"));
            if (expiry.isBefore(YearMonth.now())) throw new IllegalArgumentException("La tarjeta está vencida");
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException("La fecha de vencimiento debe tener formato MM/AA");
        }
        String brand = tarjeta.startsWith("4") ? "VISA" : tarjeta.startsWith("5") ? "MASTERCARD" : "TARJETA";
        return new Resultado(true, brand, tarjeta.substring(tarjeta.length() - 4), "APROBADO_SIMULADO");
    }
}
