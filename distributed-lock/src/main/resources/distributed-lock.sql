CREATE
    DATABASE distributed_lock
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_general_ci;

CREATE TABLE `sys_distributed_lock`
(
    `id`          bigint(20)  NOT NULL AUTO_INCREMENT COMMENT '主键',
    `lock_key`    varchar(64) NOT NULL DEFAULT '' COMMENT '锁编码',
    `locked`      varchar(64) NOT NULL COMMENT '锁的持有者',
    `version`     int(10) unsigned     DEFAULT '0' COMMENT '版本号',
    `expire_time` bigint(20)  NOT NULL COMMENT '锁到期时间(毫秒时间戳)',
    `remark`      varchar(100)         DEFAULT NULL COMMENT '备注',
    `created_at`  datetime(3)          DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
    `updated_at`  datetime(3)          DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_lock_key` (`lock_key`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='分布式锁表';
