CREATE TABLE IF NOT EXISTS `USERS` (
  `email` VARCHAR(100) PRIMARY KEY,
  `password` VARCHAR(256) NOT NULL,
  `uuid` VARCHAR(100) NOT NULL
);