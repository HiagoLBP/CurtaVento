import os

path = 'src/main/resources/db/migration/V10__Inserir_Usinas_Bahia_ANEEL.sql'

with open(path, 'r', encoding='utf-8') as f:
    text = f.read()

# Trocar a string literal "\n" por uma quebra de linha de verdade
fixed_text = text.replace('\\n', '\n')

with open(path, 'w', encoding='utf-8') as f:
    f.write(fixed_text)

print("Arquivo corrigido com sucesso!")
