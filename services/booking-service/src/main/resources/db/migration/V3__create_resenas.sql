CREATE TABLE resenas (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    id_reserva BIGINT NOT NULL,
    id_usuario BIGINT NOT NULL,
    id_alojamiento BIGINT NOT NULL,
    calificacion TINYINT NOT NULL,
    comentario VARCHAR(1000) NULL,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_resena_reserva UNIQUE (id_reserva),
    CONSTRAINT fk_resena_reserva FOREIGN KEY (id_reserva) REFERENCES reservas(id_reserva),
    CONSTRAINT chk_resena_calificacion CHECK (calificacion BETWEEN 1 AND 5),
    INDEX idx_resena_alojamiento_fecha (id_alojamiento, fecha_creacion)
);
