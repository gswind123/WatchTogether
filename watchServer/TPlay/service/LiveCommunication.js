const Communication = require("../../TServer/Communication");
const CommunicationManager = require("../../TServer/CommunicationManager");
const LiveCommRequestModel = require("./model/LiveCommRequestModel");
const LiveCommResponseModel = require("./model/LiveCommResponseModel");
const LiveHouseManager = require("../business/LiveHouseManager");
const AudienceManager = require("../business/AudienceManager");
const LiveState = require("../business/type/LiveState");
const Util = require("util");

const ClientSyncBound = 500; //ms

function LiveCommunication(uid) {
    Communication.call(this, uid);
    this._isHost = false;
    this._liveId = "";
}
Util.inherits(LiveCommunication, Communication);

function sendLiveData(isBroadcast,liveHouse, comm) {
    var responseModel = new LiveCommResponseModel(isBroadcast, liveHouse._livePosMill, liveHouse._liveState);
    comm.send(responseModel.parseResponse());
}

function doBroadcast(liveHouse, brdcstId) {
    var callList = liveHouse._audienceList.concat(liveHouse._hostList);
    for(var uid in callList) {
        if(uid === brdcstId) {
            continue;
        }
        var comm = AudienceManager.getAudienceById(uid);
        if(comm) {
            sendLiveData(1, liveHouse, comm);
        }
    }
}

LiveCommunication.prototype.onReceive = function(requestData) {
    var liveHouse;
    if(!this._userId || !this._liveId ||
        !(liveHouse = LiveHouseManager.getLiveHouseById(this._liveId))) {
        this._connection.close();
        this._connection = null;
        CommunicationManager.remove(this._id);
    }

    var requestModel = LiveCommRequestModel.parseResponse(requestData);

    if(this._isHost) {
        liveHouse.updateLive(requestModel.livePosMill, requestModel.liveState);
        if(requestModel.isBroadcast) {
            doBroadcast(liveHouse, this._userId);
        }
    } else {
        var fixDiff = 0;
        if(liveHouse._liveState === LiveState.Living) {
            fixDiff = Date.now() - liveHouse._lastUpdateTime;
        }
        var diff = Math.abs(fixDiff + liveHouse._livePosMill - curPos);
        if(diff > ClientSyncBound) {
            sendLiveData(0, liveHouse, this);
        }
    }
};

module.exports = LiveCommunication;
