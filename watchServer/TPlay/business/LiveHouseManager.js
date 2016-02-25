LiveHouseManager = new Object();

LiveHouseManager._liveHouseMap = new Object();

/**
 * Register a live house so that it can be updated
 * @param liveHouse LiveHouseModel
 */
LiveHouseManager.registerLiveHouse = function(liveHouse) {
    this._liveHouseMap[liveHouse._id] = liveHouse;
};

/**
 * Remove a live house from registered list
 * @param liveHouse LiveHouseModel
 */
LiveHouseManager.unregisterLiveHouse = function(liveHouse) {
    delete this._liveHouseMap[liveHouse._id];
};

LiveHouseManager.getLiveHouseById = function(id) {
    return this._liveHouseMap[id];
};

module.exports = LiveHouseManager;
