import urllib.request
req = urllib.request.Request('https://ons-aws-prod-opendata.s3.amazonaws.com/dataset/restricao_coff_eolica_detail_tm/RESTRICAO_COFF_EOLICA_DETAIL_2026_01.csv', method='HEAD')
res = urllib.request.urlopen(req)
print(f'Size: {int(res.headers.get("Content-Length")) / 1024 / 1024:.2f} MB')
