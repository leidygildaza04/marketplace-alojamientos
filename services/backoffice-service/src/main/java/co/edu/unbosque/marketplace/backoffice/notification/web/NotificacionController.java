package co.edu.unbosque.marketplace.backoffice.notification.web;

import co.edu.unbosque.marketplace.backoffice.notification.dto.CrearNotificacionRequest;
import co.edu.unbosque.marketplace.backoffice.notification.dto.NotificacionResponse;
import co.edu.unbosque.marketplace.backoffice.notification.service.NotificacionService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/notificaciones")
public class NotificacionController {

    private final NotificacionService service;

    public NotificacionController(NotificacionService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NotificacionResponse crear(@Valid @RequestBody CrearNotificacionRequest request) {
        return service.crear(request);
    }

    @GetMapping("/usuario/{usuarioId}")
    public List<NotificacionResponse> listarPorUsuario(@PathVariable Long usuarioId) {
        return service.listarPorUsuario(usuarioId);
    }

    @PatchMapping("/usuario/{usuarioId}/{notificacionId}/leida")
    public NotificacionResponse marcarComoLeida(
            @PathVariable Long usuarioId,
            @PathVariable Long notificacionId) {
        return service.marcarComoLeida(usuarioId, notificacionId);
    }
}
