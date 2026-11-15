-- reglas y politica_cancelacion ya existen desde V2__extend_listing_details;
-- aqui solo se ajusta politica_cancelacion al modelo actual (texto opcional).
ALTER TABLE alojamientos
    ADD COLUMN tipo_alojamiento VARCHAR(50) NULL;

ALTER TABLE alojamientos
    MODIFY COLUMN politica_cancelacion TEXT NULL;

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

-- Conserva los datos guardados con las columnas de V2 (tipo e imagen_url).
UPDATE alojamientos
    SET tipo_alojamiento = LEFT(tipo, 50);

INSERT INTO alojamiento_imagenes (id_alojamiento, imagen_url)
    SELECT id_alojamiento, imagen_url
    FROM alojamientos
    WHERE imagen_url IS NOT NULL
      AND imagen_url <> ''
      AND CHAR_LENGTH(imagen_url) <= 1000;
