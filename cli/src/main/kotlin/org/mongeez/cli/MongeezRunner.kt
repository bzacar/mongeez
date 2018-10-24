package org.mongeez.cli

import com.mongodb.ServerAddress
import org.mongeez.Mongeez
import org.mongeez.MongoAuth
import org.springframework.core.io.FileSystemResource

internal object MongeezRunner {
    private val console = System.console()

    fun run(arguments: Arguments) {
        Mongeez().apply {
            setFile(FileSystemResource(arguments.changeSetListFile))
            setServerAddress(ServerAddress(arguments.hostAddress, arguments.port))
            setDbName(arguments.databaseName)
            setUseMongoShell(arguments.useMongoShell)
            if (arguments.authenticationEnabled) {
                print("Please enter user name: ")
                val userName = console.readLine().trim()
                print("Please enter password:  ")
                val password = console.readPassword()
                setAuth(MongoAuth(userName, password, arguments.authenticationDatabase))
            }
            arguments.context?.also { setContext(it) }
        }.process()
    }
}
