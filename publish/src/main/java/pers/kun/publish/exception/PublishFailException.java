package pers.kun.publish.exception;

import pers.kun.core.exception.BizException;

/**
 * @author : qihang.liu
 * @date 2021-05-15
 */
public class PublishFailException extends BizException {
    public PublishFailException(String goodsId) {
        super("刊登失败, id=" + goodsId);
    }
}
