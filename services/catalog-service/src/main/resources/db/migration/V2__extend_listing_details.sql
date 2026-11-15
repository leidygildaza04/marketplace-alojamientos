ALTER TABLE alojamientos
    ADD COLUMN tipo VARCHAR(60) NOT NULL DEFAULT 'Alojamiento',
    ADD COLUMN imagen_url VARCHAR(1024) NULL,
    ADD COLUMN servicios TEXT NULL,
    ADD COLUMN reglas TEXT NULL,
    ADD COLUMN politica_cancelacion VARCHAR(30) NOT NULL DEFAULT 'Flexible',
    ADD COLUMN tarifa_aseo DECIMAL(12, 2) NOT NULL DEFAULT 0;
