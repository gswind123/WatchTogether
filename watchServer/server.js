const TProxy = require("./TServer/TProxy");
const Http = require("http");
const TCrypto = require("./TCommon/security/TCrypto");
const WebSocketServer = require("ws").Server;

var httpServer = Http.createServer();
var webSocketServer = new WebSocketServer({"server":httpServer});
webSocketServer.on("connection", function(connection){
    connection.on("message", function(request){
        var requestEntity = null;
        try{
            requestEntity = JSON.parse(request);
        } catch(e) {}
        TProxy.execService(requestEntity, function(responseBean, errorMessage) {
            var responseEntity = {
                result : errorMessage.result,
                resultMessage : errorMessage.resultMessage,
                serviceCode : requestEntity.serviceCode
            };
            function onResponseEntity(responseEntity) {
                connection.send(JSON.stringify(responseEntity));
            }
            if(responseBean) {
                TCrypto.cipher(JSON.stringify(responseBean), function(ciphered) {
                    responseEntity.responseBean = ciphered;
                    onResponseEntity(responseEntity);
                });
            } else {
                onResponseEntity(responseEntity);
            }
        });
    });
});
httpServer.listen(18080);


