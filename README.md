# Sistema de Estacionamento
Este projeto implementa um sistema de estacionamento utilizando arquitetura de microserviços com Java e Spring Boot.

O sistema permite que condutores registrem o tempo de estacionamento, escolham entre tempo fixo ou por hora, monitorem os tempos de estacionamento e recebam alertas antes dos tempos expirarem.

## Tecnologias Utilizadas
 - Java: Linguagem de programação principal.
 - Spring Boot: Framework utilizado para criar aplicações robustas e escaláveis.
 - Spring Web: Para criar APIs RESTful.
 - Spring Data JPA: Para persistência de dados.
 - Spring Data MongoDB: Para integração com MongoDB.
 - Spring Cache: Para caching interno.
 - MongoDB: Banco de dados NoSQL.
 - Maven: Ferramenta de gerenciamento de dependências e build.
 - Spring Doc Open API: Para documentação automática das APIs.
 - ScheduledExecutorService: Para agendamento de tarefas em segundo plano.


## Requisitos

- JDK 17 ou superior
- Maven
- MongoDB

Obs. Collection disponível neste repositório

##Documentação
http://localhost:{{portaDoMicroserviço}}/swagger-ui/index.html#/

##Relatórios
 - Para ver os pdfs de relatório ajustar o path no application.properties do serviço de monitoramento

## Sistema
O sistema é composto por 3 microserviços que se comunicam entre si e se conversam através do eureka para facilitar a escalabilidade

Os endpoint foram feitos pensados em chamadas ocorrendo de algum front web ou aplicativos



