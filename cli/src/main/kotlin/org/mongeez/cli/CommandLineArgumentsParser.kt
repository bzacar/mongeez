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
            getParametersFileDirectory(commandLineArguments)?.also { rootDirectory ->
                arguments.changeSetListFile = "$rootDirectory/${arguments.changeSetListFile}"
            }
        } catch (e: ParameterException) {
            LOGGER.error(e.message)
            jCommander.usage()
            System.exit(-1)
        }
        return arguments
    }

    fun usage() {
        jCommander.usage()
    }

    private fun getParametersFileDirectory(commandLineArguments: Array<String>): String? {
        return commandLineArguments
                .singleOrNull()
                ?.takeIf { it.startsWith("@") }
                ?.substring(1)
                ?.substringBeforeLast('/', ".")
    }

    private companion object {
        val LOGGER: Logger = LoggerFactory.getLogger(CommandLineArgumentsParser::class.java)
    }
}
