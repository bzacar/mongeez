package org.mongeez.reader.js

import org.mongeez.commands.ChangeSet
import org.mongeez.reader.ChangeSetReaderUtil
import org.mongeez.reader.js.FormattedJavascriptLinesProvider.Companion.LINE_COMMENT
import org.springframework.core.io.Resource
import java.text.ParseException

internal data class FormattedJavascriptLines
constructor(private val lines: List<Any>, private val file: Resource) {
    val size = lines.size

    operator fun get(index: Int): Any = lines[index]

    fun getAsChangeSet(index: Int): ChangeSet {
        return lines[index] as? ChangeSet
                ?: throw ParseException("$file has content outside of a changeset.  " +
                        "To start a changeset, add a comment in the format:\n" +
                        "${LINE_COMMENT}changeset author:id", 0)
    }

    fun isString(index: Int): Boolean {
        return index < size && lines[index] is String
    }

    fun getChangeSets(): List<ChangeSet> {
        return lines.filterIsInstance<ChangeSet>()
    }

    fun populateChangeSetResourceInfo(changeSet: ChangeSet) {
        ChangeSetReaderUtil.populateChangeSetResourceInfo(changeSet, file)
    }
}
