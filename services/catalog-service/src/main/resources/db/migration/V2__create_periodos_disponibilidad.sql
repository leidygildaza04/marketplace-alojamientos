CREATE TABLE periodos_disponibilidad (
    id_periodo BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    id_alojamiento BIGINT NOT NULL,
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'DISPONIBLE',
    CONSTRAINT fk_periodo_alojamiento
        FOREIGN KEY (id_alojamiento) REFERENCES alojamientos(id_alojamiento),
    INDEX idx_periodo_alojamiento_fechas (id_alojamiento, fecha_inicio, fecha_fin)
);
