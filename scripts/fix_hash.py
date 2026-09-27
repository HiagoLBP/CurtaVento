import mysql.connector

conn = mysql.connector.connect(host='localhost',user='root',password='admin123',database='curtavento')
cursor = conn.cursor()
cursor.execute("UPDATE usuario SET senha_hash='$2a$10$Sxptk1DjZwWmg7Vh7IF6A.Fveh2rmVqC.gM99HgipoD7xzXDHKFpW'")
conn.commit()
print("Fixed properly!")
