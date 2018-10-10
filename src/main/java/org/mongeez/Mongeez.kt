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

import com.mongodb.ServerAddress
import org.mongeez.commands.ChangeSet
import org.mongeez.reader.ChangeSetFileProvider
import org.mongeez.reader.ChangeSetReaderFactory
import org.mongeez.reader.FilesetXMLChangeSetFileProvider
import org.mongeez.validation.ChangeSetsValidator
import org.mongeez.validation.DefaultChangeSetsValidator
import org.slf4j.LoggerFactory
import org.springframework.core.io.Resource

class Mongeez {
    private lateinit var serverAddress: ServerAddress
    private lateinit var dbName: String
    private var auth: MongoAuth? = null
    private lateinit var changeSetFileProvider: ChangeSetFileProvider
    private var changeSetsValidator: ChangeSetsValidator = DefaultChangeSetsValidator()
    private var context: String? = null
    private var useMongoShell = false

    private val changeSets: List<ChangeSet>
        get() {
            val readerFactory = ChangeSetReaderFactory.getInstance()
            return changeSetFileProvider.changeSetFiles.asSequence()
                    .mapNotNull { file ->
                        readerFactory.getChangeSetReader(file)?.getChangeSets(file)
                    }
                    .flatten().toList()
                    .also {
                        logChangeSets(it)
                        changeSetsValidator.validate(it)
                    }
        }

    fun process() {
        val changeSets = changeSets
        ChangeSetExecutor(serverAddress, dbName, context, auth, useMongoShell).execute(changeSets)
    }

    private fun logChangeSets(changeSets: List<ChangeSet>) {
        if (logger.isTraceEnabled) {
            changeSets.forEach { changeSet ->
                logger.trace("Changeset")
                logger.trace("id: " + changeSet.changeId)
                logger.trace("author: " + changeSet.author)
                if (changeSet.getContexts().isNotEmpty()) {
                    logger.trace("contexts: {}", changeSet.getContexts())
                }
                changeSet.getCommands().forEach { command ->
                    logger.trace("script")
                    logger.trace(command.body)
                }
            }
        }
    }

    fun setServerAddress(serverAddress: ServerAddress) {
        this.serverAddress = serverAddress
    }

    fun setDbName(dbName: String) {
        this.dbName = dbName
    }

    fun setAuth(auth: MongoAuth) {
        this.auth = auth
    }

    fun setChangeSetsValidator(changeSetsValidator: ChangeSetsValidator) {
        this.changeSetsValidator = changeSetsValidator
    }

    /**
     * Convenience method to set the ChangeSetFileProvider to an XML fileset based on the specified file
     */
    fun setFile(file: Resource) {
        setChangeSetFileProvider(FilesetXMLChangeSetFileProvider(file))
    }

    fun setChangeSetFileProvider(changeSetFileProvider: ChangeSetFileProvider) {
        this.changeSetFileProvider = changeSetFileProvider
    }

    fun setContext(context: String) {
        this.context = context
    }

    fun setUseMongoShell(useMongoShell: Boolean) {
        this.useMongoShell = useMongoShell
    }

    companion object {
        private val logger = LoggerFactory.getLogger(Mongeez::class.java)
    }

}
