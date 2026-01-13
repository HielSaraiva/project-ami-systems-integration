# Projeto de Integração AMI

## Índice

- [Objetivos Gerais](#objetivos-gerais)
- [Projeto 0 - Preparar o Ambiente](#projeto-0---preparar-o-ambiente)
  - [Entregáveis](#entregáveis)

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