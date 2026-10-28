# Meu objetivo com o Desafio

Quis revisar alguns conceitos dentro de Java/Spring Boot, e aprender coisas novas. Logo, como um bom Cadete da 42, fui procurar algum desafio para por a mão na massa e praticar.
Dito isso, meu principal objetivo com este projeto é APRENDER!

---

# Transação API

API REST para receber transações e retornar estatísticas dos últimos 60 segundos, implementada como solução do [desafio técnico Itaú — vaga 99 (júnior)](https://github.com/rafaellins-itau/desafio-itau-vaga-99-junior).

Todas as transações são mantidas **em memória**, sem banco de dados e sem cache, conforme a restrição do desafio.

---

## Stack

| Item | Versão |
| --- | --- |
| Java | 25 |
| Spring Boot | 4.1.1 |
| Gradle | wrapper incluso no projeto |

## Pré-requisitos

- JDK 25 instalado e no `PATH`
- Nenhum serviço externo: a aplicação não depende de banco, cache ou fila

## Como executar

```bash
./gradlew bootRun
```

A aplicação sobe em `http://localhost:8080`.

No Windows, use `gradlew.bat bootRun`.

## Como buildar

```bash
./gradlew build
java -jar build/libs/transacaoApi-0.0.1-SNAPSHOT.jar
```

> Confira o nome exato do `.jar` gerado em `build/libs/`.

## Como rodar os testes

```bash
./gradlew test
```

---

## Endpoints

### `POST /transacao`

Recebe uma transação.

**Corpo:**

```json
{
  "valor": 123.45,
  "dataHora": "2026-10-28T09:12:33.001-03:00"
}
```

**Respostas:**

| Status | Quando |
| --- | --- |
| `201 Created` | transação aceita (sem corpo) |
| `422 Unprocessable Content` | JSON válido, mas recusado pelas regras de negócio |
| `400 Bad Request` | JSON malformado ou com tipos inválidos |

**Regras de aceite:**

- `valor` e `dataHora` são obrigatórios
- `valor` deve ser maior ou igual a zero
- `dataHora` não pode estar no futuro

**Exemplo:**

```bash
curl -i -X POST http://localhost:8080/transacao \
  -H "Content-Type: application/json" \
  -d '{"valor": 123.45, "dataHora": "2026-10-28T09:12:33.001-03:00"}'
```

### `DELETE /transacao`

Apaga todas as transações registradas.

| Status | Quando |
| --- | --- |
| `200 OK` | sempre (sem corpo) |

```bash
curl -i -X DELETE http://localhost:8080/transacao
```

### `GET /estatistica`

Retorna as estatísticas das transações dentro da janela configurada.

**Resposta (`200 OK`):**

```json
{
  "count": 3,
  "sum": 30.0,
  "avg": 10.0,
  "min": 5.0,
  "max": 15.0
}
```

Quando não há transações na janela, todos os campos retornam zero.

```bash
curl -i http://localhost:8080/estatistica
```

---

## Configuração

| Propriedade | Variável de ambiente | Padrão | O que faz |
| --- | --- | --- | --- |
| `statistics.time` | `STATISTICS_TIME` | `60` | Tamanho da janela de estatísticas, em segundos |

A aplicação também importa um arquivo `.env` opcional na raiz do projeto.

```bash
STATISTICS_TIME=120 ./gradlew bootRun
```

---

## Arquitetura

```
Controller  →  Service  →  Repository
    ↑                           ↓
   DTO                  TransacaoModel
```

| Camada | Responsabilidade |
| --- | --- |
| `controller` | Recebe HTTP, dispara a validação do DTO, devolve o status |
| `service` | Regras de negócio: converte DTO em domínio, calcula a janela e as estatísticas |
| `repository` | Armazenamento em memória e consultas por critério |
| `dto` | Contrato com o cliente, na entrada e na saída |
| `model` | Conceito de domínio, independente de HTTP |
| `handler` | Tratamento centralizado de exceções |
| `config` | Beans de infraestrutura |

A separação entre DTO e model existe para desacoplar o contrato público do domínio: o formato da API pode mudar sem afetar as regras, e campos internos nunca são expostos nem aceitos acidentalmente do cliente.

---

## Decisões de arquitetura

### `double` em vez de `BigDecimal`

Valores monetários normalmente pedem `BigDecimal`, pela precisão exata em base decimal. Aqui foi usado ponto flutuante porque o enunciado especifica esse tipo para os campos de estatística e porque `DoubleSummaryStatistics` é a ferramenta natural para a agregação. Em um sistema financeiro real, a escolha seria `BigDecimal`.

### `Double` (wrapper) no DTO, não `double`

Com o primitivo, um JSON sem o campo `valor` seria desserializado como `0.0` e passaria pela validação como se fosse uma transação legítima de valor zero. O wrapper permite distinguir "campo ausente" de "valor zero", e é o que faz o `@NotNull` funcionar.

### Validação concentrada no DTO

Todas as regras de aceite cabem em anotações do Bean Validation. Como consequência, nenhuma exceção customizada foi necessária: as condições de erro são detectadas pelo framework antes do controller executar, e o `@RestControllerAdvice` apenas remapeia o status.

Se alguma regra fosse movida para o service, seria preciso criar uma exceção própria para comunicá-la à camada web.

### `422` para regra de negócio, `400` para JSON ilegível

O Spring responde `400` por padrão quando o Bean Validation falha. O handler global remapeia `MethodArgumentNotValidException` para `422`, reservando o `400` para `HttpMessageNotReadableException`, que é quando a requisição sequer pôde ser interpretada.

### `Clock` injetado, com `Clock.systemUTC()`

O relógio é um bean, e não uma chamada estática a `OffsetDateTime.now()`. Isso torna o cálculo da janela determinístico em testes, que podem congelar o tempo com `Clock.fixed`.

A escolha por `systemUTC()` em vez de `systemDefaultZone()` não altera o resultado das comparações — `OffsetDateTime.isAfter` compara instantes absolutos, então uma transação com offset `-03:00` é avaliada corretamente contra um limite em UTC. O ganho é de previsibilidade: o comportamento e os logs não mudam conforme o fuso da máquina ou do container.

### Janela aberta na borda

O filtro usa `isAfter`, que é estritamente maior. Uma transação com exatamente 60 segundos de idade **não** entra no cálculo: o intervalo é aberto nessa ponta.

### `Collections.synchronizedList` com sincronização explícita na iteração

O repositório é um bean singleton acessado em paralelo pelas threads do servidor, então a estrutura precisa ser thread-safe. `synchronizedList` protege cada operação individual, mas **não** a iteração: por isso o filtro por data roda dentro de um bloco `synchronized` sobre a própria lista, como a documentação do Java exige.

A consulta devolve uma lista nova e imutável, então a coleção interna nunca escapa do repositório.

Trade-off aceito: essa abordagem serializa também as leituras, isto é, duas consultas de estatística não rodam em paralelo. Para o volume deste desafio o impacto é irrelevante. Com um perfil de leitura muito mais intenso que o de escrita, um `ReadWriteLock` permitiria leituras concorrentes; com escrita dominante e sem necessidade de `clear()` atômico, `ConcurrentLinkedQueue` teria melhor throughput.

### Model imutável

`TransacaoModel` não tem setters nem construtor vazio, e seus campos são `final`. Uma transação é um fato histórico: não faz sentido alterá-la depois de registrada. Como efeito colateral, objetos imutáveis são seguros para compartilhamento entre threads por construção.

### Injeção por construtor

Todas as dependências chegam por construtor, com campos `final`. Isso torna as classes instanciáveis em testes unitários sem subir o contexto do Spring, garante que o objeto nunca exista em estado incompleto e expõe as dependências na assinatura em vez de escondê-las no corpo da classe.

### Logs sem dados de transação

O caminho de escrita (`POST /transacao`) registra apenas `DEBUG`, sem o valor da transação. Em contexto financeiro, valores em log são dado sensível, sujeito a política de retenção e acesso. Operações destrutivas e raras, como o `DELETE`, são registradas em `INFO` com a contagem de registros afetados, porque são eventos que precisam ser reconstituíveis depois.

---

## Requisitos obrigatórios

| Requisito | Status |
| --- | --- |
| `POST /transacao` com `201`, `400` e `422` | ✅ |
| `DELETE /transacao` com `200` | ✅ |
| `GET /estatistica` com os cinco campos | ✅ |
| Armazenamento em memória, sem banco nem cache | ✅ |
| Apenas JSON na entrada e na saída | ✅ |
| Java + Spring Boot | ✅ |

## Requisitos extras

| # | Extra | Status |
| --- | --- | --- |
| 1 | Testes automatizados | 🚧 parcial |
| 2 | Containerização | ❌ |
| 3 | Logs | ✅ |
| 4 | Observabilidade | 🚧 parcial |
| 5 | Performance | ❌ |
| 6 | Tratamento de erros | ✅ |
| 7 | Documentação da API | ❌ |
| 8 | Documentação do sistema | ✅ este arquivo |
| 9 | Configurações | ✅ |

### 1. Testes automatizados (parcial)

Cobertura atual:

- **Unitários** dos services e do repository, com JUnit 5, Mockito e AssertJ
- **`@WebMvcTest`** dos dois controllers, verificando status codes e corpo JSON com `MockMvc`
- **`@SpringBootTest`** de integração, exercitando o fluxo completo de ponta a ponta

Pendente: congelar o relógio com `Clock.fixed` para verificar o limite calculado, fixar a semântica da borda da janela e cobrir erros de protocolo (corpo ausente, `Content-Type` inválido, campo desconhecido).

### 3. Logs

SLF4J com Logback, com padrão de console customizado. Os níveis seguem um critério explícito:

| Nível | Uso |
| --- | --- |
| `ERROR` | Falhas inesperadas, com stack trace completa |
| `WARN` | Requisições recusadas |
| `INFO` | Eventos raros ou destrutivos, e a configuração lida no startup |
| `DEBUG` | Passos internos e operações de alto volume |

### 4. Observabilidade (parcial)

Spring Boot Actuator habilitado. Pendente: definir explicitamente quais endpoints são expostos.

### 6. Tratamento de erros

`@RestControllerAdvice` global com três handlers: falha de validação (`422`), corpo ilegível (`400`) e erro inesperado (`500`, com stack trace no log).

O corpo das respostas de erro é vazio, para respeitar o contrato dos endpoints obrigatórios; o detalhe de quais campos falharam vai para o log, não para o cliente.

### 9. Configurações

Janela de estatísticas externalizada, com precedência de variável de ambiente sobre o arquivo de propriedades, e log do valor efetivo no startup.

---

## Roadmap

- [ ] Completar a suíte de testes (ver seção acima)
- [ ] Corrigir o handler genérico, que hoje converte `415` em `500`
- [ ] `Dockerfile` com multi-stage build
- [ ] Expor endpoints do Actuator de forma explícita
- [ ] OpenAPI / Swagger UI
- [ ] Medição de performance
- [ ] Correlation ID via MDC
