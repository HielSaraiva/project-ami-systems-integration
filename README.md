# Projeto de Integração AMI

## Índice

- [Objetivos Gerais](#objetivos-gerais)
- [Projeto 0 - Preparar o Ambiente](#projeto-0---preparar-o-ambiente)
  - [Entregáveis](#entregáveis)
- [Projeto 1 - Consumo Simples](#projeto-1---consumo-simples)
   - [Entregáveis](#entregáveis-1)
- [Projeto 2 - Alteração nas regras de negócio](#projeto-2---alteração-nas-regras-de-negócio)
   - [Entregáveis](#entregáveis-2)

---

## Objetivos gerais

- Subir third-parties

   - Redis;
   - ActiveMQ;
   - ~~Eureka~~;
   - ~~API Gateway~~;
   - CronJob;
   - Postgres;
   - FTP;

- ~~Desenvolver funcionalidades no frontend (angular)~~

- Desenvolver serviço de converter

- Desenvolver serviço de business

- Desenvolver serviço de network

- Estabelecer uma comunicação ponta-a-ponta entre esses 3 serviços

## Projeto 0 - Preparar o ambiente

A primeira atividade é configurar um ambiente minimalista para o projeto.

Instale o docker e crie um docker-compose que suba: Um **redis**, um **activemq-artemis**  e um **postgresql**.

Acesse cada um desses serviços via terminal, e execute comandos simples (criação, escrita, remoção).

O entregável dessa atividade é um docker compose funcional e um relatório que demonstre conhecimentos rudimentares nos sistemas acima.

### Entregáveis

- **DockerCompose:** [docker-compose.yml](/P0/docker-compose.yml)

- **Relatório:** [README.md](./P0/README.md) 

--- 

## Projeto 1 - Consumo Simples

Primeiramente: bem-vinde! Você acabou de chegar na empresa, e suas atividades serão bem simples nesse primeiro momento. Que tal começar com algo fácil: converter uma mensagem JSON em um CSV.

As mensagens estão chegando via fila do ActiveMQ, mas estão formatadas em JSON. Para a aplicação, o cliente requer que seja feito o backup das mensagens em forma de CSV. Outro desenvolvedor está responsável pela funcionalidade de salvar essas mensagens em um arquivo. Você só precisa estruturar a mensagem do JSON para um CSV.

### Entregáveis

- **DockerCompose:** [docker-compose.yml](/P1/docker-compose.yml)

- **Projeto Java Spring:** [microsservices/converter](P1/microsservices/converter/)

- **Relatório:** [README.md](./P1/README.md) 

---

## Projeto 2 - Alteração nas regras de negócio

O cliente mudou a regra de negócio 🤬🤬🤬. O cliente não quer mais salvar o nome de usuário, mas sim o ID desse usuário. Porém, o BUSINESS antigo não pode ser alterado pois já está sendo usado por outro projeto. Sendo assim, você precisa criar um BUSINESS novo, que repasse esses dados para a CONVERTER previamente criada. ALTERAR A CONVERTER NÃO É UMA OPÇÃO!

### Entregáveis

- **Projeto Java Spring:** [microsservices/business](P2/microsservices/business/)

- **Relatório:** [README.md](./P2/README.md) 

---