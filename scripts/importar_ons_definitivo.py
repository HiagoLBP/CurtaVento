import pandas as pd
import urllib.request
import sqlalchemy
import time
import unicodedata
import sys
import re
from io import StringIO

def clean_name(name):
    if not name or pd.isna(name): return ""
    nfkd = unicodedata.normalize('NFKD', str(name))
    text = ''.join(c for c in nfkd if not unicodedata.combining(c)).upper()
    text = re.sub(r'^(VENTOS DE |SERRA DO |SERRA DA |PARQUE EOLICO |EOLICA |CENTRAL |CONJ\. |CONJUNTO |COMPLEXO )', '', text)
    text = text.replace('SAO ', '').replace('SANTA ', '').replace('SANTO ', '')
    romans = {'I': '1', 'II': '2', 'III': '3', 'IV': '4', 'V': '5', 'VI': '6', 'VII': '7', 'VIII': '8', 'IX': '9', 'X': '10'}
    parts = text.split()
    if parts and parts[-1] in romans:
        parts[-1] = romans[parts[-1]]
    if parts and parts[-1].isdigit():
        parts[-1] = str(int(parts[-1]))
    return " ".join(parts).strip()

def baixar_e_processar(ano, mes):
    url = f"https://ons-aws-prod-opendata.s3.amazonaws.com/dataset/restricao_coff_eolica_detail_tm/RESTRICAO_COFF_EOLICA_DETAIL_{ano}_{mes:02d}.csv"
    print(f"\n[{mes:02d}/{ano}] Baixando dados do ONS... (Isso pode levar 1-2 minutos devido ao tamanho do CSV)")
    
    start_time = time.time()
    try:
        req = urllib.request.urlopen(url)
        csv_data = req.read().decode('utf-8')
    except Exception as e:
        print(f"[{mes:02d}/{ano}] Erro ao baixar ou arquivo inexistente: {e}")
        return
    print(f"[{mes:02d}/{ano}] Download concluido em {time.time() - start_time:.1f}s.")

    df = pd.read_csv(StringIO(csv_data), sep=';', usecols=[
        'id_estado', 'nom_usina', 'din_instante', 'val_geracaoestimada', 'val_geracaoverificada', 'flg_geracaorestrita'
    ])
    
    # Filtra apenas Bahia e restricoes ativas (geracao restrita)
    df = df[df['id_estado'] == 'BA']
    df = df[df['flg_geracaorestrita'] == 1]
    
    if df.empty:
        print(f"[{mes:02d}/{ano}] Nenhum corte registrado na Bahia para este mes.")
        return

    # Conectar ao banco MySQL
    engine = sqlalchemy.create_engine('mysql+mysqlconnector://root:admin123@localhost/curtavento')
    with engine.connect() as conn:
        res = conn.execute(sqlalchemy.text("SELECT id, nome FROM usina"))
        db_usinas = {row[0]: row[1] for row in res.fetchall()}

    # Criar um mapa de 'nome limpo' -> id_usina
    clean_to_id = {}
    for uid, nome in db_usinas.items():
        clean_to_id[clean_name(nome)] = uid

    # Funcao de mapeamento (exact clean ou contains)
    def mapear_usina(ons_name):
        c = clean_name(ons_name)
        if c in clean_to_id: return clean_to_id[c]
        # Tenta match parcial (ex: Assurua 5 1 -> Assurua 5 I)
        for db_c, uid in clean_to_id.items():
            if c in db_c or db_c in c:
                return uid
        return None

    # Cache do mapeamento para performance
    ons_usinas_unicas = df['nom_usina'].unique()
    ons_map = {n: mapear_usina(n) for n in ons_usinas_unicas}
    
    df['usina_id'] = df['nom_usina'].map(ons_map)
    
    mapped_count = df['usina_id'].notna().sum()
    total_count = len(df)
    
    df = df.dropna(subset=['usina_id'])
    if df.empty:
        print(f"[{mes:02d}/{ano}] Usinas com corte no ONS nao bateram com nenhuma do banco.")
        return

    print(f"[{mes:02d}/{ano}] Mapeamento de usinas aprovado: {mapped_count}/{total_count} registros processaveis ({mapped_count/total_count*100:.1f}%)")

    # Converter tempo
    df['din_instante'] = pd.to_datetime(df['din_instante'])
    df['data_dia'] = df['din_instante'].dt.date
    
    # Calculo do Corte (MWh) para os 30 min (MW * 0.5)
    # corte = Max(Estimada - Verificada, 0) -> Efeito Jensen
    df['corte_mw'] = (df['val_geracaoestimada'] - df['val_geracaoverificada']).clip(lower=0)
    df['corte_mwh'] = df['corte_mw'] * 0.5

    # Agregar por Usina e Dia
    agrupado = df[df['corte_mw'] > 0].groupby(['usina_id', 'data_dia']).agg(
        total_mwh=('corte_mwh', 'sum'),
        blocos_30min=('corte_mw', 'count')
    ).reset_index()

    if agrupado.empty:
        print(f"[{mes:02d}/{ano}] Sem cortes positivos reais neste mes.")
        return

    # DataFrame final para inserir no banco
    registros = pd.DataFrame({
        'usina_id': agrupado['usina_id'].astype(int),
        'data_corte': pd.to_datetime(agrupado['data_dia']),
        'duracao_minutos': agrupado['blocos_30min'] * 30,
        'energia_cortada_mwh': agrupado['total_mwh'],
        'motivo_ons': 'ONS_RESTRICAO_VERIFICADA'
    })

    # Evitar duplicidades deletando o mes antes de inserir
    mes_str = f"{ano}-{mes:02d}"
    with engine.begin() as conn:
        conn.execute(sqlalchemy.text(f"DELETE FROM historico_corte_ons WHERE DATE_FORMAT(data_corte, '%Y-%m') = '{mes_str}'"))
    
    # Inserir no MySQL
    registros.to_sql('historico_corte_ons', con=engine, if_exists='append', index=False)
    print(f"[{mes:02d}/{ano}] Sucesso! {len(registros)} registros (usina/dia) de corte salvos no banco.\n")

if __name__ == "__main__":
    ano = int(sys.argv[1]) if len(sys.argv) > 1 else 2023
    mes_inicio = int(sys.argv[2]) if len(sys.argv) > 2 else 1
    mes_fim = int(sys.argv[3]) if len(sys.argv) > 3 else 12
    
    print(f"==================================================")
    print(f" Iniciando Importacao ONS para {ano} (Meses {mes_inicio} a {mes_fim})")
    print(f"==================================================")
    for m in range(mes_inicio, mes_fim + 1):
        baixar_e_processar(ano, m)
