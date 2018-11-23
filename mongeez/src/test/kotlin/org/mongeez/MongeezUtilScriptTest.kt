package org.mongeez

import com.mongodb.MongoCommandException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test

@Tag(DAO_TAG)
class MongeezUtilScriptTest {
    private val mongeezUtilScriptTestSuite = MongeezUtilScriptTestSuite(createMongeezWithoutShell)

    @BeforeEach
    fun setUp() {
        db.drop()
    }

    @Test
    fun testUtilFunctionalityInXmlChangeSets() {
        mongeezUtilScriptTestSuite.testUtilFunctionalityInXmlChangeSets()
    }

    @Test
    fun testMultipleUtilsFunctionalityInXmlChangeSets() {
        mongeezUtilScriptTestSuite.testMultipleUtilsFunctionalityInXmlChangeSets()
    }

    @Test
    fun testUtilFunctionalityInXmlChangeSetsWhenUtilIsNotSpecified() {
        mongeezUtilScriptTestSuite.testUtilFunctionalityInXmlChangeSetsWhenUtilIsNotSpecified()
    }

    @Test
    fun testUtilFunctionalityInJsChangeSets() {
        mongeezUtilScriptTestSuite.testUtilFunctionalityInJsChangeSets()
    }

    @Test
    fun testMultipleUtilsFunctionalityInJsChangeSets() {
        mongeezUtilScriptTestSuite.testMultipleUtilsFunctionalityInJsChangeSets()
    }

    @Test
    fun testUtilFunctionalityInJsChangeSetsWhenUtilIsNotSpecified() {
        mongeezUtilScriptTestSuite.testUtilFunctionalityInJsChangeSetsWhenUtilIsNotSpecified()
    }

    @Test
    fun testUtilFunctionalityWhenUtilFlagIsNotSet() {
        mongeezUtilScriptTestSuite.testUtilFunctionalityWhenUtilFlagIsNotSet(MongoCommandException::class.java)
    }
}
