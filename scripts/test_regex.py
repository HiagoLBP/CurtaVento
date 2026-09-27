import re
import mysql.connector

data = """[Abaré](https://www.ibge.gov.br/cidades-e-estados?c=2900207)
2900207
[Acajutiba](https://www.ibge.gov.br/cidades-e-estados?c=2900306)
2900306
B[Baianópolis](https://www.ibge.gov.br/cidades-e-estados?c=2902500)
2902500"""

pattern = r'\[(.*?)\]\(.*?\)\s*(\d+)'
matches = re.findall(pattern, data)

for name, code in matches:
    print(f"Name: {name}, Code: {code}")
