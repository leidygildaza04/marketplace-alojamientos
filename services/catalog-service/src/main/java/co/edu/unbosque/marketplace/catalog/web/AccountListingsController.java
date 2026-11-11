
package co.edu.unbosque.marketplace.catalog.web;

import co.edu.unbosque.marketplace.catalog.repository.AlojamientoRepository;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/accounts")
public class AccountListingsController {

    private final AlojamientoRepository alojamientos;

    public AccountListingsController(AlojamientoRepository alojamientos) {
        this.alojamientos = alojamientos;
    }

    @GetMapping("/{userId}/listings")
    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, Object>> listings(
            @PathVariable Long userId) {

        var ids = alojamientos.findByHostId(userId)
                .stream()
                .map(alojamiento -> alojamiento.getId())
                .toList();

        return ResponseEntity.ok(
                Map.of(
                        "userId", userId,
                        "accommodationIds", ids,
                        "count", ids.size()
                )
        );
    }

    @PostMapping("/{userId}/pause-listings")
    @Transactional
    public ResponseEntity<Map<String, Object>> pause(
            @PathVariable Long userId) {

        var active = alojamientos.findByHostIdAndEstado(userId, "ACTIVA");

        active.forEach(a -> a.cambiarEstadoA("PAUSADA"));
        alojamientos.saveAll(active);

        return ResponseEntity.ok(
                Map.of(
                        "userId", userId,
                        "pausedCount", active.size(),
                        "status", "OK"
                )
        );
    }
}
