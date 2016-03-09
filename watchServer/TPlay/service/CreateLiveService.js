const Util = require("util");
const TTaskService = require("../../TServer/TTaskService");
const CreateLiveRequestBean = require("./model/CreateLiveRequestBean");
const CreateLiveResponseBean = require("./model/CreateLiveResponseBean");
const LiveHouseModel = require("../business/model/LiveHouseModel");
const AudienceModel = require("../business/model/AudienceModel");
const AudienceManager = require("../business/AudienceManager");
const LiveHouseManager = require("../business/LiveHouseManager");

function CreateLiveService() {
    TTaskService.call(this);
}
Util.inherits(CreateLiveService, TTaskService);

CreateLiveService.prototype.onReceive = function(clientModel, outputCallBack) {
    var responseBean = new CreateLiveResponseBean();
    var requestBean = new CreateLiveRequestBean();
    var errorMessage = requestBean.fillBean(clientModel);
    do{ //whie false
        if(errorMessage) {
            responseBean.result = 1;
            responseBean.errorMessage = errorMessage;
            break;
        }
        var fileSig = requestBean.fileSignature;
        var liveName = requestBean.liveName;
        var uid = requestBean.localMac;
        if(!liveName) {
            responseBean.result = 1;
            responseBean.errorMessage = "直播名不能为空";
            break;
        }
        if(!fileSig) {
            responseBean.result = 1;
            responseBean.errorMessage = "直播文件不能为空";
            break;
        }
        if(!uid) {
            responseBean.result = 1;
            responseBean.errorMessage = "主机名不能为空";
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
        responseBean.liveId = liveHouse._id.toString();
    }while(false);
    outputCallBack(responseBean);
};

module.exports = CreateLiveService;
