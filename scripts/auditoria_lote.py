import pandas as pd
import urllib.request
import sqlalchemy
from io import StringIO
import argparse

MAPEAMENTO_USINAS = {
    "CAETIT": "Conj. Caetite",
    "IGAPORA II": "Conj. Igapora II",
    "IGAPORA III": "Conj. Igapora III",
    "PINDA": "Conj. Pindaí II"
}

def resolver_nome_padrao(nome_ons):
    nome_upper = str(nome_ons).upper()
    if "PINDA" in nome_upper: return MAPEAMENTO_USINAS["PINDA"]
    if "IGAPOR" in nome_upper and "III" in nome_upper: return MAPEAMENTO_USINAS["IGAPORA III"]
    if "IGAPOR" in nome_upper and "II" in nome_upper: return MAPEAMENTO_USINAS["IGAPORA II"]
    if "CAETIT" in nome_upper: return MAPEAMENTO_USINAS["CAETIT"]
    return None

def auditar_ano(ano):
    print(f"==================================================")
    print(f"--- AUDITORIA GERAL EM LOTE ONS ({ano}) ---")
    print(f"==================================================")
    
    # 1. Buscar tudo do banco de dados para o ano
    engine = sqlalchemy.create_engine('mysql+mysqlconnector://root:admin123@localhost/curtavento')
    query = f"""
        SELECT u.nome, DATE(h.data_corte), h.energia_cortada_mwh 
        FROM historico_corte_ons h
        JOIN usina u ON h.usina_id = u.id
        WHERE YEAR(h.data_corte) = {ano}
    """
    db_data = {}
    try:
        with engine.connect() as conn:
            for row in conn.execute(sqlalchemy.text(query)):
                # Armazena usando uma chave sem acento para facilitar o match (Pindaí -> Pindai)
                nome_db = row[0].replace('í', 'i').replace('ã', 'a').replace('é', 'e')
                db_data[(nome_db, str(row[1]))] = float(row[2])
    except Exception as e:
        print(f"Erro no banco: {e}")
        return
        
    print(f"[BANCO] Encontrados {len(db_data)} registros diarios salvos para {ano}.")
    
    total_verificados = 0
    total_aprovados = 0
    total_reprovados = 0
    falhas = []

    # 2. Processar mes a mes no ONS
    for mes in range(1, 13):
        url = f"https://ons-aws-prod-opendata.s3.amazonaws.com/dataset/restricao_coff_eolica_detail_tm/RESTRICAO_COFF_EOLICA_DETAIL_{ano}_{mes:02d}.csv"
        print(f"[{mes:02d}/{ano}] Baixando CSV original do servidor ONS...")
        
        try:
            csv_data = urllib.request.urlopen(url).read().decode('utf-8')
            df = pd.read_csv(StringIO(csv_data), sep=';')
        except Exception as e:
            print(f"[{mes:02d}/{ano}] Sem dados (O mes pode ainda nao ter fechado no ONS).")
            continue
            
        df['din_instante'] = pd.to_datetime(df['din_instante'])
        df['data_dia'] = df['din_instante'].dt.date.astype(str)
        df = df[(df['id_estado'] == 'BA') & (df['flg_geracaorestrita'] == 1)]
        
        # Mapear para nossos complexos unificados
        df['usina_padrao'] = df['nom_conjuntousina'].apply(resolver_nome_padrao)
        df = df.dropna(subset=['usina_padrao'])
        
        if df.empty:
            continue
            
        # Agrupar NIVEL 1: Usina + Instante (Juntar todos os sub-parques na mesma meia hora)
        agregado_inst = df.groupby(['usina_padrao', 'din_instante', 'data_dia']).agg(
            est=('val_geracaoestimada', 'sum'),
            ver=('val_geracaoverificada', 'sum')
        ).reset_index()
        
        # Matematica ONS (Jensen Effect)
        agregado_inst['corte_mw'] = (agregado_inst['est'] - agregado_inst['ver']).clip(lower=0)
        agregado_inst['corte_mwh'] = agregado_inst['corte_mw'] * 0.5
        
        # Agrupar NIVEL 2: Somar o total cortado no dia por Usina Unificada
        agregado_dia = agregado_inst.groupby(['usina_padrao', 'data_dia'])['corte_mwh'].sum().reset_index()
        
        # Bater com o banco de dados
        for _, row in agregado_dia.iterrows():
            usina = row['usina_padrao'].replace('í', 'i').replace('ã', 'a').replace('é', 'e')
            dia = row['data_dia']
            ons_val = row['corte_mwh']
            
            # ONS as vezes deixa o registro mas o corte real apos matematica da 0
            if ons_val == 0:
                continue
                
            total_verificados += 1
            db_val = db_data.get((usina, dia), 0.0)
            
            diff = abs(db_val - ons_val)
            if diff < 0.01:
                total_aprovados += 1
            else:
                total_reprovados += 1
                falhas.append(f"{dia} | {usina} -> Banco: {db_val:.2f} MWh | ONS: {ons_val:.2f} MWh")

    # 3. Gerar o relatorio final
    print(f"\n==================================================")
    print(f"--- RELATORIO FINAL DA AUDITORIA ({ano}) ---")
    print(f"==================================================")
    print(f"Registros diarios checados : {total_verificados}")
    print(f"Registros [APROVADOS]      : {total_aprovados}")
    print(f"Registros [REPROVADOS]     : {total_reprovados}")
    
    taxa = (total_aprovados / total_verificados * 100) if total_verificados > 0 else 100
    print(f"Taxa de Exatidao ONS/Banco : {taxa:.2f}%")
    
    if total_reprovados > 0:
        print(f"\nLista de Divergencias:")
        for f in falhas[:15]:
            print(f" - {f}")
        if len(falhas) > 15:
            print(f" - ... e mais {len(falhas)-15} dias com divergencia.")
    else:
        print(f"\n[SUCESSO ABSOLUTO] Todos os dados cruzados batem 100% com a fonte original!")

if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("--ano", type=int, default=2026, help="Ano para realizar a auditoria completa")
    args = parser.parse_args()
    auditar_ano(args.ano)
