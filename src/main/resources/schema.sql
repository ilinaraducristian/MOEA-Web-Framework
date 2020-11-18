CREATE TABLE users
(
    id       SERIAL PRIMARY KEY NOT NULL,
    username TEXT               NOT NULL
);


CREATE TABLE algorithms
(
    id     SERIAL PRIMARY KEY NOT NULL,
    name   TEXT               NOT NULL,
    sha256 TEXT               NOT NULL
);

CREATE TABLE algorithms_users
(
    id           SERIAL PRIMARY KEY NOT NULL,
    user_id      INT                NOT NULL,
    algorithm_id INT                NOT NULL
);


CREATE TABLE problems
(
    id                   SERIAL PRIMARY KEY NOT NULL,
    name                 TEXT               NOT NULL,
    problem_sha256       TEXT               NOT NULL,
    reference_set_sha256 TEXT               NOT NULL
);


CREATE TABLE problems_users
(
    id         SERIAL PRIMARY KEY NOT NULL,
    user_id    INT                NOT NULL,
    problem_id INT                NOT NULL
);


CREATE TABLE processes
(
    id                    SERIAL PRIMARY KEY NOT NULL,
    name                  TEXT,
    number_of_evaluations INT                NOT NULL,
    number_of_seeds       INT                NOT NULL,
    status                TEXT,
    rabbit_id             TEXT,
    results               TEXT,
    algorithm_sha256      TEXT               NOT NULL,
    problem_sha256        TEXT               NOT NULL,
    reference_set_sha256  TEXT               NOT NULL,
    user_id               INT                NOT NULL
);