class BaseClient:
    def __init__(self, client_id, client_secret, cookies):
        self.client_id = client_id
        self.client_secret = client_secret
        self.cookies = cookies

    def is_session_expired(self):
        pass

    def reload(self):
        pass


class ProductClient(BaseClient):
    def upload_item(self, products):
        pass

    def get_upload_status(self, upload_id):
        pass

    def enable_sale(self, goods_ids):
        pass

    def disable_sale(self, goods_ids):
        pass

    def delete_item(self, goods_ids):
        pass


class OrderClient(BaseClient):
    def list_unhandled_order(self):
        pass

    def list_refund_order(self, start_time, end_time):
        pass


def check_session(fun):
    def warp(*args, **kwargs):
        obj = args[0]
        assert isinstance(obj, BaseClient)
        if obj.is_session_expired():
            obj.reload()
        return fun(*args, **kwargs)

    return warp
