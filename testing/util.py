import requests
import json
import logging
import urllib

log = logging.getLogger(__name__)


def url_encode(params):
    """Wrapper for urllib.urlencode which doesn't handle UTF-8 encoded characters"""

    for key, value in params.items():
        if isinstance(value, str):
            params[key] = value.encode('utf-8')

    return urllib.parse.urlencode(params, doseq=True)


def http_get(url, headers=None, data=None):
    if data:
        data = {k: v for k, v in data.items() if v}
        encoded_args = url_encode(data)

        if len(encoded_args) > 0:
            url = "%s?%s" % (url, encoded_args)
            
    result = requests.get(url, headers=headers)

    try:
        return result.status_code, json.loads(result.text)
    except:
        return result.status_code, result.text


def http_post(url, headers=None, data=None):
    content_type = headers.get('Content-Type') if headers else "application/json"

    if data:
        data = {k: v for k, v in data.items() if v}
        if content_type == 'application/x-www-form-urlencoded':
            data = url_encode(data)
        else:
            data = json.dumps(data) if isinstance(data, dict) else data

    result = requests.post(url=url, headers=headers, data=data)

    try:
        return result.status_code, json.loads(result.text)
    except:
        return result.status_code, result.text


def http_delete(url, headers=None, data=None):
    if data:
        data = {k: v for k, v in data.items() if v}
        encoded_args = url_encode(data)

        if len(encoded_args) > 0:
            url = "%s?%s" % (url, encoded_args)
            
    result = requests.delete(url, headers=headers)

    try:
        return result.status_code, json.loads(result.text)
    except:
        return result.status_code, result.text


class Client:

    def __init__(self, host, port):
        self.url = 'http://%s:%s' % (host, port)

    def get(self, route, data = None):
        full_url = '/'.join([self.url, route])
        return http_get(full_url, data=data)

    def post(self, route, data=None):
        full_url = '/'.join([self.url, route])
        return http_post(full_url, headers={'Content-Type': 'application/json'}, data=data)

    def delete(self, route, data = None):
        full_url = '/'.join([self.url, route])
        return http_delete(full_url, data=data)
