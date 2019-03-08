package org.mongeez.data

data class DryRunResult(val lastChangeSet: String?, val executableChangeSets: List<String>) {
    fun print(printer: (String) -> Unit) {
        lastChangeSet
                ?.also { printer("Last executed change set:") }
                ?.printChangeSetSummary(printer)
                ?: printer("No change set executed on this database yet!")
        printer("")
        executableChangeSets
                .takeIf { it.isNotEmpty() }
                ?.also { printer("Following change sets will be executed at the next run:") }
                ?.forEach { it.printChangeSetSummary(printer, true) }
                ?: printer("There is no change set to be run!")
    }

    private fun String.printChangeSetSummary(printer: (String) -> Unit, isListElement: Boolean = false) {
        val changeSetInfo = split(":")
        val bullet = if (isListElement) "- " else ""
        val indentation = if (isListElement) "  " else ""
        printer("  ${bullet}Author    : ${changeSetInfo[0]}")
        printer("  ${indentation}Change ID : ${changeSetInfo[1]}")
        printer("  ${indentation}File      : ${changeSetInfo[2]}")
    }
}
