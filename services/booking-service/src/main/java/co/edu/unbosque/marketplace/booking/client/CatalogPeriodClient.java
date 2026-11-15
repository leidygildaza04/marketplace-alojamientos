package co.edu.unbosque.marketplace.booking.client;

import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/** Cliente interno para reservar y liberar periodos en catalog-service. */
@Component
public class CatalogPeriodClient {

    private final RestClient restClient;
    private final String internalSecret;

    public CatalogPeriodClient(
            @Value("${services.catalog.base-url:http://localhost:8082}") String baseUrl,
            @Value("${app.internal.secret}") String internalSecret) {

        this.restClient = RestClient.builder().baseUrl(baseUrl).build();
        this.internalSecret = internalSecret;
    }

    public void reservar(Long alojamientoId, Long reservaId,
            LocalDate fechaInicio, LocalDate fechaFin) {
        restClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/internal/alojamientos/{alojamientoId}/periodos-disponibilidad/reservar")
                        .queryParam("reservaId", reservaId)
                        .queryParam("fechaInicio", fechaInicio)
                        .queryParam("fechaFin", fechaFin)
                        .build(alojamientoId))
                .header("X-Internal-Secret", internalSecret)
                .retrieve()
                .toBodilessEntity();
    }

    public void liberar(Long alojamientoId, Long reservaId) {
        restClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/internal/alojamientos/{alojamientoId}/periodos-disponibilidad/liberar")
                        .queryParam("reservaId", reservaId)
                        .build(alojamientoId))
                .header("X-Internal-Secret", internalSecret)
                .retrieve()
                .toBodilessEntity();
    }
}
