const TService = require("../../TServer/TService");
const CreateLiveResponseBean = require("../model/CreateLiveResponseBean");
const Util = require("util");
function CreateLiveService() {
    TService.call(this);
}
Util.inherits(CreateLiveService, TService);
CreateLiveService.prototype.onReceive = function(requestBean, outputCallBack) {
    var responeBean = new CreateLiveResponseBean();
    console.log(requestBean.fileCode);
    responeBean.result = 1001;
    outputCallBack(responeBean);
};
module.exports = CreateLiveService;
