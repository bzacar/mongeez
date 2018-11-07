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

package org.mongeez.dao

import com.mongodb.MongoCommandException
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Filters.exists
import com.mongodb.client.model.Sorts.descending
import org.bson.Document
import org.mongeez.commands.ChangeSet
import org.mongeez.dao.shell.MongoShellRunner
import org.mongeez.dao.shell.ShellException
import java.util.Date

class MongeezDao
internal constructor(private val db: MongoDatabase,
                     private val mongoShellRunner: MongoShellRunner,
                     private val changeSetAttributes: List<ChangeSetAttribute>,
                     private val useMongoShell: Boolean) {
    private val mongeezCollection: MongoCollection<Document>
        get() = db.getCollection("mongeez")

    fun wasExecuted(changeSet: ChangeSet): Boolean {
        val query = Document("type", RecordType.CHANGE_SET_EXECUTION.dbVal)
        for (attribute in changeSetAttributes) {
            query.append(attribute.dbFieldName, attribute.getAttributeValue(changeSet))
        }
        return mongeezCollection.countDocuments(query) > 0
    }

    fun runScript(code: String, util: String?) {
        val theCode = util.getTheCode(code)
        try {
            if (useMongoShell) {
                mongoShellRunner.run(theCode)
            } else {
                val command = Document("eval", theCode)
                db.runCommand(command)
            }
        } catch (ex: RuntimeException) {
            if (ex is MongoCommandException || ex is ShellException) {
                throw MongeezDaoException(ex)
            } else {
                throw ex
            }
        }
    }

    fun logChangeSet(changeSet: ChangeSet) {
        val dbObject = Document("type", RecordType.CHANGE_SET_EXECUTION.dbVal)
        for (attribute in changeSetAttributes) {
            dbObject.append(attribute.dbFieldName, attribute.getAttributeValue(changeSet))
        }
        dbObject.append("date", Date())
        mongeezCollection.insertOne(dbObject)
    }

    fun getLastExecutedChangeSet(): ChangeSet? {
        return mongeezCollection
                .find(exists("date"))
                .sort(descending("date"))
                .firstOrNull()
                ?.let { changeSetDocument ->
                    ChangeSet().apply {
                        changeId = changeSetDocument.getString(ChangeSetAttribute.CHANGE_ID.dbFieldName)
                        author = changeSetDocument.getString(ChangeSetAttribute.AUTHOR.dbFieldName)
                        file = changeSetDocument.getString(ChangeSetAttribute.FILE.dbFieldName)
                        resourcePath = changeSetDocument[ChangeSetAttribute.RESOURCE_PATH.dbFieldName] as? String?
                    }
                }
    }

    private companion object {
        fun String?.getTheCode(code: String): String {
            return if (this == null) {
                code
            } else {
                "$this\n$code"
            }
        }
    }
}
