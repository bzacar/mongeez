package org.mongeez.validation


import org.mongeez.commands.ChangeSet

interface ChangeSetsValidator {
    fun validate(changeSets: List<ChangeSet>)
}
