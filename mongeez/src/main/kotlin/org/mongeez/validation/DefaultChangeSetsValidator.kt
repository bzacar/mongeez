package org.mongeez.validation

import org.mongeez.commands.ChangeSet

import java.util.HashSet

class DefaultChangeSetsValidator : ChangeSetsValidator {

    override fun validate(changeSets: List<ChangeSet>) {
        val idSet = HashSet<String>()
        for (changeSet in changeSets) {
            if (idSet.contains(changeSet.changeId)) {
                throw ValidationException("ChangeSetId " + changeSet.changeId + " is not unique.")
            }
            idSet.add(changeSet.changeId)
        }
    }
}
