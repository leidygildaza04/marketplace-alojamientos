package co.edu.unbosque.marketplace.catalog.client;

import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/** Cliente HTTP para consultar reservas confirmadas en booking-service. */
@Component
public class BookingReservationClient {

    private final RestClient restClient;

    public BookingReservationClient(
            @Value("${services.booking.base-url:http://localhost:8083}") String baseUrl) {
        this.restClient = RestClient.builder().baseUrl(baseUrl).build();
    }

    public boolean existeReservaConfirmada(Long alojamientoId,
            LocalDate fechaInicio, LocalDate fechaFin) {
        Boolean respuesta = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/internal/alojamientos/{alojamientoId}/reservas-confirmadas/solapamiento")
                        .queryParam("fechaInicio", fechaInicio)
                        .queryParam("fechaFin", fechaFin)
                        .build(alojamientoId))
                .retrieve()
                .body(Boolean.class);
        return Boolean.TRUE.equals(respuesta);
    }
}
