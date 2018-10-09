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

import org.mongeez.commands.ChangeSet
import org.mongeez.commands.Script
import org.mongeez.validation.ValidationException
import org.springframework.core.io.Resource

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.nio.charset.Charset
import java.text.ParseException
import java.util.ArrayList
import java.util.regex.Matcher
import java.util.regex.Pattern
import java.util.regex.Pattern.CASE_INSENSITIVE

class FormattedJavascriptChangeSetReader
constructor(private val cs: Charset = Charset.forName("UTF-8")) : ChangeSetReader {

    override fun supports(file: Resource): Boolean {
        return file.filename.endsWith(".js")
    }

    override fun getChangeSets(file: Resource): List<ChangeSet> {
        try {
            return parse(file)
        } catch (e: IOException) {
            throw ValidationException(e)
        } catch (e: ParseException) {
            throw ValidationException(e)
        }
    }

    @Throws(IOException::class, ParseException::class)
    private fun parse(file: Resource): List<ChangeSet> {
        val changeSets = ArrayList<ChangeSet>()
        val reader = BufferedReader(InputStreamReader(file.inputStream, cs))
        try {
            val firstLine: String? = reader.readLine()
            parseFileHeader(file, firstLine)
            var changeSet: ChangeSet? = null
            var scriptBody: StringBuilder? = null
            reader.lines().forEach { line ->
                val newChangeSet = parseChangeSetStart(line)
                if (newChangeSet != null) {
                    addScriptToChangeSet(changeSet, scriptBody)
                    ChangeSetReaderUtil.populateChangeSetResourceInfo(newChangeSet, file)
                    changeSets.add(newChangeSet)
                    changeSet = newChangeSet
                    scriptBody = StringBuilder()
                } else if (!line.isCode()) {
                    // Silently ignore whitespace-only and comment-only lines
                } else if (scriptBody != null) {
                    scriptBody?.appendln(line)
                } else if (line.isCode()) {
                    throw ParseException("$file has content outside of a changeset.  " +
                            "To start a changeset, add a comment in the format:\n" +
                            "${LINE_COMMENT}changeset author:id", 0)
                }
            }
            addScriptToChangeSet(changeSet, scriptBody)
        } finally {
            try {
                reader.close()
            } catch (ignore: IOException) {
            }
        }
        return changeSets
    }

    private fun String.isCode() = trim().isNotEmpty() && !startsWith(LINE_COMMENT)

    @Throws(ParseException::class)
    private fun addScriptToChangeSet(changeSet: ChangeSet?, scriptBody: StringBuilder?) {
        changeSet?.apply {
            val script = scriptBody?.toString()?.trim()
                    ?.takeIf { it.isNotEmpty() }
                    ?.let { Script(it) }
                    ?: throw ParseException("No JavaScript found for changeset ${asString()}", -1)
            add(script)
        }
    }

    private fun ChangeSet.asString(): String {
        return "$author:$changeId"
    }

    @Throws(IOException::class, ParseException::class)
    private fun parseFileHeader(file: Resource, line: String?) {
        line?.takeIf { FILE_HEADER_PATTERN.matcher(it).matches() }
                ?: throw ParseException(fileHeaderExceptionMessage(file), -1)
    }

    private fun parseChangeSetStart(line: String): ChangeSet? {
        return CHANGESET_PATTERN.matcher(line)
                ?.takeIf { it.matches() }
                ?.let { matcher ->
                    ChangeSet().apply {
                        author = matcher.group(1)
                        changeId = matcher.group(2)
                        isRunAlways = parseAttribute(ATTRIBUTE_RUN_ALWAYS_PATTERN.matcher(line))
                        setContexts(parseAttributeString(ATTRIBUTE_CONTEXTS_PATTERN.matcher(line)))
                    }
                }
    }

    private fun parseAttribute(attributeMatcher: Matcher, defaultValue: Boolean = false): Boolean {
        return attributeMatcher
                .takeIf { it.matches() }
                ?.group(1)?.toBoolean()
                ?: defaultValue
    }

    private fun parseAttributeString(attributeMatcher: Matcher): String? {
        return attributeMatcher.takeIf { it.matches() }?.group(1)
    }

    companion object {
        private const val LINE_COMMENT = "//"
        private const val FILE_HEADER = "mongeez formatted javascript"
        private val FILE_HEADER_PATTERN = Pattern.compile("//\\s*mongeez\\s+formatted\\s+javascript\\s*", CASE_INSENSITIVE)
        private val CHANGESET_PATTERN = Pattern.compile("//\\s*changeset\\s+([\\w\\-]+):([\\w\\-]+).*", CASE_INSENSITIVE)
        private val ATTRIBUTE_RUN_ALWAYS_PATTERN = Pattern.compile(".*runAlways:(\\w+).*", CASE_INSENSITIVE)
        private val ATTRIBUTE_CONTEXTS_PATTERN = Pattern.compile(".*contexts:([\\w]+(?:, *[\\w]+)*).*", CASE_INSENSITIVE)

        private fun fileHeaderExceptionMessage(file: Resource): String {
            return "${file.file.path} did not begin with the expected comment:\n$LINE_COMMENT$FILE_HEADER"
        }
    }

}
