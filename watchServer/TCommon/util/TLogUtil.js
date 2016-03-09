const TLogUtil = new Object();

const ShowLog = true;

TLogUtil.log = function(message) {
    if(ShowLog) {
        console.log(message);
    }
};

module.exports = TLogUtil;
