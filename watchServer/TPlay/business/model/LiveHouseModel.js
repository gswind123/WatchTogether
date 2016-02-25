function LiveHouseModel(name,fileSig) {
    this._id = Date.now();
    this._name = name;
    this._hostList = new Object();
    this._audienceList = new Object();
    this._fileSig = fileSig;
}
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
