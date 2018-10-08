package org.mongeez.validation

import org.mongeez.commands.ChangeSet
import org.testng.annotations.Test

class DefaultChangeSetsValidatorTest {

    @Test(expectedExceptions = [ValidationException::class])
    fun testDetectDuplicateFirstHalf() {
        val validator = DefaultChangeSetsValidator()
        val changeSets = listOf(makeChangeSet("1"),
                makeChangeSet("10"),
                makeChangeSet("3"),
                makeChangeSet("4"),
                makeChangeSet("5"),
                makeChangeSet("6"),
                makeChangeSet("7"),
                makeChangeSet("8"),
                makeChangeSet("9"),
                makeChangeSet("10"))
        validator.validate(changeSets)
    }

    @Test(expectedExceptions = [ValidationException::class])
    fun testDetectDuplicateSecondHalf() {
        val validator = DefaultChangeSetsValidator()
        val changeSets = listOf(makeChangeSet("1"),
                makeChangeSet("2"),
                makeChangeSet("3"),
                makeChangeSet("4"),
                makeChangeSet("5"),
                makeChangeSet("6"),
                makeChangeSet("7"),
                makeChangeSet("8"),
                makeChangeSet("10"),
                makeChangeSet("10"))
        validator.validate(changeSets)
    }

    @Test
    fun testValidateNoDuplicates() {
        val validator = DefaultChangeSetsValidator()
        val changeSets = listOf(makeChangeSet("1"),
                makeChangeSet("2"),
                makeChangeSet("3"),
                makeChangeSet("4"),
                makeChangeSet("5"),
                makeChangeSet("6"),
                makeChangeSet("7"),
                makeChangeSet("8"),
                makeChangeSet("9"),
                makeChangeSet("10"))
        validator.validate(changeSets)
    }

    private fun makeChangeSet(id: String): ChangeSet {
        val changeSet = ChangeSet()
        changeSet.changeId = id
        return changeSet
    }

}
