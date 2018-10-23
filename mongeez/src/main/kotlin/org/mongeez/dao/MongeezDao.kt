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

import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import org.apache.commons.lang3.time.DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT
import org.bson.Document
import org.mongeez.commands.ChangeSet
import org.mongeez.dao.shell.MongoShellRunner

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
        if (useMongoShell) {
            mongoShellRunner.run(theCode)
        } else {
            val command = Document("eval", theCode)
            db.runCommand(command)
        }
    }

    fun logChangeSet(changeSet: ChangeSet) {
        val dbObject = Document("type", RecordType.CHANGE_SET_EXECUTION.dbVal)
        for (attribute in changeSetAttributes) {
            dbObject.append(attribute.dbFieldName, attribute.getAttributeValue(changeSet))
        }
        dbObject.append("date", ISO_DATETIME_TIME_ZONE_FORMAT.format(System.currentTimeMillis()))
        mongeezCollection.insertOne(dbObject)
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
