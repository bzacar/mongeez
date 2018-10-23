package org.mongeez.cli

import ch.qos.logback.classic.Level
import org.slf4j.Logger
import org.slf4j.LoggerFactory

internal const val PROGRAM_NAME = "mongeez-cli"

fun main(args: Array<String>) {
    val parser = CommandLineArgumentsParser()
    val arguments = parser.parse(args)
    if (arguments.debug) {
        setDebug()
    }
    if (arguments.help) {
        parser.usage()
    } else {
        MongeezRunner.run(arguments)
    }
}

private fun setDebug() {
    val rootLogger = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME) as ch.qos.logback.classic.Logger
    rootLogger.level = Level.DEBUG
}
