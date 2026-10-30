package co.edu.unbosque.marketplace.identity.service;

import java.util.Map;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class AccountServicesClient {

    private final RestClient catalog;
    private final RestClient booking;

    public AccountServicesClient(RestClient.Builder builder, Environment env) {
        this.catalog = builder
                .baseUrl(env.getProperty("app.services.catalog-url", "http://localhost:8082"))
                .build();
        this.booking = builder
                .baseUrl(env.getProperty("app.services.booking-url", "http://localhost:8083"))
                .build();
    }

    public int countFutureConfirmedBookings(Long userId) {
        Map<String, Object> result = booking.get()
                .uri("/internal/accounts/{id}/future-confirmed-bookings", userId)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
        return ((Number) result.getOrDefault("count", 0)).intValue();
    }

    public void pauseActiveListings(Long userId) {
        catalog.post()
                .uri("/internal/accounts/{id}/pause-listings", userId)
                .retrieve()
                .toBodilessEntity();
    }
}
