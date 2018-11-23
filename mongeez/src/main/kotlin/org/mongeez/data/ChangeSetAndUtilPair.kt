package org.mongeez.data

import org.mongeez.commands.ChangeSet
import org.mongeez.validation.ValidationException

data class ChangeSetAndUtilPair(val changeSets: List<ChangeSet>, private val util: Map<String, ChangeSet>) {
    fun getUtilScript(utilPaths: List<String>): String {
        return utilPaths.asSequence()
                .map { path ->
                    util[path]?.getMergedScript()?.body
                            ?: throw ValidationException("Changeset flagged to use util `$path` but it is not found!")
                }
                .joinToString("\n")

    }
}
