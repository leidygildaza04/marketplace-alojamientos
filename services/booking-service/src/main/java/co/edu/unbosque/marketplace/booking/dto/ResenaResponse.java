package co.edu.unbosque.marketplace.booking.dto;

import co.edu.unbosque.marketplace.booking.domain.Resena;
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
