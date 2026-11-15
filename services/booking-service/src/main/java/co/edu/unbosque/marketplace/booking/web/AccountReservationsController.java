package co.edu.unbosque.marketplace.booking.web;

import co.edu.unbosque.marketplace.booking.dto.ReservaResponse;
import co.edu.unbosque.marketplace.booking.service.ReservaService;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/reservas")
public class AccountReservationsController {

    private final ReservaService reservas;

    public AccountReservationsController(ReservaService reservas) {
        this.reservas = reservas;
    }

    @GetMapping("/mias")
    public List<ReservaResponse> listar(Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());
        return reservas.listarMias(userId);
    }
}
