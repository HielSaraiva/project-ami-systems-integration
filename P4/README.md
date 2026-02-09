# Projeto 4 - O projeto evoluiu!

## Índice

- [Lore](#lore)
- [Pré-condição](#pré-condição)
- [Atividade](#atividade)
- [Pós-condição](#pós-condição)
- [Passo-a-Passo](#passo-a-passo)

## Lore

Felizmente está tudo dando certo, e agora temos um fluxo de ponta-a-ponta funcional!

Porém, nossos arquitetos viram que, a solução como está hoje, não é muito boa para que possamos identificar pontos de falhas, e manter históricos. Então, nesse momento, precisaremos adaptar a solução para usar um banco de dados!

## Pré-condição

- Os serviços third-parties estão funcionando;
- A CONVERTER criada nas atividades passadas;
- A BUSINESS criada nas atividades passadas;
- A NETWORK criada na atividade passada;
- .proto da API gRPC do cliente.

## Atividade
 
- Criar um projeto novo (um módulo) que use maven, springboot, java 17 e FLYWAY;
- Criar uma controller com um endpoint que leia da API gRPC;
- Criar migrations que descrevam o banco onde essas mensagens vão ser armazenadas;
- Persistir mensagens vindas do grpc no banco, na forma de tickets/processos (sendo um ticket um agrupamento de processos);
- Enviar o ID da mensagem para a fila training-converter.receive_raw;
- Alterar as CONVERTER/BUSINESS/NETWORK criadas nas atividades anteriores para receber o ID da mensagem via fila (não mais a mensagem);
- Adaptar o código para esse novo comportamento.

## Pós-condição

- Uma NETWORK-GRPC foi criada;
- As informações coletadas da API agora serão salvas no banco;
- Todo o fluxo agora trabalha somente com ID de mensagem, não mais com a mensagem pura;
- 100% de cobertura de teste.

## Passo-a-Passo

### 1. Entendendo o que é o gRPC API

- [O que é o gRPC](https://zup.com.br/blog/grpc-o-que-e-beagle/)
- [Diferença entre gRPC e REST](https://aws.amazon.com/pt/compare/the-difference-between-grpc-and-rest/)
- [Introdução ao gRPC](https://grpc.io/docs/what-is-grpc/introduction/)
- [Java with gRPC - Quickstart](https://grpc.io/docs/languages/java/quickstart/)

### 2. Subindo serviços Third-parties

Vamos subir os serviços utilizando o arquivo [docker-compose.yml](docker-compose.yml), basta usar o comando na pasta raiz P4:

````shell
docker compose up -d
````

### 3. Copiando microsserviços anteriores

Como vamos precisar alterar os microsserviços anteriormente construídos, vou criar cópias deles dentro da pasta [microsservices](microsservices/), pois não quero alterar o que foi já foi construído.

Dito isso, vou aproveitar o momento para realizar melhorias nos microsserviços ``converter`` e ``business``:

**Melhorias:**

- Converter:
   - Configurando variáveis de ambiente do projeto 
   - Implementando sistema de Log com Log4j2 
- Business:
   - Configurando variáveis de ambiente do projeto

Após as melhorias, rode os serviços e verifique se estão funcionando.

### 4. Criando Projeto Java Spring

Usei a IntelliJ IDE da JetBrains para criar o módulo:

![Criando Projeto Java Spring](assets/image1.png)

Adicionei essas dependências ao criar o Projeto, incluindo a dependência do framework gRPC:

![Dependências do Projeto Java Spring](assets/image2.png)

> **Observação:** Consulte a documentação do Spring: [HELP.md](microsservices/network-grpc/HELP.md)

### 5. Configurando application.properties

Depois da criação do projeto, eu configurei o arquivo [application.properties](microsservices/network-grpc/src/main/resources/application.properties) com as informações relacionadas ao Banco de Dados PostgreSQL e ao ActiveMQ Artemis.

### 6. Logging com Apache Log4j2

Vamos usar o [Log4j2](https://logging.apache.org/log4j/2.12.x/maven-artifacts.html) para realizar o logging do nosso microsserviço network.

Primeiro, é necessário adicionar a seguinte dependência ao [pom.xml](microsservices/network-grpc/pom.xml):

````
<!-- Exclude Logback -->
<dependency>
   <groupId>org.springframework.boot</groupId>
   <artifactId>spring-boot-starter</artifactId>
   <exclusions>
         <exclusion>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-logging</artifactId>
         </exclusion>
   </exclusions>
</dependency>
<!-- Add Log4j2 -->
<dependency>
   <groupId>org.springframework.boot</groupId>
   <artifactId>spring-boot-starter-log4j2</artifactId>
</dependency>
````

> **Observação:** O Spring boot utiliza o Logback como logging padrão! Verifique a [documentação](https://docs.spring.io/spring-boot/how-to/logging.html)!

Depois, criei o arquivo de configuração [log4j2.xml](microsservices/network-grpc/src/main/resources/log4j2.xml) e fiz com que todos os status de logs fossem impressos no console e apenas os WARNs e ERRORs fossem escritos nos arquivos [network-grpc-[yyyy-MM-dd].log](microsservices/network-grpc/logs).

Entretanto, ao realizar os testes, percebi que os logs dos testes estavam indo para o mesmo arquivo, e isso não me parece correto! Logo, criei um novo arquivo de configuração [log4j2.xml](microsservices/network-grpc/src/test/resources/log4j2.xml) apenas para o escopo de testes.

Para usar o logger do log4j2, basta usar a annotation ``@Log4j2`` na respectiva classe.

### 7. 