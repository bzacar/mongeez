package org.mongeez.data

import org.mongeez.commands.ChangeSet
import org.mongeez.validation.ValidationException

data class ChangeSetAndUtilPair(val changeSets: List<ChangeSet>, private val util: ChangeSet?) {
    val utilScript: String by lazy {
        util?.getMergedScript()?.body
                ?: throw ValidationException("Changeset flagged to use util but no util found!")
    }
}
