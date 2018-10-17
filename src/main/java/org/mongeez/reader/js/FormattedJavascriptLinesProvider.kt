package org.mongeez.reader.js

import org.mongeez.commands.ChangeSet
import org.springframework.core.io.Resource
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.nio.charset.Charset
import java.text.ParseException
import java.util.regex.Pattern
import java.util.regex.Pattern.CASE_INSENSITIVE

internal class FormattedJavascriptLinesProvider
constructor(private val cs: Charset = Charset.forName("UTF-8")) {
    private val formattedJavascriptChangeSetParser = FormattedJavascriptChangeSetParser()

    fun parse(file: Resource): FormattedJavascriptLines {
        val reader = BufferedReader(InputStreamReader(file.inputStream, cs))
        try {
            parseFileHeader(file, reader)
            val lines = reader.lineSequence()
                    .map { formattedJavascriptChangeSetParser.parse(it) ?: it }
                    .filter { it is ChangeSet || (it is String && it.isCode()) }
                    .toList()
            return FormattedJavascriptLines(lines, file)
        } finally {
            try {
                reader.close()
            } catch (ignore: IOException) {
            }
        }
    }

    private fun parseFileHeader(file: Resource, reader: BufferedReader) {
        reader.readLine()
                ?.takeIf { FILE_HEADER_PATTERN.matcher(it).matches() }
                ?: throw ParseException(fileHeaderExceptionMessage(file), -1)
    }

    companion object {
        const val LINE_COMMENT = "//"
        private val FILE_HEADER_PATTERN = Pattern.compile("//\\s*mongeez\\s+formatted\\s+javascript\\s*", CASE_INSENSITIVE)

        private fun fileHeaderExceptionMessage(file: Resource): String {
            return "${file.file.path} did not begin with the expected comment:\n${LINE_COMMENT}mongeez formatted javascript"
        }

        private fun String.isCode() = trim().isNotEmpty() && !startsWith(LINE_COMMENT)
    }
}
