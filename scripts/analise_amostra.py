import urllib.request
import csv
from io import TextIOWrapper

url = 'https://ons-aws-prod-opendata.s3.amazonaws.com/dataset/restricao_coff_eolica_detail_tm/RESTRICAO_COFF_EOLICA_DETAIL_2026_01.csv'

# Download the first 5MB
req = urllib.request.Request(url, headers={'Range': 'bytes=0-5000000'})
response = urllib.request.urlopen(req)

conjuntos_ba = set()
usinas_ba = set()

content = response.read().decode('utf-8', errors='ignore')
lines = content.split('\n')

header = lines[0].split(';')

idx_estado = header.index("id_estado")
idx_conjunto = header.index("nom_conjuntousina")
idx_usina = header.index("nom_usina")

for line in lines[1:]:
    row = line.split(';')
    if len(row) <= max(idx_estado, idx_conjunto, idx_usina):
        continue
        
    estado = row[idx_estado].strip().upper()
    if estado == 'BA':
        conjuntos_ba.add(row[idx_conjunto].strip())
        usinas_ba.add(row[idx_usina].strip())

print("-" * 50)
print(f"Linhas lidas (amostra de 5MB): {len(lines)}")
print(f"Quantidade de CONJUNTOS na Bahia (amostra): {len(conjuntos_ba)}")
print(f"Quantidade de USINAS na Bahia (amostra): {len(usinas_ba)}")
print("-" * 50)
print("Amostra de 5 Conjuntos:")
for c in list(conjuntos_ba)[:5]:
    print(f"  - {c}")

print("\nAmostra de 5 Usinas:")
for u in list(usinas_ba)[:5]:
    print(f"  - {u}")
