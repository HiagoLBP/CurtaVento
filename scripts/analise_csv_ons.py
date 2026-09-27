import urllib.request
import csv
from io import TextIOWrapper

url = 'https://ons-aws-prod-opendata.s3.amazonaws.com/dataset/restricao_coff_eolica_detail_tm/RESTRICAO_COFF_EOLICA_DETAIL_2026_02.csv'

print(f"Baixando e analisando o arquivo: {url}")
print("Isso pode levar cerca de 1 a 2 minutos...")

req = urllib.request.Request(url)
response = urllib.request.urlopen(req)

conjuntos_ba = set()
usinas_ba = set()

# Processa o CSV via streaming sem carregar tudo na RAM de uma vez
reader = csv.reader(TextIOWrapper(response, encoding='utf-8'), delimiter=';')
header = next(reader)

idx_estado = header.index("id_estado")
idx_conjunto = header.index("nom_conjuntousina")
idx_usina = header.index("nom_usina")

linhas_processadas = 0

for row in reader:
    linhas_processadas += 1
    if len(row) <= max(idx_estado, idx_conjunto, idx_usina):
        continue
        
    estado = row[idx_estado].strip().upper()
    
    if estado == 'BA':
        conjunto = row[idx_conjunto].strip()
        usina = row[idx_usina].strip()
        
        conjuntos_ba.add(conjunto)
        usinas_ba.add(usina)

print("-" * 50)
print(f"Linhas totais no CSV: {linhas_processadas}")
print(f"Quantidade de CONJUNTOS na Bahia: {len(conjuntos_ba)}")
print(f"Quantidade de USINAS na Bahia: {len(usinas_ba)}")
print("-" * 50)
print("Amostra de 5 Conjuntos:")
for c in list(conjuntos_ba)[:5]:
    print(f"  - {c}")

print("\nAmostra de 5 Usinas:")
for u in list(usinas_ba)[:5]:
    print(f"  - {u}")
