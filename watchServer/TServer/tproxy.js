const Path = require("path");
const TCrypto = require("../TCommon/security/TCrypto");
const ServiceError = require("./ServiceError");
const TProxy = {};
const ProjectHome = __dirname.slice(0, -8);

const ServiceMap = {
    10000001 : "TPlay/service/CreateLiveService"
};

/**
 * Routing and execute a task service
 * @param requestBean Must have a serviceCode
 * @param callBack Called when the service returns : callBack(responseBean, errorMsg).
 */
TProxy.execTaskService = function(requestEntity, callBack) {
    var error = 0;
    var service = null;
    do{
        if(!(requestEntity && requestEntity.serviceCode)) {
            error = ServiceError.InvalidServiceCode;
            break;
        }
        var serviceDir = ServiceMap[requestEntity.serviceCode];
        if(!serviceDir) {
            error = ServiceError.InvalidServiceCode;
            break;
        }
        var servicePath = Path.join(ProjectHome, serviceDir);
        try{
            var ServiceType = require(servicePath);
            service = new ServiceType();
        } catch(e) {}
        if(!service || !service.receive || !service.on) {
            error = ServiceError.ServiceNotFount;
            break;
        }
        service.on("finish", function(responseBean, errorMessage){
            callBack(responseBean, errorMessage);
        });
    }while(false);
    if(error != 0) {
        callBack(null, {result:error});
    } else {
        service.receive(requestEntity.requestBean);
    }
};

/**
 * Routing and execute a communication service
 * @param requestEntity
 * @param connection The current using connection
 */
TProxy.execCommunicationService = function(requestEntity, connection) {
    var error = 0;
    do{ //while false
        if(!(requestEntity && requestEntity.serviceCode)) {
            error = ServiceError.InvalidServiceCode;
            break;
        }
        var comm = CommunicationManager.get(requestEntity.serviceCode);
        if(!comm) {
            error = ServiceError.ServiceNotFount;
            break;
        }
        comm.updateConnection(connection);
        comm.receive(requestEntity.requestBody);
    }while(false);
};

module.exports = TProxy;
