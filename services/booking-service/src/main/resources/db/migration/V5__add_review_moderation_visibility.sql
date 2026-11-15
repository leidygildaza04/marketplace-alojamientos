ALTER TABLE resenas
    ADD COLUMN visible BOOLEAN NOT NULL DEFAULT TRUE;

CREATE INDEX idx_resena_visible_fecha ON resenas (visible, fecha_creacion);
