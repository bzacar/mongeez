package org.mongeez

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test

@Tag(DAO_TAG)
class MongeezContextsTest {
    private val mongeezTestSuite = MongeezTestSuite(createMongeezWithoutShell)

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
    fun testChangesWContextContextSetToUsersOther() {
        mongeezTestSuite.testChangesWContextContextSetToUsersOther()
    }

    @Test
    fun testChangesWContextContextSetToOrganizations() {
        mongeezTestSuite.testChangesWContextContextSetToOrganizations()
    }
}
