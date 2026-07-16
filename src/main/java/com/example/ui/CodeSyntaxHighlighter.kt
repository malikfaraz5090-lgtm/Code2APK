package com.example.ui

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import com.example.ui.theme.*
import java.util.regex.Pattern

class CodeSyntaxHighlighter(private val language: String) : VisualTransformation {

    override fun filter(text: AnnotatedString): TransformedText {
        return TransformedText(highlight(text.text, language), OffsetMapping.Identity)
    }

    private fun highlight(code: String, lang: String): AnnotatedString {
        val builder = AnnotatedString.Builder(code)
        if (code.isEmpty()) return builder.toAnnotatedString()

        try {
            when (lang.lowercase()) {
                "html" -> {
                    // 1. Highlight tags (e.g. <div>, </html>, <meta...>)
                    val tagPattern = Pattern.compile("(?s)<[^>]*?>")
                    val tagMatcher = tagPattern.matcher(code)
                    while (tagMatcher.find()) {
                        builder.addStyle(
                            SpanStyle(color = CodeHtml),
                            tagMatcher.start(),
                            tagMatcher.end()
                        )
                    }

                    // 2. Overwrite strings inside quotes inside tags
                    val stringPattern = Pattern.compile("\"(.*?)\"|'(.*?)'")
                    val stringMatcher = stringPattern.matcher(code)
                    while (stringMatcher.find()) {
                        builder.addStyle(
                            SpanStyle(color = CodeString),
                            stringMatcher.start(),
                            stringMatcher.end()
                        )
                    }

                    // 3. Highlight comments: <!-- ... -->
                    val commentPattern = Pattern.compile("<!--.*?-->", Pattern.DOTALL)
                    val commentMatcher = commentPattern.matcher(code)
                    while (commentMatcher.find()) {
                        builder.addStyle(
                            SpanStyle(color = CodeComment),
                            commentMatcher.start(),
                            commentMatcher.end()
                        )
                    }
                }
                "css" -> {
                    // 1. Highlight Properties (e.g., color, background-color)
                    val propPattern = Pattern.compile("([a-zA-Z\\-]+)\\s*:")
                    val propMatcher = propPattern.matcher(code)
                    while (propMatcher.find()) {
                        builder.addStyle(
                            SpanStyle(color = CodeKeyword),
                            propMatcher.start(1),
                            propMatcher.end(1)
                        )
                    }

                    // 2. Highlight values and units (px, rem, %, hex colors)
                    val valuePattern = Pattern.compile("\\b(\\d+px|\\d+rem|\\d+%|#[0-9a-fA-F]{3,6})\\b")
                    val valueMatcher = valuePattern.matcher(code)
                    while (valueMatcher.find()) {
                        builder.addStyle(
                            SpanStyle(color = CodeNumber),
                            valueMatcher.start(),
                            valueMatcher.end()
                        )
                    }

                    // 3. Highlight selectors
                    val selectorPattern = Pattern.compile("([.#a-zA-Z0-9_\\-\\s]+)\\s*\\{")
                    val selectorMatcher = selectorPattern.matcher(code)
                    while (selectorMatcher.find()) {
                        builder.addStyle(
                            SpanStyle(color = CodeHtml),
                            selectorMatcher.start(1),
                            selectorMatcher.end(1)
                        )
                    }

                    // 4. Comments
                    val commentPattern = Pattern.compile("/\\*.*?\\*/", Pattern.DOTALL)
                    val commentMatcher = commentPattern.matcher(code)
                    while (commentMatcher.find()) {
                        builder.addStyle(
                            SpanStyle(color = CodeComment),
                            commentMatcher.start(),
                            commentMatcher.end()
                        )
                    }
                }
                "js" -> {
                    // 1. Keywords
                    val kwPattern = Pattern.compile(
                        "\\b(const|let|var|function|return|if|else|for|while|import|export|class|new|document|window|alert)\\b"
                    )
                    val kwMatcher = kwPattern.matcher(code)
                    while (kwMatcher.find()) {
                        builder.addStyle(
                            SpanStyle(color = CodeKeyword),
                            kwMatcher.start(),
                            kwMatcher.end()
                        )
                    }

                    // 2. Numbers
                    val numPattern = Pattern.compile("\\b(\\d+)\\b")
                    val numMatcher = numPattern.matcher(code)
                    while (numMatcher.find()) {
                        builder.addStyle(
                            SpanStyle(color = CodeNumber),
                            numMatcher.start(),
                            numMatcher.end()
                        )
                    }

                    // 3. Strings
                    val strPattern = Pattern.compile("\"(.*?)\"|'(.*?)'")
                    val strMatcher = strPattern.matcher(code)
                    while (strMatcher.find()) {
                        builder.addStyle(
                            SpanStyle(color = CodeString),
                            strMatcher.start(),
                            strMatcher.end()
                        )
                    }

                    // 4. Comments (single & multi-line)
                    val commentPattern = Pattern.compile("//.*|/\\*.*?\\*/", Pattern.DOTALL)
                    val commentMatcher = commentPattern.matcher(code)
                    while (commentMatcher.find()) {
                        builder.addStyle(
                            SpanStyle(color = CodeComment),
                            commentMatcher.start(),
                            commentMatcher.end()
                        )
                    }
                }
            }
        } catch (e: Exception) {
            // Fallback gracefully to default style on regex errors
        }

        return builder.toAnnotatedString()
    }
}
