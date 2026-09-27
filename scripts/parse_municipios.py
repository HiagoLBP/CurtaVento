import re
import mysql.connector
import unicodedata

# Pega o texto do user que tem as tags
with open("scripts/test_regex.py", "r", encoding="utf-8") as f:
    pass # I'll copy the text to another file or just read from the transcript.

transcript_path = r"C:\Users\Hiago\.gemini\antigravity\brain\dd24e648-9dd8-487b-9a86-72dd62896047\.system_generated\logs\transcript_full.jsonl"
import json
with open(transcript_path, 'r', encoding='utf-8') as f:
    lines = f.readlines()
last_user_msg = ""
for line in reversed(lines):
    data = json.loads(line)
    if data.get('source') == 'USER_EXPLICIT':
        last_user_msg = data.get('content', '')
        break

# Parse IBGE codes
ibge_pattern = r'\[(.*?)\]\(https://www.ibge.gov.br/cidades-e-estados\?c=\d+\)\s*(\d+)'
ibge_matches = re.findall(ibge_pattern, last_user_msg)

ibge_dict_fixed = {}
for name, code in ibge_matches:
    if "]" in name:
        name = name.split("]")[-1]
    clean_name = ''.join(c for c in unicodedata.normalize('NFD', name) if unicodedata.category(c) != 'Mn').upper()
    ibge_dict_fixed[clean_name] = code

# Parse IPM
ipm_dict = {}
with open("scripts/ipm_raw.txt", "r", encoding="utf-8") as f:
    for line in f:
        line = line.strip()
        if not line: continue
        # "1 00107 ABAIRA 0,0047765 0,0149171 0,0094250 0,0035862 0,0051506 0,0378554"
        match = re.match(r'^\d+\s+\d+\s+(.*?)\s+\d+,\d+\s+\d+,\d+\s+\d+,\d+\s+\d+,\d+\s+\d+,\d+\s+(\d+,\d+)$', line)
        if match:
            name = match.group(1).strip()
            clean_name = ''.join(c for c in unicodedata.normalize('NFD', name) if unicodedata.category(c) != 'Mn').upper()
            ipm = match.group(2).replace(',', '.')
            ipm_dict[clean_name] = ipm

print(f"Loaded {len(ibge_dict_fixed)} IBGE codes.")
print(f"Loaded {len(ipm_dict)} IPM values.")

# Generate SQL
sql_lines = []
sql_lines.append("ALTER TABLE municipio ADD COLUMN IF NOT EXISTS ipm DECIMAL(10,7);")
sql_lines.append("")

matched_count = 0
aliases = {
    'ABAIRA': '2900108',
    'CAMACA': '2905602',
    'CAPELA ALTO ALEGRE': '2906857',
    'MACURERE': '2919900',
    'PRESIDENTE J. QUADROS': '2925709',
    'SANTA TERESINHA': '2928505'
}

for clean_name, ipm in ipm_dict.items():
    ibge = ibge_dict_fixed.get(clean_name) or aliases.get(clean_name)
    if ibge:
        sql = f"UPDATE municipio SET codigo_ibge = '{ibge}', ipm = {ipm}, vaf_ano_base = {ipm} WHERE UPPER(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(nome,'Á','A'),'É','E'),'Í','I'),'Ó','O'),'Ú','U'),'Ç','C'),'Ã','A')) = '{clean_name.replace('Ç','C').replace('Ã','A')}';"
        sql_lines.append(sql)
        matched_count += 1
    else:
        print("Missing IBGE for:", clean_name)


sql_lines.append(f"SELECT 'Atualizados {matched_count} municípios' AS Resultado;")

with open(r"C:\Users\Hiago\.copilot\repos\copilot-worktrees\curta-vento\hiagolbp-fantastic-doodle\src\main\resources\db\migration\V21__Inserir_Codigos_IBGE_E_IPM.sql", "w", encoding="utf-8") as f:
    f.write("\n".join(sql_lines))

print(f"Generated SQL with {matched_count} updates.")
