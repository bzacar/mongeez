package org.mongeez.reader.js

import org.mongeez.commands.ChangeSet
import org.mongeez.commands.Script
import java.text.ParseException

internal class FormattedJavascriptLinesToChangeSetListConverter {

    fun convert(lines: FormattedJavascriptLines): List<ChangeSet> {
        var lineIndex = 0
        while (lineIndex < lines.size) {
            val changeSet = lines.getAsChangeSet(lineIndex++)
            lines.populateChangeSetResourceInfo(changeSet)
            val scriptBody = StringBuilder()
            while (lines.isString(lineIndex)) {
                scriptBody.appendln(lines[lineIndex++])
            }
            changeSet.addScript(scriptBody)
        }
        return lines.getChangeSets()
    }

    private fun ChangeSet.addScript(scriptBody: StringBuilder) {
        val script = scriptBody.toString().trim()
                .takeIf { it.isNotEmpty() }
                ?.let { Script(it) }
                ?: throw ParseException("No JavaScript found for changeset $author:$changeId", -1)
        add(script)
    }
}
