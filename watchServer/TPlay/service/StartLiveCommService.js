const Util = require("util");
const TTaskService = require("../../TServer/TTaskService");
const CommunicationManager = require("../business/CommunicationManager");
const ServiceError = require("../../TServer/ServiceError");

function StartLiveCommService() {
    TTaskService.call(this);
}

Util.inherits(StartLiveCommService, TTaskService);

StartLiveCommService.prototype.onReceive = function(requestBean, outputCallBack) {
    var serviceError = ServiceError.Null;
    do{ //while false
        var uid = requestBean.localMac;
        if(!uid) {
            serviceError = ServiceError.IllegalArguments;
        }
    }while(false);
};
