const Path = require("path");
const TCrypto = require("../TCommon/security/TCrypto");
const ServiceError = require("./ServiceError");
const TProxy = {};
const ProjectHome = __dirname.slice(0, -8);

const ServiceMap = {
    10000001 : "TPlay/service/CreateLiveService"
};

/**
 * Routing and execute a service
 * @param requestBean Must have a serviceCode
 * @param callBack Called when the service returns : callBack(responseBean, errorMsg).
 */
TProxy.execService = function(requestEntity, callBack) {
    var received  =false;
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
        received = true;
    }while(false);
    if(error != 0) {
        callBack(null, {result:error});
        return ;
    }
    TCrypto.decipher(requestEntity.requestBean, function(deciphered) {
        var request = null;
        try{
            request = JSON.parse(deciphered);
        }catch(e) {}
        if(!request) {
            error = ServiceError.DeserilizeFailed;
            callBack(null, {result:error});
        } else {
            service.receive(request);
        }
    });
};

module.exports = TProxy;
