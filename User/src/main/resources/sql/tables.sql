-- this SQL script is not use in SpringBoot --
-- only use this as reference of tables --

CREATE TABLE `user` (
    `id` BIGINT(20) AUTO_INCREMENT PRIMARY KEY,
    `nickname` VARCHAR(30) NOT NULL,
    `created_at` TIMESTAMP NOT NULL
);

CREATE TABLE `account` (
    `id` BIGINT(20) AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT(20) NOT NULL,
    `vendor` ENUM('GOOGLE') NOT NULL,
    `local_part` VARCHAR(40) NOT NULL,
    `created_at` TIMESTAMP NOT NULL,
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE
);