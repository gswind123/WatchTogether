const TCrypto = require("./TCommon/security/tcrypto");
const Http = require("http");
const WebSocketServer = require("ws").Server;

var httpServer = Http.createServer();
var webSocketServer = new WebSocketServer({"server":httpServer});
webSocketServer.on("connection", function(connection){
    connection.on("message", function(data){
        TCrypto.decipher(data, function(request) {
            var requestBean = null;
            try{
                requestBean = JSON.parse(request);
            } catch(e) {
                console.log(e);
            }
            if(requestBean) {
                console.log(requestBean.userMessage);
                try{
                    var response = {
                        "serverMessage":"对你说一句，只是说一句"
                    };
                    TCrypto.cipher(JSON.stringify(response), function(data){
                        connection.send(data);
                    });
                }catch(e) {
                    console.log(e);
                }
            }
        })
    });
});
httpServer.listen(18080);


