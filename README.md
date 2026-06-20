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
- Banco de dados H2 (em memória)
- Liquibase (versionamento de banco)
- JUnit 5 + Mockito (testes)
- Servidor de aplicação: WildFly / JBoss EAP (Jakarta EE 10)

## Pré-requisitos

- JDK 17 ou superior
- Maven 3.8+
- Um servidor de aplicação compatível com Jakarta EE 10 (WildFly 27+ / JBoss EAP 8)

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

- Banco **H2 em memória**: `jdbc:h2:mem:autorizacao;DB_CLOSE_DELAY=-1`
- Usuário: `sa` / Senha: *(vazia)*
- O schema e os dados iniciais são criados automaticamente pelo **Liquibase**
  na inicialização da aplicação (`LiquibaseInitializer`), a partir de
  `db/changelog/db.changelog-master.xml`.
- As regras iniciais inseridas correspondem exatamente à tabela do enunciado.

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

### Deploy no WildFly / JBoss

1. Faça o build: `mvn clean package`.
2. Copie `target/autorizacao.war` para `WILDFLY_HOME/standalone/deployments/`.
3. Inicie o servidor: `WILDFLY_HOME/bin/standalone.sh`.
4. Acesse a aplicação (veja as URLs abaixo).

### URLs de acesso

| Recurso | URL |
|---------|-----|
| Aplicação | `http://localhost:8080/autorizacao/` |
| Console de administração do WildFly (requer usuário criado via `bin/add-user.sh`) | `http://localhost:9990/` |

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

