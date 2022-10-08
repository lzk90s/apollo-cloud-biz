DEFAULT_COLOR = 'Multi-color'

# 颜色分类表
COLOR_CATEGORY_MAP = {
    '混合': 'Multi-color',
    '灰白': 'Off-White',
    '青铜': 'Bronze',
    '卡其': 'Yellow',
    '咖啡': 'Brown',

    # --------------- #
    '杏': 'Beige',
    '红': 'Red',
    '橙': 'Orange',
    '黄': 'Yellow',
    '绿': 'Green',
    '青': 'cyan',
    '蓝': 'Blue',
    '紫': 'Purple',
    '米': 'Beige',
    '棕': 'Brown',
    '金': 'Gold',
    '灰': 'Gray',
    '浅': 'Clear',
    '黑': 'Black',
    '粉': 'Pink',
    '白': 'White',
    '银': 'Silver',
}


def recognize(desc, image_url=None):
    v = _recognize_desc(desc)
    if v:
        return v
    v = DEFAULT_COLOR
    return v


def _recognize_desc(desc):
    if not desc:
        return None
    colors = []
    for c in COLOR_CATEGORY_MAP:
        if c in desc:
            colors.append(COLOR_CATEGORY_MAP[c])
    return ",".join(colors)


def _recognize_image(image_url):
    if not image_url:
        return None
    return None
