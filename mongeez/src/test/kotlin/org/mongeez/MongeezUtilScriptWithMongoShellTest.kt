package org.mongeez

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.mongeez.dao.shell.ShellException
import org.springframework.core.io.ClassPathResource

@Tag("shell")
class MongeezUtilScriptWithMongoShellTest {
    private val mongeezUtilScriptTestSuite = MongeezUtilScriptTestSuite { path ->
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
        mongeezUtilScriptTestSuite.testUtilFunctionalityWhenUtilFlagIsNotSet(ShellException::class.java)
    }
}
