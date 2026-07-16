package com.example.util

import android.content.Context
import android.net.Uri
import com.example.data.Project
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

object ZipUtils {

    /**
     * Compresses a Project's HTML, CSS, and JS files into a ZIP byte array.
     */
    fun exportProjectToZipBytes(project: Project): ByteArray {
        val outputStream = ByteArrayOutputStream()
        ZipOutputStream(outputStream).use { zipOut ->
            // Add index.html
            zipOut.putNextEntry(ZipEntry("index.html"))
            zipOut.write(project.htmlCode.toByteArray(Charsets.UTF_8))
            zipOut.closeEntry()

            // Add style.css
            zipOut.putNextEntry(ZipEntry("style.css"))
            zipOut.write(project.cssCode.toByteArray(Charsets.UTF_8))
            zipOut.closeEntry()

            // Add script.js
            zipOut.putNextEntry(ZipEntry("script.js"))
            zipOut.write(project.jsCode.toByteArray(Charsets.UTF_8))
            zipOut.closeEntry()
        }
        return outputStream.toByteArray()
    }

    /**
     * Extracts index.html, style.css, and script.js files from a ZIP InputStream.
     * Returns a Triple of (html, css, js).
     */
    fun importProjectFromZip(inputStream: InputStream): Triple<String, String, String>? {
        var htmlContent = ""
        var cssContent = ""
        var jsContent = ""

        try {
            ZipInputStream(inputStream).use { zipIn ->
                var entry: ZipEntry? = zipIn.nextEntry
                while (entry != null) {
                    val name = entry.name
                    if (!entry.isDirectory) {
                        val output = ByteArrayOutputStream()
                        val buffer = ByteArray(1024)
                        var len = zipIn.read(buffer)
                        while (len > 0) {
                            output.write(buffer, 0, len)
                            len = zipIn.read(buffer)
                        }
                        val content = output.toString("UTF-8")
                        when (name.lowercase()) {
                            "index.html" -> htmlContent = content
                            "style.css" -> cssContent = content
                            "script.js" -> jsContent = content
                        }
                    }
                    zipIn.closeEntry()
                    entry = zipIn.nextEntry
                }
            }
            if (htmlContent.isNotEmpty() || cssContent.isNotEmpty() || jsContent.isNotEmpty()) {
                return Triple(htmlContent, cssContent, jsContent)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}
