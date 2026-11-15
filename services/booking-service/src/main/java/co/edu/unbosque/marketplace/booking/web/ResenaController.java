package co.edu.unbosque.marketplace.booking.web;

import co.edu.unbosque.marketplace.booking.dto.CrearResenaRequest;
import co.edu.unbosque.marketplace.booking.dto.ResenaResponse;
import co.edu.unbosque.marketplace.booking.dto.ResenasAlojamientoResponse;
import co.edu.unbosque.marketplace.booking.service.ResenaService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class ResenaController {
    private final ResenaService resenas;

    public ResenaController(ResenaService resenas) {
        this.resenas = resenas;
    }

    @PostMapping("/reservas/{reservaId}/resena")
    public ResenaResponse crear(@PathVariable Long reservaId, @Valid @RequestBody CrearResenaRequest request,
            Authentication authentication) {
        return resenas.crear(reservaId, Long.valueOf(authentication.getName()), request);
    }

    @GetMapping("/reservas/mias/resenas")
    public List<ResenaResponse> listarMias(Authentication authentication) {
        return resenas.listarMias(Long.valueOf(authentication.getName()));
    }

    @GetMapping("/alojamientos/{alojamientoId}/resenas")
    public ResenasAlojamientoResponse listarAlojamiento(@PathVariable Long alojamientoId) {
        return resenas.listarAlojamiento(alojamientoId);
    }
}
