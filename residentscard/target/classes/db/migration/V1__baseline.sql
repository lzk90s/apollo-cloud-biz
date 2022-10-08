drop table IF EXISTS `t_apply_record`;
create TABLE `t_apply_record`  (
  `id` bigint(10) NOT NULL AUTO_INCREMENT COMMENT '商品id',
  `tenant_id` varchar(64) NOT NULL COMMENT '租户id',
  `user` varchar(64) NOT NULL COMMENT '用户',
  `phone` varchar(32) NOT NULL DEFAULT '' COMMENT '手机号',
  `handle_user` varchar(64) NOT NULL DEFAULT '' COMMENT '经办人',
  `handle_time` timestamp NOT NULL COMMENT '办理时间',
  `handle_address` varchar(128) NOT NULL DEFAULT '' COMMENT '办理地点',
  `status` tinyint(2) NOT NULL DEFAULT 0 COMMENT '状态',
AUTO_INCREMENT
  `add_time` timestamp NOT NULL DEFAULT current_timestamp() COMMENT '添加时间',
  `update_time` timestamp NOT NULL DEFAULT current_timestamp() ON update current_timestamp() COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_tenant_id_user_phone` (`tenant_id`, `user`, `phone`)
) ENGINE = InnoDB COMMENT = '申请表';

drop table IF EXISTS `t_activate_record`;
create TABLE `t_activate_record`  (
  `id` bigint(10) NOT NULL AUTO_INCREMENT COMMENT '商品id',
  `tenant_id` varchar(64) NOT NULL COMMENT '租户id',
  `user` varchar(64) NOT NULL COMMENT '用户',
  `phone` varchar(32) NOT NULL DEFAULT '' COMMENT '手机号',
  `handle_user` varchar(64) NOT NULL DEFAULT '' COMMENT '经办人',
  `handle_time` timestamp NOT NULL COMMENT '办理时间',
  `handle_address` varchar(128) NOT NULL DEFAULT '' COMMENT '办理地点',
  `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否已删除',
  `add_time` timestamp NOT NULL DEFAULT current_timestamp() COMMENT '添加时间',
  `update_time` timestamp NOT NULL DEFAULT current_timestamp() ON update current_timestamp() COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_tenant_id_user_phone_handle_address` (`tenant_id`, `user`, `phone`, `handle_address`)
) ENGINE = InnoDB COMMENT = '激活表';

drop table IF EXISTS `t_handle_user`;
create TABLE `t_handle_user`  (
  `id` bigint(10) NOT NULL AUTO_INCREMENT COMMENT '商品id',
  `tenant_id` varchar(64) NOT NULL COMMENT '租户id',
  `name` varchar(64) NOT NULL COMMENT '名字',
  `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否已删除',
  `add_time` timestamp NOT NULL DEFAULT current_timestamp() COMMENT '添加时间',
  `update_time` timestamp NOT NULL DEFAULT current_timestamp() ON update current_timestamp() COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_tenant_id_name` (`tenant_id`, `name`)
) ENGINE = InnoDB COMMENT = '经办人信息';

drop table IF EXISTS `t_handle_address`;
create TABLE `t_handle_address`  (
  `id` bigint(10) NOT NULL AUTO_INCREMENT COMMENT '商品id',
  `tenant_id` varchar(64) NOT NULL COMMENT '租户id',
  `name` varchar(64) NOT NULL COMMENT '名字',
  `detail` varchar(128) NOT NULL DEFAULT '' COMMENT '详细信息',
  `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否已删除',
  `add_time` timestamp NOT NULL DEFAULT current_timestamp() COMMENT '添加时间',
  `update_time` timestamp NOT NULL DEFAULT current_timestamp() ON update current_timestamp() COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_tenant_id_name` (`tenant_id`, `name`)
) ENGINE = InnoDB COMMENT = '点位地址信息';
