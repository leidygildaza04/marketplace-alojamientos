CREATE TABLE evento_auditoria (
    id_evento BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_usuario BIGINT NULL,
    accion VARCHAR(100) NOT NULL,
    entidad VARCHAR(100) NOT NULL,
    id_entidad BIGINT NOT NULL,
    detalle TEXT,
    resultado VARCHAR(50) NOT NULL,
    fecha_evento TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
