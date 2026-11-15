package co.edu.unbosque.marketplace.booking.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class CatalogPricingClient {

    private final RestClient restClient;
    private final String internalSecret;

    public CatalogPricingClient(
            @Value("${services.catalog.base-url:http://localhost:8082}") String baseUrl,
            @Value("${app.internal.secret}") String internalSecret) {
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
        this.internalSecret = internalSecret;
    }

    public AlojamientoPreciosResponse consultarPrecios(Long alojamientoId) {
        return restClient.get()
                .uri("/internal/alojamientos/{alojamientoId}/precios", alojamientoId)
                .header("X-Internal-Secret", internalSecret)
                .retrieve()
                .body(AlojamientoPreciosResponse.class);
    }
}