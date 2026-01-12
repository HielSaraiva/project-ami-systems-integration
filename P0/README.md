# Projeto 0 - Preparar o ambiente

## Visão Geral

Antes de começar o projeto, será necessário aprender algumas coisas, adquirir alguns softwares…

Por padrão a equipe usa o **Intellij** para trabalhar com **java (17)**. De framework, trabalhamos com o **spring framework**.

O projeto principal tem uma estrutura bem similar ao padrão de projeto **Saga baseado em coreografia** com banco compartilhado (ou seja, um banco para a aplicação toda).

O **SGBD** usado majoritariamente é o **Postgres**, mas com ressalvas para usos pontuais no **OracleDb**.

Como o próprio padrão de projeto sugere, temos um meio para que possamos organizar a comunicação entre os módulos. No caso, usamos um gerenciador de filas **ActiveMQ Artemis**.

Para armazenar alguns valores comum da aplicação (expandindo o conceito de **variáveis de ambiente**) usamos um gerenciador de cache **Redis**. Assim, mesmo que o projeto esteja separado em mais de uma máquina, vão todos configurar as mesmas variáveis. Esse também é usado para armazenamento rápido de mensagens, ou como cache (de fato).

Para auxiliar na manutenção dos módulos, e na “inversão de dependência” entre eles, utiliza-se o **Eureka**. Assim, um módulo não precisa saber onde o outro está. Todos só precisam saber onde o Eureka está. Podendo assim realizar alguns **Load Balance**, fora a avaliação (superficial) do estado dos módulos do ambiente.

Tudo isso roda dentro de containeres no **Docker**, e são configurados com um **DockerCompose** e um **.env**.

Dito isso, a primeira atividade é configurar um ambiente minimalista para o projeto.

Instale o docker e crie um docker-compose que suba: Um redis, um activemq-artemis  e um postgresql.

Acesse cada um desses serviços via terminal, e execute comandos simples (criação, escrita, remoção)…

O entregável dessa atividade é um docker compose funcional e a demonstração de conhecimentos rudimentares nos sistemas acima.

---

## Passo-a-Passo

### 1. Criando arquivo docker-compose.yml e subindo Containers

O primeiro passo do projeto foi criar um arquivo [docker-compose.yaml](/P0/docker-compose.yml) contendo os serviços requeridos e suas respectivas configurações.

Para subir todos os serviços, use o seguinte comando na pasta raiz [P0](/P0/):

````shell
docker compose up -d # A tag -d, d de detached, serve para liberar o terminal após iniciar os serviços.
````

Após subir os serviços, verifique os status deles com o seguinte comando:

````shell
docker ps -a # A tag -a serve para listar todos os containers, inclusive os parados ou encerrados.
````

Deve aparecer algo semelhante:

![Status dos containers Docker](./assets/image1.png)