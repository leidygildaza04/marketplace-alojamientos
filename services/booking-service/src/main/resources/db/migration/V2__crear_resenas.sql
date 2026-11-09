CREATE TABLE resenas (
    id_resena BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    id_alojamiento BIGINT NOT NULL,
    id_usuario BIGINT NOT NULL,
    calificacion INT NOT NULL,
    comentario TEXT NULL,
    visible BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_resena_calificacion
        CHECK (calificacion >= 1 AND calificacion <= 5),

    INDEX idx_resena_alojamiento_visible_fecha
        (id_alojamiento, visible, fecha_creacion)
);
