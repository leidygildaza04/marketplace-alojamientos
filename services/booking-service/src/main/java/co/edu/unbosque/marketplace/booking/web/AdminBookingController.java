package co.edu.unbosque.marketplace.booking.web;

import co.edu.unbosque.marketplace.booking.dto.ResenaResponse;
import co.edu.unbosque.marketplace.booking.repository.ResenaRepository;
import co.edu.unbosque.marketplace.booking.repository.ReservaRepository;
import co.edu.unbosque.marketplace.booking.service.ResenaService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminBookingController {
    private final ReservaRepository reservas;
    private final ResenaRepository resenas;
    private final ResenaService resenaService;

    public AdminBookingController(ReservaRepository reservas, ResenaRepository resenas, ResenaService resenaService) {
        this.reservas = reservas;
        this.resenas = resenas;
        this.resenaService = resenaService;
    }

    @GetMapping("/metricas")
    public AdminMetrics metricas() {
        return new AdminMetrics(reservas.count(), reservas.countByEstado("CONFIRMADA"),
                reservas.countByEstado("COMPLETADA"), reservas.countByEstado("CANCELADA"),
                reservas.countByEstado("PENDIENTE"), resenas.countByVisibleTrue(),
                resenas.promedioVisible() == null ? 0.0 : Math.round(resenas.promedioVisible() * 10.0) / 10.0,
                reservas.sumarIngresosRegistrados());
    }

    @GetMapping("/resenas")
    public List<ResenaResponse> listarResenas() {
        return resenaService.listarTodas();
    }

    @PatchMapping("/resenas/{reviewId}/visibilidad")
    public ResenaResponse cambiarVisibilidad(@PathVariable Long reviewId,
            @Valid @RequestBody CambiarVisibilidadRequest request) {
        if (resenas.findById(reviewId).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "La reseña no existe");
        }
        return resenaService.cambiarVisibilidad(reviewId, request.visible());
    }

    public record CambiarVisibilidadRequest(@NotNull Boolean visible) {}

    public record AdminMetrics(long totalReservas, long reservasConfirmadas, long estanciasCompletadas,
            long reservasCanceladas, long reservasPendientes, long resenasVisibles,
            double calificacionPromedio, BigDecimal ingresosRegistrados) {}
}
