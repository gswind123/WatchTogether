const Generator = require("../../../TCommon/util/Generator");
const LiveState = require("../type/LiveState");

function LiveHouseModel(name,fileSig) {
    this._id = Generator.generateId();
    this._name = name;
    this._hostList = new Object();
    this._audienceList = new Object();
    this._fileSig = fileSig;
    this._livePosMill = 0;
    this._liveState = LiveState.Pause;
    this._lastUpdateTime = Date.now();
}
LiveHouseModel.prototype.updateLive = function(curPos, curState) {
    this._livePosMill = curPos;
    if(curState == LiveState.Living) {
        this._liveState = LiveState.Living;
    } else if(curState == LiveState.Pause) {
        this._liveState = LiveState.Pause;
    }
    this._lastUpdateTime = Date.now();
};
LiveHouseModel.prototype.addAudience = function(audience/*AudienceModel*/) {
    this._audienceList[audience._uid] = audience;
    audience._liveHouseId = this._id;
};
LiveHouseModel.prototype.removeAudience = function(audience/*AudienceModel*/) {
    delete this._audienceList[audience._uid];
};
LiveHouseModel.prototype.addHost = function(host/*AudienceModel*/) {
    this._hostList[host._uid] = host;
    host._liveHouseId = this._id;
};
LiveHouseModel.prototype.removeHost = function(host/*AudienceModel*/) {
    delete this._hostList[host._uid];
};
LiveHouseModel.prototype.isHost = function(audience/*AudienceModel*/){
    return this._hostList[audience._uid];
};
LiveHouseModel.prototype.isAudience = function(audience/*AudienceModel*/){
    return this._audienceList[audience._uid];
};

module.exports = LiveHouseModel;
