function Communication(userId, commService) {
    this._id = "comm"+Date.now();
    this._userId = userId;
    this._lastUpdateTime = Date.now();
    this._communicationService = commService;
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
    if(this._communicationService && this._communicationService.receive) {
        this._communicationService.receive(requestData);
    }
};

module.exports = Communication;
