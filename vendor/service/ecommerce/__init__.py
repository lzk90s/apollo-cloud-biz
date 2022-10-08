from service.ecommerce.walmart.order_client import WalmartOrderClient
from service.ecommerce.walmart.product_client import WalmartProductClient

ALL_CLIENTS = {
    'walmart_product': WalmartProductClient,
    'walmart_order': WalmartOrderClient
}