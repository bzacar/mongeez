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
import org.mongeez.commands.ChangeFile
import org.mongeez.commands.ChangeFileSet
import org.mongeez.validation.ValidationException
import org.slf4j.LoggerFactory
import org.springframework.core.io.Resource
import java.io.IOException

class FilesetXMLReader {

    fun getFiles(file: Resource): List<Resource> {
        try {
            val digester = getDigester()
            logger.info("Parsing XML Fileset file {}", file.filename)
            val changeFileSet = digester.parse<Any>(file.inputStream) as? ChangeFileSet
            return changeFileSet?.getResourceList(file)
                    ?: throw ValidationException(getValidationExceptionMessage(file))
        } catch (e: IOException) {
            throw ValidationException(e)
        } catch (e: org.xml.sax.SAXException) {
            throw ValidationException(e)
        }
    }

    private fun ChangeFileSet.getResourceList(file: Resource): List<Resource> {
        val changeFiles = getChangeFiles()
        logger.info("Num of changefiles found " + changeFiles.size)
        return changeFiles
                .map { changeFile ->
                    file.createRelative(changeFile.path)
                }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(FilesetXMLReader::class.java)

        private fun getDigester() = Digester().apply {
            validating = false
            addObjectCreate("changeFiles", ChangeFileSet::class.java)
            addObjectCreate("changeFiles/file", ChangeFile::class.java)
            addSetProperties("changeFiles/file")
            addSetNext("changeFiles/file", "add")
        }

        private fun getValidationExceptionMessage(file: Resource)
                = "The file ${file.filename} doesn't seem to contain a changeFiles declaration. Are you using the correct file to initialize Mongeez?"
    }
}
