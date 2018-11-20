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

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test

@Tag(DAO_TAG)
class MongeezTest {
    private val mongeezTestSuite = MongeezTestSuite(createMongeezWithoutShell)

    @BeforeEach
    fun setUp() {
        db.drop()
    }

    @Test
    fun testMongeez() {
        mongeezTestSuite.testMongeez()
    }

    @Test
    fun testRunTwice() {
        mongeezTestSuite.testMongeez()
        mongeezTestSuite.testMongeez()
    }

    @Test
    fun testFailOnError_False() {
        mongeezTestSuite.testFailOnError_False()
    }

    @Test
    fun testNoFiles() {
        mongeezTestSuite.testNoFiles()
    }

    @Test
    fun testNoFailureOnEmptyChangeLog() {
        mongeezTestSuite.testNoFailureOnEmptyChangeLog()
    }
}
