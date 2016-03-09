const FormatUtil = new Object();

FormatUtil.byteToFixed32 = function(buffer) {
    var res = 0;
    for(var i=0;i<buffer.byteLength;i++) {
        res <<= 8;
        res += buffer[i];
    }
    return res;
};

FormatUtil.fixed32ToByte = function(fixed32) {
    var buffer = new Buffer(4);
    buffer[0] = ((0xff000000&fixed32)>>24);
    buffer[1] = ((0x00ff0000&fixed32)>>16);
    buffer[2] = ((0x0000ff00&fixed32)>>8);
    buffer[3] = ((0x000000ff&fixed32));
    return buffer.toString("hex");
};

FormatUtil.fixed8ToByte = function(fixed8) {
    var res = (fixed8&0xff).toString(16);
    if(res.length == 1) {
        res = "0" + res;
    }
    return res;
};

module.exports = FormatUtil;
