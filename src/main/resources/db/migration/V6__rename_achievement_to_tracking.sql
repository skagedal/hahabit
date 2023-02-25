ALTER TABLE achievements
    RENAME TO trackings;

ALTER TABLE trackings
    RENAME achieving_habit TO habit_id;
