import mysql.connector
conn = mysql.connector.connect(host='localhost',user='root',password='admin123',database='curtavento')
cursor = conn.cursor(dictionary=True)
cursor.execute('SELECT YEAR(data_corte) as ano, MONTH(data_corte) as mes, COUNT(*) as qtd FROM historico_corte_ons GROUP BY ano, mes ORDER BY ano, mes')
result = cursor.fetchall()
for r in result:
    print(f'Ano: {r["ano"]} | Mês: {r["mes"]:02d} | Registros: {r["qtd"]}')
