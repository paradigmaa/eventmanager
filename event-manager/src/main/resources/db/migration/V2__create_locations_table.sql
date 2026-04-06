CREATE TABLE location (
                          id BIGSERIAL PRIMARY KEY,
                          name VARCHAR(255) NOT NULL,
                          address VARCHAR(255) NOT NULL,
                          capacity INTEGER NOT NULL,
                          description TEXT NOT NULL
);