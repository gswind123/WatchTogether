const Util = require("util");
const TTaskService = require("../../TServer/TTaskService");
const CreateLiveResponseBean = require("./model/CreateLiveResponseBean");
const LiveHouseModel = require("../business/model/LiveHouseModel");
const AudienceModel = require("../business/model/AudienceModel");
const AudienceManager = require("../business/AudienceManager");
const LiveHouseManager = require("../business/LiveHouseManager");

function CreateLiveService() {
    TService.call(this);
}
Util.inherits(CreateLiveService, TTaskService);

CreateLiveService.prototype.onReceive = function(requestBean, outputCallBack) {
    var responeBean = new CreateLiveResponseBean();
    do{ //whie false
        var fileSig = requestBean.fileSignature;
        var liveName = requestBean.liveName;
        var uid = requestBean.localMac;
        if(!liveName) {
            responeBean.result = 1;
            responeBean.errorMessage = "直播名不能为空";
            break;
        }
        if(!fileSig) {
            responeBean.result = 1;
            responeBean.errorMessage = "直播文件不能为空";
            break;
        }
        if(!uid) {
            responeBean.result = 1;
            responeBean.errorMessage = "主机名不能为空";
            break;
        }
        var audience = AudienceManager.getAudienceById(uid);
        var liveHouse = null;
        if(audience) {
            var prevLiveHouse = LiveHouseManager.getLiveHouseById(audience._liveHouseId);
            if(prevLiveHouse != null) {
                if(prevLiveHouse._fileSig === fileSig && prevLiveHouse.isHost(audience)) {
                    liveHouse = prevLiveHouse;
                } else {
                    prevLiveHouse.removeHost(audience);
                    prevLiveHouse.removeAudience(audience);
                }
            }
        } else {
            audience = new AudienceModel(uid);
            AudienceManager.addAudience(audience);
        }
        if(!liveHouse) {
            liveHouse = new LiveHouseModel(liveName, fileSig);
            liveHouse.addHost(audience);
            LiveHouseManager.registerLiveHouse(liveHouse);
        }
        responeBean.liveId = liveHouse._id.toString();
    }while(false);
    outputCallBack(responeBean);
};

module.exports = CreateLiveService;
