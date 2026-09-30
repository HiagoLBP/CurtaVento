# AUDITORIA DE TESTES — CURTAVENTO

## 1. Estado atual dos testes
O cenário atual aponta para uma **extrema deficiência na cobertura de testes automatizados**. Foram encontrados apenas 3 arquivos de teste em toda a aplicação, que conta com quase 60 classes no backend. 
Os testes existentes cobrem pontualmente alguns cenários unitários das regras de negócio, mas não exploram os casos extremos. A vasta maioria do projeto, incluindo segurança, comunicação de APIs REST, persistência em banco de dados e integração com o ONS, opera sem nenhuma validação automatizada. O risco de regressão em manutenções futuras é gravíssimo.

## 2. Testes existentes
Foram identificadas apenas as 3 classes abaixo na pasta `src/test/java`:

*   **`AlertaServiceTest.java`**: 
    *   **Classe Testada**: `AlertaService`
    *   **Tipo**: Teste Unitário (Mockito)
    *   **Situação Atual**: Bom teste unitário. Cobre as lógicas de criação de alerta (limite de 1 por dia) e validação de segurança ao marcar alerta como lido (Garante que usuário X não leia alerta da usina Y).
*   **`TokenServiceTest.java`**:
    *   **Classe Testada**: `TokenService`
    *   **Tipo**: Teste Unitário
    *   **Situação Atual**: Muito básico. Confirma se os dados injetados na criação do token JWT (email, perfil, ids) são extraídos com sucesso. Não verifica exceções matemáticas reais de criptografia ou tokens expirados.
*   **`ImpactoVafServiceTest.java`**:
    *   **Classe Testada**: `ImpactoVafService`
    *   **Tipo**: Teste Unitário (Falso-Positivo)
    *   **Situação Atual**: Crítico/Inútil. O teste injeta os *mocks*, constrói um objeto manual e chama `impactoRepository.save()` diretamente. A classe `ImpactoVafService` e sua regra de negócio de cálculo financeiro nunca é efetivamente testada aqui. O teste existe apenas para o Mockito confirmar que ele mesmo sabe retornar um objeto.

## 3. Cobertura do backend
A cobertura está restrita a fragmentos de 3 services. As lacunas são absolutas em:

*   **Controllers**: 0% de cobertura.
*   **Services Complexos**: 0% (PrevisaoRiscoService, BackfillOnsService, HistoricoCorteOnsService).
*   **Repositories**: 0% de validação em `@Query` ou Query Nativas, que carregam lógicas vitais de relatórios e filtros.
*   **Segurança/Auth**: 0% de cobertura nos filtros (`SecurityFilter`) e configuração de endpoints.
*   **Integração e Batch**: 0% nos *Jobs* (SincronizacaoOnsJob, GeracaoAlertasJob, etc.).

## 4. Cobertura do Spring Boot
Neste momento não há testes baseados no ecossistema Spring Boot, gerando lacunas que devem ser atacadas conforme a arquitetura:

*   **`@WebMvcTest`**: Inexistente. Recomenda-se para cobrir `AutenticacaoController`, `CortesOnsController` e `ImpactoMunicipalController`, garantindo o retorno JSON esperado (Field Names), códigos 200, 400, 403 e formatações de data corretas.
*   **`@DataJpaTest`**: Inexistente. Crucial para validar as Queries nativas criadas em `HistoricoCorteOnsRepository` e constraints do banco de dados (ex: `@Column(unique=true)`).
*   **`@SpringBootTest`**: Não há testes de integração E2E (Fim a fim) que simulem o fluxo real (Ex: Trigger do Batch ONS rodando -> Validar se salvou no BD -> Chamar Endpoints Dashboard).
*   **Mockito (`@ExtendWith`)**: Utilizado, mas de forma frágil no caso do VAF.

## 5. Cobertura da integração ONS
**Inexistente.** O arquivo `BackfillOnsService.java` contém a rotina mais densa da aplicação (Parsing de CSV do S3 da ONS, cálculos de desvios, "Correção de Jensen" e agregações por usina e data). 

Sem testes de integração ONS, é impossível saber se:
*   O parse decimal falha ao encontrar ponto ao invés de vírgula ou campos vazios no CSV;
*   Valores duplicados vindos do ONS corrompem os totais ou disparam a chave duplicada (`ON DUPLICATE KEY UPDATE`);
*   Dados com o estado diferente de "BA" estão sendo filtrados de fato.

## 6. Cobertura da segurança
**Muito fraca.** A classe `SecurityConfig.java` protege os endpoints corretamente e exige Roles Específicas, mas isso NUNCA foi testado.
*   **Ausente**: Testes confirmando que usuários sem perfil `ADMIN` recebem um `HTTP 403` ao tentar executar chamadas destrutivas.
*   **Ausente**: Testes validando se chamadas a `/api/pld/**` com token ausente funcionam adequadamente (Endpoints abertos).
*   **Ausente**: Testes do filtro `SecurityFilter` para verificar o bloqueio real de requisições com token expirado.

## 7. Cobertura do banco/JPA
**Nula.** Nenhuma tabela do banco de dados possui validação via H2 (ou Testcontainers) via `@DataJpaTest`.
*   O relacionamento vital de perda (CorteOns -> Usina -> Municipio -> ImpactoVAF) não é validado contra transações órfãs.
*   Em caso de remoção/atualização de uma Usina, não há garantia programática de que o histórico da ONS em cascata lida adequadamente com isso.
*   Faltam validações do mapeamento para saber se `nome` nulo aciona exception da DB de fato.

## 8. Problemas encontrados nos testes
1.  **Testes Artificiais (Falso Positivo)**: `ImpactoVafServiceTest.java` não valida o cálculo de `18% ICMS` nem a cota parte do VAF do Município. O método real `calcularImpacto()` sequer é chamado.
2.  **Mocks Incompletos**: Em `TokenServiceTest`, não se simula cenários destrutivos como token assinado com chaves falsas.
3.  **Dependência Excessiva no Unitário**: Falta de testes envolvendo injeção de dependência (`@Autowired`), para assegurar que os serviços conseguem rodar dentro do contêiner Spring real sem falhas de mapeamento.

## 9. Classes e métodos sem cobertura relevante
As artérias principais do negócio estão sem rede de proteção, priorizando:
*   `BackfillOnsService.java` (`processarCsvDiario`) -> Integração crítica.
*   `PrevisaoRiscoService.java` (`calcularRisco`) -> Lógica de matriz de vento.
*   `ImpactoVafService.java` (`calcularImpacto`) -> Faturamento e imposto, lógica vital do dashboard.
*   `DashboardController.java` (`getResumoGeral`) -> O motor do Front-end.
*   `HistoricoCorteOnsRepository.java` (`findRankingCortesAno`, `findImpactoPorMunicipio`) -> Consultas pesadas que dependem 100% de precisão de SUM e GROUP BY.

## 10. Testes recomendados

| Classe / Método | Cenário | Entrada | Resultado Esperado | Tipo de Teste | Prioridade |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `PrevisaoRiscoService.calcularRisco()` | Regra de negócio (Vento Severo) | `ultimaLeitura` = 15 m/s | Retornar Nível `CRITICO` e `scoreRisco > 60`. | Unitário | ALTA |
| `ImpactoVafService.calcularImpacto()` | Cálculo ICMS/VAF base | Município Válido com 100MWh cortados | `perdaReceita` = R$ 15.000, ICMS gerado = R$ 2.700 | Unitário | ALTA |
| `BackfillOnsService.processarCsvDiario()`| Erro de Parsing / Incompleto | CSV com formato corrompido / `null` | Capturar e logar erro, evitar pânico. | Unitário / Mock | ALTA |
| `HistoricoCorteOnsRepository.findRanking`| Correção de Query SQL | Banco H2 injetado c/ 3 registros de corte | Somatório agrupado correto de forma decrescente | `@DataJpaTest` | ALTA |
| `AutenticacaoController.login()` | Dados Inválidos | Objeto `{senha: Errada}` | Status HTTP 403 / 401 Unauthorized | `@WebMvcTest` | MÉDIA |
| `SecurityFilter.doFilterInternal()` | Token Inválido/Expirado | Request c/ Header Authorization Vencido | Bloqueio imediato da requisição, usuário não é setado. | Integração | MÉDIA |
| `UsinaService.salvar()` | Município Inexistente | Usina vinculada ao ID 999 | Exceção customizada/Runtime "não encontrado" | Unitário | MÉDIA |

## 11. Prioridade dos testes
1.  **ALTA PRIORIDADE**: Regras de Negócio Core: Cálculo de Risco (`PrevisaoRiscoService`) e Conversão Monetária de Energia (`ImpactoVafService`). Se esses dados estiverem incorretos, o propósito inteiro da ferramenta para os prefeitos (CurtaVento) falha.
2.  **ALTA PRIORIDADE**: `BackfillOnsService`. A ingestão não pode falhar silenciosamente ou duplicar faturamento.
3.  **ALTA PRIORIDADE**: Repositórios com `@Query`. O ranking do Dashboard não pode gerar anomalias estatísticas por GROUP BY falho.
4.  **MÉDIA PRIORIDADE**: Cobertura de Segurança dos Controllers via `@WebMvcTest`.

## 12. Plano de implementação

### O que já está bem testado
Praticamente **NADA**. Somente as regras primitivas de alerta de operação de manutenção (`AlertaService`).

### O que está parcialmente testado
A assinatura e decodificação do Token JWT no `TokenService`, cobrindo de forma muito superficial se a criação ocorre.

### O que praticamente não possui testes
*Todo o sistema*.
Módulo ONS, Módulo de VAF/Impostos Monetários, Integrações de Endpoints (JSON Maps), Rotinas Batch de Sincronização, Consultas Nativas e Políticas de Controle de Acesso (RBAC).

### Quais testes deveriam ser criados primeiro
A implementação deverá seguir o seguinte mapa tático:
1.  **Regras de Negócio Puras (Mockito / JUnit)**: Desenvolver testes unitários abrangentes cobrindo `calcularImpacto` (VAF) e `calcularRisco` (Clima), passando matrizes diversificadas e valores limiares para provar a consistência matemática da aplicação.
2.  **Ingestão de Dados (Mockito / Streams)**: Isolar o Parser de CSV do `BackfillOnsService` com dados falsos para simular duplicações e tratativas de string corrompida, garantindo consistência no agregador *Jensen*.
3.  **Segurança e Endpoints (`@WebMvcTest`)**: Trancar a entrada da aplicação verificando se requisições para `/api/usinas/` recebem um rígido bloqueio se o JWT for omitido ou não for ADMIN.
4.  **Consultas (`@DataJpaTest`)**: Preencher um H2 Database efêmero e executar o repositório de histórico para checar se o *ranking* de cortes bate 100% com a base real injetada.
