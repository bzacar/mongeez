package org.mongeez.dao.factory

import com.mongodb.client.MongoCollection
import com.mongodb.client.model.Filters.exists
import com.mongodb.client.model.Updates.set
import org.bson.Document
import org.mongeez.dao.RecordType

object MongeezDaoRecordTypeConfigurer {
    fun addTypeToUntypedRecords(mongeezCollection: MongoCollection<Document>) {
        val query = exists(TYPE_FIELD_NAME, false)
        val update = set(TYPE_FIELD_NAME, RecordType.CHANGE_SET_EXECUTION.dbVal)
        mongeezCollection.updateMany(query, update)
    }
}
