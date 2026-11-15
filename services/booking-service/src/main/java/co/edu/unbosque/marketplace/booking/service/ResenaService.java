package co.edu.unbosque.marketplace.booking.service;

import co.edu.unbosque.marketplace.booking.domain.Resena;
import co.edu.unbosque.marketplace.booking.dto.CrearResenaRequest;
import co.edu.unbosque.marketplace.booking.dto.ResenaResponse;
import co.edu.unbosque.marketplace.booking.dto.ResenasAlojamientoResponse;
import co.edu.unbosque.marketplace.booking.repository.ResenaRepository;
import co.edu.unbosque.marketplace.booking.repository.ReservaRepository;
import java.time.LocalDate;
import java.time.ZoneId;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ResenaService {
    private static final ZoneId ZONA_HORARIA = ZoneId.of("America/Bogota");
    private final ReservaRepository reservas;
    private final ResenaRepository resenas;

    public ResenaService(ReservaRepository reservas, ResenaRepository resenas) {
        this.reservas = reservas;
        this.resenas = resenas;
    }

    @Transactional
    public ResenaResponse crear(Long reservaId, Long userId, CrearResenaRequest request) {
        var reserva = reservas.findById(reservaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "La reserva no existe"));
        if (!reserva.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Solo puedes calificar tus propias reservas");
        }
        if ("CONFIRMADA".equals(reserva.getEstado())
                && !reserva.getCheckOut().isAfter(LocalDate.now(ZONA_HORARIA))) {
            reserva.cambiarEstado("COMPLETADA");
        }
        if (!"COMPLETADA".equals(reserva.getEstado())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Solo puedes calificar una estancia terminada");
        }
        if (request.calificacion() < 1 || request.calificacion() > 5) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La calificación debe ser de 1 a 5 estrellas");
        }
        if (resenas.existsByReservaId(reservaId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Esta reserva ya tiene una calificación");
        }
        String comentario = request.comentario() == null ? null : request.comentario().trim();
        if (comentario != null && comentario.isBlank()) comentario = null;
        return ResenaResponse.desde(resenas.save(new Resena(reservaId, userId,
                reserva.getAlojamientoId(), request.calificacion(), comentario)));
    }

    @Transactional(readOnly = true)
    public ResenasAlojamientoResponse listarAlojamiento(Long alojamientoId) {
        var items = resenas.findByAlojamientoIdAndVisibleTrueOrderByFechaCreacionDesc(alojamientoId)
                .stream().map(ResenaResponse::desde).toList();
        double average = items.stream().mapToInt(ResenaResponse::calificacion).average().orElse(0.0);
        return new ResenasAlojamientoResponse(Math.round(average * 10.0) / 10.0, items.size(), items);
    }

    @Transactional(readOnly = true)
    public java.util.List<ResenaResponse> listarMias(Long userId) {
        return resenas.findByUserIdOrderByFechaCreacionDesc(userId).stream().map(ResenaResponse::desde).toList();
    }

    @Transactional(readOnly = true)
    public java.util.List<ResenaResponse> listarTodas() {
        return resenas.findAllByOrderByFechaCreacionDesc().stream().map(ResenaResponse::desde).toList();
    }

    @Transactional
    public ResenaResponse cambiarVisibilidad(Long reviewId, boolean visible) {
        var review = resenas.findById(reviewId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "La reseña no existe"));
        review.setVisible(visible);
        return ResenaResponse.desde(resenas.save(review));
    }
}
