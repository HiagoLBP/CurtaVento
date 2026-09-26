import urllib.request
import csv
import ssl
import json

def fetch_eolicas_bahia():
    print("Iniciando o script de extração ANEEL...")
    
    # Desativa verificação SSL por segurança em conexões com sites do governo
    ctx = ssl.create_default_context()
    ctx.check_hostname = False
    ctx.verify_mode = ssl.CERT_NONE

    # URL oficial do CSV do SIGA (Sistema de Informações de Geração da ANEEL)
    print("1. Conectando à base de Dados Abertos da ANEEL...")
    url = "https://dadosabertos.aneel.gov.br/dataset/6d90b77c-c5f5-4d81-bdec-7bc619494bb9/resource/11ec447d-698d-4ab8-977f-b424d5deee6a/download/siga-empreendimentos-geracao.csv"
    
    req = urllib.request.Request(url, headers={'User-Agent': 'Mozilla/5.0 (CurtaVento Fetcher)'})
    
    try:
        with urllib.request.urlopen(req, context=ctx) as response:
            csv_data = response.read().decode('utf-8', errors='replace')
    except Exception as e:
        print(f"Erro ao baixar o CSV da ANEEL: {e}")
        return

    print("2. Dados baixados com sucesso! Processando e filtrando...")

    # ANEEL usa ';' como separador e aspas duplas
    reader = csv.DictReader(csv_data.splitlines(), delimiter=';', quotechar='"')

    usinas_ba = []
    
    for row in reader:
        tipo = row.get('SigTipoGeracao', '')
        uf = row.get('SigUFPrincipal', '')
        fase = row.get('DscFaseUsina', '')

        # Filtrar apenas EÓLICAS, na BAHIA, que já estejam em OPERAÇÃO
        if tipo == 'EOL' and uf == 'BA' and 'Opera' in fase:
            # Extrair capacidade instalada em MW
            kw_str = row.get('MdaPotenciaFiscalizadaKw', '0').replace(',', '.')
            try:
                capacidade_mw = round(float(kw_str) / 1000, 2)
            except:
                capacidade_mw = 0.0
                
            # Extrair município limpo (ex: "Guanambi - BA" -> "Guanambi")
            municipio_bruto = row.get('DscMuninicpios', '')
            municipio = municipio_bruto.split('-')[0].strip() if '-' in municipio_bruto else municipio_bruto
            
            # Coordenadas
            lat = row.get('NumCoordNEmpreendimento', '').replace(',', '.')
            lng = row.get('NumCoordEEmpreendimento', '').replace(',', '.')
            
            # Nome do operador (A ANEEL junta "100% para NOME_EMPRESA - CNPJ (PI)")
            operador_bruto = row.get('DscPropriRegimePariticipacao', '')
            operador = 'N/I'
            if 'para ' in operador_bruto:
                operador = operador_bruto.split('para ')[1].split('-')[0].strip()
            elif operador_bruto:
                operador = operador_bruto.split('-')[0].strip()

            usinas_ba.append({
                "nome": row.get('NomEmpreendimento', '').strip(),
                "municipio": municipio,
                "capacidade_instalada_mw": capacidade_mw,
                "latitude": float(lat) if lat else None,
                "longitude": float(lng) if lng else None,
                "operador": operador
            })

    print(f"\\nSUCESSO! Foram encontradas {len(usinas_ba)} usinas eólicas em operação na Bahia.")

    print("3. Gerando arquivos de saída...")
    
    # Gerar JSON
    with open('usinas_bahia.json', 'w', encoding='utf-8') as f:
        json.dump(usinas_ba, f, ensure_ascii=False, indent=2)
    print(" -> Criado: usinas_bahia.json (Para usar no frontend ou via API)")

    # Gerar SQL
    with open('usinas_bahia.sql', 'w', encoding='utf-8') as f:
        f.write("-- INSERT automático gerado do SIGA/ANEEL para o CurtaVento\\n")
        f.write("-- Inclui apenas eólicas (EOL) em operação na Bahia (BA)\\n\\n")
        
        for u in usinas_ba:
            lat_str = str(u['latitude']) if u['latitude'] else 'NULL'
            lng_str = str(u['longitude']) if u['longitude'] else 'NULL'
            
            # Escapar aspas simples no nome
            nome_esc = u['nome'].replace("'", "''")
            mun_esc = u['municipio'].replace("'", "''")
            op_esc = u['operador'].replace("'", "''")
            
            sql = f"INSERT INTO usina (nome, municipio, capacidade_instalada_mw, latitude, longitude, operador) VALUES ('{nome_esc}', '{mun_esc}', {u['capacidade_instalada_mw']}, {lat_str}, {lng_str}, '{op_esc}');\\n"
            f.write(sql)
            
    print(" -> Criado: usinas_bahia.sql (Para rodar no banco de dados)")
    print("\\nScript finalizado!")

if __name__ == '__main__':
    fetch_eolicas_bahia()
