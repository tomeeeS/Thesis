--
-- Database
--
DROP USER IF EXISTS dbadmin@localhost;
DROP DATABASE IF EXISTS ride_share;
CREATE DATABASE IF NOT EXISTS `ride_share` DEFAULT CHARSET = utf8mb4 COLLATE utf8mb4_general_ci;
CREATE USER 'dbadmin'@'localhost' IDENTIFIED BY 'dbadmin';
GRANT SELECT, INSERT, UPDATE, DELETE, CREATE, DROP, FILE, INDEX, ALTER, CREATE TEMPORARY TABLES, CREATE VIEW, EVENT, TRIGGER, SHOW VIEW, CREATE ROUTINE, ALTER ROUTINE, EXECUTE
    ON *.* TO 'dbadmin'@'localhost' REQUIRE NONE WITH MAX_QUERIES_PER_HOUR 0 MAX_CONNECTIONS_PER_HOUR 0 MAX_UPDATES_PER_HOUR 0 MAX_USER_CONNECTIONS 0;
GRANT ALL PRIVILEGES ON `ride_share`.* TO 'dbadmin'@'localhost';
FLUSH PRIVILEGES;

USE `ride_share`;

CREATE TABLE `authorities` (
    `felhasznalo_email` varchar(40) NOT NULL,
    INDEX (felhasznalo_email)
);
CREATE TABLE `felhasznalo` (
    `id` int(11) NOT NULL,
    `email` varchar(40) NOT NULL,
    PRIMARY KEY (id),
    INDEX(email)
);
CREATE TABLE `fuvar` (
    `id` int(11) NOT NULL,
    `sofor_id` int(11) NOT NULL,
    PRIMARY KEY (id)
);

-- utasok-fuvar junction table
CREATE TABLE `felhasznalo_fuvar` (
    `utas_id` int(11) NOT NULL,
    `fuvar_id` int(11) NOT NULL,
    PRIMARY KEY (`utas_id`,`fuvar_id`)
);