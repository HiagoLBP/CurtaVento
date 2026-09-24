import pandas as pd
import urllib.request
import sqlalchemy
from io import StringIO
import sys
import argparse

def auditar(ano, mes, dia_str, termo_usina):
    print(f"==================================================")
    print(f"--- AUDITORIA DE EXATIDAO DE CORTES ONS ---")
    print(f"Data Alvo: {dia_str}")
    print(f"Termo de busca da Usina: '{termo_usina}'")
    print(f"==================================================")
    
    # 1. Puxar do Banco de Dados
    engine = sqlalchemy.create_engine('mysql+mysqlconnector://root:admin123@localhost/curtavento')
    query = f"""
        SELECT u.nome, h.energia_cortada_mwh 
        FROM historico_corte_ons h
        JOIN usina u ON h.usina_id = u.id
        WHERE DATE(h.data_corte) = '{dia_str}' AND UPPER(u.nome) LIKE '%%{termo_usina}%%'
    """
    try:
        with engine.connect() as conn:
            res = conn.execute(sqlalchemy.text(query)).fetchone()
        
        if res:
            db_nome = res[0]
            db_mwh = float(res[1])
            print(f"[BANCO DE DADOS] Usina '{db_nome}'")
            print(f"[BANCO DE DADOS] MWh Cortados: {db_mwh:.4f} MWh")
        else:
            db_nome = "Não encontrada"
            db_mwh = 0.0
            print(f"[BANCO DE DADOS] Nenhuma usina correspondente encontrada para esta data.")
    except Exception as e:
        print(f"Erro ao conectar no banco: {e}")
        return

    # 2. Puxar do ONS
    url = f"https://ons-aws-prod-opendata.s3.amazonaws.com/dataset/restricao_coff_eolica_detail_tm/RESTRICAO_COFF_EOLICA_DETAIL_{ano}_{mes:02d}.csv"
    print(f"\n[ONS OPEN DATA] Baixando arquivo bruto CSV direto da fonte ({mes:02d}/{ano})...")
    
    try:
        csv_data = urllib.request.urlopen(url).read().decode('utf-8')
        df = pd.read_csv(StringIO(csv_data), sep=';')
    except Exception as e:
        print(f"Erro ao baixar CSV do ONS: {e}")
        return
        
    df['din_instante'] = pd.to_datetime(df['din_instante'])
    
    # 3. Filtrar Exatamente como as Regras de Negócio exigem
    df_dia = df[(df['din_instante'].dt.date.astype(str) == dia_str) & 
                (df['id_estado'] == 'BA') & 
                (df['flg_geracaorestrita'] == 1)]
    
    # Encontrar todos os parques do ONS que correspondem ao termo
    df_usina_ons = df_dia[df_dia['nom_conjuntousina'].str.upper().str.contains(termo_usina)]
    parques_encontrados = df_usina_ons['nom_conjuntousina'].unique()
    
    if len(parques_encontrados) == 0:
        ons_mwh = 0.0
        print(f"[ONS OPEN DATA] Nenhum corte registrado na fonte para este termo na data.")
    else:
        print(f"[ONS OPEN DATA] Parques ONS agregados nesta conta: {', '.join(parques_encontrados)}")
        
        # O PULO DO GATO: Aplicando a matemática exata (Efeito Jensen do Submódulo 6.5)
        # Agrupa os instantes de meia em meia hora
        agregado = df_usina_ons.groupby('din_instante').agg(
            est=('val_geracaoestimada', 'sum'),
            ver=('val_geracaoverificada', 'sum')
        )
        
        # Max(0, Estimada - Verificada)
        agregado['corte_mw'] = (agregado['est'] - agregado['ver']).clip(lower=0)
        
        # Multiplica por 0.5 (meia hora) para virar MWh
        agregado['corte_mwh'] = agregado['corte_mw'] * 0.5
        
        ons_mwh = agregado['corte_mwh'].sum()
        print(f"[ONS OPEN DATA] Soma matemática (Efeito Jensen aplicado): {ons_mwh:.4f} MWh")

    print(f"\n==================================================")
    print(f"VEREDITO DE EXATIDAO")
    print(f"==================================================")
    print(f"Valor Banco: {db_mwh:.4f} MWh")
    print(f"Valor ONS:   {ons_mwh:.4f} MWh")
    
    diferenca = abs(db_mwh - ons_mwh)
    
    if diferenca < 0.01:
        print(f"\n[APROVADO]: Os dados estao 100% EXATOS e integros com a fonte primaria.")
    else:
        print(f"\n[REPROVADO]: Diferenca de {diferenca:.4f} MWh encontrada.")

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Auditar exatidão de dados ONS")
    parser.add_argument("--ano", type=int, default=2026, help="Ano do arquivo ONS")
    parser.add_argument("--mes", type=int, default=8, help="Mês do arquivo ONS")
    parser.add_argument("--dia", type=str, default="2026-08-15", help="Dia alvo no formato YYYY-MM-DD")
    parser.add_argument("--usina", type=str, default="CAETIT", help="Termo de busca em maiúsculo (ex: PINDA, CAETIT, IGAPORA II)")
    
    args = parser.parse_args()
    auditar(args.ano, args.mes, args.dia, args.usina)
