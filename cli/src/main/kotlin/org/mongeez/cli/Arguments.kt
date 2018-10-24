package org.mongeez.cli

import com.beust.jcommander.Parameter

internal class Arguments {
    @Parameter(names = ["-c", "--changeSetListFile"], description = "Path of the file that contains descriptions of changeset files", order = 0)
    var changeSetListFile = "mongeez.xml"
    @Parameter(names = ["-h", "--host"], description = "Host name/address of the database server", order = 1)
    var hostAddress = "localhost"
    @Parameter(names = ["-p", "--port"], description = "Port of the database server", order = 2)
    var port = 27017
    @Parameter(names = ["-d", "--databaseName"], description = "Name of the database on which the change sets will be executed", order = 3)
    var databaseName = "test"
    @Parameter(names = ["-ae", "--authenticationEnabled"], arity = 1, description = "Option to enable/disable authentication on database", order = 4)
    var authenticationEnabled = true
    @Parameter(names = ["-ad", "--authenticationDatabase"], description = "Name of the authentication database", order = 5)
    var authenticationDatabase = "admin"
    @Parameter(names = ["-ctx", "--context"], description = "Context of the change sets to be run", order = 6)
    var context: String? = null
    @Parameter(names = ["-s", "--useMongoShell"], arity = 1, description = "Option to use mongo shell or 'db.eval' to execute change sets", order = 7)
    var useMongoShell = true
    @Parameter(names = ["--help"], description = "Displays the options for the application", help = true, order = 8)
    var help = false
    @Parameter(names = ["--debug"], description = "Changes the log level to debug", order = 9)
    var debug = false

    override fun toString(): String {
        return mapOf(
                "changeSetListFile" to changeSetListFile,
                "hostAddress" to hostAddress,
                "port" to port,
                "databaseName" to databaseName,
                "authenticationEnabled" to authenticationEnabled,
                "authenticationDatabase" to authenticationDatabase,
                "context" to context,
                "useMongoShell" to useMongoShell,
                "help" to help,
                "debug" to debug
        ).toString()
    }
}
