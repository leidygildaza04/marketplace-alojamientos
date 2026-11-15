package co.edu.unbosque.marketplace.booking.dto;

import co.edu.unbosque.marketplace.booking.domain.Resena;
import java.time.Instant;

public record ResenaResponse(Long id, Long reservaId, Long alojamientoId, Long usuarioId,
        int calificacion, String comentario, Instant fechaCreacion, boolean visible) {
    public static ResenaResponse desde(Resena resena) {
        return new ResenaResponse(resena.getId(), resena.getReservaId(), resena.getAlojamientoId(),
                resena.getUserId(), resena.getCalificacion(), resena.getComentario(),
                resena.getFechaCreacion(), resena.isVisible());
    }
}
