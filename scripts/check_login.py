import urllib.request, urllib.error

req = urllib.request.Request(
    'http://localhost:8080/api/auth/login', 
    data=b'{"email":"admin@curtavento.com","senha":"123456"}', 
    headers={'Content-Type':'application/json'}
)
try:
    res = urllib.request.urlopen(req)
    print(res.read().decode())
except urllib.error.HTTPError as e:
    print(e.read().decode())
