# Autorização de Procedimentos - Teste Técnico Fullstack ATD

[![CI](https://github.com/HenriquePavoni/Desafio/actions/workflows/ci.yml/badge.svg)](https://github.com/HenriquePavoni/Desafio/actions/workflows/ci.yml)

Sistema de controle de autorizações de procedimentos médicos para um plano de saúde.
A autorização de um procedimento é decidida com base em **idade** e **sexo**, conforme as regras
cadastradas. Procedimentos não cadastrados nas regras são **negados** com mensagem justificada.

## Stack

- Java 17+ (compatível com Java 21)
- Jakarta EE 10 (Servlets + JSP)
- Maven (build → `.war`)
- Hibernate ORM 6 (JPA)
- Banco de dados Oracle (XE) em container Docker
- Liquibase (versionamento de banco)
- JUnit 5 + Mockito (testes)
- Servidor de aplicação: WildFly (Jakarta EE 10)
- Docker + Docker Compose (Oracle + aplicação)

## Pré-requisitos

- JDK 17 ou superior
- Maven 3.8+
- Docker e Docker Compose (para subir o Oracle e a aplicação)

## Estrutura do projeto

```
src/main/java/com/atd/autorizacao/
  config/        Infra (JPAUtil, inicialização do Liquibase)
  model/         Entidades de domínio
  repository/    Acesso a dados (JPA)
  service/       Regras de negócio (autorização por idade/sexo)
  servlet/       Controllers (requisições HTTP / AJAX)
  dto/           Objetos de request/response
  exception/     Exceções de domínio
src/main/resources/
  META-INF/persistence.xml        Configuração JPA
  db/changelog/                   Scripts Liquibase
src/main/webapp/                  JSPs, CSS e JS
src/test/java/                    Testes (JUnit + Mockito)
```

## Banco de dados

- Banco **Oracle XE** rodando em container Docker (`gvenzl/oracle-xe`).
- O schema e os dados iniciais são criados automaticamente pelo **Liquibase**
  na inicialização da aplicação (`LiquibaseInitializer`), a partir de
  `db/changelog/db.changelog-master.xml`.
- As regras iniciais inseridas correspondem exatamente à tabela do enunciado.

### Configuração da conexão

A conexão é lida em `DatabaseConfig` a partir de **variáveis de ambiente obrigatórias**,
definidas no `docker-compose.yml` para o serviço `app`:

| Variável | Descrição |
|----------|-----------|
| `DB_URL` | URL JDBC do Oracle (`jdbc:oracle:thin:@oracle:1521/XEPDB1`) |
| `DB_USER` | Usuário da aplicação |
| `DB_PASSWORD` | Senha do usuário |
| `DB_DRIVER` | Driver JDBC (`oracle.jdbc.OracleDriver`) |

Sem essas variáveis, a aplicação falha na inicialização.

## Regra de autorização

A decisão é feita por **correspondência exata** entre a solicitação e as regras cadastradas:

1. Se existe uma regra para a combinação `procedimento` + `idade` + `sexo`, o resultado segue o
   campo `permitido` dessa regra (**AUTORIZADO** ou **NEGADO**).
2. Se o procedimento **não está cadastrado** em nenhuma regra, a solicitação é **NEGADA** com
   justificativa ("código não cadastrado").
3. Se o procedimento existe, mas não há regra para aquela idade/sexo, a solicitação é **NEGADA**
   com justificativa.

Toda solicitação avaliada é **persistida** (tabela `solicitacao_autorizacao`) com status e motivo.
A lógica fica isolada em `AutorizacaoService`, coberta por testes unitários em
`AutorizacaoServiceTest`.

## Build

```bash
mvn clean package
```

O artefato será gerado em `target/autorizacao.war`.

> **Atenção (JDK):** o build precisa de um **JDK completo** (com `javac`), versão 17 ou superior.
> Se o `mvn` estiver usando apenas um JRE (erro `release version 17 not supported`),
> aponte o `JAVA_HOME` para um JDK válido antes do build, por exemplo:
>
> ```bash
> export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
> mvn clean package
> ```

## Execução

A aplicação sobe junto com o banco via Docker Compose. O WildFly só inicia depois que o Oracle
está saudável (`healthcheck`).

```bash
docker compose up --build
```

Para parar e remover os containers (mantendo os dados do banco no volume):

```bash
docker compose down
```

Para remover também os dados do banco:

```bash
docker compose down -v
```

### URLs de acesso

| Recurso | URL |
|---------|-----|
| Aplicação | `http://localhost:8080/autorizacao/` |
| Solicitação e consulta de autorizações | `http://localhost:8080/autorizacao/autorizacoes` |
| Oracle (JDBC) | `jdbc:oracle:thin:@localhost:1521/XEPDB1` |

### Consultando o banco (SELECTs)

Como o Oracle roda em container com a porta `1521` exposta, é possível conectar com qualquer
cliente SQL (DBeaver, SQLDeveloper, `sqlplus`) usando:

- **Host:** `localhost` · **Porta:** `1521` · **Service:** `XEPDB1`
- **Usuário:** `autorizacao` · **Senha:** `autorizacao`

Exemplo via `sqlplus` dentro do container:

```bash
docker compose exec oracle sqlplus autorizacao/autorizacao@localhost:1521/XEPDB1
```

```sql
SELECT * FROM regra_autorizacao;
SELECT * FROM solicitacao_autorizacao;
```

## Fluxo da aplicação

A página inicial (`index.jsp`) leva à tela de autorizações (`/autorizacoes`), atendida pelo
`AutorizacaoServlet`:

- **GET `/autorizacoes`**: exibe o formulário e a lista das solicitações já avaliadas.
- **POST `/autorizacoes`**: recebe `codigoProcedimento`, `idade` e `sexo` (`M`/`F`), aplica a
  regra de autorização (`AutorizacaoService`), persiste a solicitação e reapresenta o resultado
  (autorizado/negado com o motivo) junto da lista atualizada.

## Testes

```bash
mvn test
```

## Integração Contínua (CI)

O projeto usa **GitHub Actions** (`.github/workflows/ci.yml`). A cada `push` ou `pull request`
na branch `main`, o pipeline:

1. Faz checkout do código;
2. Configura o **JDK 17** (Temurin) com cache do Maven;
3. Executa `mvn -B clean verify` (build + testes);
4. Publica o artefato `target/autorizacao.war`.

