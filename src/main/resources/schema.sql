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
    sha256 VARCHAR(255) NOT NULL
);

CREATE TABLE problems_users
(
    id         INT IDENTITY,
    user_id    INT NOT NULL,
    problem_id INT NOT NULL
);

CREATE TABLE reference_sets
(
    id     INT IDENTITY,
    name   VARCHAR(255) NOT NULL,
    sha256 VARCHAR(255) NOT NULL
);

CREATE TABLE reference_sets_users
(
    id               INT IDENTITY,
    user_id          INT NOT NULL,
    reference_set_id INT NOT NULL
);

CREATE TABLE algorithms
(
    id     INT IDENTITY,
    name   VARCHAR(255) NOT NULL,
    sha256 VARCHAR(255) NOT NULL
);

CREATE TABLE algorithms_users
(
    id           INT IDENTITY,
    user_id      INT NOT NULL,
    algorithm_id INT NOT NULL
);

CREATE TABLE processes
(
    id                    INT IDENTITY,
    name                  VARCHAR(255),
    number_of_evaluations INT NOT NULL,
    number_of_seeds       INT NOT NULL,
    status                VARCHAR(255),
    rabbit_id             VARCHAR(255),
    results               VARCHAR(255),
    algorithm_sha256      VARCHAR(255) NOT NULL,
    problem_sha256        VARCHAR(255) NOT NULL,
    reference_set_sha256  VARCHAR(255) NOT NULL,
    user_id               INT NOT NULL
);
