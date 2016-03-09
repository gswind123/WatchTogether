const Util = require("util");
const RequestBean = require("../../../TServer/model/RequestBean");
function StartLiveCommRequestBean() {
    this.localMac = "";
    this.liveId = "";
    this.fileSignature = "";
}
Util.inherits(StartLiveCommRequestBean, RequestBean);

module.exports = StartLiveCommRequestBean;
