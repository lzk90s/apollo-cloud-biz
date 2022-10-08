from common import exception
from service.ecommerce.ali1688 import alibaba_lib

_BASE_URL = "https://s.1688.com"


def __get_real_price(detail_url):
    detail_data = alibaba_lib.AlibabaDetail().get_detail_data(detail_url)
    return "0"


def _check_result(result):
    if not result:
        raise exception.BizException("Invalid result")
    if result["code"] != 200:
        raise exception.BizException(result["msg"])


if __name__ == '__main__':
    # image_url = "https://image-tb.vova.com/image/262_262/filler/6b/cc/1035aa9aefc6b869ad9ce4e985966bcc.jpg"
    image_url = "https://image-tb.vova.com/image/262_262/filler/89/c5/fdcecee8b782bcc1ac6f726ab4b889c5.jpg"
