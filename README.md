Task Service (Core & Event-Driven) 🚀

O Task Service é o núcleo de gerenciamento de tarefas do ecossistema. Ele é responsável pelo ciclo de vida completo das tarefas, garantindo alta performance através de cache distribuído e mantendo a consistência dos dados com persistência em MySQL. Como um Event Producer, ele utiliza mensageria assíncrona para garantir o desacoplamento e a resiliência do sistema.

🎯 Responsabilidades
Gestão de Tarefas: CRUD completo para criação, edição, listagem e exclusão de tarefas em lote.

Alta Performance (Cache): Implementação de cache inteligente com Redis para reduzir a latência em consultas repetitivas.

Segurança (JWT): Integração com o IAM-Service para validação de tokens e proteção de endpoints baseada em contexto.

Arquitetura Orientada a Eventos (EDA): Publicação de eventos de criação e alteração em filas do RabbitMQ para processamento desacoplado.

🔐 Segurança e Autenticação
Este serviço exige um Token JWT válido para todas as operações.

Validador: O serviço consome o segredo compartilhado para validar a assinatura do token.

Header Requerido: Authorization: Bearer <TOKEN_JWT>.

🛠️ Tecnologias
Java 21: Uso de Records, Virtual Threads e as últimas funcionalidades da linguagem.

Spring Boot 3.4: Framework base com foco em microsserviços modernos.

Spring Data JPA & MySQL: Persistência robusta com suporte a transações ACID.

Redis: Cache distribuído para otimização de leitura.

RabbitMQ: Broker de mensagens para comunicação assíncrona e resiliente entre serviços.

MapStruct & Lombok: Redução de boilerplate e mapeamento eficiente de DTOs.

📡 Integração e Eventos
O serviço atua como um Producer enviando mensagens para o Broker:

Exchange: task.exchange (Direct)

Routing Key: task.created.rk

Fila Destino: task.created.queue

Endpoints Principais
🔗 Documentação Swagger: http://localhost:8081/swagger-ui.html

[POST] /v1/tasks: Cria tarefas e dispara evento TaskCreatedEvent.

[GET] /v1/tasks: Listagem filtrada com suporte a paginação e cache.

[PUT] /v1/tasks: Atualiza tarefas e dispara evento de alteração com invalidação de cache.

[DELETE] /v1/tasks: Remoção em lote e limpeza de chaves no Redis.

🔄 Fluxo de Dados Sênior
O cliente autentica-se no IAM-Service e obtém o token.

O Task-Service valida o token, processa a regra no banco de dados e gerencia o estado no Redis.

Garantia de Entrega: O serviço publica o evento no RabbitMQ utilizando publisher-confirms para garantir que a notificação nunca seja perdida, mesmo em cenários de instabilidade.

O Notification-Service (Consumer) consome a fila de forma assíncrona para alertar os interessados via MQTT/Push.