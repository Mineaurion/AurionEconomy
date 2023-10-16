-- Economy MySQL Schema

CREATE TABLE `{prefix}accounts` (
  `uuid`       VARCHAR(36)        NOT NULL,
  `balance`    BIGINT             DEFAULT 0,
  PRIMARY KEY (`uuid`)
) DEFAULT CHARSET = utf8mb4;


CREATE table `{prefix}transactions` (
  `id`              INT AUTO_INCREMENT NOT NULL,
  `time`            DATETIME NOT NULL,
  `sender_name`     VARCHAR(255) NOT NULL,
  `sender_uuid`     VARCHAR(255) NULL,
  `receiver_name`   VARCHAR(255) NULL,
  `receiver_uuid`   VARCHAR(255) NULL,
  `amount`          INT NOT NULL,
  `type`            VARCHAR(255),
  `comments`        VARCHAR(255),
  PRIMARY KEY (`id`)
) DEFAULT CHARSET = utf8mb4;