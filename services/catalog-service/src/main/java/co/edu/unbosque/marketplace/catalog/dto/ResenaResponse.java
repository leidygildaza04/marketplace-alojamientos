package co.edu.unbosque.marketplace.catalog.dto;

import java.time.LocalDateTime;

public record ResenaResponse(
        Long id,
        Long usuarioId,
        Integer calificacion,
        String comentario,
        LocalDateTime fechaCreacion
) {
}