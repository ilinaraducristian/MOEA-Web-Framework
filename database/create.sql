CREATE TABLE `USER_ENTITY` (
  `id` char(36) PRIMARY KEY,
  `first_name` varchar(255),
  `last_name` varchar(255),
  `username` varchar(255),
  `password` varchar(255),
  `email` varchar(255)
);

CREATE TABLE `algorithms` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `user_id` char(36),
  `name` varchar(255) NOT NULL,
  `md5` char(32) NOT NULL
);

CREATE TABLE `problems` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `user_id` char(36),
  `name` varchar(255) NOT NULL,
  `md5` char(32) NOT NULL
);

CREATE TABLE `reference_sets` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `user_id` char(36),
  `name` varchar(255) NOT NULL,
  `md5` char(32) NOT NULL
);

CREATE TABLE `evaluations` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `user_id` char(36),
  `name` varchar(255),
  `nfe` int,
  `seeds` int,
  `algorithm_id` int,
  `problem_id` int,
  `reference_set_id` int,
  `status` ENUM ('created', 'running', 'done'),
  `results` json
);

ALTER TABLE `algorithms` ADD FOREIGN KEY (`user_id`) REFERENCES `USER_ENTITY` (`id`);

ALTER TABLE `problems` ADD FOREIGN KEY (`user_id`) REFERENCES `USER_ENTITY` (`id`);

ALTER TABLE `reference_sets` ADD FOREIGN KEY (`user_id`) REFERENCES `USER_ENTITY` (`id`);

ALTER TABLE `evaluations` ADD FOREIGN KEY (`user_id`) REFERENCES `USER_ENTITY` (`id`);

ALTER TABLE `evaluations` ADD FOREIGN KEY (`algorithm_id`) REFERENCES `algorithms` (`id`);

ALTER TABLE `evaluations` ADD FOREIGN KEY (`problem_id`) REFERENCES `problems` (`id`);

ALTER TABLE `evaluations` ADD FOREIGN KEY (`reference_set_id`) REFERENCES `reference_sets` (`id`);
