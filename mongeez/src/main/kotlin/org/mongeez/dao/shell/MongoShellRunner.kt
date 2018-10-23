package org.mongeez.dao.shell

import com.mongodb.MongoCredential
import com.mongodb.ServerAddress
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
        fun MongoCredential?.getCredentialParameters(): String {
            return this
                    ?.let { "-u $userName -p ${String(password)} --authenticationDatabase=$source" }
                    ?: ""
        }

        fun Process.throwShellExceptionIfNecessary() {
            val errorMessage = BufferedReader(InputStreamReader(errorStream)).lines().toList().joinToString("\n")
            val stdOutput = BufferedReader(InputStreamReader(inputStream)).lines().toList().joinToString("\n")
            if (errorMessage.isNotEmpty()) throw ShellException(errorMessage)
            if (stdOutput.contains(Pattern.compile(".*ReferenceError.*", CASE_INSENSITIVE).toRegex())) throw ShellException(stdOutput)
        }
    }
}
