CREATE TABLE users
(
    id         SERIAL PRIMARY KEY,
    email      VARCHAR(355) UNIQUE NOT NULL,
    password   VARCHAR(50)         NOT NULL,
    created_at TIMESTAMP           NOT NULL
);
