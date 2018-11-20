package org.mongeez

import com.mongodb.MongoCommandException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test

@Tag(DAO_TAG)
class MongeezFailuresTest {
    private val mongeezTestSuite = MongeezTestSuite(createMongeezWithoutShell)

    @BeforeEach
    fun setUp() {
        db.drop()
    }

    @Test
    fun testFailOnError_True() {
        mongeezTestSuite.testFailOnError_True(MongoCommandException::class.java)
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
