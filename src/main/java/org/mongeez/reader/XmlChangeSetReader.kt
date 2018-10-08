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

package org.mongeez.reader

import org.apache.commons.digester3.Digester
import org.mongeez.commands.ChangeSet
import org.mongeez.commands.ChangeSetList
import org.mongeez.commands.Script
import org.mongeez.validation.ValidationException
import org.slf4j.LoggerFactory
import org.springframework.core.io.Resource
import java.io.IOException

class XmlChangeSetReader
internal constructor() : ChangeSetReader {

    private val digester = Digester().apply {
        validating = false

        addObjectCreate("mongoChangeLog", ChangeSetList::class.java)
        addObjectCreate("mongoChangeLog/changeSet", ChangeSet::class.java)
        addSetProperties("mongoChangeLog/changeSet")
        addSetNext("mongoChangeLog/changeSet", "add")

        addObjectCreate("mongoChangeLog/changeSet/script", Script::class.java)
        addBeanPropertySetter("mongoChangeLog/changeSet/script", "body")
        addSetNext("mongoChangeLog/changeSet/script", "add")
    }

    override fun supports(file: Resource) = true

    override fun getChangeSets(file: Resource): List<ChangeSet> {
        logger.info("Parsing XML Change Set File {}", file.filename)
        return getChangeSetsList(file).onEach { changeSet ->
            ChangeSetReaderUtil.populateChangeSetResourceInfo(changeSet, file)
        }
    }

    private fun getChangeSetsList(file: Resource): List<ChangeSet> {
        try {
            val changeFileSet = digester.parse<Any>(file.inputStream) as? ChangeSetList
            return changeFileSet?.list ?: emptyList<ChangeSet>().also {
                logger.warn("Ignoring change file {}, the parser returned null. Please check your formatting.", file.filename)
            }
        } catch (e: IOException) {
            throw ValidationException(e)
        } catch (e: org.xml.sax.SAXException) {
            throw ValidationException(e)
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(XmlChangeSetReader::class.java)
    }
}
