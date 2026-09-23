import pandas as pd
import urllib.request
import sqlalchemy
import time
import sys
from io import StringIO

def baixar_e_processar(ano, mes):
    url = f"https://ons-aws-prod-opendata.s3.amazonaws.com/dataset/restricao_coff_eolica_detail_tm/RESTRICAO_COFF_EOLICA_DETAIL_{ano}_{mes:02d}.csv"
    print(f"[{mes:02d}/{ano}] Iniciando download... ({url})")
    
    start_time = time.time()
    try:
        response = urllib.request.urlopen(url)
        csv_data = response.read().decode('utf-8')
    except Exception as e:
        print(f"[{mes:02d}/{ano}] Erro ou arquivo inexistente: {e}")
        return
    print(f"[{mes:02d}/{ano}] Download concluído em {time.time() - start_time:.2f} segundos. Processando...")

    # Usa o Pandas (feito em C) para ler o CSV super rápido
    df = pd.read_csv(StringIO(csv_data), sep=';')
    
    # Filtra apenas Bahia e Usinas que nos importam
    df = df[df['id_estado'] == 'BA']
    
    conjuntos_alvo = [
        "Conj. Caetité", "Conj. Caetité A", "Conj. Caetité Norte",
        "Conj. Caetité 123", "Conj. Igaporã II", "Conj. Igaporã III 500 kV"
    ]
    df = df[df['nom_conjuntousina'].isin(conjuntos_alvo)]
    df = df[df['flg_geracaorestrita'] == 1]
    
    if df.empty:
        print(f"[{mes:02d}/{ano}] Nenhum dado de corte encontrado para nossas usinas.")
        return
        
    # Converte coluna de tempo
    df['din_instante'] = pd.to_datetime(df['din_instante'])
    df['data_dia'] = df['din_instante'].dt.date
    
    # CORREÇÃO EFEITO JENSEN (Baseado no RTI Brik Engenharia / ONS Submódulo 6.5-PR):
    # Passo 1: Agrupar por CONJUNTO e INSTANTE (30 min) e somar a energia das usinas
    agregado_hora = df.groupby(['nom_conjuntousina', 'din_instante', 'data_dia']).agg(
        soma_estimada=('val_geracaoestimada', 'sum'),
        soma_verificada=('val_geracaoverificada', 'sum')
    ).reset_index()
    
    # Passo 2: Calcular o corte aplicando a não-negatividade (max 0) no AGREGADO do conjunto
    # curtailment = máx( Σ val_geracaoreferencia − Σ val_geracao ; 0 )
    agregado_hora['corte_mw'] = (agregado_hora['soma_estimada'] - agregado_hora['soma_verificada']).clip(lower=0)
    agregado_hora['corte_mwh'] = agregado_hora['corte_mw'] * 0.5
    
    # Passo 3: Mapear nomes para IDs reais consultando o banco de dados
    # Conectamos ao MySQL e pegamos o dicionário de nome -> id
    engine = sqlalchemy.create_engine('mysql+mysqlconnector://root:admin123@localhost/curtavento')
    
    with engine.connect() as conn:
        result = conn.execute(sqlalchemy.text("SELECT id, nome FROM usina"))
        mapa_usinas = {row[1].upper(): row[0] for row in result.fetchall()}
        
    def resolver_usina_db(nome):
        nome_upper = nome.upper()
        # Se for nome exato
        if nome_upper in mapa_usinas:
            return mapa_usinas[nome_upper]
        # Se for string parecida (fallback do ONS)
        for nome_db, usina_id in mapa_usinas.items():
            # Ex: nome_db = "CONJ. CAETITE" e nome = "CONJ. CAETITÉ A"
            if nome_db.replace("CONJ. ", "") in nome_upper.replace("É", "E").replace("Ã", "A"):
                return usina_id
        return None
        
    agregado_hora['usina_id'] = agregado_hora['nom_conjuntousina'].apply(resolver_usina_db)
    agregado_hora = agregado_hora.dropna(subset=['usina_id']) # Remove o que não mapeou

    # Passo 4: Agrupa por usina_id e Dia para salvar no banco
    agrupado = agregado_hora.groupby(['usina_id', 'data_dia']).agg(
        total_mwh=('corte_mwh', 'sum'),
        blocos=('corte_mwh', 'count')
    ).reset_index()
    
    # Montar DataFrame final para o MySQL
    registros = pd.DataFrame({
        'usina_id': agrupado['usina_id'],
        'data_corte': pd.to_datetime(agrupado['data_dia']) + pd.Timedelta(hours=12),
        'duracao_minutos': agrupado['blocos'] * 30,
        'energia_cortada_mwh': agrupado['total_mwh'],
        'motivo_ons': 'ONS_DETALHAMENTO_USINA_SEM_MOTIVO'
    })
    
    # Salvar direto no banco MySQL
    engine = sqlalchemy.create_engine('mysql+mysqlconnector://root:admin123@localhost/curtavento')
    registros.to_sql('historico_corte_ons', con=engine, if_exists='append', index=False)
    
    print(f"[{mes:02d}/{ano}] Sucesso! {len(registros)} registros agregados inseridos no banco.")

if __name__ == "__main__":
    ano = int(sys.argv[1]) if len(sys.argv) > 1 else 2026
    mes_inicio = int(sys.argv[2]) if len(sys.argv) > 2 else 1
    mes_fim = int(sys.argv[3]) if len(sys.argv) > 3 else 12
    
    print(f"Iniciando Importação ONS com Python/Pandas para {ano} (Meses {mes_inicio} a {mes_fim})")
    for m in range(mes_inicio, mes_fim + 1):
        baixar_e_processar(ano, m)
