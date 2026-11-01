CREATE TABLE account_status_history (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    old_status VARCHAR(20) NOT NULL,
    new_status VARCHAR(20) NOT NULL,
    changed_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    INDEX idx_account_status_history_user (user_id),
    CONSTRAINT fk_account_status_history_user
        FOREIGN KEY (user_id) REFERENCES users(id)
);
