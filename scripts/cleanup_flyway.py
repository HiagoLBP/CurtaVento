import mysql.connector

conn = mysql.connector.connect(host='localhost',user='root',password='admin123',database='curtavento')
cursor = conn.cursor()

# 1. Limpa a entrada com falha no Flyway
cursor.execute("DELETE FROM flyway_schema_history WHERE version = '21'")

# 2. Remove a coluna que ficou pela metade
try:
    cursor.execute("ALTER TABLE municipio DROP COLUMN ipm")
    print("Dropped partial ipm column.")
except Exception as e:
    print("ipm column:", e)

# 3. Restaura códigos IBGE temporários se houver algum
cursor.execute("SELECT id, codigo_ibge FROM municipio WHERE codigo_ibge LIKE 'TMP_%'")
tmps = cursor.fetchall()
print("Found temporary ibge codes:", len(tmps))

conn.commit()
print("Cleanup completed successfully!")
