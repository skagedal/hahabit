-- This creates an admin user with the password admin. Needs to be changed when deployed.
INSERT INTO users (username, password, enabled)
VALUES ('admin','{bcrypt}$2a$10$DmSEyHaX4d3EiZlhUk7Exe5PsF2TMrGg7uzI1mharr5z4p88Tl5Iq',true);

INSERT INTO authorities (username, authority)
VALUES ('admin', 'ROLE_USER'),
       ('admin', 'ROLE_ADMIN');
