class BizException(Exception):
    def __init__(self, message, status=500, payload=None):
        Exception.__init__(self)
        self.message = message
        self.status = status
        self.payload = payload

    def to_dict(self):
        rv = dict(self.payload or ())
        rv['status'] = self.status
        rv['message'] = self.message
        return rv

    def __str__(self):
        return "Error [" + str(self.status) + "] => " + self.message


class RecognizeBizException(BizException):
    pass
