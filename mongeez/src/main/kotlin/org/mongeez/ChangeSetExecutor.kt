package org.mongeez

import com.mongodb.ServerAddress
import org.mongeez.ChangeSetsExecutor.Companion.logger
import org.mongeez.commands.ChangeSet
import org.mongeez.dao.MongeezDao
import org.mongeez.dao.factory.MongeezDaoFactory

internal class ChangeSetExecutor
constructor(serverAddress: ServerAddress,
            dbName: String,
            auth: MongoAuth? = null,
            useMongoShell: Boolean = false) {

    private val dao: MongeezDao = MongeezDaoFactory.create(serverAddress, dbName, auth, useMongoShell)

    fun execute(changeSet: ChangeSet, utilScript: String? = null) {
        try {
            changeSet.getMergedScript().run(dao, utilScript)
        } catch (e: RuntimeException) {
            if (changeSet.isFailOnError) {
                logger.error(e.message)
                throw e
            }
            logger.warn("ChangeSet " + changeSet.changeId + " has failed, but failOnError is set to false", e.message)
        }
        dao.logChangeSet(changeSet)
    }

    fun notExecuted(changeSet: ChangeSet) = !dao.wasExecuted(changeSet)
}
