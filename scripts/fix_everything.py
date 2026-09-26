"""
Script completo que:
1. Regenera o V11 SQL com as colunas corretas
2. Limpa o historico do Flyway (deleta V11 com falha)
3. Copia o arquivo para target/classes/db/migration/
"""
import subprocess
import shutil
import os

BASE = r'c:\Users\Hiago\Desktop\curta-vento'

print("=" * 60)
print("PASSO 1: Regenerar V11 SQL")
print("=" * 60)
result = subprocess.run(
    [r'C:\Users\Hiago\AppData\Local\Microsoft\WindowsApps\python.exe', 'scripts/generate_v11.py'],
    cwd=BASE, capture_output=True, text=True
)
print(result.stdout)
if result.stderr:
    print("STDERR:", result.stderr)
if result.returncode != 0:
    print("ERRO ao gerar V11!")
    exit(1)

# Verificar que o arquivo gerado tem conteudo real
sql_path = os.path.join(BASE, 'src', 'main', 'resources', 'db', 'migration', 'V11__Inserir_Usinas_Bahia_ANEEL.sql')
with open(sql_path, 'r', encoding='utf-8') as f:
    content = f.read()
    
line_count = content.count('\n')
print(f"  Arquivo tem {line_count} linhas")
print(f"  Primeiros 200 chars: {repr(content[:200])}")

# Validacao basica
assert line_count > 100, f"Arquivo muito pequeno! So {line_count} linhas"
assert 'estado' not in content, "ERRO: Arquivo ainda contem coluna 'estado'!"
assert 'indice_vaf' not in content, "ERRO: Arquivo ainda contem coluna 'indice_vaf'!"
assert content.startswith('--'), "ERRO: Arquivo nao comeca com comentario SQL '--'"
print("  VALIDACAO OK: Sem colunas fantasma, formato correto")

print()
print("=" * 60)
print("PASSO 2: Limpar Flyway (deletar V11 com falha do banco)")
print("=" * 60)
result2 = subprocess.run(
    ['java', '-cp', r'C:\Users\Hiago\.m2\repository\com\mysql\mysql-connector-j\9.7.0\mysql-connector-j-9.7.0.jar',
     'scripts/CheckFlyway.java'],
    cwd=BASE, capture_output=True, text=True
)
print(result2.stdout)
if result2.stderr:
    print("STDERR:", result2.stderr)

print()
print("=" * 60)
print("PASSO 3: Copiar V11 para target/classes/db/migration/")
print("=" * 60)
target_dir = os.path.join(BASE, 'target', 'classes', 'db', 'migration')
os.makedirs(target_dir, exist_ok=True)
target_path = os.path.join(target_dir, 'V11__Inserir_Usinas_Bahia_ANEEL.sql')
shutil.copy2(sql_path, target_path)
print(f"  Copiado para: {target_path}")

# Verificar que o target tem o arquivo correto
with open(target_path, 'r', encoding='utf-8') as f:
    target_content = f.read()
assert target_content == content, "ERRO: Arquivo no target difere do source!"
print("  VERIFICACAO: target == source OK")

print()
print("=" * 60)
print("TUDO PRONTO! Pode rodar o Spring Boot no IntelliJ.")
print("=" * 60)
