# --- !Ups

create table `user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `name` TEXT NOT NULL
);


# --- !Downs

DROP TABLE `user`;