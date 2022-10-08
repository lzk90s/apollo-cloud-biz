package pers.kun.publish.exception;

import pers.kun.core.exception.BizException;

/**
 * @author : qihang.liu
 * @date 2021-05-15
 */
public class ConformanceFailException extends BizException {
    public ConformanceFailException(String goodsId) {
        super("合规检查失败, id=" + goodsId);
    }
}
