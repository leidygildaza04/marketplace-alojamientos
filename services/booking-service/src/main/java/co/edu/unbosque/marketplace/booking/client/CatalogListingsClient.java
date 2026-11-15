package co.edu.unbosque.marketplace.booking.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * Cliente interno para consultar los alojamientos de un anfitrión.
 */
@Component
public class CatalogListingsClient {

    private final RestClient restClient;
    private final String internalSecret;

    public CatalogListingsClient(
            @Value("${services.catalog.base-url:http://localhost:8082}") String baseUrl,
            @Value("${app.internal.secret}") String internalSecret) {

        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();

        this.internalSecret = internalSecret;
    }

    public CatalogListingsResponse consultarAlojamientos(Long userId) {
        return restClient.get()
                .uri("/internal/accounts/{userId}/listings", userId)
                .header("X-Internal-Secret", internalSecret)
                .retrieve()
                .body(CatalogListingsResponse.class);
    }
}