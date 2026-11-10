package io.github.romanfabio.voidapps.core.licenses.data

private val PARAGRAPH_BREAK = Regex("""\n\s*\n""")
private val WHITESPACE = Regex("""\s+""")

/**
 * Joins the hard-wrapped lines of a plain-text license into paragraphs, so the text
 * wraps to the screen width instead of the 80-column width of the LICENSE file.
 */
internal fun String.reflowParagraphs(): String = replace("\r\n", "\n")
    .trim()
    .split(PARAGRAPH_BREAK)
    .joinToString("\n\n") { it.trim().replace(WHITESPACE, " ") }
