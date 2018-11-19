/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */

package org.mongeez.reader

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.assertj.core.api.Assertions.tuple
import org.junit.jupiter.api.Test
import org.mongeez.commands.ChangeSet
import org.mongeez.containsChangeSets
import org.mongeez.validation.ValidationException
import org.springframework.core.io.ClassPathResource
import java.io.FileNotFoundException
import java.nio.charset.Charset
import java.text.ParseException

class FormattedJavascriptChangeSetReaderTest {
    @Test
    fun testGetChangeSets1() {
        val changeSets = parse(CHANGE_SET_FILE_1)
        assertThat(changeSets)
                .hasSize(2)
                .containsChangeSets(
                        tuple(AUTHOR_MLYSAGHT, CHANGE_SET_ID_1, false, CHANGE_SET_FILE_1) to CHANGE_SET_1,
                        tuple(AUTHOR_MLYSAGHT, CHANGE_SET_ID_2, false, CHANGE_SET_FILE_1) to
                                "db.user.insert({ \"Name\" : \"Michael Lysaght\"});\n" + "db.user.insert({ \"Name\" : \"Oleksii Iepishkin\"});"
                )
    }

    @Test
    fun testGetChangeSets2() {
        val changeSets = parse(CHANGE_SET_FILE_2)
        assertThat(changeSets)
                .hasSize(2)
                .containsChangeSets(
                        tuple("someuser", "cs3", true, CHANGE_SET_FILE_2) to
                                "db.organization.update({Location : \"NYC\"}, {\$set : {Location : \"NY\"}}, false, true);",
                        tuple("someotheruser", "cs4", false, CHANGE_SET_FILE_2) to
                                "db.organization.find().forEach(function(org) {\n" +
                                "    var year = org.DateFounded.Year;\n" +
                                "    var month = org.DateFounded.Month;\n" +
                                "    var day = org.DateFounded.day;\n" +
                                "    //Year is minimum required information\n" +
                                "    if (year != null) {\n" +
                                "    var date = new Date();\n" +
                                "    if (month != null) {\n" +
                                "    if (day != null) {\n" +
                                "    date.setUTCDate(day);\n" +
                                "    }\n" +
                                "date.setMonth(month - 1);\n" +
                                "}\n" +
                                "date.setFullYear(year);\n" +
                                "}\n" +
                                "if (date != null) {\n" +
                                "    db.organization.update({Organization : org.Organization}, {\$set : {DateFounded : date}});\n" +
                                "}\n" +
                                "else {\n" +
                                "    db.organization.update({Organization : org.Organization}, {\$unset : {DateFounded : 1 }});\n" +
                                "}\n" +
                                "});"
                )
    }

    @Test
    fun testGetChangeSetsNoHeader() {
        assertThatThrownBy { parse("changeset_noheader.js") }
                .isInstanceOf(ValidationException::class.java)
                .hasMessageContaining("changeset_noheader.js did not begin with the expected comment:\n//mongeez formatted javascript")
                .hasCauseInstanceOf(ParseException::class.java)
    }

    @Test
    fun testGetChangeSetsEmptyScript() {
        assertThatThrownBy { parse("changeset_emptyscript.js") }
                .isInstanceOf(ValidationException::class.java)
                .hasMessageContaining("No JavaScript found for changeset joseph:4:changeset_emptyscript.js")
                .hasCauseInstanceOf(ParseException::class.java)
    }

    @Test
    fun testGetChangeSetsAlternateEncoding() {
        val changeSets = parse(CHANGE_SET_FILE_ALTERNATE_ENCODING, Charset.forName("Cp1252"))
        assertThat(changeSets)
                .hasSize(2)
                .containsChangeSets(
                        tuple(AUTHOR_MLYSAGHT, CHANGE_SET_ID_1, false, CHANGE_SET_FILE_ALTERNATE_ENCODING) to CHANGE_SET_1,
                        tuple(AUTHOR_MLYSAGHT, CHANGE_SET_ID_2, false, CHANGE_SET_FILE_ALTERNATE_ENCODING) to
                                "db.user.insert({ \"Name\" : \"Michaël Lyságht\"});\n" + "db.user.insert({ \"Name\" : \"Oleksïï Iepishkin\"});"
                )
    }

    /**
     * Verification that the file used to test encoding support is actually a
     * valid test.  For example, a file that included only ASCII characters
     * would not be a valid test of the difference between ASCII, UTF-8, and
     * Cp1252 encodings, as ASCII is a subset of the other two encodings.
     */
    @Test
    fun testGetChangeSetsWrongEncoding() {
        val changeSets = parse(CHANGE_SET_FILE_ALTERNATE_ENCODING)
        assertThat(changeSets)
                .hasSize(2)
                .containsChangeSets(
                        tuple(AUTHOR_MLYSAGHT, CHANGE_SET_ID_1, false, CHANGE_SET_FILE_ALTERNATE_ENCODING) to CHANGE_SET_1,
                        tuple(AUTHOR_MLYSAGHT, CHANGE_SET_ID_2, false, CHANGE_SET_FILE_ALTERNATE_ENCODING) to
                                "db.user.insert({ \"Name\" : \"Micha�l Lys�ght\"});\n" + "db.user.insert({ \"Name\" : \"Oleks�� Iepishkin\"});"
                )
    }

    @Test
    fun testGetChangeSetsIOFailure() {
        assertThatThrownBy { parse("changeset_nonexistant.js") }
                .isInstanceOf(ValidationException::class.java)
                .hasMessageContaining("class path resource [org/mongeez/reader/changeset_nonexistant.js] cannot be opened because it does not exist")
                .hasCauseInstanceOf(FileNotFoundException::class.java)
    }

    @Test
    fun testChangeSetWithContexts() {
        val changeSets = parse("changeset_contexts.js")
        assertThat(changeSets).hasSize(5)
                .extracting("contexts", "isRunAlways")
                .containsExactly(
                        tuple("users", false),
                        tuple("users,organizations", false),
                        tuple("users,organizations", true),
                        tuple("users, organizations", false),
                        tuple("users, organizations", true))
    }

    private fun parse(fileName: String, charset: Charset? = null): List<ChangeSet> {
        val reader = charset?.let { FormattedJavascriptChangeSetReader(it) }
                ?: FormattedJavascriptChangeSetReader()
        val file = ClassPathResource(fileName, javaClass)
        return reader.getChangeSets(file)
    }

    private companion object {
        const val AUTHOR_MLYSAGHT = "mlysaght"
        const val CHANGE_SET_FILE_1 = "changeset1.js"
        const val CHANGE_SET_FILE_2 = "changeset2.js"
        const val CHANGE_SET_FILE_ALTERNATE_ENCODING = "changeset_Cp1252.js"
        const val CHANGE_SET_ID_1 = "ChangeSet-1"
        const val CHANGE_SET_ID_2 = "ChangeSet-2"
        const val CHANGE_SET_1 = "db.organization.insert({\n" +
                "    \"Organization\" : \"10Gen\",\n" +
                "    \"Location\" : \"NYC\",\n" +
                "    DateFounded : {\"Year\" : 2008, \"Month\" : 01, \"day\" :01}\n" +
                "});\n" +
                "db.organization.insert({\n" +
                "    \"Organization\" : \"SecondMarket\",\n" +
                "    \"Location\" : \"NYC\",\n" +
                "    DateFounded : {\"Year\" : 2004, \"Month\" : 05, \"day\" :04}\n" +
                "});"
    }
}
