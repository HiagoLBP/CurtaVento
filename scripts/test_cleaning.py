import unicodedata
import re

def clean_name(name):
    nfkd = unicodedata.normalize('NFKD', str(name))
    text = ''.join(c for c in nfkd if not unicodedata.combining(c)).upper()
    # Remove prefixos
    text = re.sub(r'^(VENTOS DE |SERRA DO |SERRA DA |PARQUE EOLICO |EOLICA |CENTRAL |CONJ\. |CONJUNTO )', '', text)
    # Tira a palavra SAO / SANTA para focar no nome util
    text = text.replace('SAO ', '').replace('SANTA ', '').replace('SANTO ', '')
    
    # Arrumar algarismos romanos no fim
    romans = {'I': '1', 'II': '2', 'III': '3', 'IV': '4', 'V': '5', 'VI': '6', 'VII': '7', 'VIII': '8', 'IX': '9', 'X': '10'}
    parts = text.split()
    if parts and parts[-1] in romans:
        parts[-1] = romans[parts[-1]]
        
    # Tirar zeros a esquerda de numeros no final
    if parts and parts[-1].isdigit():
        parts[-1] = str(int(parts[-1]))
        
    return " ".join(parts).strip()

print(clean_name("Ventos de São Vítor 09"))
print(clean_name("VENTOS DE SAO VITOR 9"))
print(clean_name("TUCANO III"))
print(clean_name("Tucano 3"))
print(clean_name("Assuruá 5 I"))
