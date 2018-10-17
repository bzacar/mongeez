package org.mongeez.dao.factory

import com.mongodb.client.MongoCollection
import com.mongodb.client.model.Filters.eq
import org.bson.Document
import org.mongeez.dao.ChangeSetAttribute
import org.mongeez.dao.RecordType


object MongeezDaoConfigurationRecordConfigurer {
    private val DEFAULT_CHANGE_SET_ATTRIBUTES = listOf(ChangeSetAttribute.FILE, ChangeSetAttribute.CHANGE_ID, ChangeSetAttribute.AUTHOR)

    fun getChangeSetAttributeList(mongeezCollection: MongoCollection<Document>): List<ChangeSetAttribute> {
        val query = eq("type", RecordType.CONFIGURATION.dbVal)
        val configRecord = mongeezCollection.find(query).first() ?: createNewConfigRecord(mongeezCollection)
        val supportResourcePath = configRecord.getBoolean("supportResourcePath")
        return if (supportResourcePath) {
            DEFAULT_CHANGE_SET_ATTRIBUTES + ChangeSetAttribute.RESOURCE_PATH
        } else {
            DEFAULT_CHANGE_SET_ATTRIBUTES
        }
    }

    private fun createNewConfigRecord(mongeezCollection: MongoCollection<Document>): Document {
        val configRecord = if (mongeezCollection.countDocuments() > 0L) {
            // We have pre-existing records, so don't assume that they support the latest features
            Document()
                    .append("type", RecordType.CONFIGURATION.dbVal)
                    .append("supportResourcePath", false)
        } else {
            Document()
                    .append("type", RecordType.CONFIGURATION.dbVal)
                    .append("supportResourcePath", true)
        }
        mongeezCollection.insertOne(configRecord)
        return configRecord
    }
}
