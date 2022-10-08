package pers.kun.stock.task;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pers.kun.core.util.DateUtil;
import pers.kun.core.util.NumberUtil;
import pers.kun.internal.client.messager.MessageDTO;
import pers.kun.internal.client.messager.MessageTypeConst;
import pers.kun.stock.dao.entity.StockDailyDO;
import pers.kun.stock.dao.entity.StockHQDO;
import pers.kun.stock.integration.MessageMServiceFeign;
import pers.kun.stock.service.StockDailyDaoService;
import pers.kun.stock.service.StockDaoService;
import pers.kun.stock.service.StockHQDaoService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.stream.Collectors;

/**
 * @author : qihang.liu
 * @date 2022-01-11
 */
@Slf4j
@RestController
@RequestMapping("/tasks/stock_hq_notify_task")
public class StockHQNotifyTask {
    @Value("${stockHQ.notify.minChangePercent:6}")
    private Float minChangePercent;
    @Value("${stockHQ.notify.maxChangePercent:10.05}")
    private Float maxChangePercent;

    @Autowired
    private StockHQDaoService stockHQDaoService;

    @Autowired
    private StockDaoService stockDaoService;

    @Autowired
    private StockDailyDaoService stockDailyDaoService;

    @Autowired
    private MessageMServiceFeign messageApiFeign;

    @GetMapping("/execute")
    @Scheduled(cron = "0 */1 9-22 * * ?")
    public void execute() {
        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime todayEnd = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        LocalDateTime yestdayStart = todayStart.minusDays(1);
        var wrapper = new QueryWrapper<StockHQDO>()
                .between("hq_time", DateUtil.asDate(todayStart), DateUtil.asDate(todayEnd))
                .notLike("code", "sz.300");
        
        var changeInfoList = stockHQDaoService.list(wrapper).stream()
                .filter(s -> s.getChangePercent() >= minChangePercent && s.getChangePercent() <= maxChangePercent)
                .sorted(Comparator.comparingDouble(StockHQDO::getChangePercent).reversed())
                .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(changeInfoList)) {
            log.info("No record found");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("| 股票代码 | 名称 | 市值 | 换手率 | -T2 | -T1 | T0 |").append("\n")
                .append("|:--|:--|:--|:--|:--|:--|:--|").append("\n");

        for (var item : changeInfoList) {
            var stock = stockDaoService.getById(item.getCode());
            if (null == stock) {
                log.warn("Stock {} not found", item.getCode());
                continue;
            }
            var stockDailyList = stockDailyDaoService.list(new QueryWrapper<StockDailyDO>()
                    .eq("code", item.getCode())
                    .le("date", DateUtil.asDate(yestdayStart)));
            if (CollectionUtils.isEmpty(stockDailyList) || stockDailyList.size() < 2) {
                log.warn("Stock daily not found for {}", item.getCode());
                continue;
            }
            var t1Daily = stockDailyList.get(0);
            var t2Daily = stockDailyList.get(1);
            sb.append("| ").append(stock.getCode())
                    .append(" | ").append(stock.getName())
                    .append(" | ").append(NumberUtil.keepPrecision(t1Daily.getTcap() / 100000000, 2)).append("亿")
                    .append(" | ").append(t1Daily.getTurn()).append("%")
                    .append(" | ").append(t2Daily.getChangePercent()).append("%")
                    .append(" | ").append(t1Daily.getChangePercent()).append("%")
                    .append(" | ").append(item.getChangePercent()).append("%")
                    .append(" |")
                    .append("\n");
        }

        var msg = new MessageDTO();
        msg.setTopic("stock.stockNotify");
        msg.setMessageType(MessageTypeConst.MARKDOWN);
        msg.setMessage(sb.toString());
        messageApiFeign.sendMessage(msg);
    }
}
