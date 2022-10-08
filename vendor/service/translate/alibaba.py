import translators as ts

from service.translate.cache import cache

LANG_MAPPING = {
    "zh_CN": "zh",
    "en_US": "en"
}


class AlibabaTranslator:

    @cache
    def translate(self, from_lan, to_lan, text):
        ts._alibaba.query_count = 0
        return ts.alibaba(text, self.lang_mapping(from_lan), self.lang_mapping(to_lan), professional_field='general',
                          sleep_seconds=1)

    def lang_mapping(self, lang):
        if lang in LANG_MAPPING:
            return LANG_MAPPING.get(lang)
        return lang


if __name__ == "__main__":
    text = "质地: PU\n款式: 小方包\n流行元素: 印花\n风格: 时尚\n闭合方式: 拉链搭扣"
    tt = AlibabaTranslator()
    text1 = "质地: PU\n款式: 小方包\n流行元素: 印花\n风格: 时尚\n闭合方式: 拉链搭扣111"
    print(tt.translate("zh_CN", "en_US", text))
