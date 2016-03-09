const FormatUtil = require("../../../TCommon/util/FormatUtil");
function LiveCommResponseModel(isBroadcast, livePosMill, liveState) {
    this.isBroadcast = isBroadcast;
    this.livePosMill = livePosMill;
    this.liveState = liveState;
}
LiveCommResponseModel.prototype.parseResponse = function() {
    return FormatUtil.fixed8ToByte(this.isBroadcast) +
        FormatUtil.fixed32ToByte(this.livePosMill) +
        FormatUtil.fixed8ToByte(this.liveState);
};

module.exports = LiveCommResponseModel;
