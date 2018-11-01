package org.mongeez.cli

import com.mongodb.ServerAddress
import org.mongeez.Mongeez
import org.springframework.core.io.FileSystemResource

internal object MongeezRunner {
    fun run(arguments: Arguments) {
        val mongeez = Mongeez().apply {
            setFile(FileSystemResource(arguments.changeSetListFile))
            setServerAddress(ServerAddress(arguments.hostAddress, arguments.port))
            setDbName(arguments.databaseName)
            setUseMongoShell(arguments.useMongoShell)
            arguments.getMongoAuth()?.also { setAuth(it) }
            arguments.context?.also { setContext(it) }
        }
        if (arguments.dryRun) {
            mongeez.executeDryRun()
        } else {
            mongeez.process()
        }
    }
}
