//mongeez formatted javascript
//changeset bzacar:Util-ChangeSet1
var Util = {
    stringToASCIISum: function (value) {
        return value
            .split("")
            .map(function (char) {
                return char.charCodeAt(0);
            })
            .reduce(function (curr, prev) {
                return prev + curr;
            });
    }
};
