CREATE TABLE `common_structures`
(
    `id`      int PRIMARY KEY AUTO_INCREMENT,
    `user_id` char(36),
    `type`    int          NOT NULL,
    `name`    varchar(255) NOT NULL,
    `md5`     char(32)     NOT NULL
) $$

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
) $$

ALTER TABLE `common_structures`
    ADD FOREIGN KEY (`user_id`) REFERENCES `USER_ENTITY` (`id`) $$

ALTER TABLE `evaluations`
    ADD FOREIGN KEY (`user_id`) REFERENCES `USER_ENTITY` (`id`) $$

ALTER TABLE `evaluations`
    ADD FOREIGN KEY (`algorithm_id`) REFERENCES `common_structures` (`id`) $$

ALTER TABLE `evaluations`
    ADD FOREIGN KEY (`problem_id`) REFERENCES `common_structures` (`id`) $$

ALTER TABLE `evaluations`
    ADD FOREIGN KEY (`reference_set_id`) REFERENCES `common_structures` (`id`) $$

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

    IF @algorithm_type <> 1 OR @problem_type <> 2 OR @reference_set_type <> 3 THEN
        SIGNAL SQLSTATE '10000'
            SET MESSAGE_TEXT = 'Common structure type violation';
    END IF;

END $$