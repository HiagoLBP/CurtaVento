import urllib.request
req = urllib.request.Request('https://ons-aws-prod-opendata.s3.amazonaws.com/dataset/restricao_coff_eolica_detail_tm/RESTRICAO_COFF_EOLICA_DETAIL_2026_01.csv', headers={'Range': 'bytes=0-2000'})
res = urllib.request.urlopen(req)
print(res.read().decode('utf-8').split('\n')[0])
