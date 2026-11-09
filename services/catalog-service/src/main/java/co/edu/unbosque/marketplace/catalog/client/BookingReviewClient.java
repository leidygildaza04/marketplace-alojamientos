package co.edu.unbosque.marketplace.catalog.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class BookingReviewClient {

    private final RestClient restClient;
    private final String internalSecret;

    public BookingReviewClient(
            @Value("${services.booking.base-url:http://localhost:8083}") String baseUrl,
            @Value("${app.internal.secret}") String internalSecret) {
        this.restClient = RestClient.builder().baseUrl(baseUrl).build();
        this.internalSecret = internalSecret;
    }

    public Integer enviarReservasARevisionManual(Long alojamientoId) {
        Integer cantidad = restClient.post()
                .uri("/internal/alojamientos/{id}/reservas/revision-manual", alojamientoId)
                .header("X-Internal-Secret", internalSecret)
                .retrieve()
                .body(Integer.class);

        return cantidad == null ? 0 : cantidad;
    }
}
