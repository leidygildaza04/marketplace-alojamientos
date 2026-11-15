ALTER TABLE alojamientos
    ADD COLUMN tarifa_limpieza DECIMAL(12,2) NOT NULL DEFAULT 0.00;

-- Conserva la tarifa guardada con la columna tarifa_aseo de V2.
UPDATE alojamientos
    SET tarifa_limpieza = tarifa_aseo;

CREATE TABLE alojamiento_servicios_adicionales (
    id_servicio_adicional BIGINT NOT NULL AUTO_INCREMENT,
    id_alojamiento BIGINT NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    precio DECIMAL(12,2) NOT NULL,
    PRIMARY KEY (id_servicio_adicional),
    CONSTRAINT fk_servicio_adicional_alojamiento
        FOREIGN KEY (id_alojamiento)
        REFERENCES alojamientos(id_alojamiento),
    CONSTRAINT chk_servicio_adicional_precio
        CHECK (precio >= 0)
);
