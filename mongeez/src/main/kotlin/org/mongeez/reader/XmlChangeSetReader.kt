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

        addObjectCreate(MONGO_CHANGE_LOG_TAG, ChangeSetList::class.java)
        addObjectCreate(MONGO_CHANGE_LOG_CHANGE_SET_TAG, ChangeSet::class.java)
        addSetProperties(MONGO_CHANGE_LOG_CHANGE_SET_TAG)
        addSetNext(MONGO_CHANGE_LOG_CHANGE_SET_TAG, "add")

        addObjectCreate(MONGO_CHANGE_LOG_CHANGE_SET_SCRIPT_TAG, Script::class.java)
        addBeanPropertySetter(MONGO_CHANGE_LOG_CHANGE_SET_SCRIPT_TAG, "body")
        addSetNext(MONGO_CHANGE_LOG_CHANGE_SET_SCRIPT_TAG, "add")
    }

    override fun supports(file: Resource) = true

    override fun getChangeSets(file: Resource): List<ChangeSet> {
        LOGGER.info("Parsing XML Change Set File {}", file.filename)
        return getChangeSetsList(file).onEach { changeSet ->
            ChangeSetReaderUtil.populateChangeSetResourceInfo(changeSet, file)
        }
    }

    private fun getChangeSetsList(file: Resource): List<ChangeSet> {
        try {
            val changeFileSet = digester.parse<Any>(file.inputStream) as? ChangeSetList
            return changeFileSet?.list.orEmpty().also {
                LOGGER.warn("Ignoring change file {}, the parser returned null. Please check your formatting.", file.filename)
            }
        } catch (e: IOException) {
            throw ValidationException(e)
        } catch (e: org.xml.sax.SAXException) {
            throw ValidationException(e)
        }
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(XmlChangeSetReader::class.java)
        private const val MONGO_CHANGE_LOG_TAG = "mongoChangeLog"
        private const val MONGO_CHANGE_LOG_CHANGE_SET_TAG = "$MONGO_CHANGE_LOG_TAG/changeSet"
        private const val MONGO_CHANGE_LOG_CHANGE_SET_SCRIPT_TAG = "$MONGO_CHANGE_LOG_CHANGE_SET_TAG/script"
    }
}
