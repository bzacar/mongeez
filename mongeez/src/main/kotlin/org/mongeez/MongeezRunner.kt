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
import org.mongeez.reader.ChangeSetFileProvider
import org.mongeez.validation.ChangeSetsValidator
import org.mongeez.validation.DefaultChangeSetsValidator
import org.springframework.beans.factory.InitializingBean
import org.springframework.core.io.Resource

/**
 * @author oleksii
 * @since 5/2/11
 */
class MongeezRunner : InitializingBean {
    var isExecuteEnabled = false
    private lateinit var serverAddress: ServerAddress
    lateinit var dbName: String
    private var file: Resource? = null

    private var userName: String? = null
    private var passWord: String? = null
    private var authDb: String? = null

    private var changeSetFileProvider: ChangeSetFileProvider? = null

    private var changeSetsValidator: ChangeSetsValidator? = null

    @Throws(Exception::class)
    override fun afterPropertiesSet() {
        if (isExecuteEnabled) {
            execute()
        }
    }

    fun execute() {
        val mongeez = Mongeez()
        mongeez.setServerAddress(serverAddress)
        mongeez.setDbName(dbName)

        mongeez.setChangeSetsValidator(changeSetsValidator ?: DefaultChangeSetsValidator())

        changeSetFileProvider
                ?.let { mongeez.setChangeSetFileProvider(it) }
                ?: file?.let { mongeez.setFile(it) }
                ?: throw IllegalStateException("Both change set file path and change set file provider cannot be null!")

        if (!userName.isNullOrEmpty() && !passWord.isNullOrEmpty()) {
            mongeez.setAuth(MongoAuth(userName.orEmpty(), passWord.orEmpty().toCharArray(), authDb))
        }

        mongeez.process()
    }

    fun setServerAddress(serverAddress: ServerAddress) {
        this.serverAddress = serverAddress
    }

    fun setFile(file: Resource) {
        this.file = file
    }

    fun setChangeSetFileProvider(changeSetFileProvider: ChangeSetFileProvider) {
        this.changeSetFileProvider = changeSetFileProvider
    }

    fun setUserName(userName: String) {
        this.userName = userName
    }

    fun setPassWord(passWord: String) {
        this.passWord = passWord
    }

    fun setAuthDb(authDb: String) {
        this.authDb = authDb
    }
}
