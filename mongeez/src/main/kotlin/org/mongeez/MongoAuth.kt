package org.mongeez

import com.mongodb.MongoCredential

data class MongoAuth(val username: String, val password: String, val authDb: String?) {
    fun getCredential(databaseName: String): MongoCredential {
        return if (authDb == null || authDb == databaseName) {
            MongoCredential.createCredential(username, databaseName, password.toCharArray())
        } else {
            MongoCredential.createCredential(username, authDb, password.toCharArray())
        }
    }
}
