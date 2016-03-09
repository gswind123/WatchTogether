const Process = require("process");
const Generator = {};

Generator.generateId = function() {
    return Process.pid.toString() + Date.now();
};

module.exports = Generator;
