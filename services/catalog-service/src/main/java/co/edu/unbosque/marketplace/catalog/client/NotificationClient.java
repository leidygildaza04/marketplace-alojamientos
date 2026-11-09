package co.edu.unbosque.marketplace.catalog.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class NotificationClient {

    private final RestClient restClient;
    private final String internalSecret;

    public NotificationClient(
            @Value("${services.backoffice.base-url:http://localhost:8084}") String baseUrl,
            @Value("${app.internal.secret}") String internalSecret) {
        this.restClient = RestClient.builder().baseUrl(baseUrl).build();
        this.internalSecret = internalSecret;
    }

    public void notificarBloqueo(Long hostId, Long alojamientoId, String motivo) {
        NotificacionRequest request = new NotificacionRequest(
                hostId,
                "Publicación bloqueada",
                "Tu alojamiento fue bloqueado por un administrador. Motivo: " + motivo,
                "BLOQUEO_ALOJAMIENTO",
                alojamientoId
        );

        restClient.post()
                .uri("/internal/notificaciones")
                .header("X-Internal-Secret", internalSecret)
                .body(request)
                .retrieve()
                .toBodilessEntity();
    }

    private record NotificacionRequest(
            Long usuarioId,
            String titulo,
            String mensaje,
            String tipo,
            Long entidadId
    ) {
    }
}
