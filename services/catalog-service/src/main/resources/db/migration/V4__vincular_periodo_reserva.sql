ALTER TABLE periodos_disponibilidad
    ADD COLUMN id_reserva BIGINT NULL;

CREATE UNIQUE INDEX uk_periodos_disponibilidad_reserva
    ON periodos_disponibilidad (id_reserva);
