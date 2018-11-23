package org.mongeez

import org.assertj.core.api.Assertions.assertThatThrownBy
import org.mongeez.dao.MongeezDao.Companion.MONGEEZ_COLLECTION_NAME
import org.mongeez.dao.MongeezDaoException
import org.mongeez.validation.ValidationException

class MongeezUtilScriptTestSuite(private val create: (String) -> Mongeez) {
    fun testUtilFunctionalityInXmlChangeSets() {
        create("mongeez_with_util.xml").process()
        assertThatCollections(MONGEEZ_COLLECTION_NAME, USER_COLLECTION_NAME).have(2, 2).documents()
    }

    fun testMultipleUtilsFunctionalityInXmlChangeSets() {
        create("mongeez_with_multiple_utils.xml").process()
        assertThatCollections(MONGEEZ_COLLECTION_NAME, USER_COLLECTION_NAME).have(2, 2).documents()
    }

    fun testUtilFunctionalityInXmlChangeSetsWhenUtilIsNotSpecified() {
        assertThatThrownBy { create("mongeez_without_util.xml").process() }
                .isInstanceOf(ValidationException::class.java)
                .hasMessageContaining("Changeset flagged to use util `util.xml` but it is not found!")
    }

    fun testUtilFunctionalityInJsChangeSets() {
        create("mongeez_with_util_js.xml").process()
        assertThatCollections(MONGEEZ_COLLECTION_NAME, USER_COLLECTION_NAME).have(2, 2).documents()
    }

    fun testMultipleUtilsFunctionalityInJsChangeSets() {
        create("mongeez_with_multiple_utils_js.xml").process()
        assertThatCollections(MONGEEZ_COLLECTION_NAME, USER_COLLECTION_NAME).have(2, 2).documents()
    }

    fun testUtilFunctionalityInJsChangeSetsWhenUtilIsNotSpecified() {
        assertThatThrownBy { create("mongeez_without_util_js.xml").process() }
                .isInstanceOf(ValidationException::class.java)
                .hasMessageContaining("Changeset flagged to use util `org/mongeez/reader/util.js` but it is not found!")
    }

    fun testUtilFunctionalityWhenUtilFlagIsNotSet(cause: Class<out Throwable>) {
        assertThatThrownBy { create("mongeez_without_util_flag.xml").process() }
                .isInstanceOf(MongeezDaoException::class.java)
                .hasMessageContaining("addNuanceAndInsert is not defined")
                .hasCauseInstanceOf(cause)
    }

    private companion object {
        const val USER_COLLECTION_NAME = "users"
    }
}
