-- Economy MySQL Schema

CREATE TABLE `{prefix}account` (
  `uuid`       VARCHAR(36)        NOT NULL,
  `balance`    BIGINT             DEFAULT 0,
  PRIMARY KEY (`uuid`)
) DEFAULT CHARSET = utf8mb4;

CREATE TABLE `{prefix}actions` (
  `id`         INT AUTO_INCREMENT NOT NULL,
  `time`       BIGINT             NOT NULL,
  `actor_uuid` VARCHAR(36)        NOT NULL,
  `actor_name` VARCHAR(100)       NOT NULL,
  `type`       CHAR(1)            NOT NULL,
  `acted_uuid` VARCHAR(36)        NOT NULL,
  `acted_name` VARCHAR(36)        NOT NULL,
  `action`     VARCHAR(300)       NOT NULL,
  PRIMARY KEY (`id`)
) DEFAULT CHARSET = utf8mb4;