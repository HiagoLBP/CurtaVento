import json
import codecs

def generate_v11():
    with open('usinas_bahia.json', 'r', encoding='utf-8') as f:
        usinas = json.load(f)

    sql_lines = []
    sql_lines.append("-- 1. Inserir municipios faltantes (usando uma subquery para evitar duplicacao)")
    
    municipios = set()
    for u in usinas:
        municipios.add(u['municipio'])
        
    for mun in sorted(list(municipios)):
        mun_esc = mun.replace("'", "''")
        sql_lines.append(f"INSERT INTO municipio (nome, estado, indice_vaf)")
        sql_lines.append(f"SELECT '{mun_esc}', 'BA', 0.001")
        sql_lines.append(f"WHERE NOT EXISTS (SELECT 1 FROM municipio WHERE nome = '{mun_esc}' AND estado = 'BA');")
        sql_lines.append("")
        
    sql_lines.append("-- 2. Inserir as usinas com subquery para pegar o ID do municipio correto")
    for u in usinas:
        nome_esc = u['nome'].replace("'", "''")
        mun_esc = u['municipio'].replace("'", "''")
        op_esc = u['operador'].replace("'", "''")
        
        lat_str = str(u['latitude']) if u['latitude'] else 'NULL'
        lng_str = str(u['longitude']) if u['longitude'] else 'NULL'
        
        sql_lines.append(f"INSERT INTO usina (nome, municipio_id, capacidade_instalada_mw, latitude, longitude, operador)")
        sql_lines.append(f"SELECT '{nome_esc}', id, {u['capacidade_instalada_mw']}, {lat_str}, {lng_str}, '{op_esc}'")
        sql_lines.append(f"FROM municipio WHERE nome = '{mun_esc}' AND estado = 'BA' LIMIT 1;")
        sql_lines.append("")
        
    final_sql = "\n".join(sql_lines)
    
    output_path = 'src/main/resources/db/migration/V11__Inserir_Usinas_Bahia_ANEEL.sql'
    with open(output_path, 'w', encoding='utf-8') as f:
        f.write(final_sql)
        
    print("V11 recriado com sucesso e limpo de caracteres estranhos!")

if __name__ == '__main__':
    generate_v11()
