const TCrypto = require("../../TCommon/security/TCrypto");
const ServiceType = require("../ServiceType");
const ServiceError = require("../ServiceError");

function ResponseEntity(type, code, error, bean, body){
    this.serviceType = type;
    this.serviceCode = code;
    this.serviceError = error;
    this.responseBean = bean;
    this.responseBody = body;
}

/**
 * @param outputCallBack function(responseSeq)
 */
ResponseEntity.prototype.parseResponse = function(outputCallBack) {
    var beanSeq = "";
    try {
        if(this.serviceType == ServiceType.TaskService) {
            beanSeq = JSON.stringify(this.responseBean);
        } else if(this.serviceType == ServiceType.CommunicationService) {
            beanSeq = this.responseBody;
        }
    } catch(e) {}
    if(!beanSeq) {
        this.serviceError = ServiceError.ServerRuntimeError;
    }
    var headBuffer = new Buffer(this.serviceType+" "+this.serviceCode+" "+this.serviceError+" ");
    var bodyBuffer = new Buffer(beanSeq);
    var uncipherSeq = Buffer.concat([headBuffer, bodyBuffer]).toString();
    TCrypto.cipher(uncipherSeq.toString(), function(ciphered){
        outputCallBack(ciphered);
    });
};

module.exports = ResponseEntity;
