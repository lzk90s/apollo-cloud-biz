drop table IF EXISTS `t_stock`;
create TABLE `t_stock`  (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增主键ID',
  `code` varchar(64) NOT NULL COMMENT '证券代码',
  `type` varchar(64) NOT NULL COMMENT '证券类型',
  `name` varchar(256) NOT NULL DEFAULT '' COMMENT '名字',
  `ipo_date` datetime NOT NULL COMMENT '上市时间',
  `out_date` datetime COMMENT '退市时间',
  `industry` varchar(255) NOT NULL DEFAULT '' COMMENT '行业',
  `industry_classification` varchar(255) NOT NULL DEFAULT '' comment '行业类型',
  `status` tinyint(4) NOT NULL DEFAULT 1 COMMENT '状态[0:退市，1:上市]',
  `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否已删除',
  `add_time` timestamp NOT NULL DEFAULT current_timestamp() COMMENT '添加时间',
  `update_time` timestamp NOT NULL DEFAULT current_timestamp() ON update current_timestamp() COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_code` (`code`)
  ) ENGINE = InnoDB COMMENT = '股票信息表';

drop table IF EXISTS `t_stock_hq`;
create TABLE `t_stock_hq`  (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增主键ID',
  `code` varchar(64) NOT NULL COMMENT '证券代码',
  `hq_time` datetime NOT NULL COMMENT '行情时间',
  `price` float(10,2) NOT NULL DEFAULT 0 COMMENT '当前价格',
  `change` float(5,2) NOT NULL DEFAULT 0 COMMENT '语言',
  `change_percent` float(5,2) NOT NULL DEFAULT 0 COMMENT '类别',
  `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否已删除',
  `add_time` timestamp NOT NULL DEFAULT current_timestamp() COMMENT '添加时间',
  `update_time` timestamp NOT NULL DEFAULT current_timestamp() ON update current_timestamp() COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_code` (`code`)
) ENGINE = InnoDB COMMENT = '股票实时行情表';


drop table IF EXISTS `t_stock_daily`;
CREATE TABLE `t_stock_daily` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增主键ID',
  `code` varchar(255) NOT NULL COMMENT '股票代码',
  `date` datetime NOT NULL COMMENT '日期',
  `open` float(10,2) NOT NULL DEFAULT 0 COMMENT '开盘价',
  `high` float(10,2) NOT NULL DEFAULT 0 COMMENT '最高价',
  `low` float(10,2) NOT NULL DEFAULT 0 COMMENT '最低价',
  `close` float(10,2) NOT NULL DEFAULT 0 COMMENT '收盘价',
  `pre_close` float(10,2) NOT NULL DEFAULT 0 COMMENT '前收盘价',
  `change` float(5,2) NOT NULL DEFAULT 0 COMMENT '涨跌额',
  `change_percent` float(5,2) NOT NULL DEFAULT 0 COMMENT '涨跌百分比',
  `volume` bigint(20) NOT NULL DEFAULT 0 COMMENT '成交量（累计 单位：股）',
  `amount` bigint(20) NOT NULL DEFAULT 0 COMMENT '成交额（单位：人民币元）',
  `turn` float(5,2) NOT NULL DEFAULT 0 COMMENT '换手率',
  `trade_status` tinyint(1) NOT NULL DEFAULT 1 COMMENT '交易状态(1：正常交易 0：停牌）',
  `pe_ttm` float(5,2) NOT NULL DEFAULT 0 COMMENT '滚动市盈率',
  `tcap` float(15,2) NOT NULL DEFAULT 0 COMMENT '总市值',
  `mcap` float(15,2) NOT NULL DEFAULT 0 COMMENT '流通市值',
  `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否已删除',
  `add_time` timestamp NOT NULL DEFAULT current_timestamp() COMMENT '添加时间',
  `update_time` timestamp NOT NULL DEFAULT current_timestamp() ON update current_timestamp() COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_date_code` (`date`,`code`)
) ENGINE=InnoDB COMMENT = '股票日K数据表'

