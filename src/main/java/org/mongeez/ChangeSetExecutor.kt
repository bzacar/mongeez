/*
 * Copyright 2011 SecondMarket Labs, LLC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at  http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */

package org.mongeez

import com.mongodb.Mongo
import org.mongeez.commands.ChangeSet
import org.mongeez.dao.MongeezDao
import org.slf4j.LoggerFactory

class ChangeSetExecutor(mongo: Mongo,
                        dbName: String,
                        private val context: String?,
                        auth: MongoAuth? = null,
                        useMongoShell: Boolean = false) {
    private val logger = LoggerFactory.getLogger(ChangeSetExecutor::class.java)

    private val dao: MongeezDao = MongeezDao(mongo, dbName, auth, useMongoShell)

    fun execute(changeSets: List<ChangeSet>) {
        changeSets.forEach { changeSet ->
            if (changeSet.canBeAppliedInContext(context)) {
                if (changeSet.isRunAlways || !dao.wasExecuted(changeSet)) {
                    execute(changeSet)
                    logger.info("ChangeSet " + changeSet.changeId + " has been executed")
                } else {
                    logger.info("ChangeSet already executed: " + changeSet.changeId)
                }
            } else {
                logger.info("Not executing Changeset {} it cannot run in the context {}", changeSet.changeId, context)
            }
        }
    }

    private fun execute(changeSet: ChangeSet) {
        try {
            changeSet.getCommands().forEach {
                it.run(dao)
            }
        } catch (e: RuntimeException) {
            if (changeSet.isFailOnError) {
                throw e
            } else {
                logger.warn("ChangeSet " + changeSet.changeId + " has failed, but failOnError is set to false", e.message)
            }
        }
        dao.logChangeSet(changeSet)
    }
}
