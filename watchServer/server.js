const Http = require("http");
const WebSocketServer = require("ws").Server;
const TProxy = require("./TServer/TProxy");
const RequestEntity = require("./TServer/model/RequestEntity");
const ResponseEntity = require("./TServer/model/ResponseEntity");
const ServiceError = require("./TServer/ServiceError");
const ServiceType = require("./TServer/ServiceType");

var httpServer = Http.createServer();
var webSocketServer = new WebSocketServer({"server":httpServer});
webSocketServer.on("connection", function(connection){
    connection.on("message", function(request){
        RequestEntity.parseRequest(request, function(requestEntity, serviceError){
            if(!(!serviceError || serviceError === ServiceError.Null)) {
                connection.close();
                return ;
            }
            if(requestEntity.serviceType === ServiceType.TaskService) {
                TProxy.execService(requestEntity, function(responseBean, errorMessage) {
                    responseEntity = new ResponseEntity(requestEntity.serviceType, requestEntity.serviceCode,
                        errorMessage.result, responseBean);
                    responseEntity.parseResponse(function(responseSeq) {
                        try{
                            connection.send(responseSeq);
                        }catch(e){}
                    });
                });
            }
        });
    });
});
httpServer.listen(18080);
