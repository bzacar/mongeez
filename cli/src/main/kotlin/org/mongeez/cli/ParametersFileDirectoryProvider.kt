package org.mongeez.cli

internal object ParametersFileDirectoryProvider {
    private val CHANGE_SET_LIST_FILE_PARAMETER_NAMES = setOf("-c", "--changeSetListFile", "--change-set-list-file")

    fun get(commandLineArguments: Array<String>): String? {
        return commandLineArguments
                .takeUnless { it.containsAnyChangeSetListFileParameter() }
                ?.find { it.startsWith("@") }
                ?.substring(1)
                ?.substringBeforeLast('/', ".")
    }

    private fun Array<String>.containsAnyChangeSetListFileParameter(): Boolean {
        return any { CHANGE_SET_LIST_FILE_PARAMETER_NAMES.contains(it) }
    }
}
