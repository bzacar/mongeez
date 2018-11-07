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
import org.mongeez.dao.MongeezDaoException
import org.mongeez.validation.ValidationException

class MongeezTestSuite(private val create: (String) -> Mongeez) {
    fun testMongeez() {
        create("mongeez.xml").process()
        assertThatCollections("mongeez", "organization", "user")
                .have(5, 2, 2).documents()
    }

    fun testFailOnError_False() {
        assertThatCollections("mongeez").containsNoDocuments()
        create("mongeez_fail.xml").process()
        assertThatCollections("mongeez").have(2).documents()
    }

    fun testFailOnError_True(cause: Class<out Throwable>) {
        assertThatThrownBy { create("mongeez_fail_fail.xml").process() }
                .isInstanceOf(MongeezDaoException::class.java)
                .hasCauseInstanceOf(cause)
    }

    fun testNoFiles() {
        create("mongeez_empty.xml").process()
        assertThatCollections("mongeez").have(1).documents()
    }

    fun testNoFailureOnEmptyChangeLog() {
        assertThatCollections("mongeez").containsNoDocuments()
        create("mongeez_empty_changelog.xml").process()
        assertThatCollections("mongeez").have(1).documents()
    }

    fun testNoFailureOnNoChangeFilesBlock() {
        assertThatCollections("mongeez").containsNoDocuments()
        assertThatThrownBy { create("mongeez_no_changefiles_declared.xml").process() }
                .isInstanceOf(ValidationException::class.java)
    }

    fun testChangesWContextContextNotSet() {
        assertThatCollections("mongeez").containsNoDocuments()
        create("mongeez_contexts.xml").process()
        assertThatCollections("mongeez", "car").have(2, 2).documents()
        assertThatCollections("user", "organization", "house").containsNoDocuments()
    }

    fun testChangesWContextContextSetToUsers() {
        assertThatCollections("mongeez").containsNoDocuments()
        create("mongeez_contexts.xml")
                .apply { setContext("users") }
                .process()
        assertThatCollections("mongeez", "car", "user", "house").have(4, 2, 2, 2).documents()
        assertThatCollections("organization").containsNoDocuments()
    }

    fun testChangesWContextContextSetToOrganizations() {
        assertThatCollections("mongeez").containsNoDocuments()
        create("mongeez_contexts.xml")
                .apply { setContext("organizations") }
                .process()
        assertThatCollections("mongeez", "car", "organization", "house").have(4, 2, 2, 2).documents()
        assertThatCollections("user").containsNoDocuments()
    }

    fun testFailDuplicateIds() {
        assertThatThrownBy { create("mongeez_fail_on_duplicate_changeset_ids.xml").process() }
                .isInstanceOf(ValidationException::class.java)
    }
}
