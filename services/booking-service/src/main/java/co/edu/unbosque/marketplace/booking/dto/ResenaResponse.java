package co.edu.unbosque.marketplace.booking.dto;

import co.edu.unbosque.marketplace.booking.domain.Resena;
import java.time.Instant;

public record ResenaResponse(Long id, Long reservaId, Long alojamientoId,
        int calificacion, String comentario, Instant fechaCreacion, boolean visible) {
    public static ResenaResponse desde(Resena resena) {
        return new ResenaResponse(resena.getId(), resena.getReservaId(), resena.getAlojamientoId(),
                resena.getCalificacion(), resena.getComentario(), resena.getFechaCreacion(), resena.isVisible());
import java.time.LocalDateTime;

public record ResenaResponse(
        Long id,
        Long usuarioId,
        Integer calificacion,
        String comentario,
        LocalDateTime fechaCreacion
) {
    public static ResenaResponse desde(Resena resena) {
        return new ResenaResponse(
                resena.getId(),
                resena.getUsuarioId(),
                resena.getCalificacion(),
                resena.getComentario(),
                resena.getFechaCreacion()
        );
    }
}
