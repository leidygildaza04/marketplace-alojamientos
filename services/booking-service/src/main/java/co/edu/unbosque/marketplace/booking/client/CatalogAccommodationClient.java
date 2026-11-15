package co.edu.unbosque.marketplace.booking.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * Cliente interno para consultar los datos básicos de un alojamiento.
 */
@Component
public class CatalogAccommodationClient {

    private final RestClient restClient;
    private final String internalSecret;

    public CatalogAccommodationClient(
            @Value("${services.catalog.base-url:http://localhost:8082}") String baseUrl,
            @Value("${app.internal.secret}") String internalSecret) {

        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();

        this.internalSecret = internalSecret;
    }

    public CatalogAccommodationSummaryResponse consultarResumen(
            Long alojamientoId) {

        return restClient.get()
                .uri("/internal/alojamientos/{alojamientoId}/resumen",
                        alojamientoId)
                .header("X-Internal-Secret", internalSecret)
                .retrieve()
                .body(CatalogAccommodationSummaryResponse.class);
    }
}