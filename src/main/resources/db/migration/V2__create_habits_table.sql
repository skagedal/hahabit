CREATE TABLE habits
(
    id          SERIAL PRIMARY KEY,
    description TEXT,
    owned_by    VARCHAR(50),
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT owned_by_fk FOREIGN KEY (owned_by) REFERENCES users (username)
);