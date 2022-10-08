from service.ecommerce.walmart.item import clothing

BUILDER_MAP = {
    "Clothing": clothing.build_visible_from_sku,
    "Baby Clothing": clothing.build_visible_from_sku
}


def get_visible_builder(category):
    if category not in BUILDER_MAP:
        return None
    return BUILDER_MAP[category]
