# CurtaVento — Sistema de Inteligência & Monitoramento de Curtailment Eólico

Plataforma analítica e operacional projetada para o monitoramento, previsão de risco, auditoria regulatória e avaliação de impactos econômicos decorrentes do ***curtailment*** (cortes de geração e restrições operacionais) em parques eólicos na Bahia.

---

## Visão Geral

O **CurtaVento** integra dados meteorológicos em tempo real com as bases públicas do **ONS (Operador Nacional do Sistema Elétrico)** e os dados de liquidação da **CCEE (PLD - Preço de Liquidação das Diferenças)**.

A plataforma permite que operadores, gestores de usinas e administrações municipais acompanhem perdas financeiras, elegibilidade a ressarcimento regulatório, previsibilidade de restrições na rede, viabilidade de armazenamento (BESS) e reflexos fiscais na arrecadação municipal (VAF - Valor Adicionado Fiscal).

---

## Principais Funcionalidades

* **Dashboard Executivo & Mapa GIS:** Indicadores consolidados de energia cortada, perda bruta, impacto VAF/ICMS, taxa de desperdício, filtro por subestação coletora e mapa geoespacial interativo (Leaflet) dos parques monitorados.
* **Auditoria Regulatória ANEEL (REN 1.030/2022 & NT 183/2026):** Classificação dos cortes por razão de restrição (`REL`, `CNF`, `ENE`), cálculo de ressarcimento líquido via ESS com franquia regulatória configurável (h/ano), simulador de impacto da NT 183/2026, ranking de usinas com busca e tabela de auditoria das últimas inserções.
* **Previsão de Risco 48h & Segurança O&M (NR-35):** Curva horária de risco de curtailment para as próximas 48 horas cruzando vento a 80m, rajadas e radiação solar (Open-Meteo), com classificação de janela de manutenção em altura (`LIBERADO`, `ALERTA`, `BLOQUEADO`).
* **Simulador de Armazenamento BESS:** Modelagem técnica e financeira para sistemas de baterias LFP (CAPEX, eficiência round-trip, arbitragem com PLD, receita anual recuperada, payback e ROI).
* **Impacto Fiscal Municipal (VAF) & Arrendamento Rural:** Projeção orçamentária plurianual (`t+1` e `t+2`) baseada no IPM municipal (LC 63/1990) e auditoria de perda de royalties fundiários para proprietários de terras.
* **Monitoramento Meteorológico:** Integração com a API **Open-Meteo** para acompanhamento contínuo de vento, rajadas, radiação solar e Rosa dos Ventos.
* **Sincronização & Backfill ONS:** Processamento dos CSVs oficiais do ONS (nível conjunto e detalhe por usina em blocos de 30 minutos e agregado diário), com execução via API administrativa ou linha de comando (CLI).
* **Relatório Executivo & Exportação:** Geração de relatório consolidado para impressão/PDF e exportação de dados em CSV.
* **Segurança & Controle de Acesso:** Autenticação stateless via **JWT**, com isolamento de permissões entre perfis (`ADMIN` e `VISUALIZADOR`).

---

## Arquitetura de Software

O projeto segue os princípios de **Clean Architecture (Arquitetura Hexagonal)**, separando regras de negócio, infraestrutura e adaptadores de entrada:

```
src/main/java/com/curtavento/curta_vento/
├── domain/                    # Entidades de domínio e portas (interfaces)
│   ├── model/
│   └── port/
├── application/               # Casos de uso e regras de negócio
│   └── usecase/
├── config/                    # Configurações da aplicação (Flyway, etc.)
├── infrastructure/            # Implementações externas, banco e integrações
│   ├── batch/                 # Jobs agendados (clima, risco, impacto municipal)
│   ├── client/                # Cliente HTTP Open-Meteo
│   ├── ons/                   # Processamento e backfill de datasets do ONS
│   ├── persistence/           # Entidades JPA, Migrations e Repositories
│   └── security/              # Filtros de autenticação e provedor JWT
└── interfaces/                # Adaptadores de entrada (REST API)
    └── http/
        ├── advice/            # Tratamento global de exceções
        ├── controller/        # Endpoints REST
        └── dto/               # Objetos de transferência de dados (Request/Response)
```

---

## Tecnologias Utilizadas

* **Linguagem:** Java 21 LTS
* **Framework:** Spring Boot 4.x
  * Spring Data JPA (com batching JDBC habilitado)
  * Spring Security (JWT)
  * Flyway Migration
  * Spring Retry & Scheduling
* **Banco de Dados:** MySQL 8.x
* **Frontend:** HTML5, CSS3, JavaScript (Vanilla), Chart.js e Leaflet.js
* **Integrações Externas:** API Open-Meteo & Repositório Open Data do ONS (AWS S3)
* **Deploy & Container:** Docker (Multi-stage build com imagem JRE Alpine)
* **Gerenciador de Dependências:** Maven Wrapper (`mvnw` / `mvnw.cmd`)

---

## Endpoints da API REST

| Módulo | Método | Rota | Descrição |
| :--- | :---: | :--- | :--- |
| **Auth** | `POST` | `/api/auth/login` | Autenticação e emissão de token JWT |
| **Usuários** | `POST` | `/api/usuarios` | Cadastro de novos usuários |
| **Dashboard** | `GET` | `/api/dashboard/resumo-geral` | Métricas globais, mapa, subestações e municípios |
| **Usinas** | `GET` / `POST` | `/api/usinas` | Listagem e cadastro de usinas eólicas |
| **Cortes ONS** | `GET` | `/api/cortes-ons/usina/{usinaId}` | Histórico de cortes de uma usina |
| **Cortes ONS** | `GET` | `/api/cortes-ons/ranking` | Ranking das usinas com maiores perdas |
| **Cortes ONS** | `GET` | `/api/cortes-ons/motivos` | Agrupamento por motivos de restrição (`REL`, `CNF`, `ENE`) |
| **Cortes ONS** | `GET` | `/api/cortes-ons/evolucao` | Evolução mensal dos cortes no ano |
| **Cortes ONS** | `GET` | `/api/cortes-ons/auditoria` | Últimas 100 inserções registradas |
| **Cortes ONS** | `GET` | `/api/cortes-ons/ressarcimento` | Apuração ANEEL REN 1.030/2022 e simulação NT 183/2026 |
| **Clima** | `GET` | `/api/clima/usinas/{id}/historico` | Dados meteorológicos históricos da usina |
| **Clima** | `GET` | `/api/clima/previsao-rajadas` | Previsão de rajadas de vento |
| **Clima** | `GET` | `/api/clima/rosa-ventos` | Distribuição direcional e intensidade do vento |
| **Previsões** | `GET` | `/api/previsoes/usina/{usinaId}` | Previsão calculada de risco de corte |
| **Previsões** | `GET` | `/api/previsoes/curva-48h/{usinaId}` | Curva horária de risco 48h e janela NR-35 |
| **Simulador** | `GET` | `/api/simulador/bess` | Simulação financeira de armazenamento BESS (LFP) |
| **Impacto VAF** | `GET` | `/api/impacto-vaf` | Relatório de impacto financeiro municipal |
| **Impacto VAF** | `GET` | `/api/impacto-vaf/projecao-plurianual` | Projeção fiscal `t+1` e `t+2` por município |
| **Impacto VAF** | `GET` | `/api/impacto-vaf/arrendamento-rural` | Auditoria de perda de royalties fundiários |
| **Alertas** | `GET` | `/api/alertas` | Alertas gerados para a conta autenticada |
| **Alertas** | `PUT` | `/api/alertas/{id}/marcar-lido` | Confirmação de leitura do alerta |
| **Backfill** | `POST` | `/api/admin/backfill-ons` | Disparo de carga histórica mensal do ONS (Admin) |
| **Backfill** | `POST` | `/api/admin/backfill-ons/intervalo` | Disparo de carga histórica por intervalo de meses (Admin) |

---

## Como Executar o Projeto

### Pré-requisitos
* Java 21 JDK instalado
* MySQL 8 instalado e em execução (ou container Docker)
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

As migrações do schema e tabelas iniciais são executadas automaticamente pelo **Flyway** no startup da aplicação.

### 3. Variáveis de Ambiente (Opcional)
Você pode sobrescrever as credenciais padrão definindo as variáveis:
* `SPRING_DATASOURCE_URL` (padrão: `jdbc:mysql://localhost:3306/curtavento?serverTimezone=UTC&rewriteBatchedStatements=true`)
* `SPRING_DATASOURCE_USERNAME` (padrão: `root`)
* `SPRING_DATASOURCE_PASSWORD` (padrão: `admin123`)
* `JWT_SECRET` (chave de segurança do token)
* `PORT` (porta da aplicação, padrão `8080`)

### 4. Executar com Maven
```bash
# Windows
.\mvnw.cmd spring-boot:run

# Linux / macOS
./mvnw spring-boot:run
```

A aplicação estará disponível em: `http://localhost:8080`.

### 5. Executar Backfill Histórico via CLI (Opcional)
Para importar um intervalo de meses do ONS diretamente pela linha de comando sem subir o servidor web:
```bash
# Windows
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.arguments=--backfill=2026-01:2026-11 --spring.main.web-application-type=none"

# Linux / macOS
./mvnw spring-boot:run -Dspring-boot.run.arguments="--backfill=2026-01:2026-11 --spring.main.web-application-type=none"
```

### 6. Executar Testes
```bash
# Windows
.\mvnw.cmd test

# Linux / macOS
./mvnw test
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

Desenvolvido por **[Hiago L. B. P.](https://github.com/HiagoLBP)**.
