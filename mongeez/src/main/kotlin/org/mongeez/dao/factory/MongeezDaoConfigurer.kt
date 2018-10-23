package org.mongeez.dao.factory

import com.mongodb.client.MongoCollection
import org.bson.Document
import org.mongeez.dao.ChangeSetAttribute

internal object MongeezDaoConfigurer {
    fun configure(mongeezCollection: MongoCollection<Document>): List<ChangeSetAttribute> {
        MongeezDaoRecordTypeConfigurer.addTypeToUntypedRecords(mongeezCollection)
        val changeSetAttributes = MongeezDaoConfigurationRecordConfigurer.getChangeSetAttributeList(mongeezCollection)
        MongeezDaoIndexConfigurer.configureIndexes(mongeezCollection, changeSetAttributes)
        return changeSetAttributes
    }
}
