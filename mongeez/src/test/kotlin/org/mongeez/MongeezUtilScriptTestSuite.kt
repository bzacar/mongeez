package org.mongeez

import org.assertj.core.api.Assertions.assertThatThrownBy
import org.mongeez.dao.MongeezDaoException
import org.mongeez.validation.ValidationException

class MongeezUtilScriptTestSuite(private val create: (String) -> Mongeez) {
    fun testUtilFunctionalityInXmlChangeSets() {
        create("mongeez_with_util.xml").process()
        assertThatCollections("mongeez", "users").have(2, 2).documents()
    }

    fun testUtilFunctionalityInXmlChangeSetsWhenUtilIsNotSpecified() {
        assertThatThrownBy { create("mongeez_without_util.xml").process() }
                .isInstanceOf(ValidationException::class.java)
                .hasMessageContaining("Changeset flagged to use util but no util found!")
    }

    fun testUtilFunctionalityInJsChangeSets() {
        create("mongeez_with_util_js.xml").process()
        assertThatCollections("mongeez", "users").have(2, 2).documents()
    }

    fun testUtilFunctionalityInJsChangeSetsWhenUtilIsNotSpecified() {
        assertThatThrownBy { create("mongeez_without_util_js.xml").process() }
                .isInstanceOf(ValidationException::class.java)
                .hasMessageContaining("Changeset flagged to use util but no util found!")
    }

    fun testUtilFunctionalityWhenUtilFlagIsNotSet(cause: Class<out Throwable>) {
        assertThatThrownBy { create("mongeez_without_util_flag.xml").process() }
                .isInstanceOf(MongeezDaoException::class.java)
                .hasMessageContaining("addNuanceAndInsert is not defined")
                .hasCauseInstanceOf(cause)
    }
}
