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
import org.mongeez.dao.factory.DATE_FIELD_NAME
import org.mongeez.dao.factory.TYPE_FIELD_NAME
import org.mongeez.dao.shell.MongoShellRunner
import org.mongeez.dao.shell.ShellException
import java.util.Date

class MongeezDao
internal constructor(private val db: MongoDatabase,
                     private val mongoShellRunner: MongoShellRunner,
                     private val changeSetAttributes: List<ChangeSetAttribute>,
                     private val useMongoShell: Boolean) {
    private val mongeezCollection: MongoCollection<Document>
        get() = db.getCollection(MONGEEZ_COLLECTION_NAME)

    fun wasExecuted(changeSet: ChangeSet): Boolean {
        val query = Document(TYPE_FIELD_NAME, RecordType.CHANGE_SET_EXECUTION.dbVal)
        for (attribute in changeSetAttributes) {
            query.append(attribute.dbFieldName, attribute.getAttributeValue(changeSet))
        }
        return mongeezCollection.countDocuments(query) > 0
    }

    fun runScript(code: String, util: String) {
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
        val dbObject = Document(TYPE_FIELD_NAME, RecordType.CHANGE_SET_EXECUTION.dbVal)
        for (attribute in changeSetAttributes) {
            dbObject.append(attribute.dbFieldName, attribute.getAttributeValue(changeSet))
        }
        dbObject.append(DATE_FIELD_NAME, Date())
        mongeezCollection.insertOne(dbObject)
    }

    fun getLastExecutedChangeSet(): ChangeSet? {
        return mongeezCollection
                .find(exists(DATE_FIELD_NAME))
                .sort(descending(DATE_FIELD_NAME))
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

    internal companion object {
        const val MONGEEZ_COLLECTION_NAME = "mongeez"
        private fun String.getTheCode(code: String): String {
            return if (this.isEmpty()) {
                code
            } else {
                "$this\n$code"
            }
        }
    }
}
