CREATE TABLE users
(
    id         INT IDENTITY,
    username   VARCHAR(255) NOT NULL,
    password   VARCHAR(255) NOT NULL,
    email      VARCHAR(255) NOT NULL,
    first_name VARCHAR(255) NOT NULL,
    last_name  VARCHAR(255)
);

CREATE TABLE problems
(
    id     INT IDENTITY,
    name   VARCHAR(255) NOT NULL,
    sha256 VARCHAR(255) NULL
);

CREATE TABLE problems_users
(
    id         INT IDENTITY,
    user_id    INT NOT NULL,
    problem_id INT NOT NULL
);