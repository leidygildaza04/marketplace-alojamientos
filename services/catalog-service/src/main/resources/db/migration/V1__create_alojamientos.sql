CREATE TABLE alojamientos (
 id_alojamiento BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
 id_anfitrion BIGINT NOT NULL,
 titulo VARCHAR(180) NOT NULL,
 descripcion TEXT NULL,
 direccion VARCHAR(255) NULL,
 ciudad VARCHAR(100) NULL,
 precio_noche DECIMAL(12,2) NULL,
 capacidad INT NULL,
 estado_publicacion VARCHAR(30) NOT NULL DEFAULT 'BORRADOR',
 fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 INDEX idx_alojamiento_anfitrion_estado (id_anfitrion, estado_publicacion)
);