package org.mongeez.validation

import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.mongeez.commands.ChangeSet

class DefaultChangeSetsValidatorTest {
    @Test
    fun testDetectDuplicateFirstHalf() {
        val validator = DefaultChangeSetsValidator()
        val changeSets = listOf("1", "10", "3", "4", "5", "6", "7", "8", "9", "10")
                .map(::makeChangeSet)
        assertThatThrownBy { validator.validate(changeSets) }
                .isInstanceOf(ValidationException::class.java)
    }

    @Test
    fun testDetectDuplicateSecondHalf() {
        val validator = DefaultChangeSetsValidator()
        val changeSets = listOf("1", "2", "3", "4", "5", "6", "7", "8", "10", "10")
                .map(::makeChangeSet)
        assertThatThrownBy { validator.validate(changeSets) }
                .isInstanceOf(ValidationException::class.java)
    }

    @Test
    fun testValidateNoDuplicates() {
        val validator = DefaultChangeSetsValidator()
        val changeSets = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10")
                .map(::makeChangeSet)
        validator.validate(changeSets)
    }

    private fun makeChangeSet(id: String): ChangeSet {
        val changeSet = ChangeSet()
        changeSet.changeId = id
        return changeSet
    }
}
