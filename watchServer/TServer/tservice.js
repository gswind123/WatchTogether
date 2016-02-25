const Events = require("events");
const ServiceError = require("./ServiceError");
const Util = require("util");

/**
 * Override #onReceive to deal with request
 */
function TService() {
    Events.EventEmitter.call(this);
}
Util.inherits(TService, Events.EventEmitter);

TService.prototype.onReceive = function(requestBean, outputCallBack){
    outputCallBack(null, ServiceError.Null);
};

TService.prototype.receive = function(requestBean) {
    var self = this;
    if(self.onReceive) {
        try{
            self.onReceive(requestBean, function(responseBean, serviceError) {
                var errorMessage = {
                    result : 0
                };
                if(serviceError) {
                    errorMessage.result = serviceError;
                }
                self.emit("finish", responseBean, errorMessage);
            });
        } catch(e) {
            self.emit("finish", null, {result:ServiceError.ServerRuntimeError});
        }
    }
};
module.exports = TService;
