const TCrypto = require("../../TCommon/security/TCrypto");
const ServiceError = require("../ServiceError");

function RequestEntity() {
    this.serviceType = 0;
    this.serviceCode = "";
    this.requestBean = {};
}

/**
 * @param cipherRequest Client request data.
 * @param outputCallBack function(RequestEntity, ServiceError): data output callback
 */
RequestEntity.parseRequest = function(cipherRequest, outputCallBack) {
    TCrypto.decipher(cipherRequest, function(requestSeq) {
        var entity = new RequestEntity();
        var requestBuffer = new Buffer(requestSeq);
        var spaceCtr = 0, lastSpace = -1;
        var type, code, beanSeq;
        var bufferLen = requestBuffer.byteLength;
        for(var i=0;i<bufferLen;i++) {
            var c = requestBuffer[i];
            if(c === 32) {
                if(spaceCtr == 0) {
                    type = requestBuffer.slice(lastSpace+1, i).toString();
                } else if(spaceCtr == 1) {
                    code = requestBuffer.slice(lastSpace+1, i).toString();
                    if(i < bufferLen) {
                        beanSeq = requestBuffer.slice(i+1).toString();
                    }
                    break;
                }
                lastSpace = i;
                spaceCtr++;
            }
        }
        var beanObj = null;
        try{
            type = parseInt(type);
            beanObj = JSON.parse(beanSeq);
        }catch(e){}
        if(!type || !code || !beanObj) {
            outputCallBack(null, ServiceError.InvalidRequest);
            return ;
        }
        entity.serviceType = type;
        entity.serviceCode = code;
        entity.requestBean = beanObj;
        outputCallBack(entity, ServiceError.Null);
    });
};

module.exports = RequestEntity;
