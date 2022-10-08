
drop table IF EXISTS `t_publish_config`;
create TABLE `t_publish_config`  (
   `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增主键ID',
  `tenant_id` varchar(64) NOT NULL COMMENT '租户id',
  `name` varchar(128) not null default '' COMMENT '规则名称',
  `src_platform` varchar(64) NOT NULL COMMENT '源平台',
  `src_category` varchar(2048) NOT NULL DEFAULT '' COMMENT '源分类',
  `dst_platform` varchar(64) NOT NULL COMMENT '目标平台',
  `dst_platform_account` varchar(512) NOT NULL COMMENT 'sku颜色',
  `formulas` varchar(2048) NOT NULL DEFAULT '' COMMENT '公式',
  `filters` varchar(2048) DEFAULT '' COMMENT '过滤器',
  `translator_type` varchar(64) DEFAULT '' COMMENT '翻译器类型',
  `publish_cursor` varchar(512) DEFAULT '' COMMENT '刊登游标',
  `status` tinyint(1) not null default 1 COMMENT '状态',
  `add_time` timestamp NOT NULL DEFAULT current_timestamp() COMMENT '添加时间',
  `update_time` timestamp NOT NULL DEFAULT current_timestamp() ON update current_timestamp() COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB COMMENT = '商品刊登配置';

drop table IF EXISTS `t_publish_item`;
create table `t_publish_item` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增主键ID',
  `tenant_id` varchar(64) NOT NULL COMMENT '租户id',
  `publish_config_id` bigint(10) not null default -1 COMMENT '刊登规则id',
  `goods_id` bigint(20) NOT NULL COMMENT '刊登的产品id',
  `publish_data` longtext NOT NULL DEFAULT '' COMMENT '刊登的数据',
  `remark` varchar(512) COMMENT '备注',
  `status` tinyint(4) NOT NULL DEFAULT 0 COMMENT '状态',
  `add_time` timestamp NOT NULL DEFAULT current_timestamp() COMMENT '添加时间',
  `update_time` timestamp NOT NULL DEFAULT current_timestamp() ON update current_timestamp() COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY uk_tenant_id_goods_id (`tenant_id`, `goods_id`)
) ENGINE = InnoDB COMMENT = '商品上传信息表';


drop table IF EXISTS `t_feed_status`;
create table `t_feed_status` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增主键ID',
  `tenant_id` varchar(64) NOT NULL COMMENT '租户id',
  `publish_config_id` bigint(10) not null default -1 COMMENT '刊登规则id',
  `publish_item_id` bigint(10) not null default -1 COMMENT '刊登商品信息id',
  `platform` varchar(64) not null default '' COMMENT '平台',
  `platform_account` varchar(128) NOT NULL COMMENT '上传的目的平台帐号',
  `goods_id` bigint(20) NOT NULL COMMENT '刊登的产品id',
  `goods_sku_ids` text NOT NULL DEFAULT '' COMMENT '刊登的sku id，逗号分割',
  `target_goods_id` varchar(128) NOT NULL COMMENT '目标平台商品id',
  `feed_result_id` varchar(128) COMMENT 'feed结果id',
  `feed_result_detail` text COMMENT 'feed结果详情，json',
  `status` tinyint(4) NOT NULL DEFAULT 0 COMMENT '上传状态',
  `message` varchar(4096) DEFAULT '' COMMENT '异常信息',
  `add_time` timestamp NOT NULL DEFAULT current_timestamp() COMMENT '添加时间',
  `update_time` timestamp NOT NULL DEFAULT current_timestamp() ON update current_timestamp() COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB COMMENT = '商品上传状态表';
