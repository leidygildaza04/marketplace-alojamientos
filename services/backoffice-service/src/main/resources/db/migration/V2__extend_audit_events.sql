ALTER TABLE audit_events
    ADD COLUMN actor_label VARCHAR(120) NOT NULL DEFAULT 'Administrador';

CREATE INDEX idx_audit_events_created_at ON audit_events (created_at);
CREATE INDEX idx_audit_events_actor_id ON audit_events (actor_id);
