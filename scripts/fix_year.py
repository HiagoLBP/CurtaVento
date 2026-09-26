import os

index_file = r"c:\Users\Hiago\Desktop\curta-vento\src\main\resources\static\index.html"
with open(index_file, "r", encoding="utf-8") as f:
    html = f.read()

html = html.replace('value="2024">2024', 'value="2023">2023')

with open(index_file, "w", encoding="utf-8") as f:
    f.write(html)
print("Ano atualizado.")
