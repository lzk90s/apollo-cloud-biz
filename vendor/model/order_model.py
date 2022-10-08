class Order:
    def __init__(self, id, type, confirm_time, sn, delivery_count_down, order_cancel_count_down,
                 order_collection_count_down, num, price, total_price, pay_status, image_url, detail_url, sku, remark):
        self.id = id
        self.type = type
        self.confirm_time = confirm_time
        self.sn = sn
        self.delivery_count_down = delivery_count_down
        self.order_cancel_count_down = order_cancel_count_down
        self.order_collection_count_down = order_collection_count_down
        self.num = num
        self.price = price
        self.total_price = total_price
        self.pay_status = pay_status
        self.image_url = image_url
        self.detail_url = detail_url
        self.sku = sku
        self.remark = remark


class RefundOrder:
    def __init__(self, sn, apply_refund_time, deadline_time, sku, image_url, refund_reason, remark):
        self.sn = sn
        self.apply_refund_time = apply_refund_time
        self.deadline_time = deadline_time
        self.sku = sku
        self.image_url = image_url
        self.refund_reason = refund_reason
        self.remark = remark
