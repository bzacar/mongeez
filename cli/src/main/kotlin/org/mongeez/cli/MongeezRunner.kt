package org.mongeez.cli

import com.mongodb.ServerAddress
import org.mongeez.Mongeez
import org.slf4j.LoggerFactory
import org.springframework.core.io.FileSystemResource

internal object MongeezRunner {
    private val LOGGER = LoggerFactory.getLogger(MongeezRunner::class.java)

    fun run(arguments: Arguments) {
        val mongeez = createMongeez(arguments)
        mongeez.run(arguments.dryRun)
    }

    private fun createMongeez(arguments: Arguments): Mongeez {
        return try {
            Mongeez().apply {
                setFile(FileSystemResource(arguments.changeSetListFile))
                setServerAddress(ServerAddress(arguments.hostAddress, arguments.port))
                setDbName(arguments.databaseName)
                setUseMongoShell(arguments.useMongoShell)
                arguments.getMongoAuth()?.also { setAuth(it) }
                arguments.context?.also { setContext(it) }
            }
        } catch (ex: RuntimeException) {
            LOGGER.error(ex.message)
            System.exit(-2)
            //This line will never be reached but was necessary for return type
            throw ex
        }
    }

    private fun Mongeez.run(isDryRun: Boolean) {
        try {
            if (isDryRun) {
                dryRun().print { println(it) }
            } else {
                process()
            }
        } catch (ex: RuntimeException) {
            LOGGER.error(ex.message)
            System.exit(-3)
        }
    }
}
