CREATE TABLE notification_entity
(
    notification_id BIGSERIAL PRIMARY KEY,
    user_id         BIGINT    NOT NULL,
    pay_load_id     BIGINT    NOT NULL,
    is_read         BOOLEAN   NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMP NOT NULL,
    read_at         TIMESTAMP
);

CREATE TABLE notification_event_payload_entity
(
    payload_id    BIGSERIAL PRIMARY KEY,
    message_id    UUID         NOT NULL,
    event_type    VARCHAR(255) NOT NULL,
    event_id      BIGINT       NOT NULL,
    occurred_at   TIMESTAMP    NOT NULL,
    changed_by_id BIGINT       NOT NULL,
    owner_id      BIGINT       NOT NULL,
    payload       JSON
);