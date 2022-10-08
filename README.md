# 业务模块

[apollo-cloud-basic](https://github.com/lzk90s/apollo-cloud-basic)

|服务 | 名称 | 功能 |依赖 |
| :---- | :---- | ---- | ---- |
| account | 账号服务 | 保存各种三方账号 | |
| cookies-pool | cookie池服务 | 保存各种cookie，给爬虫等使用 | remote-chrome |
| haipproxy | ip代理池 | 保存代理ip，给爬虫使用 | |
| dbbackup | 数据库备份服务| 数据库初始化，自动备份 | |
| vendor | 第三方集成 | 第三方服务集成 | |
| product | 商品服务 | 保存电商商品 | |
| messager | 消息服务 | 邮件消息，微信消息推送 | |
| order | 订单服务| 订单提醒 | account, goods, vendor |
| portal | 前台页面服务(web) | web聚合 | |
| stock | 商品刊登服务 | 根据规则刊登商品 | account, goods, vendor |
| recognize | 识别服务| 各类识别，ocr等 | |
| remote-chrome | 远程chrome服务| chrome远程调用 | |
| residentscard | 市民卡服务| 市民卡业务 | |
| reptile-control | 爬虫控制服务| 配置爬虫规则 |   |
| reptile-agent | 爬虫客户端agent | 爬取信息 | |
| operation | 电商运营 | 电商运营 | goods | 
| stock | 股票 | 股票 | | 
| quant | 量化交易 | | |