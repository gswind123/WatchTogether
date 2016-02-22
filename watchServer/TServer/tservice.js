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

TService.prototype.receive = function(requestBean) {
    var self = this;
    if(self.onReceive) {
        try{
            self.onReceive(requestBean, function(responseBean) {
                self.emit("finish", responseBean, {result:0});
            });
        } catch(e) {
            self.emit({}, {result:ServiceError.ServerRuntimeError});
        }
    }
};
module.exports = TService;
