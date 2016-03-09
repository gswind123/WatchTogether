const Util = require("util");
const TTaskService = require("../../TServer/TTaskService");
const CommunicationManager = require("../../TServer/CommunicationManager");
const LiveCommunication = require("./LiveCommunication");
const StartLiveCommRequestBean = require("./model/StartLiveCommRequestBean");
const StartLiveCommResponseBean = require("./model/StartLiveCommResponseBean");
const LiveHouseManager = require("../business/LiveHouseManager");
const AudienceManager = require("../business/AudienceManager");
const AudienceModel = require("../business/model/AudienceModel");

function StartLiveCommService() {
    TTaskService.call(this);
}
Util.inherits(StartLiveCommService, TTaskService);

StartLiveCommService.prototype.onReceive = function(clientModel, outputCallBack) {
    var responseBean = new StartLiveCommResponseBean();
    var requestBean = new StartLiveCommRequestBean();
    var errorMessage = requestBean.fillBean(clientModel);
    do{ //while false
        if(errorMessage) {
            responseBean.result = 1;
            responseBean.errorMessage = errorMessage;
            break;
        }
        var uid = requestBean.localMac;
        var liveId = requestBean.liveId;
        var fileSig = requestBean.fileSignature;
        if(!uid) {
            responseBean.result = 1;
            responseBean.errorMessage = "用户名不能为空";
            break;
        }

        var user = AudienceManager.getAudienceById(uid);
        if(!user) {
            user = new AudienceModel();
            AudienceManager.addAudience(user);
        }

        var liveHouse = LiveHouseManager.getLiveHouseById(liveId);
        if(!liveHouse) {
            responseBean.result = 1;
            responseBean.errorMessage = "指定的直播不存在";
            break;
        }

        var comm;
        if(user._communication && (comm = CommunicationManager.get(user._communication._id))) {
            //comm assigned
        } else {
            comm = new LiveCommunication(user._id);
            CommunicationManager.put(comm);
        }

        if(liveHouse.isHost(user)) {
            comm._isHost = true;
        } else if(!liveHouse.isAudience(user)) {
            liveHouse.addAudience(user);
        }
        comm._userId = user._id;
        comm._liveId = liveHouse._id;
        user._communication = comm;

        if(liveHouse._fileSig === requestBean.fileSignature) {
            responseBean.result = 0;
        } else {
            responseBean.result = 2;
            responseBean.errorMessage = "本地文件与直播文件不一致";
        }

        responseBean.commCode = comm._id;
    }while(false);
    outputCallBack(responseBean);
};

module.exports = StartLiveCommService;
