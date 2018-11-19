package org.mongeez

import com.mongodb.MongoCredential
import java.util.Arrays

data class MongoAuth(val username: String, val password: CharArray, val authDb: String?) {
    fun getCredential(databaseName: String): MongoCredential {
        return if (authDb == null || authDb == databaseName) {
            MongoCredential.createCredential(username, databaseName, password)
        } else {
            MongoCredential.createCredential(username, authDb, password)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        if (other !is MongoAuth) return false

        if (username != other.username) return false
        if (!Arrays.equals(password, other.password)) return false
        if (authDb != other.authDb) return false

        return true
    }

    override fun hashCode(): Int {
        var result = username.hashCode()
        result = 31 * result + Arrays.hashCode(password)
        result = 31 * result + (authDb?.hashCode() ?: 0)
        return result
    }
}
