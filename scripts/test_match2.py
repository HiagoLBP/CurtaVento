import mysql.connector
import urllib.request
import csv
from io import TextIOWrapper
import unicodedata

# 1. Get DB Usinas
conn = mysql.connector.connect(host='localhost',user='root',password='admin123',database='curtavento')
cursor = conn.cursor(dictionary=True)
cursor.execute('SELECT id, nome, operador FROM usina')
db_usinas = cursor.fetchall()

def remove_accents(s):
    return ''.join(c for c in unicodedata.normalize('NFD', s) if unicodedata.category(c) != 'Mn').upper()

# 2. Get ONS Usinas
url = 'https://ons-aws-prod-opendata.s3.amazonaws.com/dataset/restricao_coff_eolica_detail_tm/RESTRICAO_COFF_EOLICA_DETAIL_2026_01.csv'
req = urllib.request.Request(url, headers={'Range': 'bytes=0-5000000'})
response = urllib.request.urlopen(req)

content = response.read().decode('utf-8', errors='ignore')
lines = content.split('\n')
header = lines[0].split(';')

idx_estado = header.index("id_estado")
idx_usina = header.index("nom_usina")

ons_usinas = set()
for line in lines[1:]:
    row = line.split(';')
    if len(row) > max(idx_estado, idx_usina) and row[idx_estado].strip().upper() == 'BA':
        ons_usinas.add(row[idx_usina].strip())

unique_matched_db = set()

for ons_nome in ons_usinas:
    cleanOns = remove_accents(ons_nome).replace("EOLICA", "").replace("CENTRAL", "").replace("GERADORA", "").replace("KV", "")
    import re
    cleanOns = re.sub(r'[0-9]', '', cleanOns).strip()
    palavras = cleanOns.split()
    
    maxScore = 0
    melhor = None
    
    for u in db_usinas:
        cleanDb = remove_accents(u['nome'])
        cleanOp = remove_accents(u['operador'] or '')
        score = 0
        for p in palavras:
            if len(p) <= 2:
                if p not in ["I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X"]:
                    continue
            if p in ["DE", "DA", "DO", "DOS", "DAS"]:
                continue
            
            if p in cleanDb: score += 2
            if re.search(r'\b' + p + r'\b', cleanDb): score += 10
            if re.search(r'\b' + p + r'\b', cleanOp): score += 5
            
        if score > maxScore and score > 0:
            maxScore = score
            melhor = u
            
    if melhor:
        unique_matched_db.add(melhor['id'])

print(f"Unique DB usinas matched: {len(unique_matched_db)}")
