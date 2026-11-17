# 🍃 CurtaVento — Sistema de Inteligência & Monitoramento de Curtailment Eólico

Plataforma analítica e operacional projetada para o monitoramento, previsão de risco e avaliação de impactos econômicos decorrentes do ***curtailment*** (cortes de geração e restrições operacionais) em parques eólicos no Brasil.

---

## 📌 Visão Geral

O **CurtaVento** integra dados meteorológicos de alta resolução em tempo real com as bases públicas do **ONS (Operador Nacional do Sistema Elétrico)** e os dados de liquidação da **CCEE (PLD - Preço de Liquidação das Diferenças)**.

A plataforma permite que operadores, gestores de usinas e administrações municipais tenham clareza sobre perdas financeiras, previsibilidade de interrupções na rede e reflexos fiscais diretos na arrecadação municipal (VAF - Valor Adicionado Fiscal).

---

## ✨ Principais Funcionalidades

* 📊 **Dashboard Executivo:** Indicadores consolidados de geração estimada, geração verificada e taxas históricas de restrição.
* ⚡ **Sincronização & Backfill ONS:** Mecanismo de carga histórica e processamento de arquivos CSV oficiais do ONS, categorizando os motivos operacionais de cada restrição.
* 🌦️ **Monitoramento Meteorológico:** Conexão direta com a API **Open-Meteo** para monitoramento contínuo de velocidade do vento a 80m, direção, previsão de rajadas e projeção da Rosa dos Ventos.
* 🔮 **Previsão de Risco:** Algoritmos para estimativa de risco de corte por usina, cruzando dados climáticos com limites de despacho da rede.
* 💰 **Impacto Fiscal Municipal (VAF):** Cálculo transparente das perdas de arrecadação do Valor Adicionado Fiscal nos municípios onde as usinas estão instaladas.
* 🔔 **Sistema de Alertas:** Notificações automáticas para operadores sobre condições meteorológicas críticas e desvios de geração.
* 🔒 **Segurança & Controle de Acesso:** Autenticação stateless via **JWT**, com isolamento de permissões entre operadores, gestores e administradores.

---

## 🏗️ Arquitetura de Software

O projeto foi construído seguindo os princípios de **Clean Architecture (Arquitetura Hexagonal)**, garantindo baixo acoplamento entre regras de negócio e tecnologias externas:

```
src/main/java/com/curtavento/curta_vento/
├── domain/                    # Entidades puras e portas (interfaces)
│   ├── model/
│   └── port/
├── application/               # Casos de uso e lógica de negócio
│   └── usecase/
├── infrastructure/            # Implementações externas, banco e integrações
│   ├── batch/                 # Jobs e rotinas agendadas
│   ├── client/                # Cliente HTTP Open-Meteo
│   ├── ons/                   # Processamento e parsing de dados do ONS
│   ├── persistence/           # Entidades JPA, Migrations e Repositories
│   └── security/              # Filtros de autenticação e provedor JWT
└── interfaces/                # Adaptadores de entrada (REST API)
    └── http/
        ├── advice/            # Tratamento global de exceções
        ├── controller/        # Endpoints REST
        └── dto/               # Objetos de transferência de dados (Request/Response)
```

---

## 🛠️ Tecnologias Utilizadas

* **Linguagem:** Java 21 LTS
* **Framework:** Spring Boot 4.x
  * Spring Data JPA
  * Spring Security (JWT)
  * Flyway Migration
  * Validation
* **Banco de Dados:** MySQL 8.x
* **Integrações Externas:** API Open-Meteo & Repositório de Dados ONS
* **Deploy & Container:** Docker (Multi-stage build com imagem JRE Alpine otimizada para baixo consumo de memória)
* **Gerenciador de Dependências:** Maven

---

## 🌐 Endpoints da API REST

| Módulo | Método | Rota | Descrição |
| :--- | :---: | :--- | :--- |
| **Auth** | `POST` | `/api/auth/login` | Autenticação e emissão de token JWT |
| **Dashboard** | `GET` | `/api/dashboard/resumo-geral` | Métricas e KPIs globais |
| **Usinas** | `GET` / `POST` | `/api/usinas` | Listagem e cadastro de usinas eólicas |
| **Cortes ONS** | `GET` | `/api/cortes-ons/usina/{usinaId}` | Histórico de cortes de uma usina |
| **Cortes ONS** | `GET` | `/api/cortes-ons/ranking` | Ranking das usinas com maiores perdas |
| **Cortes ONS** | `GET` | `/api/cortes-ons/motivos` | Agrupamento por motivos de restrição |
| **Clima** | `GET` | `/api/clima/usinas/{id}/historico`| Dados meteorológicos históricos da usina |
| **Clima** | `GET` | `/api/clima/previsao-rajadas` | Previsão de rajadas de vento |
| **Clima** | `GET` | `/api/clima/rosa-ventos` | Distribuição direcional e intensidade do vento |
| **Previsões** | `GET` | `/api/previsoes/usina/{usinaId}` | Previsão calculada de risco de corte |
| **Alertas** | `GET` | `/api/alertas` | Alertas gerados para a conta autenticada |
| **Alertas** | `PUT` | `/api/alertas/{id}/marcar-lido` | Confirmação de leitura do alerta |
| **Impacto VAF**| `GET` | `/api/impacto-vaf` | Relatório de impacto financeiro municipal |
| **Backfill** | `POST` | `/api/admin/backfill-ons` | Disparo manual de carga histórica do ONS |

---

## 🚀 Como Executar o Projeto

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
* `SPRING_DATASOURCE_URL` (padrão: `jdbc:mysql://localhost:3306/curtavento?serverTimezone=UTC`)
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

A API estará disponível em: `http://localhost:8080`.

### 5. Executar com Docker
```bash
# Construir a imagem otimizada
docker build -t curtavento-api .

# Executar o container
docker run -p 8080:8080 --name curtavento curtavento-api
```

---

## 👨‍💻 Autor

Desenvolvido por **[Hiago L. B. P.](https://github.com/HiagoLBP)**.
