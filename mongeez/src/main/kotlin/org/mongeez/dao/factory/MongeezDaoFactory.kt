package org.mongeez.dao.factory

import com.mongodb.ServerAddress
import org.mongeez.MongoAuth
import org.mongeez.dao.MongeezDao
import org.mongeez.dao.MongeezDao.Companion.MONGEEZ_COLLECTION_NAME

internal object MongeezDaoFactory {
    fun create(serverAddress: ServerAddress,
               databaseName: String,
               auth: MongoAuth? = null,
               useMongoShell: Boolean = false): MongeezDao {
        val (db, mongoShellRunner) = MongeezDaoInitializer.getDbAndShellRunner(serverAddress, databaseName, auth)
        val changeSetAttributes = MongeezDaoConfigurer.configure(db.getCollection(MONGEEZ_COLLECTION_NAME))
        return MongeezDao(db, mongoShellRunner, changeSetAttributes, useMongoShell)
    }
}
