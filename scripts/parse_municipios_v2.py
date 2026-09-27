import re
import mysql.connector
import unicodedata
import json

# 1. Obter IBGE do user
transcript_path = r"C:\Users\Hiago\.gemini\antigravity\brain\dd24e648-9dd8-487b-9a86-72dd62896047\.system_generated\logs\transcript_full.jsonl"
with open(transcript_path, 'r', encoding='utf-8') as f:
    lines = f.readlines()
last_user_msg = ""
for line in reversed(lines):
    data = json.loads(line)
    if data.get('source') == 'USER_EXPLICIT':
        last_user_msg = data.get('content', '')
        break

ibge_pattern = r'\[(.*?)\]\(https://www.ibge.gov.br/cidades-e-estados\?c=\d+\)\n(\d+)'
ibge_matches = re.findall(ibge_pattern, last_user_msg)

ibge_dict_fixed = {}
original_names = {}
for name, code in ibge_matches:
    if "]" in name:
        name = name.split("]")[-1]
    name = name.strip()
    clean_name = ''.join(c for c in unicodedata.normalize('NFD', name) if unicodedata.category(c) != 'Mn').upper()
    ibge_dict_fixed[clean_name] = code
    original_names[clean_name] = name

aliases = {
    'ABAIRA': '2900108',
    'CAMACA': '2905602',
    'CAPELA ALTO ALEGRE': '2906857',
    'MACURERE': '2919900',
    'PRESIDENTE J. QUADROS': '2925709',
    'SANTA TERESINHA': '2928505',
    'XIQUEXIQUE': '2933604'
}
original_names['ABAIRA'] = 'Abaíra'
original_names['CAMACA'] = 'Camacã'
original_names['CAPELA ALTO ALEGRE'] = 'Capela do Alto Alegre'
original_names['MACURERE'] = 'Macururé'
original_names['PRESIDENTE J. QUADROS'] = 'Presidente Jânio Quadros'
original_names['SANTA TERESINHA'] = 'Santa Teresinha'

# 2. Obter IPM
ipm_dict = {}
with open("scripts/ipm_raw.txt", "r", encoding="utf-8") as f:
    for line in f:
        line = line.strip()
        if not line: continue
        match = re.match(r'^\d+\s+\d+\s+(.*?)\s+\d+,\d+\s+\d+,\d+\s+\d+,\d+\s+\d+,\d+\s+\d+,\d+\s+(\d+,\d+)$', line)
        if match:
            name = match.group(1).strip()
            clean_name = ''.join(c for c in unicodedata.normalize('NFD', name) if unicodedata.category(c) != 'Mn').upper()
            ipm = match.group(2).replace(',', '.')
            ipm_dict[clean_name] = ipm

# 3. Pegar do BD
conn = mysql.connector.connect(host='localhost',user='root',password='admin123',database='curtavento')
cursor = conn.cursor(dictionary=True)
cursor.execute('SELECT id, nome FROM municipio')
db_cities = cursor.fetchall()

db_mapping = {}
for row in db_cities:
    name = row['nome']
    if name == 'Xique': name = 'Xique-Xique'
    clean_name = ''.join(c for c in unicodedata.normalize('NFD', name) if unicodedata.category(c) != 'Mn').upper()
    if clean_name == 'XIQUEXIQUE': clean_name = 'XIQUE-XIQUE'
    db_mapping[clean_name] = row['id']

# 4. Construir SQL
sql_lines = []
sql_lines.append("ALTER TABLE municipio ADD COLUMN IF NOT EXISTS ipm DECIMAL(10,7);")
sql_lines.append("")

for clean_name, ipm in ipm_dict.items():
    ibge = ibge_dict_fixed.get(clean_name) or aliases.get(clean_name)
    if clean_name == 'XIQUE-XIQUE': ibge = '2933604'
    if not ibge:
        print("Missing IBGE for:", clean_name)
        continue
    
    orig_name = original_names.get(clean_name, clean_name.title())
    
    if clean_name in db_mapping:
        cid = db_mapping[clean_name]
        sql_lines.append(f"UPDATE municipio SET codigo_ibge = '{ibge}', ipm = {ipm}, vaf_ano_base = {ipm}, nome = '{orig_name}' WHERE id = {cid};")
    else:
        sql_lines.append(f"INSERT INTO municipio (nome, codigo_ibge, ipm, vaf_ano_base) VALUES ('{orig_name}', '{ibge}', {ipm}, {ipm});")

with open(r"C:\Users\Hiago\.copilot\repos\copilot-worktrees\curta-vento\hiagolbp-fantastic-doodle\src\main\resources\db\migration\V21__Inserir_Codigos_IBGE_E_IPM.sql", "w", encoding="utf-8") as f:
    f.write("\n".join(sql_lines))

print(f"Generated SQL with {len(sql_lines)} statements.")
