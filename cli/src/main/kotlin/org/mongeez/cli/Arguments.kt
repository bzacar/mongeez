package org.mongeez.cli

import com.beust.jcommander.Parameter
import org.mongeez.MongoAuth

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
    @Parameter(names = ["-u", "--userName"], description = "Name of the user to authenticate to database (should not be used unless parameters are passed by properties file)", order = 6)
    var userName: String? = null
    @Parameter(names = ["-pw", "--password"], description = "Password of the user (should not be used unless parameters are passed by properties file)", order = 7)
    var password: String? = null
    @Parameter(names = ["-ctx", "--context"], description = "Context of the change sets to be run", order = 8)
    var context: String? = null
    @Parameter(names = ["-s", "--useMongoShell"], arity = 1, description = "Option to use mongo shell or 'db.eval' to execute change sets", order = 9)
    var useMongoShell = true
    @Parameter(names = ["--dryRun"], description = "Sets the option to dry run change sets (lists all change sets that would be executed and the last executed changeset)", order = 10)
    var dryRun = false
    @Parameter(names = ["--help"], description = "Displays the options for the application", help = true, order = 11)
    var help = false
    @Parameter(names = ["--debug"], description = "Changes the log level to debug", order = 12)
    var debug = false
    @Parameter(names = ["--log"], description = "Adds a console log appender", order = 13)
    var logConsole = false
    private val console = System.console()

    fun getMongoAuth(): MongoAuth? {
        return if (authenticationEnabled) {
            val userName = userName ?: run {
                println("Please enter user name: ")
                console?.readLine()?.trim() ?: throw IllegalStateException("Console not available to read user name!")
            }
            val password = password?.toCharArray() ?: run {
                println("Please enter password:  ")
                console?.readPassword() ?: throw IllegalStateException("Console not available to read password!")
            }
            MongoAuth(userName, password, authenticationDatabase)
        } else {
            null
        }
    }

    override fun toString(): String {
        return mapOf(
                "changeSetListFile" to changeSetListFile,
                "hostAddress" to hostAddress,
                "port" to port,
                "databaseName" to databaseName,
                "authenticationEnabled" to authenticationEnabled,
                "authenticationDatabase" to authenticationDatabase,
                "userName" to userName,
                "password" to password,
                "context" to context,
                "useMongoShell" to useMongoShell,
                "dryRun" to dryRun,
                "help" to help,
                "debug" to debug,
                "logConsole" to logConsole
        ).toString()
    }
}
