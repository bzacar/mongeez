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

import com.mongodb.DB
import com.mongodb.MongoClient
import com.mongodb.ServerAddress
import org.mongeez.validation.ValidationException
import org.springframework.core.io.ClassPathResource
import org.testng.Assert.assertEquals
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

@Test
class MongeezTest {
    private lateinit var serverAddress: ServerAddress
    private lateinit var db: DB

    @BeforeMethod
    fun setUp() {
        serverAddress = ServerAddress()
        db = MongoClient(serverAddress).getDB(DB_NAME)
        db.dropDatabase()
    }

    @Test(groups = ["dao"])
    fun testMongeez() {
        val mongeez = create("mongeez.xml")

        mongeez.process()

        assertEquals(db.getCollection("mongeez").count(), 5)

        assertEquals(db.getCollection("organization").count(), 2)
        assertEquals(db.getCollection("user").count(), 2)
    }

    @Test(groups = ["dao"])
    fun testRunTwice() {
        testMongeez()
        testMongeez()
    }

    @Test(groups = ["dao"])
    fun testFailOnError_False() {
        assertEquals(db.getCollection("mongeez").count(), 0)

        val mongeez = create("mongeez_fail.xml")
        mongeez.process()

        assertEquals(db.getCollection("mongeez").count(), 2)
    }

    @Test(groups = ["dao"], expectedExceptions = [com.mongodb.MongoCommandException::class])
    fun testFailOnError_True() {
        val mongeez = create("mongeez_fail_fail.xml")
        mongeez.process()
    }

    @Test(groups = ["dao"])
    fun testNoFiles() {
        val mongeez = create("mongeez_empty.xml")
        mongeez.process()

        assertEquals(db.getCollection("mongeez").count(), 1)
    }

    @Test(groups = ["dao"])
    fun testNoFailureOnEmptyChangeLog() {
        assertEquals(db.getCollection("mongeez").count(), 0)

        val mongeez = create("mongeez_empty_changelog.xml")
        mongeez.process()

        assertEquals(db.getCollection("mongeez").count(), 1)
    }

    @Test(groups = ["dao"], expectedExceptions = [ValidationException::class])
    fun testNoFailureOnNoChangeFilesBlock() {
        assertEquals(db.getCollection("mongeez").count(), 0)

        val mongeez = create("mongeez_no_changefiles_declared.xml")
        mongeez.process()
    }

    @Test(groups = ["dao"])
    fun testChangesWContextContextNotSet() {
        assertEquals(db.getCollection("mongeez").count(), 0)

        val mongeez = create("mongeez_contexts.xml")
        mongeez.process()
        assertEquals(db.getCollection("mongeez").count(), 2)
        assertEquals(db.getCollection("car").count(), 2)
        assertEquals(db.getCollection("user").count(), 0)
        assertEquals(db.getCollection("organization").count(), 0)
        assertEquals(db.getCollection("house").count(), 0)
    }

    @Test(groups = ["dao"])
    fun testChangesWContextContextSetToUsers() {
        assertEquals(db.getCollection("mongeez").count(), 0)

        val mongeez = create("mongeez_contexts.xml")
        mongeez.setContext("users")
        mongeez.process()
        assertEquals(db.getCollection("mongeez").count(), 4)
        assertEquals(db.getCollection("car").count(), 2)
        assertEquals(db.getCollection("user").count(), 2)
        assertEquals(db.getCollection("organization").count(), 0)
        assertEquals(db.getCollection("house").count(), 2)
    }

    @Test(groups = ["dao"])
    fun testChangesWContextContextSetToOrganizations() {
        assertEquals(db.getCollection("mongeez").count(), 0)

        val mongeez = create("mongeez_contexts.xml")
        mongeez.setContext("organizations")
        mongeez.process()
        assertEquals(db.getCollection("mongeez").count(), 4)
        assertEquals(db.getCollection("car").count(), 2)
        assertEquals(db.getCollection("user").count(), 0)
        assertEquals(db.getCollection("organization").count(), 2)
        assertEquals(db.getCollection("house").count(), 2)
    }

    @Test(groups = ["dao"], expectedExceptions = [ValidationException::class])
    fun testFailDuplicateIds() {
        val mongeez = create("mongeez_fail_on_duplicate_changeset_ids.xml")
        mongeez.process()
    }

    private fun create(path: String): Mongeez {
        val mongeez = Mongeez()
        mongeez.setFile(ClassPathResource(path))
        mongeez.setServerAddress(serverAddress)
        mongeez.setDbName(DB_NAME)
        return mongeez
    }

    private companion object {
        private const val DB_NAME = "test_mongeez"
    }
}
