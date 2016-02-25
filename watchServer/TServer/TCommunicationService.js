const Events = require("events");
const ServiceError = require("./ServiceError");
const Util = require("util");

function TCommunicationService() {
    Events.EventEmitter.call(this);
}

Util.inherits(TCommunicationService, Events.EventEmitter);

TCommunicationService.prototype.onReceive = function(communication, requestData){}

TCommunicationService.prototype.receive = function(communication /*Communication*/, requestData) {
    this.onReceive(communication, requestData);
};


