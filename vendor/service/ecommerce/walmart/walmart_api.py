# -*- coding: utf-8 -*-
import csv
import io
import uuid
import zipfile
from datetime import datetime

import requests
from lxml import etree
from lxml.builder import E, ElementMaker
from requests.auth import HTTPBasicAuth


class BaseException(Exception):
    """
    Base Exception which implements message attr on exceptions
    Required for: Python 3
    """

    def __init__(self, message=None, *args, **kwargs):
        self.message = message
        super(BaseException, self).__init__(
            self.message, *args, **kwargs
        )

    def __str__(self):
        return self.message or self.__class__.__name__


class WalmartException(BaseException):
    pass


class WalmartForbiddenError(WalmartException):
    pass


class WalmartAuthenticationError(WalmartException):
    pass


class WalmartRateLimitError(WalmartException):
    pass


def epoch_milliseconds(dt):
    "Walmart accepts timestamps as epoch time in milliseconds"
    epoch = datetime.utcfromtimestamp(0)
    return int((dt - epoch).total_seconds() * 1000.0)


class Walmart(object):

    def __init__(self, client_id, client_secret, proxies=None):
        """To get client_id and client_secret for your Walmart Marketplace
        visit: https://developer.walmart.com/#/generateKey
        """
        self.client_id = client_id
        self.client_secret = client_secret
        self.proxies = proxies
        self.token = None
        self.token_expires_in = None
        self.base_url = "https://marketplace.walmartapis.com/v3"
        self.default_timeout = 15

        session = requests.Session()
        session.headers.update({
            "WM_SVC.NAME": "Walmart Marketplace",
            "Content-Type": "application/x-www-form-urlencoded",
            "Accept": "application/json",
        })
        session.auth = HTTPBasicAuth(self.client_id, self.client_secret)
        self.session = session

        # Get the token required for API requests
        self.authenticate()

    def authenticate(self):
        data = self.send_request(
            "POST", "{}/token".format(self.base_url),
            body={
                "grant_type": "client_credentials",
            },
        )
        self.token = data["access_token"]
        self.token_expires_in = data["expires_in"]

        self.session.headers["WM_SEC.ACCESS_TOKEN"] = self.token

    @property
    def items(self):
        return Items(connection=self)

    @property
    def inventory(self):
        return Inventory(connection=self)

    @property
    def prices(self):
        return Prices(connection=self)

    @property
    def orders(self):
        return Orders(connection=self)

    @property
    def report(self):
        return Report(connection=self)

    @property
    def feed(self):
        return Feed(connection=self)

    @property
    def category(self):
        return Category(connection=self)

    def send_request(
            self, method, url, params=None, body=None, json=None,
            request_headers=None
    ):
        # A unique ID which identifies each API call and used to track
        # and debug issues; use a random generated GUID for this ID
        headers = {
            "WM_QOS.CORRELATION_ID": uuid.uuid4().hex,
        }
        if request_headers:
            headers.update(request_headers)

        response = None
        if method == "GET":
            response = self.session.get(url, params=params, headers=headers, proxies=self.proxies,
                                        timeout=self.default_timeout)
        elif method == "PUT":
            response = self.session.put(
                url, params=params, headers=headers, data=body, proxies=self.proxies, timeout=self.default_timeout
            )
        elif method == "POST":
            request_params = {
                "params": params,
                "headers": headers,
            }
            if json is not None:
                request_params["json"] = json
            else:
                request_params["data"] = body
            response = self.session.post(url, **request_params, proxies=self.proxies, timeout=self.default_timeout)

        if response is not None:
            try:
                response.raise_for_status()
            except requests.exceptions.HTTPError:
                print('Response body {}'.format(response.text))
                if response.status_code == 401:
                    raise WalmartAuthenticationError((
                        "Invalid client_id or client_secret. Please verify "
                        "your credentials from https://developer.walmart."
                        "com/#/generateKey"
                    ))
                elif response.status_code == 429:
                    allow_count = response.headers.get("X-Current-Token-Count", None)
                    next_time = response.headers.get("X-Next-Replenishment-Time", None)
                    text = response.text
                    raise WalmartRateLimitError((
                        "Rate limit reached, allow count {}, next time is {}, {}".format(allow_count, next_time, text)
                    ))
                elif response.status_code == 403:
                    data = response.json()
                    raise WalmartForbiddenError((
                        "Forbidden! detail {}".format(data)
                    ))
                elif response.status_code == 400:
                    data = response.json()
                    if "error" in data and data["error"][0]["code"] == \
                            "INVALID_TOKEN.GMP_GATEWAY_API":
                        # Refresh the token as the current token has expired
                        self.authenticate()
                        return self.send_request(
                            method, url, params, body, request_headers
                        )
                raise
        try:
            return response.json()
        except ValueError:
            # In case of reports, there is no JSON response, so return the
            # content instead which contains the actual report
            return response.content


class Resource(object):
    """
    A base class for all Resources to extend
    """

    def __init__(self, connection):
        self.connection = connection

    @property
    def url(self):
        return "{}/{}".format(self.connection.base_url, self.path)

    def all(self, **kwargs):
        return self.connection.send_request(
            method="GET", url=self.url, params=kwargs
        )

    def get(self, id):
        url = "{}/{}".format(self.url, id)
        return self.connection.send_request(method="GET", url=url)

    def update(self, **kwargs):
        return self.connection.send_request(
            method="PUT", url=self.url, params=kwargs
        )


class Items(Resource):
    """
    Get all items
    """

    path = 'items'

    def get_items(self):
        "Get all the items from the Item Report"
        response = self.connection.report.all(type="item")
        zf = zipfile.ZipFile(io.BytesIO(response), "r")
        product_report = zf.read(zf.infolist()[0]).decode("utf-8")

        return list(csv.DictReader(io.StringIO(product_report)))


class Inventory(Resource):
    """
    Retreives inventory of an item
    """

    path = 'inventory'
    feedType = 'inventory'

    def bulk_update(self, items):
        """Updates the inventory for multiple items at once by creating the
        feed on Walmart.

        :param items: Items for which the inventory needs to be updated in
        the format of:
            [{
                "sku": "XXXXXXXXX",
                "availability_code": "AC",
                "quantity": "10",
                "uom": "EACH",
                "fulfillment_lag_time": "1",
            }]
        """
        inventory_data = []
        for item in items:
            data = {
                "sku": item["sku"],
                "quantity": {
                    "amount": item["quantity"],
                    "unit": item.get("uom", "EACH"),
                },
                "fulfillmentLagTime": item.get("fulfillment_lag_time"),
            }
            if item.get("availability_code"):
                data["availabilityCode"] = item["availability_code"]
            inventory_data.append(data)

        body = {
            "InventoryHeader": {
                "version": "1.4",
            },
            "Inventory": inventory_data,
        }
        return self.connection.feed.create(resource="inventory", content=body)

    def update_inventory(self, sku, quantity):
        headers = {
            'Content-Type': "application/xml"
        }
        return self.connection.send_request(
            method='PUT',
            url=self.url,
            params={'sku': sku},
            body=self.get_inventory_payload(sku, quantity),
            request_headers=headers
        )

    def get_inventory_payload(self, sku, quantity):
        element = ElementMaker(
            namespace='http://walmart.com/',
            nsmap={
                'wm': 'http://walmart.com/',
            }
        )
        return etree.tostring(
            element(
                'inventory',
                element('sku', sku),
                element(
                    'quantity',
                    element('unit', 'EACH'),
                    element('amount', str(quantity)),
                ),
                element('fulfillmentLagTime', '4'),
            ), xml_declaration=True, encoding='utf-8'
        )

    def get_payload(self, items):
        return etree.tostring(
            E.InventoryFeed(
                E.InventoryHeader(E('version', '1.4')),
                *[E(
                    'inventory',
                    E('sku', item['sku']),
                    E(
                        'quantity',
                        E('unit', 'EACH'),
                        E('amount', item['quantity']),
                    )
                ) for item in items],
                xmlns='http://walmart.com/'
            )
        )


class Prices(Resource):
    """
    Retreives price of an item
    """

    path = 'prices'
    feedType = 'price'

    def get_payload(self, items):
        root = ElementMaker(
            nsmap={'gmp': 'http://walmart.com/'}
        )
        return etree.tostring(
            root.PriceFeed(
                E.PriceHeader(E('version', '1.5')),
                *[E.Price(
                    E(
                        'itemIdentifier',
                        E('sku', item['sku'])
                    ),
                    E(
                        'pricingList',
                        E(
                            'pricing',
                            E(
                                'currentPrice',
                                E(
                                    'value',
                                    **{
                                        'currency': item['currenctCurrency'],
                                        'amount': item['currenctPrice']
                                    }
                                )
                            ),
                            E('currentPriceType', item['priceType']),
                            E(
                                'comparisonPrice',
                                E(
                                    'value',
                                    **{
                                        'currency': item['comparisonCurrency'],
                                        'amount': item['comparisonPrice']
                                    }
                                )
                            ),
                            E(
                                'priceDisplayCode',
                                **{
                                    'submapType': item['displayCode']
                                }
                            ),
                        )
                    )
                ) for item in items]
            ), xml_declaration=True, encoding='utf-8'
        )


class Orders(Resource):
    """
    Retrieves Order details
    """

    path = 'orders'

    def all(self, **kwargs):
        try:
            return super(Orders, self).all(**kwargs)
        except requests.exceptions.HTTPError as exc:
            if exc.response.status_code == 404:
                # If no orders are there on walmart matching the query
                # filters, it throws 404. In this case return an empty
                # list to make the API consistent
                return {
                    "list": {
                        "elements": {
                            "order": [],
                        }
                    }
                }
            raise

    def acknowledge(self, id):
        url = self.url + '/%s/acknowledge' % id
        return self.send_request(method='POST', url=url)

    def cancel(self, id, lines):
        url = self.url + '/%s/cancel' % id
        return self.send_request(
            method='POST', url=url, data=self.get_cancel_payload(lines))

    def get_cancel_payload(self, lines):
        element = ElementMaker(
            namespace='http://walmart.com/mp/orders',
            nsmap={
                'ns2': 'http://walmart.com/mp/orders',
                'ns3': 'http://walmart.com/'
            }
        )
        return etree.tostring(
            element(
                'orderCancellation',
                element(
                    'orderLines',
                    *[element(
                        'orderLine',
                        element('lineNumber', line),
                        element(
                            'orderLineStatuses',
                            element(
                                'orderLineStatus',
                                element('status', 'Cancelled'),
                                element(
                                    'cancellationReason', 'CANCEL_BY_SELLER'),
                                element(
                                    'statusQuantity',
                                    element('unitOfMeasurement', 'EACH'),
                                    element('amount', '1')
                                )
                            )
                        )
                    ) for line in lines]
                )
            ), xml_declaration=True, encoding='utf-8'
        )

    def create_shipment(self, order_id, lines):
        """Send shipping updates to Walmart

        :param order_id: Purchase order ID of an order
        :param lines: Order lines to be fulfilled in the format:
            [{
                "line_number": "123",
                "uom": "EACH",
                "quantity": 3,
                "ship_time": datetime(2019, 04, 04, 12, 00, 00),
                "other_carrier": None,
                "carrier": "USPS",
                "carrier_service": "Standard",
                "tracking_number": "34567890567890678",
                "tracking_url": "www.fedex.com",
            }]
        """
        url = self.url + "/{}/shipping".format(order_id)

        order_lines = []
        for line in lines:
            ship_time = line.get("ship_time", "")
            if ship_time:
                ship_time = epoch_milliseconds(ship_time)
            order_lines.append({
                "lineNumber": line["line_number"],
                "orderLineStatuses": {
                    "orderLineStatus": [{
                        "status": "Shipped",
                        "statusQuantity": {
                            "unitOfMeasurement": line.get("uom", "EACH"),
                            "amount": str(int(line["quantity"])),
                        },
                        "trackingInfo": {
                            "shipDateTime": ship_time,
                            "carrierName": {
                                "otherCarrier": line.get("other_carrier"),
                                "carrier": line["carrier"],
                            },
                            "methodCode": line.get("carrier_service", ""),
                            "trackingNumber": line["tracking_number"],
                            "trackingURL": line.get("tracking_url", "")
                        }
                    }],
                }
            })

        body = {
            "orderShipment": {
                "orderLines": {
                    "orderLine": order_lines,
                }
            }
        }
        return self.connection.send_request(
            method="POST",
            url=url,
            json=body,
        )


class Report(Resource):
    """
    Get report
    """

    path = 'getReport'


class Feed(Resource):
    path = "feeds"

    def create(self, resource, content):
        """Creates the feed on Walmart for respective resource

        Once you upload the Feed, you can use the Feed ID returned in the
        response to track the status of the feed and the status of the
        item within that Feed.

        :param resource: The resource for which the feed needs to be created.
        :param content: The content needed to create the Feed.
        """
        return self.connection.send_request(
            method="POST",
            url=self.url,
            params={
                "feedType": resource,
            },
            body=content,
        )

    def get_status(self, feed_id, offset=0, limit=100):
        "Returns the feed and item status for a specified Feed ID"
        return self.connection.send_request(
            method="GET",
            url="{}/{}".format(self.url, feed_id),
            params={
                "includeDetails": "true",
                "limit": limit,
                "offset": offset,
            },
        )


class Category(Resource):
    path = "utilities/taxonomy?feedType=MP_ITEM&version=4.2"
