const ServiceError = require("../ServiceError");
function RequestBean() {}

/**
 * @param clientModel RequestBean from client
 * @return String errorMessage
 */
RequestBean.prototype.fillBean = function(clientModel) {
    var errorMessage = "";
    for(var key in this) {
        if(typeof(this[key]) === "function") {
            continue;
        }
        if(typeof(clientModel[key]) === "undefined") {
            errorMessage = "请求字段 " + key.toString() + " 缺失";
            break;
        }
        this[key] = clientModel[key];
    }
    return errorMessage;
};

module.exports = RequestBean;
