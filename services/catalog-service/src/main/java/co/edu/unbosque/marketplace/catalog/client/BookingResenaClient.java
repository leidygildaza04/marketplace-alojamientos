package co.edu.unbosque.marketplace.catalog.client;

import co.edu.unbosque.marketplace.catalog.dto.ResumenResenasResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class BookingResenaClient {

    private final RestClient restClient;
    private final String internalSecret;

    public BookingResenaClient(
            @Value("${services.booking.base-url:http://localhost:8083}") String baseUrl,
            @Value("${app.internal.secret}") String internalSecret
    ) {
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
        this.internalSecret = internalSecret;
    }

    public ResumenResenasResponse consultarResenas(Long alojamientoId) {
        return restClient.get()
                .uri("/internal/alojamientos/{alojamientoId}/resenas", alojamientoId)
                .header("X-Internal-Secret", internalSecret)
                .retrieve()
                .body(ResumenResenasResponse.class);
    }
}