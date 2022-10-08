from service.ecommerce.walmart.product_client import WalmartProductClient


def load_class_by_name(class_name):
    module_name = "."
    if class_name.rfind(".") > 0:
        module_name = class_name[0:class_name.rfind(".")]
        class_name = class_name[class_name.rfind(".") + 1:]
    try:
        m = __import__(module_name, fromlist=True)
        return getattr(m, class_name)
    except Exception as e:
        print(e)
        raise e


def load_class_by_type(class_type):
    return class_type


if __name__ == "__main__":
    print(type(WalmartProductClient))