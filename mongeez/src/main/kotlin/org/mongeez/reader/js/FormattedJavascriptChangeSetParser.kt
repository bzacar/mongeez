package org.mongeez.reader.js

import org.mongeez.commands.ChangeSet
import java.util.regex.Matcher
import java.util.regex.Pattern
import java.util.regex.Pattern.CASE_INSENSITIVE

internal class FormattedJavascriptChangeSetParser {

    fun parse(line: String): ChangeSet? {
        return CHANGESET_PATTERN.matcher(line)
                ?.takeIf { it.matches() }
                ?.let { matcher ->
                    ChangeSet().apply {
                        author = matcher.group(1)
                        changeId = matcher.group(2)
                        isRunAlways = parseAttribute(ATTRIBUTE_RUN_ALWAYS_PATTERN.matcher(line))
                        useUtil = parseAttribute(ATTRIBUTE_USE_UTIL_PATTERN.matcher(line))
                        setContexts(parseAttributeString(ATTRIBUTE_CONTEXTS_PATTERN.matcher(line)))
                    }
                }
    }

    private fun parseAttribute(attributeMatcher: Matcher): Boolean {
        return attributeMatcher
                .takeIf { it.matches() }
                ?.group(1)?.toBoolean()
                ?: false
    }

    private fun parseAttributeString(attributeMatcher: Matcher): String? {
        return attributeMatcher.takeIf { it.matches() }?.group(1)
    }

    companion object {
        private val CHANGESET_PATTERN = Pattern.compile("//\\s*changeset\\s+([\\w\\-]+):([\\w\\-]+).*", CASE_INSENSITIVE)
        private val ATTRIBUTE_RUN_ALWAYS_PATTERN = Pattern.compile(".*runAlways:\\s*(\\w+).*", CASE_INSENSITIVE)
        private val ATTRIBUTE_USE_UTIL_PATTERN = Pattern.compile(".*useUtil:\\s*(\\w+).*", CASE_INSENSITIVE)
        private val ATTRIBUTE_CONTEXTS_PATTERN = Pattern.compile(".*contexts:([\\w]+(?:, *[\\w]+)*).*", CASE_INSENSITIVE)
    }
}
