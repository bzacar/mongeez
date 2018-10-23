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
}
