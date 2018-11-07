package org.mongeez

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.mongeez.dao.shell.ShellException
import org.springframework.core.io.ClassPathResource

@Tag("shell")
class MongeezWithMongoShellTest {
    private val mongeezTestSuite = MongeezTestSuite { path ->
        Mongeez().apply {
            setFile(ClassPathResource(path))
            setServerAddress(serverAddress)
            setDbName(DB_NAME)
            setUseMongoShell(true)
        }
    }

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
    fun testFailOnError_True() {
        mongeezTestSuite.testFailOnError_True(ShellException::class.java)
    }

    @Test
    fun testNoFiles() {
        mongeezTestSuite.testNoFiles()
    }

    @Test
    fun testNoFailureOnEmptyChangeLog() {
        mongeezTestSuite.testNoFailureOnEmptyChangeLog()
    }

    @Test
    fun testNoFailureOnNoChangeFilesBlock() {
        mongeezTestSuite.testNoFailureOnNoChangeFilesBlock()
    }

    @Test
    fun testChangesWContextContextNotSet() {
        mongeezTestSuite.testChangesWContextContextNotSet()
    }

    @Test
    fun testChangesWContextContextSetToUsers() {
        mongeezTestSuite.testChangesWContextContextSetToUsers()
    }

    @Test
    fun testChangesWContextContextSetToOrganizations() {
        mongeezTestSuite.testChangesWContextContextSetToOrganizations()
    }

    @Test
    fun testFailDuplicateIds() {
        mongeezTestSuite.testFailDuplicateIds()
    }
}
