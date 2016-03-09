const UserModel = require("./UserModel");
const Util = require("util");

function AudienceModel(uid, playTimeMills, playState) {
    UserModel.call(this, uid);
    this._liveHouseId = -1;
    this._playTimeMills = playTimeMills;
    this._playState = playState;
    this._communication = null;
}
Util.inherits(AudienceModel, UserModel);

module.exports = AudienceModel;
