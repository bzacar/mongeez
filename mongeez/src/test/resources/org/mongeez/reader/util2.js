//mongeez formatted javascript
//changeset bzacar:Util-ChangeSet2
function addNuanceAndInsert(collection, documents) {
    for (let i = 0; i < documents.length; i++) {
        documents[i]["nuance"] = Util
            .stringToASCIISum(documents[i]["name"] + documents[i]["surname"])
    }
    db.getCollection(collection).insertMany(documents);
}
