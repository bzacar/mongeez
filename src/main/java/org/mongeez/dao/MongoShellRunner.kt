package org.mongeez.dao

import com.mongodb.MongoCredential
import com.mongodb.ServerAddress

class MongoShellRunner
constructor(serverAddress: ServerAddress,
            private val databaseName: String,
            private val credential: MongoCredential? = null) {

    private val databaseHost: String = serverAddress.host
    private val databasePort: Int = serverAddress.port

    fun run(code: String) {
        val command = "mongo $databaseName --eval '$code' --host $databaseHost --port $databasePort ${credential.getCredentialParameters()}"
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
        fun MongoCredential?.getCredentialParameters(): String {
            return if (this == null) {
                ""
            } else {
                "-u $userName -p ${String(password)} --authenticationDatabase=$source"
            }
        }
    }
}
