drop table IF EXISTS `t_reptile_rule`;
create TABLE `t_reptile_rule`  (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增主键ID',
  `tenant_id` varchar(64) NOT NULL COMMENT '租户id',
  `name` varchar(256) NOT NULL DEFAULT '' COMMENT '名字',
  `spider_name` varchar(64) NOT NULL DEFAULT '' COMMENT '爬虫名字',
  `exec_expr` varchar(512) NOT NULL DEFAULT '' COMMENT '执行时间表达式',
  `options` text NOT NULL DEFAULT '{}' COMMENT '选项',
  `labels` varchar(1024) COMMENT '标签',
  `status` tinyint(4) NOT NULL DEFAULT 0 COMMENT '状态',
  `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否已删除',
  `add_time` timestamp NOT NULL DEFAULT current_timestamp() COMMENT '添加时间',
  `update_time` timestamp NOT NULL DEFAULT current_timestamp() ON update current_timestamp() COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY uk_tenant_id_spider_name_name (`tenant_id`, `spider_name`, `name`)
  ) ENGINE = InnoDB COMMENT = '采集规则表';

