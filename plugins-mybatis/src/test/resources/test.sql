CREATE
    DATABASE db1;
USE
    db1;
CREATE TABLE IF NOT EXISTS `test`
(
    id  bigint primary key auto_increment,
    `key` text
) ENGINE=InnoDB ;