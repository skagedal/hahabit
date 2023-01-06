CREATE TABLE achievements
(
    id              SERIAL PRIMARY KEY,
    achieving_habit INTEGER,
    date            DATE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT achieving_habit_fk FOREIGN KEY (achieving_habit) REFERENCES habits (id),
    UNIQUE (achieving_habit, date)
);