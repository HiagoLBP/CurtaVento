"""
Gera a migration V11 com as colunas CORRETAS da tabela municipio e usina.
"""
import json
import unicodedata

IBGE_CODES = {
    'araci': '2902005',
    'biritinga': '2903904',
    'boninal': '2904050',
    'bonito': '2904308',
    'brotas de macaubas': '2904506',
    'brumado': '2904605',
    'caetite': '2905206',
    'cafarnaum': '2905305',
    'campo formoso': '2906006',
    'canudos': '2906824',
    'casa nova': '2907202',
    'gentio do ouro': '2911303',
    'guanambi': '2911709',
    'ibipeba': '2912400',
    'ibitiara': '2912806',
    'igapora': '2913200',
    'iraquara': '2914000',
    'itaguacu da bahia': '2915353',
    'jacaraci': '2917300',
    'licinio de almeida': '2919405',
    'morro do chapeu': '2921708',
    'mulungu do morro': '2922250',
    'novo horizonte': '2923035',
    'ourolandia': '2923357',
    'pindai': '2924306',
    'riacho de santana': '2926400',
    'sento se': '2930204',
    'sobradinho': '2930774',
    'souto soares': '2930808',
    'tanque novo': '2931053',
    'tucano': '2931905',
    'uibai': '2932507',
    'umburanas': '2932457',
    'urandi': '2932606',
    'varzea nova': '2933158',
    'xique': '2933604'
}

def normalize(name):
    nfkd = unicodedata.normalize('NFKD', name)
    return ''.join(c for c in nfkd if not unicodedata.combining(c)).lower()

def generate():
    with open('usinas_bahia.json', 'r', encoding='utf-8') as f:
        usinas = json.load(f)

    municipios_set = sorted(list(set(u['municipio'] for u in usinas)))

    lines = []
    lines.append("-- Migration V11: Inserir municipios e usinas")
    lines.append("")

    lines.append("-- 1. Inserir municipios faltantes")
    
    missing_counter = 1
    for mun in municipios_set:
        mun_esc = mun.replace("'", "''")
        mun_norm = normalize(mun)

        code = IBGE_CODES.get(mun_norm)
        if not code:
            code = f"BA{missing_counter:05d}"
            missing_counter += 1
            print(f"AVISO: Codigo IBGE nao encontrado para '{mun}', usando: {code}")

        lines.append(f"INSERT INTO municipio (nome, codigo_ibge)")
        lines.append(f"SELECT '{mun_esc}', '{code}'")
        lines.append(f"FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM municipio WHERE nome = '{mun_esc}');")
        lines.append("")

    lines.append("-- 2. Inserir usinas eolicas")
    for u in usinas:
        nome_esc = u['nome'].replace("'", "''")
        mun_esc = u['municipio'].replace("'", "''")
        op_esc = u['operador'].replace("'", "''")
        lat = str(u['latitude']) if u['latitude'] else 'NULL'
        lng = str(u['longitude']) if u['longitude'] else 'NULL'
        cap = u['capacidade_instalada_mw']

        lines.append(f"INSERT INTO usina (nome, operador, municipio_id, capacidade_instalada_mw, latitude, longitude)")
        lines.append(f"SELECT '{nome_esc}', '{op_esc}', id, {cap}, {lat}, {lng}")
        lines.append(f"FROM municipio WHERE nome = '{mun_esc}' LIMIT 1;")
        lines.append("")

    output = 'src/main/resources/db/migration/V11__Inserir_Usinas_Bahia_ANEEL.sql'
    with open(output, 'w', encoding='utf-8') as f:
        f.write('\n'.join(lines))

    print(f"V11 gerada com sucesso: {len(municipios_set)} municipios + {len(usinas)} usinas")

if __name__ == '__main__':
    generate()
