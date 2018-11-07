//mongeez formatted javascript
//changeset bzacar:Util-ChangeSet
function addNuanceAndInsert(collection, documents) {
    for (let i = 0; i < documents.length; i++) {
        documents[i]["nuance"] = Util
            .stringToASCIISum(documents[i]["name"] + documents[i]["surname"])
    }
    db.getCollection(collection).insertMany(documents);
}

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
