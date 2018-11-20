package org.mongeez

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.mongeez.dao.shell.ShellException

@Tag(SHELL_TAG)
class MongeezWithMongoShellFailuresTest {
    private val mongeezTestSuite = MongeezTestSuite(createMongeezWithShell)

    @BeforeEach
    fun setUp() {
        db.drop()
    }

    @Test
    fun testFailOnError_True() {
        mongeezTestSuite.testFailOnError_True(ShellException::class.java)
    }

    @Test
    fun testNoFailureOnNoChangeFilesBlock() {
        mongeezTestSuite.testNoFailureOnNoChangeFilesBlock()
    }

    @Test
    fun testFailDuplicateIds() {
        mongeezTestSuite.testFailDuplicateIds()
    }
}
