-- ============================================================
-- 在线聊天系统 - 数据库初始化脚本
-- MySQL 8.0+ / utf8mb4
-- ============================================================

CREATE DATABASE IF NOT EXISTS online_chat
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;
USE online_chat;

-- ============================================================
-- 1. 用户表
-- ============================================================
CREATE TABLE t_user
(
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    username    VARCHAR(32)  NOT NULL COMMENT '登录名',
    password    VARCHAR(128) NOT NULL COMMENT 'BCrypt 加密后的密码',
    nickname    VARCHAR(32)  DEFAULT NULL COMMENT '昵称',
    avatar      VARCHAR(255) DEFAULT NULL COMMENT '头像 URL',
    email       VARCHAR(64)  DEFAULT NULL COMMENT '邮箱',
    phone       VARCHAR(32)  DEFAULT NULL COMMENT '手机号',
    signature   VARCHAR(255) DEFAULT NULL COMMENT '个性签名',
    status      TINYINT      NOT NULL DEFAULT 0 COMMENT '0=正常 1=禁用',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE INDEX uk_username (username),
    UNIQUE INDEX uk_phone (phone)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='用户表';

-- ============================================================
-- 2. 好友分组表
-- ============================================================
CREATE TABLE t_friend_group
(
    id          BIGINT      NOT NULL AUTO_INCREMENT COMMENT '分组ID',
    user_id     BIGINT      NOT NULL COMMENT '所属用户ID',
    name        VARCHAR(32) NOT NULL COMMENT '分组名称',
    sort_order  INT         NOT NULL DEFAULT 0 COMMENT '排序',
    create_time DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    INDEX idx_user_id (user_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='好友分组表';

-- ============================================================
-- 3. 好友关系表
-- ============================================================
CREATE TABLE t_friend
(
    id          BIGINT      NOT NULL AUTO_INCREMENT COMMENT '关系ID',
    user_id     BIGINT      NOT NULL COMMENT '用户ID',
    friend_id   BIGINT      NOT NULL COMMENT '好友用户ID',
    group_id    BIGINT      NOT NULL COMMENT '所属分组ID',
    remark      VARCHAR(32) DEFAULT NULL COMMENT '备注名',
    create_time DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '成为好友时间',
    update_time DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE INDEX uk_user_friend (user_id, friend_id),
    INDEX idx_user_id (user_id),
    INDEX idx_group_id (group_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='好友关系表';

-- ============================================================
-- 4. 好友申请表
-- ============================================================
CREATE TABLE t_friend_request
(
    id           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '申请ID',
    from_user_id BIGINT       NOT NULL COMMENT '发起方用户ID',
    to_user_id   BIGINT       NOT NULL COMMENT '接收方用户ID',
    message      VARCHAR(255) DEFAULT NULL COMMENT '附言',
    status       VARCHAR(16)  NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING/ACCEPTED/REJECTED/EXPIRED',
    create_time  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间',
    update_time  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    INDEX idx_from_user (from_user_id),
    INDEX idx_to_user (to_user_id),
    INDEX idx_from_to_user (from_user_id, to_user_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='好友申请表';

-- ============================================================
-- 5. 群聊表
-- ============================================================
CREATE TABLE t_chat_group
(
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '群ID',
    name        VARCHAR(32)  NOT NULL COMMENT '群名称',
    avatar      VARCHAR(255) DEFAULT NULL COMMENT '群头像 URL',
    description VARCHAR(255) DEFAULT NULL COMMENT '群简介',
    owner_id    BIGINT       NOT NULL COMMENT '群主用户ID',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    INDEX idx_owner_id (owner_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='群聊表';

-- ============================================================
-- 6. 群成员表
-- ============================================================
CREATE TABLE t_group_member
(
    id          BIGINT      NOT NULL AUTO_INCREMENT COMMENT '成员记录ID',
    group_id    BIGINT      NOT NULL COMMENT '群ID',
    user_id     BIGINT      NOT NULL COMMENT '用户ID',
    role        VARCHAR(16) NOT NULL DEFAULT 'MEMBER' COMMENT 'OWNER/ADMIN/MEMBER',
    nickname    VARCHAR(32) DEFAULT NULL COMMENT '群内昵称',
    join_time   DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '入群时间',
    update_time DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE INDEX uk_group_user (group_id, user_id),
    INDEX idx_group_id (group_id),
    INDEX idx_user_id (user_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='群成员表';

-- ============================================================
-- 7. 消息表
-- ============================================================
CREATE TABLE t_message
(
    id           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '消息ID',
    chat_type    VARCHAR(16)  NOT NULL COMMENT 'PRIVATE / GROUP',
    message_type VARCHAR(16)  NOT NULL COMMENT 'TEXT / IMAGE / FILE / VOICE / SYSTEM',
    from_user_id BIGINT       NOT NULL COMMENT '发送方用户ID',
    to_target_id BIGINT       NOT NULL COMMENT '接收目标ID（私聊=对方用户ID 群聊=群ID）',
    content      TEXT         DEFAULT NULL COMMENT '消息正文（文本类）',
    file_url     VARCHAR(512) DEFAULT NULL COMMENT '文件/图片/语音 URL',
    file_name    VARCHAR(255) DEFAULT NULL COMMENT '原始文件名',
    file_size    BIGINT       DEFAULT NULL COMMENT '文件大小(byte)',
    is_read      TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '0=未读 1=已读',
    create_time  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间',
    PRIMARY KEY (id),
    INDEX idx_chat_target_time (chat_type, to_target_id, create_time DESC),
    INDEX idx_from_user (from_user_id),
    INDEX idx_create_time (create_time)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='消息表';
