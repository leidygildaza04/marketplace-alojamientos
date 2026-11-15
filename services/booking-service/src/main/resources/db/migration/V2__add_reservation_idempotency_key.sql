ALTER TABLE reservas
    ADD COLUMN idempotency_key VARCHAR(100) NULL;

CREATE UNIQUE INDEX uk_reserva_idempotency_key
    ON reservas (idempotency_key);
