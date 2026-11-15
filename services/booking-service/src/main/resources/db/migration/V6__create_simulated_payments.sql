CREATE TABLE pagos (
    id_pago BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    id_reserva BIGINT NOT NULL,
    monto DECIMAL(12,2) NOT NULL,
    moneda CHAR(3) NOT NULL DEFAULT 'COP',
    estado VARCHAR(20) NOT NULL,
    marca_tarjeta VARCHAR(30) NOT NULL,
    ultimos_cuatro CHAR(4) NOT NULL,
    referencia VARCHAR(80) NOT NULL,
    codigo_respuesta VARCHAR(40) NOT NULL,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_pago_reserva FOREIGN KEY (id_reserva) REFERENCES reservas(id_reserva),
    UNIQUE INDEX uk_pago_referencia (referencia),
    INDEX idx_pago_reserva (id_reserva)
);
