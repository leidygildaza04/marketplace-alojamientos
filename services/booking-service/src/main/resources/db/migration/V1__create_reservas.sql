CREATE TABLE reservas (
 id_reserva BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
 id_usuario BIGINT NOT NULL,
 id_alojamiento BIGINT NOT NULL,
 fecha_entrada DATE NOT NULL,
 fecha_salida DATE NOT NULL,
 huespedes INT NOT NULL DEFAULT 1,
 total DECIMAL(12,2) NULL,
 estado VARCHAR(30) NOT NULL,
 fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 INDEX idx_reserva_usuario_estado_fechas (id_usuario, estado, fecha_entrada, fecha_salida)
);