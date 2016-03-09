const FormatUtil = require("../../../TCommon/util/FormatUtil");
function LiveCommRequestModel(){
    this.isBroadcast = 0;
    this.livePosMill = 0;
    this.liveState = 0;
}
LiveCommRequestModel.parseResponse = function(requestSeq) {
    var buffer = new Buffer(requestSeq, "hex");
    if(buffer.byteLength != 6) {
        return null;
    }
    var resModel = new LiveCommRequestModel();
    resModel.isBroadcast = buffer[0];
    resModel.livePosMill = FormatUtil.byteToFixed32(buffer.slice(1,5));
    resModel.liveState = buffer[5];
    return resModel;
};

module.exports = LiveCommRequestModel;
