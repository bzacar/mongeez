package org.mongeez.cli

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.ConsoleAppender
import org.slf4j.Logger
import org.slf4j.LoggerFactory

internal const val PROGRAM_NAME = "mongeez-cli"

/**
 * Main method of the CLI tool
 * @param args String[]: Command line arguments
 */
fun main(args: Array<String>) {
    val parser = CommandLineArgumentsParser()
    val arguments = parser.parse(args)
    setUpLogConfiguration(arguments)
    if (arguments.help) {
        parser.usage()
    } else {
        MongeezRunner.run(arguments)
    }
}

private fun setUpLogConfiguration(arguments: Arguments) {
    val rootLogger = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME) as? ch.qos.logback.classic.Logger
            ?: throw IllegalStateException("Unexpected type of logger!")
    if (arguments.debug) {
        rootLogger.level = Level.DEBUG
    }
    if (arguments.logConsole) {
        rootLogger.addAppender(rootLogger.createConsoleLogAppender())
    }
}

private fun ch.qos.logback.classic.Logger.createConsoleLogAppender(): ConsoleAppender<ILoggingEvent> {
    return ConsoleAppender<ILoggingEvent>().apply {
        context = loggerContext
        name = "CONSOLE"
        encoder = PatternLayoutEncoder().apply {
            context = loggerContext
            pattern = "[%-5level] %logger{36} - %msg%n"
            start()
        }
        start()
    }
}
