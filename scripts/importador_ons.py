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
    
    # Calcula corte (Estimada - Verificada) * 0.5 (pois é 30 min)
    df['corte_mw'] = (df['val_geracaoestimada'] - df['val_geracaoverificada']).clip(lower=0)
    df['corte_mwh'] = df['corte_mw'] * 0.5
    
    # Agrupa por Usina e Dia
    agrupado = df.groupby(['nom_conjuntousina', 'data_dia']).agg(
        total_mwh=('corte_mwh', 'sum'),
        blocos=('corte_mwh', 'count')
    ).reset_index()
    
    # Mapear para IDs reais do banco de dados (1=I, 2=II, 3=III)
    # ATENÇÃO: Aqui assumimos que o MySQL tem essas usinas nos IDs 1, 2 e 3
    def resolver_usina(nome):
        nome_upper = nome.upper()
        if "IGAPORÃ III" in nome_upper: return 3
        if "CAETITÉ 123" in nome_upper or "IGAPORÃ II" in nome_upper: return 2
        return 1
        
    agrupado['usina_id'] = agrupado['nom_conjuntousina'].apply(resolver_usina)
    
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
