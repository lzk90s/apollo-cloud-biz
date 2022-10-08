from service.ecommerce.client import OrderClient


class WalmartOrderClient(OrderClient):
    def list_unhandled_order(self):
        return []

    def list_refund_order(self, start_time, end_time):
        return []
