import mysql.connector
import unicodedata
import re
import json

conn = mysql.connector.connect(host='localhost',user='root',password='admin123',database='curtavento')
cursor = conn.cursor(dictionary=True)

# 1. Parse IPM
ipm_dict = {}
with open('scripts/ipm_raw.txt', 'r', encoding='utf-8') as f:
    for line in f:
        line = line.strip()
        if not line: continue
        match = re.match(r'^\d+\s+\d+\s+(.*?)\s+\d+,\d+\s+\d+,\d+\s+\d+,\d+\s+\d+,\d+\s+\d+,\d+\s+(\d+,\d+)$', line)
        if match:
            name = match.group(1).strip()
            clean_name = ''.join(c for c in unicodedata.normalize('NFD', name) if unicodedata.category(c) != 'Mn').upper()
            ipm = match.group(2).replace(',', '.')
            ipm_dict[clean_name] = ipm

# 2. Parse IBGE
transcript_path = r'C:\Users\Hiago\.gemini\antigravity\brain\dd24e648-9dd8-487b-9a86-72dd62896047\.system_generated\logs\transcript_full.jsonl'
with open(transcript_path, 'r', encoding='utf-8') as f:
    lines = f.readlines()
last_user_msg = ''
for line in reversed(lines):
    data = json.loads(line)
    if data.get('source') == 'USER_EXPLICIT':
        content = data.get('content', '')
        if 'ibge.gov.br' in content:
            last_user_msg = content
            break

ibge_pattern = r'\[(.*?)\]\(https://www.ibge.gov.br/cidades-e-estados\?c=\d+\)\n(\d+)'
ibge_matches = re.findall(ibge_pattern, last_user_msg)

ibge_dict = {}
original_names = {}
for name, code in ibge_matches:
    if ']' in name:
        name = name.split(']')[-1]
    name = name.strip()
    clean_name = ''.join(c for c in unicodedata.normalize('NFD', name) if unicodedata.category(c) != 'Mn').upper()
    ibge_dict[clean_name] = code
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

cursor.execute('SELECT id, nome, codigo_ibge FROM municipio')
db_rows = cursor.fetchall()

db_map = {}
for r in db_rows:
    nome = r['nome']
    if nome == 'Xique': nome = 'Xique-Xique'
    clean = ''.join(c for c in unicodedata.normalize('NFD', nome) if unicodedata.category(c) != 'Mn').upper().replace('-', '')
    db_map[clean] = r['id']

statements = []
statements.append('ALTER TABLE municipio ADD COLUMN ipm DECIMAL(10,7);')
statements.append("UPDATE municipio SET codigo_ibge = CONCAT('TMP_', id);")

processed = set()
for clean, cid in db_map.items():
    clean_lookup = clean
    ipm = ipm_dict.get(clean_lookup)
    ibge = ibge_dict.get(clean_lookup) or aliases.get(clean_lookup)
    orig = original_names.get(clean_lookup, clean.title())
        orig_escaped = orig.replace("'", "''")
        statements.append(f"UPDATE municipio SET nome = '{orig_escaped}', codigo_ibge = '{ibge}', ipm = {ipm}, vaf_ano_base = {ipm} WHERE id = {cid};")
        processed.add(clean_lookup)

for clean_name, ipm in ipm_dict.items():
    clean_no_hyphen = clean_name.replace('-', '')
    if clean_name in processed or clean_no_hyphen in processed:
        continue
    ibge = ibge_dict.get(clean_name) or aliases.get(clean_name)
    orig = original_names.get(clean_name, clean_name.title())
    if ibge:
        orig_escaped = orig.replace("'", "''")
        statements.append(f"INSERT INTO municipio (nome, codigo_ibge, ipm, vaf_ano_base) VALUES ('{orig_escaped}', '{ibge}', {ipm}, {ipm});")
        processed.add(clean_name)
        processed.add(clean_no_hyphen)

print('Total statements to execute:', len(statements))

# Test execution in transaction!
errors = 0
for i, stmt in enumerate(statements):
    try:
        cursor.execute(stmt)
    except Exception as e:
        print(f'Error at statement {i}: {e}')
        print('Stmt:', stmt[:100])
        errors += 1
        break

if errors == 0:
    print('ALL STATEMENTS EXECUTED PERFECTLY!')
    with open('src/main/resources/db/migration/V21__Inserir_Codigos_IBGE_E_IPM.sql', 'w', encoding='utf-8') as f:
        f.write('\n'.join(statements))
    with open(r'C:\Users\Hiago\.copilot\repos\copilot-worktrees\curta-vento\hiagolbp-fantastic-doodle\src\main\resources\db\migration\V21__Inserir_Codigos_IBGE_E_IPM.sql', 'w', encoding='utf-8') as f:
        f.write('\n'.join(statements))
    print('Saved to both Desktop and Worktree!')

conn.rollback()
