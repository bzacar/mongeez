package org.mongeez.cli

import com.beust.jcommander.JCommander
import com.beust.jcommander.ParameterException
import org.slf4j.Logger
import org.slf4j.LoggerFactory

internal class CommandLineArgumentsParser {
    private val arguments = Arguments()
    private val jCommander = JCommander.newBuilder()
            .addObject(arguments)
            .build()
            .apply { programName = PROGRAM_NAME }

    fun parse(commandLineArguments: Array<String>): Arguments {
        try {
            jCommander.parse(*commandLineArguments)
            ParametersFileDirectoryProvider.get(commandLineArguments)?.also { rootDirectory ->
                arguments.changeSetListFileParameter = "$rootDirectory/${arguments.changeSetListFileParameter}"
            }
        } catch (e: ParameterException) {
            LOGGER.error(e.message)
            println(e.message)
            jCommander.usage()
            System.exit(-1)
        }
        return arguments
    }

    fun usage() {
        jCommander.usage()
    }

    private companion object {
        val LOGGER: Logger = LoggerFactory.getLogger(CommandLineArgumentsParser::class.java)
    }
}
