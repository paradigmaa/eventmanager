CREATE TABLE event_entity (
                              id BIGSERIAL PRIMARY KEY,
                              name VARCHAR(255) NOT NULL,
                              date_time TIMESTAMP NOT NULL,
                              cost DECIMAL(19,2) NOT NULL,
                              duration INTEGER NOT NULL,
                              max_places INTEGER NOT NULL,
                              owner BIGINT NOT NULL REFERENCES users(id),
                              location_entity BIGINT NOT NULL REFERENCES location(id),
                              status VARCHAR(50) NOT NULL
);