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
import org.mongeez.reader.js.FormattedJavascriptLinesProvider
import org.mongeez.reader.js.FormattedJavascriptLinesToChangeSetListConverter
import org.mongeez.validation.ValidationException
import org.springframework.core.io.Resource
import java.io.IOException
import java.nio.charset.Charset
import java.text.ParseException

class FormattedJavascriptChangeSetReader
constructor(cs: Charset = Charset.forName("UTF-8")) : ChangeSetReader {
    private val formattedJavascriptLinesProvider = FormattedJavascriptLinesProvider(cs)
    private val formattedJavascriptLinesToChangeSetListConverter = FormattedJavascriptLinesToChangeSetListConverter()

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

    private fun parse(file: Resource): List<ChangeSet> {
        val lines = formattedJavascriptLinesProvider.parse(file)
        return formattedJavascriptLinesToChangeSetListConverter.convert(lines)
    }
}
