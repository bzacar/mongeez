/*
 * Copyright 2011 SecondMarket Labs, LLC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */

package org.mongeez

import org.assertj.core.api.Assertions.assertThatThrownBy
import org.mongeez.dao.MongeezDao.Companion.MONGEEZ_COLLECTION_NAME
import org.mongeez.dao.MongeezDaoException
import org.mongeez.validation.ValidationException

class MongeezTestSuite(private val create: (String) -> Mongeez) {
    fun testMongeez() {
        create("mongeez.xml").process()
        assertThatCollections(MONGEEZ_COLLECTION_NAME, ORGANIZATION_COLLECTION_NAME, USER_COLLECTION_NAME)
                .have(5, 2, 2).documents()
    }

    fun testFailOnError_False() {
        assertThatCollections(MONGEEZ_COLLECTION_NAME)
                .containsNoDocuments()
        create("mongeez_fail.xml").process()
        assertThatCollections(MONGEEZ_COLLECTION_NAME)
                .have(2).documents()
    }

    fun testFailOnError_True(cause: Class<out Throwable>) {
        assertThatThrownBy { create("mongeez_fail_fail.xml").process() }
                .isInstanceOf(MongeezDaoException::class.java)
                .hasCauseInstanceOf(cause)
    }

    fun testNoFiles() {
        create("mongeez_empty.xml").process()
        assertThatCollections(MONGEEZ_COLLECTION_NAME)
                .have(1).documents()
    }

    fun testNoFailureOnEmptyChangeLog() {
        assertThatCollections(MONGEEZ_COLLECTION_NAME)
                .containsNoDocuments()
        create("mongeez_empty_changelog.xml").process()
        assertThatCollections(MONGEEZ_COLLECTION_NAME)
                .have(1).documents()
    }

    fun testNoFailureOnNoChangeFilesBlock() {
        assertThatCollections(MONGEEZ_COLLECTION_NAME)
                .containsNoDocuments()
        assertThatThrownBy { create("mongeez_no_changefiles_declared.xml").process() }
                .isInstanceOf(ValidationException::class.java)
    }

    fun testChangesWContextContextNotSet() {
        assertThatCollections(MONGEEZ_COLLECTION_NAME)
                .containsNoDocuments()
        create(MONGEEZ_CONTEXT_CHANGE_SETS_FILE).process()
        assertThatCollections(MONGEEZ_COLLECTION_NAME, CAR_COLLECTION_NAME)
                .have(2, 2).documents()
        assertThatCollections(USER_COLLECTION_NAME, ORGANIZATION_COLLECTION_NAME, HOUSE_COLLECTION_NAME)
                .containsNoDocuments()
    }

    fun testChangesWContextContextSetToUsers() {
        assertThatCollections(MONGEEZ_COLLECTION_NAME)
                .containsNoDocuments()
        create(MONGEEZ_CONTEXT_CHANGE_SETS_FILE)
                .apply { setContext("users") }
                .process()
        assertThatCollections(MONGEEZ_COLLECTION_NAME, CAR_COLLECTION_NAME, USER_COLLECTION_NAME, HOUSE_COLLECTION_NAME)
                .have(4, 2, 2, 2).documents()
        assertThatCollections(ORGANIZATION_COLLECTION_NAME)
                .containsNoDocuments()
    }

    fun testChangesWContextContextSetToOrganizations() {
        assertThatCollections(MONGEEZ_COLLECTION_NAME)
                .containsNoDocuments()
        create(MONGEEZ_CONTEXT_CHANGE_SETS_FILE)
                .apply { setContext("organizations") }
                .process()
        assertThatCollections(MONGEEZ_COLLECTION_NAME, CAR_COLLECTION_NAME, ORGANIZATION_COLLECTION_NAME, HOUSE_COLLECTION_NAME)
                .have(4, 2, 2, 2).documents()
        assertThatCollections(USER_COLLECTION_NAME)
                .containsNoDocuments()
    }

    fun testChangesWContextContextSetToUsersExtra() {
        assertThatCollections(MONGEEZ_COLLECTION_NAME)
                .containsNoDocuments()
        create(MONGEEZ_CONTEXT_CHANGE_SETS_FILE)
                .apply { setContext("users-extra") }
                .process()
        assertThatCollections(MONGEEZ_COLLECTION_NAME, CAR_COLLECTION_NAME, USER_COLLECTION_NAME)
                .have(3, 2, 1).documents()
        assertThatCollections(ORGANIZATION_COLLECTION_NAME, HOUSE_COLLECTION_NAME)
                .containsNoDocuments()
    }

    fun testFailDuplicateIds() {
        assertThatThrownBy { create("mongeez_fail_on_duplicate_changeset_ids.xml").process() }
                .isInstanceOf(ValidationException::class.java)
    }

    private companion object {
        const val USER_COLLECTION_NAME = "user"
        const val ORGANIZATION_COLLECTION_NAME = "organization"
        const val CAR_COLLECTION_NAME = "car"
        const val HOUSE_COLLECTION_NAME = "house"
        const val MONGEEZ_CONTEXT_CHANGE_SETS_FILE = "mongeez_contexts.xml"
    }
}
