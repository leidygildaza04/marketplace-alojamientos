ALTER TABLE reservas
    ADD COLUMN clave_idempotencia VARCHAR(100) NULL;

CREATE UNIQUE INDEX uk_reserva_usuario_idempotencia
    ON reservas (id_usuario, clave_idempotencia);
