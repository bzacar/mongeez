package org.mongeez.dao.shell

import com.mongodb.MongoCredential
import com.mongodb.ServerAddress
import org.slf4j.LoggerFactory
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.regex.Pattern
import java.util.regex.Pattern.CASE_INSENSITIVE
import kotlin.streams.toList

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
        val p: Process = Runtime.getRuntime().exec(arrayOf("bash", "-c", command))
        p.waitFor()
        p.throwShellExceptionIfNecessary()
    }

    private companion object {
        private val LOGGER = LoggerFactory.getLogger(MongoShellRunner::class.java)

        fun MongoCredential?.getCredentialParameters(): String {
            return this
                    ?.let { "-u $userName -p ${String(password)} --authenticationDatabase=$source" }
                    ?: ""
        }

        fun Process.throwShellExceptionIfNecessary() {
            val errorMessage = BufferedReader(InputStreamReader(errorStream)).lines().toList().joinToString("\n")
            val stdOutput = BufferedReader(InputStreamReader(inputStream)).lines().toList().joinToString("\n")
            LOGGER.debug("Error message from mongo shell: $errorMessage")
            LOGGER.debug("Standard output of mongo shell: $stdOutput")
            if (errorMessage.isNotEmpty()) throw ShellException(errorMessage)
            if (stdOutput.contains(Pattern.compile(".*ReferenceError.*", CASE_INSENSITIVE).toRegex())) throw ShellException(stdOutput)
        }
    }
}
