import pandas as pd
import urllib.request
from io import StringIO
import sqlalchemy
import unicodedata

def normalize(name):
    if not name or pd.isna(name): return ""
    nfkd = unicodedata.normalize('NFKD', str(name))
    return ''.join(c for c in nfkd if not unicodedata.combining(c)).upper().strip()

print("Downloading 1 month of ONS data for mapping test...")
url = "https://ons-aws-prod-opendata.s3.amazonaws.com/dataset/restricao_coff_eolica_detail_tm/RESTRICAO_COFF_EOLICA_DETAIL_2023_01.csv"
req = urllib.request.urlopen(url)
csv_data = req.read().decode('utf-8')
df = pd.read_csv(StringIO(csv_data), sep=';')

df_ba = df[df['id_estado'] == 'BA']
ons_usinas = df_ba['nom_usina'].unique()
ons_conjuntos = df_ba['nom_conjuntousina'].unique()

print(f"ONS has {len(ons_usinas)} Usinas and {len(ons_conjuntos)} Conjuntos in BA for 2023-01")

# Get DB usinas
engine = sqlalchemy.create_engine('mysql+mysqlconnector://root:admin123@localhost/curtavento')
with engine.connect() as conn:
    result = conn.execute(sqlalchemy.text("SELECT id, nome FROM usina"))
    db_usinas = {row[0]: normalize(row[1]) for row in result.fetchall()}

print(f"DB has {len(db_usinas)} Usinas")

mapped = 0
unmapped = set()

for idx, (db_id, db_nome) in enumerate(db_usinas.items()):
    # try direct match
    match_found = False
    for ons_u in ons_usinas:
        if normalize(ons_u) == db_nome or normalize(ons_u) in db_nome or db_nome in normalize(ons_u):
            mapped += 1
            match_found = True
            break
    if not match_found:
        for ons_c in ons_conjuntos:
            if normalize(ons_c) == db_nome or normalize(ons_c) in db_nome or db_nome in normalize(ons_c):
                mapped += 1
                match_found = True
                break
    
    if not match_found:
        unmapped.add(db_nome)

print(f"Mapped {mapped} DB usinas to ONS")
print(f"Unmapped: {len(unmapped)}")
print(list(unmapped)[:10])

