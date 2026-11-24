# CurtaVento — Sistema de Inteligência & Monitoramento de Curtailment Eólico

Plataforma analítica e operacional projetada para o monitoramento, previsão de risco via Machine Learning, auditoria regulatória e avaliação de impactos socioeconômicos e fiscais decorrentes do *curtailment* (cortes de geração e restrições operacionais) em parques eólicos na Bahia, com foco na microrregião do Alto Sertão Produtivo (polo de Guanambi).

---

## Visão Geral

O **CurtaVento** integra dados meteorológicos em tempo real e de reanálise atmosférica com as bases públicas do **ONS (Operador Nacional do Sistema Elétrico)** e os dados de liquidação da **CCEE (PLD - Preço de Liquidação das Diferenças)**.

A plataforma permite que operadores de parques eólicos, comercializadoras de energia e administrações municipais avaliem perdas financeiras, elegibilidade a ressarcimento regulatório, previsibilidade de restrições na rede com redes neurais artificiais, viabilidade de armazenamento em baterias (BESS) e reflexos fiscais na arrecadação municipal do Valor Adicionado Fiscal (VAF) e ICMS.

---

## Fundamentação Científica & Pré-TCC

O sistema foi concebido e expandido a partir da pesquisa acadêmica de Pré-TCC:

* **Título:** *CURTAVENTO: PLATAFORMA DE PREVISÃO DE RISCO DE CURTAILMENT EÓLICO E SEU IMPACTO SOCIOECONÔMICO MUNICIPAL NO ALTO SERTÃO (GUANAMBI/BA)*
* **Autor:** Hiago Rocha Souza
* **Instituição:** Instituto Federal de Educação, Ciência e Tecnologia Baiano (IF Baiano) — Campus Guanambi
* **Curso:** Análise e Desenvolvimento de Sistemas (ADS)

### Modelagem Fiscal e Regulatória Validada

1. **Impacto no Valor Adicionado Fiscal (Equação 5.1):**
   $$\Delta \text{VAF}_m = \Delta E_{\text{cortada},m} \times P_{\text{ref}}$$
   Onde a perda de VAF municipal ($\Delta \text{VAF}_m$) resulta do produto da energia cortada pela usina no município pelo preço de referência da energia ($P_{\text{ref}}$ em R$/MWh).

2. **Perda Arrecadatória Municipal de ICMS (Equação 5.2):**
   $$\Delta \text{ICMS}_m = \left( \frac{\Delta \text{VAF}_m}{\text{VAF}_{\text{total, BA}}} \right) \times \left( \text{ICMS}_{\text{total, BA}} \times 0{,}25 \times 0{,}65 \right)$$
   Onde 25% da arrecadação estadual de ICMS é constitucionalmente repassada aos municípios (CF/88 e LC nº 63/1990) e a Lei Complementar Estadual da Bahia nº 53/2022 fixa a cota do VAF em 65% do Índice de Participação dos Municípios (IPM).

3. **Defasagem Bienal na Arrecadação:**
   De acordo com a LC nº 63/1990, o VAF gerado no ano $t$ compõe o índice de rateio com reflexos orçamentários distribuídos nos exercícios $t+2$ (50%) e $t+3$ (50%).

4. **Marco Regulatório ANEEL & ONS:**
   * **REN ANEEL nº 1.030/2022:** Apuração de restrição por confiabilidade elétrica (`REL`), constrained-off (`CNF`) e razões energéticas (`ENE`).
   * **Nota Técnica ONS/ANEEL nº 183/2026:** Redução estimada de até 17% nos ressarcimentos elegíveis sob novo critério de corte de sub-rogação.
   * **Resolução Normativa ANEEL nº 1.170/2026:** Compartilhamento gradual do corte de energia (ENE) com o encargo de Eventos de Transição (EVT) das hidrelétricas, parametrizado na plataforma em patamares de 0%, 25%, 50%, 75% e 100%.

---

## Principais Funcionalidades

* **Dashboard Executivo & Mapa GIS:** Indicadores consolidados de energia cortada, perda bruta, impacto VAF/ICMS, taxa de desperdício, filtro por subestação coletora e mapa geoespacial interativo (Leaflet) dos complexos eólicos da Bahia.
* **Previsão de Risco com Machine Learning (6h e 12h):** Microsserviço dedicado em Python/PyTorch com codificação temporal treinável `Time2Vec` aplicada a três arquiteturas neurais (`MLP + Time2Vec`, `LSTM + Time2Vec`, `DLinear + Time2Vec`), retornando probabilidade horária de corte e estimativa de MWh cortado.
* **Benchmark de Modelos de ML:** Comparativo experimental automatizado avaliando nMAE, nRMSE, correlação de Pearson e acurácia de classificação binária sobre os dados históricos reais do ONS.
* **Reanálise Climática NASA POWER & Open-Meteo:** Ingestão de séries meteorológicas horárias históricas e previsão climática, com cisalhamento vertical de vento a 80m via Power Law Wind Shear ($v(z) = v(z_0) (z / z_0)^\alpha$).
* **Segurança O&M em Altura (NR-35):** Avaliação de janelas seguras de trabalho em torres eólicas cruzando velocidade de vento a 80m e rajadas (`LIBERADO`, `ALERTA`, `BLOQUEADO`).
* **Auditoria Regulatória ANEEL (REN 1.030/2022, NT 183/2026 & REN 1.170/2026):** Classificação de cortes por `REL`, `CNF` e `ENE`, simulador de descontos da NT 183/2026 e compartilhamento gradual de perdas hidrelétricas da REN 1.170/2026.
* **Simulador de Armazenamento BESS:** Dimensionamento técnico-econômico de sistemas de baterias LFP para mitigação de curtailment e arbitragem com PLD horário.
* **Impacto Fiscal Municipal (VAF) & Arrendamento Rural:** Projeção plurianual nos exercícios $t+1$, $t+2$ e $t+3$ dos repasses de ICMS aos municípios produtores e auditoria de royalties fundiários para proprietários rurais (1,0% a 2,5%).
* **Sincronização & Backfill ONS:** Processamento em lote de datasets oficiais do ONS (arquivos CSV com granularidade semi-horária e diária).

---

## Arquitetura de Software

A plataforma é composta por um backend monolítico modular em **Spring Boot (Java 21)** seguindo **Clean Architecture / Arquitetura Hexagonal**, integrado a um microsserviço assíncrono de inteligência artificial em **FastAPI (Python 3.11)**:

```
curta-vento/
├── src/main/java/com/curtavento/curta_vento/
│   ├── domain/                    # Entidades de domínio, enums e portas (interfaces)
│   ├── application/               # Casos de uso e orquestração de regras de negócio
│   ├── config/                    # Configurações de segurança, Flyway e MVC
│   ├── infrastructure/            # Adaptadores de saída, persistência e integrações
│   │   ├── batch/                 # Tarefas agendadas e automações
│   │   ├── client/                # Clientes HTTP (Open-Meteo, NASA POWER, MlPrevisaoClient)
│   │   ├── ons/                   # Parsers e ingestão de dados abertos do ONS
│   │   ├── persistence/           # Entidades JPA, Repositories e Migrações
│   │   └── security/              # Autenticação stateless JWT e filtros
│   └── interfaces/                # Adaptadores de entrada (REST Controllers e DTOs)
│
├── ml-service/                    # Microsserviço de Machine Learning (Python/PyTorch)
│   ├── app/
│   │   ├── features/              # Camada de embedding temporal Time2Vec
│   │   ├── models/                # Arquiteturas neurais (MLP, LSTM, DLinear)
│   │   ├── evaluation/            # Métricas científicas (nMAE, nRMSE, Pearson)
│   │   ├── training/              # Pipeline de treinamento e ingestão do banco
│   │   └── main.py                # Endpoints FastAPI de inferência e benchmark
│   ├── artifacts/                 # Relatórios consolidados de benchmark e métricas
│   └── tests/                     # Testes unitários com pytest
│
└── src/main/resources/static/     # Frontend SPA Vanilla JS, Chart.js e Leaflet
```

---

## Tecnologias Utilizadas

### Backend Principal (Java)
* **Linguagem:** Java 21 LTS
* **Framework:** Spring Boot 4.x
* **Persistência:** Spring Data JPA com batching JDBC e Hibernate
* **Migrações:** Flyway
* **Segurança:** Spring Security com autenticação JWT stateless
* **Banco de Dados:** MySQL 8.x

### Microsserviço de Inteligência Artificial (Python)
* **Linguagem:** Python 3.11+
* **Framework Web:** FastAPI e Uvicorn
* **Deep Learning:** PyTorch (CPU)
* **Processamento de Dados:** Pandas, NumPy e Scikit-Learn
* **Testes:** Pytest

### Frontend & Integrações
* **Frontend:** HTML5 semântico, CSS3 responsivo, JavaScript ES6+, Chart.js e Leaflet.js
* **Fontes de Dados:** ONS Open Data, CCEE (PLD), API Open-Meteo e API NASA POWER

---

## Modelos de Machine Learning & Métricas

O microsserviço de ML implementa embeddings temporais treináveis baseados no artigo de Kazemi et al. (2019) (*Time2Vec: Learning a Vector Representation of Time*), decompondo sinais periódicos e lineares do ciclo diário e estacional do vento.

### Arquiteturas Avaliadas
1. **MLP + Time2Vec:** Rede densa feedforward com representação temporal contínua e ativação GELU.
2. **LSTM + Time2Vec:** Rede recorrente bidirecional com células LSTM para captura de dependências de longo prazo na série temporal.
3. **DLinear + Time2Vec:** Decomposição em média móvel (tendência + componente sazonal) com projeção linear direta de baixa complexidade computacional (Zeng et al., 2023).

### Métricas de Validação (Horizontes de 6h e 12h)
* **nMAE (Normalized Mean Absolute Error):** Erro médio absoluto normalizado pela amplitude da série.
* **nRMSE (Normalized Root Mean Squared Error):** Raiz do erro quadrático médio normalizada, penalizando grandes desvios.
* **Correlação de Pearson ($r$):** Coeficiente de correlação linear entre a curva real e a curva predita.
* **Acurácia de Risco:** Exatidão na detecção binária de eventos de corte iminentes.

---

## Endpoints da API REST

### Backend Spring Boot (`http://localhost:8080`)

| Módulo | Método | Rota | Descrição |
| :--- | :---: | :--- | :--- |
| **Auth** | `POST` | `/api/auth/login` | Autenticação e emissão de token JWT |
| **Usuários** | `POST` | `/api/usuarios` | Cadastro de novos usuários |
| **Dashboard** | `GET` | `/api/dashboard/resumo-geral` | Métricas globais consolidadas, mapa e filtros |
| **Usinas** | `GET` / `POST` | `/api/usinas` | Listagem e cadastro de usinas eólicas |
| **Cortes ONS** | `GET` | `/api/cortes-ons/usina/{usinaId}` | Histórico detalhado de cortes da usina |
| **Cortes ONS** | `GET` | `/api/cortes-ons/ranking` | Ranking das usinas com maiores perdas de geração |
| **Cortes ONS** | `GET` | `/api/cortes-ons/motivos` | Agrupamento por motivos de restrição (`REL`, `CNF`, `ENE`) |
| **Cortes ONS** | `GET` | `/api/cortes-ons/evolucao` | Série temporal mensal de cortes no ano |
| **Cortes ONS** | `GET` | `/api/cortes-ons/auditoria` | Últimas 100 inserções registradas para auditoria |
| **Cortes ONS** | `GET` | `/api/cortes-ons/ressarcimento` | Apuração ANEEL REN 1.030/2022, NT 183/2026 e REN 1.170/2026 |
| **Clima** | `GET` | `/api/clima/usinas/{id}/historico` | Dados meteorológicos históricos da usina |
| **Clima** | `GET` | `/api/clima/usinas/{id}/reanalise-nasa` | Série horária de reanálise atmosférica da NASA POWER |
| **Clima** | `GET` | `/api/clima/previsao-rajadas` | Previsão de rajadas e direção do vento |
| **Clima** | `GET` | `/api/clima/rosa-ventos` | Distribuição direcional e intensidade média do vento |
| **Previsões** | `GET` | `/api/previsoes/usina/{usinaId}` | Previsão horária de risco de curtailment |
| **Previsões** | `GET` | `/api/previsoes/curva-48h/{usinaId}` | Curva 48h com inferência de ML e janela NR-35 |
| **Previsões** | `GET` | `/api/previsoes/benchmark-ml` | Relatório consolidado de métricas dos modelos Time2Vec |
| **Simulador** | `GET` | `/api/simulador/bess` | Estudo de viabilidade técnico-financeira de baterias BESS |
| **Impacto VAF** | `GET` | `/api/impacto-vaf` | Relatório de impacto municipal (Equações 5.1 e 5.2) |
| **Impacto VAF** | `GET` | `/api/impacto-vaf/projecao-plurianual` | Projeção bienal $t+1$, $t+2$ e $t+3$ por município |
| **Impacto VAF** | `GET` | `/api/impacto-vaf/arrendamento-rural` | Perda de receitas de royalties fundiários rurais |
| **Alertas** | `GET` | `/api/alertas` | Listagem de alertas operacionais da conta |
| **Alertas** | `PUT` | `/api/alertas/{id}/marcar-lido` | Confirmação de leitura de alertas |
| **Backfill** | `POST` | `/api/admin/backfill-ons` | Disparo de carga mensal histórica do ONS (Admin) |
| **Backfill** | `POST` | `/api/admin/backfill-ons/intervalo` | Disparo de carga por intervalo de meses (Admin) |

### Microsserviço de Machine Learning (`http://localhost:8001`)

| Método | Rota | Descrição |
| :---: | :--- | :--- |
| `GET` | `/health` | Verificação de status e saúde do serviço de ML |
| `GET` | `/models/benchmark` | Métricas comparativas (nMAE, nRMSE, Pearson) de MLP, LSTM e DLinear |
| `POST` | `/models/train` | Disparo do pipeline de retreinamento nos dados históricos do MySQL |
| `POST` | `/predict/curtailment` | Inferência horária de risco de corte e MWh previsto para 6h e 12h |

---

## Como Executar o Projeto

### Pré-requisitos
* Java 21 JDK instalado
* Python 3.11 ou superior instalado
* MySQL 8 instalado e em execução
* Git

### 1. Clonar o Repositório
```bash
git clone https://github.com/HiagoLBP/CurtaVento.git
cd CurtaVento
```

### 2. Configurar o Banco de Dados
Certifique-se de que o MySQL possui a base de dados criada:
```sql
CREATE DATABASE curtavento CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

As migrações do schema e tabelas iniciais são executadas automaticamente pelo **Flyway** na inicialização do backend Java.

### 3. Executar o Microsserviço de Machine Learning (Python)
Abra um terminal na pasta `ml-service/`:
```bash
# Acessar diretório
cd ml-service

# Criar ambiente virtual
python -m venv .venv

# Ativar ambiente virtual
# Windows:
.\.venv\Scripts\activate
# Linux/macOS:
source .venv/bin/activate

# Instalar dependências
pip install -r requirements.txt

# Executar os testes automatizados
pytest -v

# Iniciar o servidor FastAPI na porta 8001
uvicorn app.main:app --host 0.0.0.0 --port 8001 --reload
```

O microsserviço estará ativo em: `http://localhost:8001`. A documentação OpenAPI interativa estará disponível em `http://localhost:8001/docs`.

### 4. Executar o Backend Principal (Java/Spring Boot)
Em outro terminal, a partir da raiz do projeto:
```bash
# Windows
.\mvnw.cmd spring-boot:run

# Linux / macOS
./mvnw spring-boot:run
```

A aplicação web e a API estarão disponíveis em: `http://localhost:8080`.

### 5. Executar os Testes Unitários e de Integração do Backend Java
```bash
# Windows
.\mvnw.cmd test

# Linux / macOS
./mvnw test
```

### 6. Executar Backfill Histórico via CLI (Opcional)
Para importar dados do ONS diretamente pela linha de comando sem abrir o servidor web:
```bash
# Windows
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.arguments=--backfill=2026-01:2026-11 --spring.main.web-application-type=none"

# Linux / macOS
./mvnw spring-boot:run -Dspring-boot.run.arguments="--backfill=2026-01:2026-11 --spring.main.web-application-type=none"
```

### 7. Executar com Docker
```bash
# Construir a imagem otimizada
docker build -t curtavento-api .

# Executar o container
docker run -p 8080:8080 --name curtavento curtavento-api
```

---

## Autor

Desenvolvido por **[Hiago L. B. P.](https://github.com/HiagoLBP)** — Instituto Federal Baiano (Campus Guanambi).

---

## Licença & Propriedade Intelectual

Copyright (c) 2026 Hiago Rocha Souza. Todos os direitos reservados.

Este projeto é protegido pela legislação brasileira de direitos autorais e propriedade intelectual de programa de computador (**Lei Federal nº 9.609/1998** e **Lei Federal nº 9.610/1998**).

O código-fonte é disponibilizado publicamente em modalidade **Source-Available para fins estritamente acadêmicos, de avaliação curricular e pesquisa científica**. São expressamente proibidas a reprodução não autorizada, plágio, redistribuição ou qualquer forma de exploração comercial sem autorização prévia por escrito do autor. Consulte o arquivo [LICENSE](file:///c:/Users/Hiago/Desktop/curta-vento/LICENSE) para os termos e condições integrais.
