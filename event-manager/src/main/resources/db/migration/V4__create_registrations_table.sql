CREATE TABLE registration_entity (
                                     id BIGSERIAL PRIMARY KEY,
                                     event_id BIGINT NOT NULL REFERENCES event_entity(id),
                                     user_id BIGINT NOT NULL REFERENCES users(id)
);