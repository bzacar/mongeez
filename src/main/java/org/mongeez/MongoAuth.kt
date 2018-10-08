package org.mongeez

data class MongoAuth(val username: String, val password: String, val authDb: String?)
