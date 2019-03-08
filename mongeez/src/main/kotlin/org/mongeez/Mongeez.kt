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
import org.mongeez.data.ChangeSetAndUtilPairProvider
import org.mongeez.data.DryRunResult
import org.mongeez.reader.ChangeSetFileProvider
import org.mongeez.reader.FilesetXMLChangeSetFileProvider
import org.mongeez.validation.ChangeSetsValidator
import org.springframework.core.io.Resource

class Mongeez {
    private lateinit var serverAddress: ServerAddress
    private lateinit var dbName: String
    private var auth: MongoAuth? = null
    private val changeSetAndUtilPairProvider = ChangeSetAndUtilPairProvider()
    private var context: String? = null
    private var useMongoShell = false

    fun process() {
        val changeSets = changeSetAndUtilPairProvider.get()
        getChangeSetsExecutor().execute(changeSets)
    }

    fun dryRun(): DryRunResult {
        val changeSets = changeSetAndUtilPairProvider.get()
        return getChangeSetsExecutor().getExecutables(changeSets)
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
        this.changeSetAndUtilPairProvider.changeSetsValidator = changeSetsValidator
    }

    /**
     * Convenience method to set the ChangeSetFileProvider to an XML fileset based on the specified file
     */
    fun setFile(file: Resource) {
        setChangeSetFileProvider(FilesetXMLChangeSetFileProvider(file))
    }

    fun setChangeSetFileProvider(changeSetFileProvider: ChangeSetFileProvider) {
        this.changeSetAndUtilPairProvider.changeSetFileProvider = changeSetFileProvider
    }

    fun setContext(context: String) {
        this.context = context
    }

    fun setUseMongoShell(useMongoShell: Boolean) {
        this.useMongoShell = useMongoShell
    }

    private fun getChangeSetsExecutor(): ChangeSetsExecutor {
        val changeSetExecutor = ChangeSetExecutor(serverAddress, dbName, auth, useMongoShell)
        return ChangeSetsExecutor(context, changeSetExecutor)
    }
}
