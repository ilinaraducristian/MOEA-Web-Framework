CREATE TABLE `USER_ENTITY`

(
    `id` char(36) PRIMARY KEY
);

CREATE TABLE `common_structures`
(
    `id`      int PRIMARY KEY AUTO_INCREMENT,
    `user_id` char(36),
    `type`    int          NOT NULL,
    `name`    varchar(255) NOT NULL,
    `md5`     char(32)     NOT NULL
);

CREATE TABLE `evaluations`
(
    `id`               int PRIMARY KEY AUTO_INCREMENT,
    `user_id`          char(36),
    `name`             varchar(255)                        NOT NULL,
    `nfe`              int                                 NOT NULL check (nfe >= 500),
    `seeds`            int                                 NOT NULL check (seeds >= 1),
    `algorithm_id`     int                                 NOT NULL,
    `problem_id`       int                                 NOT NULL,
    `reference_set_id` int                                 NOT NULL,
    `status`           ENUM ('created', 'running', 'done') NOT NULL,
    `results`          json
);

ALTER TABLE `common_structures`
    ADD FOREIGN KEY (`user_id`) REFERENCES `USER_ENTITY` (`id`);

ALTER TABLE `evaluations`
    ADD FOREIGN KEY (`user_id`) REFERENCES `USER_ENTITY` (`id`);

ALTER TABLE `evaluations`
    ADD FOREIGN KEY (`algorithm_id`) REFERENCES `common_structures` (`id`);

ALTER TABLE `evaluations`
    ADD FOREIGN KEY (`problem_id`) REFERENCES `common_structures` (`id`);

ALTER TABLE `evaluations`
    ADD FOREIGN KEY (`reference_set_id`) REFERENCES `common_structures` (`id`);


CREATE TRIGGER `type_constraint`
    BEFORE INSERT
    ON `evaluations`
    FOR EACH ROW

BEGIN

    SELECT `type`
    INTO @algorithm_type
    FROM `common_structures`
    WHERE `id` = `NEW`.`algorithm_id`;

    SELECT `type`
    INTO @problem_type
    FROM `common_structures`
    WHERE `id` = `NEW`.`problem_id`;

    SELECT `type`
    INTO @reference_set_type
    FROM `common_structures`
    WHERE `id` = `NEW`.`reference_set_id`;

    IF @algorithm_type <> 0 OR @problem_type <> 1 OR @reference_set_type <> 2 THEN
        SIGNAL SQLSTATE '10000'
            SET MESSAGE_TEXT = 'Common structure type violation';
    END IF;

END;

INSERT INTO `USER_ENTITY` (`id`)
VALUES ('cdd36e48-f1c5-474e-abc3-ac7a17909878');

INSERT INTO `common_structures`
VALUES (1, NULL, 0, 'CMA-ES', 'md5');
INSERT INTO `common_structures`
VALUES (2, NULL, 1, 'Belegundu', 'md5');
INSERT INTO `common_structures`
VALUES (3, NULL, 2, 'Belegundu', 'md5');

INSERT INTO `evaluations`
VALUES (1, null, 'Test evaluation', 10000, 10, 1, 2, 3, 'created', NULL);
