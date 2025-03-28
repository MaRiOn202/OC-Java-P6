CREATE DATABASE  IF NOT EXISTS `paymybuddy`;

USE `paymybuddy`;


--
-- Table structure for table `users_transactions`
--

DROP TABLE IF EXISTS `users_transactions`;

CREATE TABLE `users_transactions` (
  `transactions_id` bigint NOT NULL,
  `user_entity_id` bigint NOT NULL,
  UNIQUE KEY `UK3tuu1i9rikixyqa8up4nung8o` (`transactions_id`),
  KEY `FKdph513c5l3ijd1lg6upigbsfm` (`user_entity_id`),
  CONSTRAINT `FKdph513c5l3ijd1lg6upigbsfm` FOREIGN KEY (`user_entity_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKno65w0edysgj60afho4q6f0yy` FOREIGN KEY (`transactions_id`) REFERENCES `transactions` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin;



--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;

CREATE TABLE `users` (
  `sold` double NOT NULL DEFAULT '0',
  `id` bigint NOT NULL AUTO_INCREMENT,
  `email` varchar(255) COLLATE utf8mb3_bin NOT NULL,
  `password` varchar(255) COLLATE utf8mb3_bin NOT NULL,
  `role` varchar(255) COLLATE utf8mb3_bin NOT NULL,
  `username` varchar(255) COLLATE utf8mb3_bin NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin;



--
-- Table structure for table `transactions`
--

DROP TABLE IF EXISTS `transactions`;

CREATE TABLE `transactions` (
  `amount` double DEFAULT NULL,
  `percentage` double DEFAULT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `local_date_time` datetime(6) NOT NULL,
  `receiver_id` bigint DEFAULT NULL,
  `sender_id` bigint DEFAULT NULL,
  `description` varchar(255) COLLATE utf8mb3_bin DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK5nn8ird7idyxyxki68gox2wbx` (`receiver_id`),
  KEY `FK3ly4r8r6ubt0blftudix2httv` (`sender_id`),
  CONSTRAINT `FK3ly4r8r6ubt0blftudix2httv` FOREIGN KEY (`sender_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FK5nn8ird7idyxyxki68gox2wbx` FOREIGN KEY (`receiver_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin;



--
-- Table structure for table `users_connections`
--

DROP TABLE IF EXISTS `users_connections`;

CREATE TABLE `users_connections` (
  `connections_id` bigint NOT NULL,
  `user_entity_id` bigint NOT NULL,
  KEY `FKe3bfhd5qe7ci5cy2rjghcvm2q` (`connections_id`),
  KEY `FKjedgvfedw2b7ak7o6yqaeeolq` (`user_entity_id`),
  CONSTRAINT `FKe3bfhd5qe7ci5cy2rjghcvm2q` FOREIGN KEY (`connections_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKjedgvfedw2b7ak7o6yqaeeolq` FOREIGN KEY (`user_entity_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin;


