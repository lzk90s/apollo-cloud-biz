AGE_KEYWORDS_MAP = {
    "中小童": "Child",
    "婴幼童": "Infant",
    "儿童通用": "Child",
    "中大童": "Teen",
    "成人": "Adult",
    "新生儿": "Newborn",
    "青少年": "Teen"
}


def recognize(desc):
    v = None
    for k in AGE_KEYWORDS_MAP:
        if k in desc:
            v = AGE_KEYWORDS_MAP[k]
    return v
