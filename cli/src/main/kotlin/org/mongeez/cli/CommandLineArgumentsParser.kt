package org.mongeez.cli

import com.beust.jcommander.JCommander

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
        } catch (e: RuntimeException) {
            println("ERROR: ${e.message}")
            println()
            jCommander.usage()
            throw e
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
                ?.substringBeforeLast('/',".")
    }
}
