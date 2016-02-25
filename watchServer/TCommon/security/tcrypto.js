const Crypto = require("crypto");
var TCrypto = {};

const SecretKey = "windning";
const AlgorithmName = "des-ecb";
const BlockSize = 8;
var textCharset = "utf-8";

function addPadding(rawData) {
    if(!rawData) {
        return "";
    }
    var buffer = new Buffer(rawData, textCharset);
    var paddingNum = BlockSize - (buffer.byteLength%BlockSize);
    if(paddingNum == 0) {
        paddingNum = BlockSize;
    }
    var paddings = new Buffer(paddingNum);
    for(var i=0;i<paddings.byteLength;i++) {
        paddings[i] = paddingNum;
    }
    var paddingBuffer = Buffer.concat([buffer, paddings]);
    return paddingBuffer.toString(textCharset);
}
function stripPadding(rawData) {
    if(!rawData) {
        return "";
    }
    var buffer = new Buffer(rawData);
    var paddingNum = buffer[buffer.byteLength - 1];
    if(paddingNum > BlockSize || paddingNum > buffer.byteLength) {
        return rawData;
    }
    var nonpadBuffer = buffer.slice(0, buffer.byteLength - paddingNum);
    return nonpadBuffer.toString(textCharset);
}

TCrypto.cipher = function(plainText, callback) {
    var ciphered = new Array();
    var cipher = Crypto.createCipheriv(AlgorithmName, new Buffer(SecretKey), new Buffer(0));
    cipher.setAutoPadding(false);
    cipher.on("readable", function(){
        var data = cipher.read();
        if(data) {
            ciphered.push(data);
        }
    });
    cipher.on("end", function() {
        callback(Buffer.concat(ciphered).toString("hex"));
    });
    plainText = addPadding(plainText);
    try{
        cipher.write(plainText, textCharset);
    }catch(e){
        callback("")
    }
    cipher.end();
};

TCrypto.decipher = function(cipherCode, callback) {
    var deciphered = new Array();;
    var decipher = Crypto.createDecipheriv(AlgorithmName, new Buffer(SecretKey), new Buffer(0));
    decipher.setAutoPadding(false);
    decipher.on("readable", function(){
        var data = decipher.read();
        if(data) {
            deciphered.push(data);
        }
    });
    decipher.on("end", function(){
        var decipheredSeq = stripPadding(Buffer.concat(deciphered));
        callback(decipheredSeq);
    });
    try{
        decipher.write(cipherCode, "hex");
    } catch(e) {
        callback("");
    }
    decipher.end();
};

module.exports = TCrypto;
