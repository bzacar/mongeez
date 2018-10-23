package org.mongeez.dao.factory

import com.mongodb.client.MongoCollection
import com.mongodb.client.model.Filters.exists
import com.mongodb.client.model.Updates.set
import org.bson.Document
import org.mongeez.dao.RecordType

object MongeezDaoRecordTypeConfigurer {
    fun addTypeToUntypedRecords(mongeezCollection: MongoCollection<Document>) {
        val query = exists("type", false)
        val update = set("type", RecordType.CHANGE_SET_EXECUTION.dbVal)
        mongeezCollection.updateMany(query, update)
    }
}
