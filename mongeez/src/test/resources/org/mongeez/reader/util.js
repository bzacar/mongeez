//mongeez formatted javascript
//changeset bzacar:Util-ChangeSet
function addUUIDIdsAndInsert(collection, documents) {
    for (let i = 0; i < documents.length; i++) {
        documents[i]["_id"] = UUID();
    }
    db.getCollection(collection).insertMany(documents);
}
