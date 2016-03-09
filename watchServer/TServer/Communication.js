const Generator = require("../TCommon/util/Generator");
const TCrypto = require("../TCommon/security/TCrypto");

function Communication(userId) {
    this._id = Generator.generateId();
    this._userId = userId;
    this._lastUpdateTime = Date.now();
}

Communication.prototype.updateConnection = function(connection) {
    if(!connection) {
        return ;
    }
    var self = this;
    self._lastUpdateTime = Date.now();
    if(self._connection !== connection) {
        delete self._connection.disconnectCallBack;
        self._connection = connection;
        connection.disconnectCallBack = function() {
            self._connection = null;
            delete this.disconnectCallBack;
        };
    }
};

Communication.prototype.receive = function(requestData) {
    if(this.onReceive) {
        this.onReceive(requestData);
    }
};

Communication.prototype.send = function(responseData) {
    var self = this;
    TCrypto.cipher(responseData, function(ciphered){
        if(self._connection) {
            try{
                self._connection.send(ciphered);
            }catch(e){}
        }
    });
};

module.exports = Communication;
