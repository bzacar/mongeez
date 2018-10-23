package org.mongeez.dao.factory

import com.mongodb.MongoClient
import com.mongodb.MongoClientOptions
import com.mongodb.ServerAddress
import com.mongodb.client.MongoDatabase
import org.mongeez.MongoAuth
import org.mongeez.dao.shell.MongoShellRunner

internal object MongeezDaoInitializer {
    fun getDbAndShellRunner(serverAddress: ServerAddress,
                            databaseName: String,
                            auth: MongoAuth?): Pair<MongoDatabase, MongoShellRunner> {
        val credential = auth?.getCredential(databaseName)
        val clientOptions = MongoClientOptions.builder().build()
        val mongoClient = credential
                ?.let { MongoClient(serverAddress, it, clientOptions) }
                ?: MongoClient(serverAddress, clientOptions)
        return mongoClient.getDatabase(databaseName) to
                MongoShellRunner(serverAddress, databaseName, credential)
    }
}
