CREATE TABLE notificaciones (
    id BIGINT NOT NULL AUTO_INCREMENT,
    usuario_id BIGINT NOT NULL,
    titulo VARCHAR(160) NOT NULL,
    mensaje VARCHAR(2000) NOT NULL,
    tipo VARCHAR(60) NOT NULL,
    entidad_id BIGINT NULL,
    leida BOOLEAN NOT NULL DEFAULT FALSE,
    fecha_creacion TIMESTAMP NOT NULL,
    CONSTRAINT pk_notificaciones PRIMARY KEY (id)
);

CREATE INDEX idx_notificaciones_usuario_fecha
    ON notificaciones (usuario_id, fecha_creacion);

CREATE INDEX idx_notificaciones_usuario_leida
    ON notificaciones (usuario_id, leida);
