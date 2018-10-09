package org.mongeez.dao

import com.mongodb.MongoCredential
import com.mongodb.ServerAddress

class MongoShellRunner
constructor(serverAddressList: List<ServerAddress>,
            private val databaseName: String,
            private val credentials: List<MongoCredential>) {

    private val databaseHost: String = serverAddressList[0].host

    fun run(code: String) {
        val command = "mongo $databaseName --eval '$code' --host $databaseHost ${credentials.getCredentialParameters()}"
        executeCommand(command)
    }

    private fun executeCommand(command: String) {
        try {
            val p: Process = Runtime.getRuntime().exec(arrayOf("bash", "-c", command))
            p.waitFor()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private companion object {
        fun List<MongoCredential>.getCredentialParameters(): String {
            return if (isEmpty()) {
                ""
            } else {
                "-u ${first().userName} -p ${String(first().password)} --authenticationDatabase=${first().source}"
            }
        }
    }
}
