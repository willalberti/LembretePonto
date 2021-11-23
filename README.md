MS - Caso de uso PreHabilitação Remota

Implementa um caso de uso de pré habilitação remota usando saga-orquestração.

Fluxo geral

nome

Diagrama de sucesso

nome

O MS pre-remote-habilitation recebe um request para habilitação remota de uma linha, armazena localmente o pedido e a etapa, publica um evento de aprovisionamento.

O MS create-provisioning escuta o tópico de provisionamento, quem possui um novo evento de criação do aprovisionamento. Esse então armazena localmente o pedido e a etapa, executa a chamada ao legado e publica um evento de resposta do aprovisionamento.
O MS pre-remote-habilitation escuta o tópico de resposta do aprovisionamento, que possui um evento de resposta. Esse então armazena localmente a etapa e verifica se a resposta é de sucesso ou erro.

caso seja de sucesso o MS pre-remote-habilitation publica um evento de pre-ativação
em caso de erro o MS pre-remote-habilitation termina o fluxo com erro.
O MS pre-activate-feature escuta o tópico de pre-ativação, que possui um evento de criação. Esse então armazena localmente o pedido e a etapa, executa a chamada ao legado e publica um evento de resposta da pré-ativação.
O MS pre-remote-habilitation escuta o tópico de resposta da pré ativação, que possui um evento de resposta. Esse então armazena localmente a etapa e verifica se a resposta é de sucesso ou erro.

caso seja de sucesso o MS pre-remote-habilitation publica um evento de criação da linha no SLR
em caso de erro o MS pre-remote-habilitation inicia o fluxo de rollback.
O MS create-line escuta o tópico de criação da linha no SLR, que possui um evento de criação. Esse então armazena localmente o pedido e a etapa, executa a chamada ao legado e publica um evento de resposta da criação da linha no SLR.
O MS pre-remote-habilitation escuta o tópico de resposta da criação da linha no SLR, que possui um evento de resposta. Esse então armazena localmente a etapa e verifica se a resposta é de sucesso ou erro.

caso seja de sucesso o MS pre-remote-habilitation encerra o fluxo de habilitação
em caso de erro o MS pre-remote-habilitation inicia o fluxo de rollback.
Diagrama de rollback

nome

O MS pre-remote-habilitation inicia o fluxo de rollback quando identifica que um evento de resposta contém um código de erro (Evento processo com httpStatusCode<>200).

1 - Erro na etapa de aprovisionamento:
O MS pre-remote-habilitation recebe um evento de resposta do aprovisionamenro com erro e encerra o fluxo de habilitação.

2 - Erro na Etapa de pre-ativação:
O MS pre-remote-habilitation recebe um evento de resposta da pre-ativação com erro e inicia o fluxo de rollback publicando um evento de ROLLBACK no tópico de aprovisionamento. O MS create-provisioning escuta o tópico de provisionamento, quem possui um novo evento de rollback do aprovisionamento. Esse então armazena localmente o pedido e a etapa, executa a chamada ao legado e publica um evento de resposta do aprovisionamento.
O MS pre-remote-habilitation escuta o tópico de resposta do aprovisionamento, que possui um evento de resposta. Esse então armazena localmente a etapa e verifica se a resposta é de sucesso ou erro.

caso seja de sucesso o MS pre-remote-habilitation encerra o fluxo.
em caso de erro o MS pre-remote-habilitation publica um evento de deadLetter.
3 - Erro na Etapa de criação linha SLR:
O MS pre-remote-habilitation recebe um evento de resposta da criação linha SLR com erro e inicia o fluxo de rollback publicando um evento de ROLLBACK no tópico de pré-ativação. O MS pre-activate-feature escuta o tópico de pre-ativação, que possui um evento de rollback. Esse então armazena localmente o pedido e a etapa, executa a chamada ao legado e publica um evento de resposta da pré-ativação.
O MS pre-remote-habilitation escuta o tópico de resposta da pré ativação, que possui um evento de resposta. Esse então armazena localmente a etapa e verifica se a resposta é de sucesso ou erro.

caso seja de sucesso o MS pre-remote-habilitation publica um evento de rollback do aprovisionamennto
em caso de erro o MS pre-remote-habilitation publica um evento de deadLetter.
URLs

Iniciar aplicação http://localhost:9090/swagger-ui.html

Gerenciar Landoop http://localhost:3030

Cenários de teste

Exemplo de request:

curl -X POST "http://localhost:9090/resource/resourcedomainmgmt/v1/preremotehabilitation" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"geographicArea\": \"string\", \"network\": \"1\", \"protocolNumber\": \"1\", \"protocolType\": \"CREATE\"}"  
Exemplo de response com sucesso:

|code | Descrição |
|-------|---------------------|
|500| Erro interno |
|401| request invalido |
|403| acesso proibido |
|404| recurso não encontra|

Exemplo de response com sucesso:

http.statusCode=200
Simular envio com sucesso
sucesso em todos os MS protocolNumber=[qualquer valor]
Simular erro de criação
Erro no ms-create-provisioning protocolNumber=999
Erro no ms-pre-active-feature protocolNumber=888
Erro no ms-create-line protocolNumber=777
Simular erro no rollback
Erro no ms-create-provisioning protocolNumber=999
Erro no ms-pre-active-feature protocolNumber=888
Erro no ms-create-line protocolNumber=777
Máquina de estado

Para controlar o fluxo de provisionamento esse MS persiste num banco local MongoDB o histórico da habilitação sendo registrado com a estrutura abaixo:
Cada habilitação contem um numero de protocolo que concentra o histórico da habilitação em uma lista de EventProcess. Nessa lista é resgistrado o pedido de aprovisionamento e a reposta para cada etapa

Protocol {
    String protocolNumber //numero do protocolo
    List<EventProcess> EventProcess //histórico de eventos
}

EventProcess {
    Step step //etapa do provisionamento [APROVISIONAR|PRE_ATIVAR|CRIAR_LINHA_SLR]
    Date date //data do evento
    String type // tipo do evento [CREATE|ROLLBACK]
    String code //http.statusCode retorno de cada etapa
    String description //descrição do retorno de cada etapa
    String protocolNumber //numero do protocolo
}
Dependências

Para o funcionamento do processo de pre-habilitação-remoto é necessário executar os MS abaixo:

create-provisioning
pre-remote-feature
create-line
Landopp (kafka + SchemaRegister)
MongoDB
Detalhes para inicio da aplicação

Iniciar o landoop, para gerenciar o Kafka executando, o docker-compose.yml com o comando abaixo.
$ cd pre-remote-habilitation/src/main/resources
$ docker-compose up -d   
Iniciar o MongoDB
executar o comando abaixo em um terminal

docker run -d -p 27017:27017 -p 28017:28017 -e AUTH=no mongo 
se a imagem não existir deve ser baixada com:

docker pull mongo
