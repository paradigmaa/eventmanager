CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       login VARCHAR(255) NOT NULL UNIQUE,
                       password_hash VARCHAR(255) NOT NULL,
                       age INTEGER,
                       role VARCHAR(50) NOT NULL
);