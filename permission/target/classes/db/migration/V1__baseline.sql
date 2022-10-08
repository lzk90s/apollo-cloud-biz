SET FOREIGN_KEY_CHECKS = 0;

CREATE TABLE IF NOT EXISTS `t_menu` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增主键ID',
  `parent_id` bigint(10) NOT NULL COMMENT 'parent id',
  `app` varchar(32) NOT NULL DEFAULT '' COMMENT 'app',
  `name` varchar(64) NOT NULL COMMENT '名称',
  `path` varchar(512) NOT NULL COMMENT '路径',
  `meta` text NOT NULL COMMENT '元数据json',
  `component` varchar(64) COMMENT '组件',
  `redirect` varchar(512) COMMENT '重定向',
  `add_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间',
  `update_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 COMMENT = '菜单表';


CREATE TABLE  IF NOT EXISTS `t_role` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增主键ID',
  `name` varchar(128) NOT NULL COMMENT '名称',
  `describe` varchar(128) NOT NULL COMMENT '描述',
  `status` int(4) NOT NULL COMMENT '状态',
  `add_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间',
  `update_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 COMMENT = '角色表';


CREATE TABLE IF NOT EXISTS `t_role_permission` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增主键ID',
  `role_id` bigint(20) NOT NULL COMMENT 'ROLE ID',
  `permission_id` varchar(128) NOT NULL COMMENT '权限id',
  `permission_name` varchar(128) NOT NULL COMMENT '名称',
  `actions` text NOT NULL COMMENT '动作json',
  `add_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间',
  `update_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
   PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 COMMENT = '菜单表';


CREATE TABLE  IF NOT EXISTS `t_user_role` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增主键ID',
  `tenant_id` varchar(64) NOT NULL COMMENT '租户id',
  `role_id` bigint(20) NOT NULL COMMENT '用户名',
  `status` int(4) NOT NULL COMMENT '状态',
  `add_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间',
  `update_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 COMMENT = '用户角色表';


DROP TABLE IF EXISTS `t_platform_account`;
CREATE TABLE `t_platform_account` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增主键ID',
  `tenant_id` varchar(64) NOT NULL COMMENT '租户id',
  `name` varchar(64) NOT NULL COMMENT '名称',
  `platform` varchar(32) NOT NULL COMMENT '平台名称',
  `platform_user` varchar(64) NOT NULL COMMENT '平台用户名',
  `platform_password` varchar(64) NOT NULL COMMENT '平台密码',
  `client_id` varchar(512) default '' COMMENT 'clientid',
  `client_secret` varchar(512) default '' COMMENT 'client secret',
  `cookies` text default '' COMMENT 'cookies',
  `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否已删除',
  `add_time` timestamp NOT NULL DEFAULT current_timestamp() COMMENT '添加时间',
  `update_time` timestamp NOT NULL DEFAULT current_timestamp() ON update current_timestamp() COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY (`name`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 COMMENT = '平台账户表';