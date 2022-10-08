from flask import Blueprint, request

from common.restapi import get_auth_from_req, result
from model import product_model
from service.ecommerce.client_factory import get_product_client

product = Blueprint('goods', __name__)


@product.route('/upload_product/<platform>', methods=['POST'])
def upload_product(platform):
    product_list = request.json
    if not product_list:
        raise ValueError("No product")
    product_list_dto = []
    for product in product_list:
        product_list_dto.extend(product_model.FlatProductInfo.conv2flat(product))
    return result(get_product_client(platform, get_auth_from_req()).upload_item(product_list_dto))


@product.route('/get_upload_status/<platform>', methods=['GET'])
def get_upload_status(platform):
    upload_id = request.args.get("uploadId")
    if not upload_id:
        raise ValueError("uploadId is null")
    return result(get_product_client(platform, get_auth_from_req()).get_upload_status(upload_id))


@product.route('/enable_product_sale/<platform>', methods=['POST'])
def enable_product_sale(platform):
    product_list = request.json
    if not product_list:
        raise ValueError("No product")
    return result(get_product_client(platform, get_auth_from_req()).enable_sale(product_list))


@product.route('/disable_product_sale/<platform>', methods=['POST'])
def disable_product_sale(platform):
    product_list = request.json
    if not product_list:
        raise ValueError("No product")
    return result(get_product_client(platform, get_auth_from_req()).disable_sale(product_list))


@product.route('/delete_product/<platform>', methods=['DELETE'])
def delete_product(platform):
    product_list = request.json
    if not product_list:
        raise ValueError("No product")
    return result(get_product_client(platform, get_auth_from_req()).delete_item(product_list))
