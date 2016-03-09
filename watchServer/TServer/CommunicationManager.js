const CommunicationManager = new Object();

CommunicationManager._commMap = new Object();

CommunicationManager.put = function(comm/*Communication*/) {
    this._commMap[comm._id] = comm;
};

CommunicationManager.get = function(id) {
    return this._commMap[id];
};

CommunicationManager.remove = function(id) {
    delete this._commMap[id];
};

module.exports = CommunicationManager;
