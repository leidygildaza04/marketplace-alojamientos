
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
        registrarEvento(
                usuarioId,
                alojamientoId,
                "ACTIVAR_ALOJAMIENTO",
                "Activación del alojamiento"
        );
    }

    public void registrarEdicion(Long usuarioId, Long alojamientoId) {
        registrarEvento(
                usuarioId,
                alojamientoId,
                "EDITAR_ALOJAMIENTO",
                "Edición de los datos del alojamiento"
        );
    }

    public void registrarPausa(Long usuarioId, Long alojamientoId) {
        registrarEvento(
                usuarioId,
                alojamientoId,
                "PAUSAR_ALOJAMIENTO",
                "Pausa de la publicación del alojamiento"
        );
    }

    private void registrarEvento(
            Long usuarioId,
            Long alojamientoId,
            String accion,
            String detalle
    ) {
        CrearEventoAuditoriaRequest request =
                new CrearEventoAuditoriaRequest(
                        usuarioId,
                        accion,
                        "ALOJAMIENTO",
                        alojamientoId,
                        detalle,
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
