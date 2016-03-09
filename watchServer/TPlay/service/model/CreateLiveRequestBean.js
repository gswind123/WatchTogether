const RequestBean = require("../../../TServer/model/RequestBean");
const Util = require("util");

function CreateLiveRequestBean() {
    this.localMac = "";
    this.liveName = "";
    this.fileSignature = "";
}

Util.inherits(CreateLiveRequestBean, RequestBean);
module.exports = CreateLiveRequestBean;
