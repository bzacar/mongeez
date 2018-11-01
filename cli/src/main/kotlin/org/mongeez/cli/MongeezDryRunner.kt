package org.mongeez.cli

import org.mongeez.Mongeez

internal fun Mongeez.executeDryRun() {
    val (lastChangeSet, executableChangeSets) = dryRun()
    lastChangeSet
            ?.also { println("Last executed change set:") }
            ?.printChangeSetSummary()
            ?: println("No change set executed on this database yet!")
    println()
    executableChangeSets
            .takeIf { it.isNotEmpty() }
            ?.also { println("Following change sets will be executed at the next run:") }
            ?.forEach { it.printChangeSetSummary(true) }
            ?: println("There is no change set to be run!")
}

private fun String.printChangeSetSummary(isListElement: Boolean = false) {
    val changeSetInfo = split(":")
    val bullet = if (isListElement) "- " else ""
    val indentation = if (isListElement) "  " else ""
    println("  ${bullet}Author    : ${changeSetInfo[0]}")
    println("  ${indentation}Change ID : ${changeSetInfo[1]}")
    println("  ${indentation}File      : ${changeSetInfo[2]}")
}
