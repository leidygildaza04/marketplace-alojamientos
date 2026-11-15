ALTER TABLE alojamientos
    ADD COLUMN tipo_alojamiento VARCHAR(50) NULL,
    ADD COLUMN reglas TEXT NULL,
    ADD COLUMN politica_cancelacion TEXT NULL;

CREATE TABLE alojamiento_servicios (
    id_alojamiento BIGINT NOT NULL,
    servicio VARCHAR(100) NOT NULL,
    CONSTRAINT fk_servicio_alojamiento
        FOREIGN KEY (id_alojamiento)
        REFERENCES alojamientos(id_alojamiento)
);

CREATE TABLE alojamiento_imagenes (
    id_alojamiento BIGINT NOT NULL,
    imagen_url VARCHAR(1000) NOT NULL,
    CONSTRAINT fk_imagen_alojamiento
        FOREIGN KEY (id_alojamiento)
        REFERENCES alojamientos(id_alojamiento)
);
