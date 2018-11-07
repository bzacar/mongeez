package org.mongeez

import com.mongodb.MongoCommandException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.springframework.core.io.ClassPathResource

@Tag("dao")
class MongeezUtilScriptTest {
    private val mongeezUtilScriptTestSuite = MongeezUtilScriptTestSuite { path ->
        Mongeez().apply {
            setFile(ClassPathResource(path))
            setServerAddress(serverAddress)
            setDbName(DB_NAME)
        }
    }

    @BeforeEach
    fun setUp() {
        db.drop()
    }

    @Test
    fun testUtilFunctionalityInXmlChangeSets() {
        mongeezUtilScriptTestSuite.testUtilFunctionalityInXmlChangeSets()
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
    fun testUtilFunctionalityInJsChangeSetsWhenUtilIsNotSpecified() {
        mongeezUtilScriptTestSuite.testUtilFunctionalityInJsChangeSetsWhenUtilIsNotSpecified()
    }

    @Test
    fun testUtilFunctionalityWhenUtilFlagIsNotSet() {
        mongeezUtilScriptTestSuite.testUtilFunctionalityWhenUtilFlagIsNotSet(MongoCommandException::class.java)
    }
}
