const TCommunicationService = require("../../TServer/TCommunicationService");
const Util = require("util");

function LiveCommunicationService() {
    TCommunicationService.call(this);
}

Util.inherits(LiveCommunicationService, TCommunicationService);

LiveCommunicationService.prototype.onReceive = function(communication, requestData) {

};
