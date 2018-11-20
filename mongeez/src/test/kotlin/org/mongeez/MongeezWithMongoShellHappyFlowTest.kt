package org.mongeez

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test

@Tag(SHELL_TAG)
class MongeezWithMongoShellHappyFlowTest {
    private val mongeezTestSuite = MongeezTestSuite(createMongeezWithShell)

    @BeforeEach
    fun setUp() {
        db.drop()
    }

    @Test
    fun testMongeez() {
        mongeezTestSuite.testMongeez()
    }

    @Test
    fun testRunTwice() {
        mongeezTestSuite.testMongeez()
        mongeezTestSuite.testMongeez()
    }

    @Test
    fun testFailOnError_False() {
        mongeezTestSuite.testFailOnError_False()
    }

    @Test
    fun testNoFiles() {
        mongeezTestSuite.testNoFiles()
    }

    @Test
    fun testNoFailureOnEmptyChangeLog() {
        mongeezTestSuite.testNoFailureOnEmptyChangeLog()
    }
}
