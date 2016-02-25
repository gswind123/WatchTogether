AudienceManager = new Object();

const AudienceAliveTimeout = 20000; //ms

AudienceManager._audienceMap = new Object();

AudienceManager.addAudience = function(audience/*AudienceModel*/) {
    this._audienceMap[audience._uid] = audience;
};

AudienceManager.removeAudience = function(audience/*AudienceModel*/) {
    delete this._audienceMap[audience._uid]
};

AudienceManager.getAudienceById = function(uid/*AudienceModel*/) {
    return this._audienceMap[uid];
};

module.exports = AudienceManager;
