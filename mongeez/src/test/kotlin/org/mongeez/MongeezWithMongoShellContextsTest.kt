package org.mongeez

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test

@Tag(SHELL_TAG)
class MongeezWithMongoShellContextsTest {
    private val mongeezTestSuite = MongeezTestSuite(createMongeezWithShell)

    @BeforeEach
    fun setUp() {
        db.drop()
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
}
