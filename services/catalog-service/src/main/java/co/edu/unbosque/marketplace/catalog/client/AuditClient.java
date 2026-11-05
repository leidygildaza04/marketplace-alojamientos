package co.edu.unbosque.marketplace.catalog.client;

import co.edu.unbosque.marketplace.catalog.dto.CrearEventoAuditoriaRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class AuditClient {

    private final RestClient restClient;
    private final String internalSecret;

    public AuditClient(
            @Value("${services.backoffice.base-url:http://localhost:8084}") String baseUrl,
            @Value("${app.internal.secret}") String internalSecret) {
        this.restClient = RestClient.builder().baseUrl(baseUrl).build();
        this.internalSecret = internalSecret;
    }

    public void registrarActivacion(Long usuarioId, Long alojamientoId) {
        CrearEventoAuditoriaRequest request = new CrearEventoAuditoriaRequest(
                usuarioId,
                "ACTIVAR_ALOJAMIENTO",
                "ALOJAMIENTO",
                alojamientoId,
                "Activación del alojamiento",
                "EXITOSO"
        );

        restClient.post()
                .uri("/internal/auditoria/eventos")
                .header("X-Internal-Secret", internalSecret)
                .body(request)
                .retrieve()
                .toBodilessEntity();
    }
}
