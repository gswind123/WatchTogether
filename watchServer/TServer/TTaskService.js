const Events = require("events");
const ServiceError = require("./ServiceError");
const Util = require("util");
const TLogUtil = require("../TCommon/util/TLogUtil");

/**
 * Override #onReceive to deal with request
 */
function TTaskService() {
    Events.EventEmitter.call(this);
}
Util.inherits(TTaskService, Events.EventEmitter);

TTaskService.onReceive = null;

TTaskService.prototype.receive = function(requestBean) {
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
            TLogUtil.log(e);
            self.emit("finish", null, {result:ServiceError.ServerRuntimeError});
        }
    }
};
module.exports = TTaskService;
