import sqlalchemy
engine = sqlalchemy.create_engine('mysql+mysqlconnector://root:admin123@localhost/curtavento')
with engine.connect() as conn:
    conn.execute(sqlalchemy.text("UPDATE usina SET nome = 'Conj. Caetité' WHERE id = 1"))
    conn.execute(sqlalchemy.text("UPDATE usina SET nome = 'Conj. Igaporã II' WHERE id = 2"))
    conn.execute(sqlalchemy.text("UPDATE usina SET nome = 'Conj. Igaporã III' WHERE id = 3"))
    conn.commit()
print('Nomes atualizados!')
