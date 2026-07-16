package com.example.ui

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import com.example.util.ZipUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.ByteArrayInputStream
import java.util.Stack

class ProjectViewModel(application: Application) : AndroidViewModel(application) {
    private val database = ProjectDatabase.getDatabase(application)
    private val repository = ProjectRepository(database.projectDao())

    // All local projects
    val allProjects: StateFlow<List<Project>> = repository.allProjects
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Active project for editor
    var activeProject by mutableStateOf<Project?>(null)
        private set

    // Code Editor Content
    var htmlContent by mutableStateOf("")
    var cssContent by mutableStateOf("")
    var jsContent by mutableStateOf("")

    // Active Tab: "html", "css", "js"
    var activeTab by mutableStateOf("html")

    // Theme preset inside the code editor webview preview
    var isPreviewDarkTheme by mutableStateOf(true)

    // Undo / Redo Stacks for each tab
    private val htmlUndoStack = Stack<String>()
    private val htmlRedoStack = Stack<String>()
    private val cssUndoStack = Stack<String>()
    private val cssRedoStack = Stack<String>()
    private val jsUndoStack = Stack<String>()
    private val jsRedoStack = Stack<String>()

    // APK Builder configuration state
    var apkAppName by mutableStateOf("")
    var apkPackageName by mutableStateOf("")
    var apkSelectedIconColor by mutableStateOf(0xFF00C853.toInt())
    var apkSelectedIconSymbol by mutableStateOf("code")

    // Cloud APK Build Simulation State
    var buildStatus by mutableStateOf("IDLE") // IDLE, BUILDING, SUCCESS, FAILED
    var buildProgress by mutableStateOf(0f)
    val buildLogs = mutableStateListOf<String>()

    // Find and Replace Panel State
    var showFindReplace by mutableStateOf(false)
    var findQuery by mutableStateOf("")
    var replaceQuery by mutableStateOf("")

    init {
        // Prepopulate templates if database is empty
        viewModelScope.launch {
            allProjects.collectLatest { list ->
                if (list.isEmpty()) {
                    repository.insert(Templates.Portfolio)
                    repository.insert(Templates.Calculator)
                    repository.insert(Templates.Todo)
                    repository.insert(Templates.RetroGame)
                }
            }
        }
    }

    fun selectProject(project: Project) {
        activeProject = project
        htmlContent = project.htmlCode
        cssContent = project.cssCode
        jsContent = project.jsCode
        activeTab = "html"

        // Initialize APK builder fields
        apkAppName = project.name
        apkPackageName = project.packageName
        apkSelectedIconColor = project.iconColor
        apkSelectedIconSymbol = project.iconSymbol

        // Clear undo/redo histories
        htmlUndoStack.clear()
        htmlRedoStack.clear()
        cssUndoStack.clear()
        cssRedoStack.clear()
        jsUndoStack.clear()
        jsRedoStack.clear()
    }

    fun updateCode(tab: String, newCode: String) {
        val currentStack = when (tab) {
            "html" -> htmlUndoStack
            "css" -> cssUndoStack
            "js" -> jsUndoStack
            else -> return
        }
        val currentRedo = when (tab) {
            "html" -> htmlRedoStack
            "css" -> cssRedoStack
            "js" -> jsRedoStack
            else -> return
        }

        val previousValue = when (tab) {
            "html" -> htmlContent
            "css" -> cssContent
            "js" -> jsContent
            else -> ""
        }

        if (previousValue != newCode) {
            // Push history on stack if the last push is different (avoid spamming every keystore)
            if (currentStack.isEmpty() || currentStack.peek() != previousValue) {
                if (currentStack.size > 50) currentStack.removeAt(0) // Cap history
                currentStack.push(previousValue)
            }
            currentRedo.clear() // clear redo on new keystroke
        }

        when (tab) {
            "html" -> htmlContent = newCode
            "css" -> cssContent = newCode
            "js" -> jsContent = newCode
        }
    }

    fun undo() {
        when (activeTab) {
            "html" -> {
                if (htmlUndoStack.isNotEmpty()) {
                    htmlRedoStack.push(htmlContent)
                    htmlContent = htmlUndoStack.pop()
                }
            }
            "css" -> {
                if (cssUndoStack.isNotEmpty()) {
                    cssRedoStack.push(cssContent)
                    cssContent = cssUndoStack.pop()
                }
            }
            "js" -> {
                if (jsUndoStack.isNotEmpty()) {
                    jsRedoStack.push(jsContent)
                    jsContent = jsUndoStack.pop()
                }
            }
        }
    }

    fun redo() {
        when (activeTab) {
            "html" -> {
                if (htmlRedoStack.isNotEmpty()) {
                    htmlUndoStack.push(htmlContent)
                    htmlContent = htmlRedoStack.pop()
                }
            }
            "css" -> {
                if (cssRedoStack.isNotEmpty()) {
                    cssUndoStack.push(cssContent)
                    cssContent = cssRedoStack.pop()
                }
            }
            "js" -> {
                if (jsRedoStack.isNotEmpty()) {
                    jsUndoStack.push(jsContent)
                    jsContent = jsRedoStack.pop()
                }
            }
        }
    }

    fun performFindReplace() {
        if (findQuery.isEmpty()) return
        when (activeTab) {
            "html" -> htmlContent = htmlContent.replace(findQuery, replaceQuery)
            "css" -> cssContent = cssContent.replace(findQuery, replaceQuery)
            "js" -> jsContent = jsContent.replace(findQuery, replaceQuery)
        }
    }

    fun saveActiveProject() {
        val proj = activeProject ?: return
        viewModelScope.launch {
            val updated = proj.copy(
                htmlCode = htmlContent,
                cssCode = cssContent,
                jsCode = jsContent,
                name = apkAppName,
                packageName = apkPackageName,
                iconColor = apkSelectedIconColor,
                iconSymbol = apkSelectedIconSymbol,
                lastUpdated = System.currentTimeMillis()
            )
            repository.update(updated)
            activeProject = updated
        }
    }

    fun createNewProject(name: String, packageName: String) {
        viewModelScope.launch {
            val newProj = Project(
                name = name,
                packageName = packageName,
                htmlCode = """
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>$name</title>
    <link rel="stylesheet" href="style.css">
</head>
<body>
    <div style="text-align:center; padding:50px; font-family:sans-serif;">
        <h1 style="color:#00C853;">Welcome to $name</h1>
        <p>Edit index.html, style.css, and script.js in Code2APK Studio!</p>
        <button id="clickBtn" style="padding:10px 20px; font-size:16px; border-radius:8px; border:none; background:#00C853; color:white; cursor:pointer;">
            Tap Me
        </button>
    </div>
    <script src="script.js"></script>
</body>
</html>
                """.trimIndent(),
                cssCode = """
body {
    background-color: #121212;
    color: #ffffff;
    margin: 0;
}
button:active {
    transform: scale(0.95);
}
                """.trimIndent(),
                jsCode = """
document.getElementById('clickBtn').addEventListener('click', () => {
    alert('Hello from Native Web App!');
});
                """.trimIndent(),
                iconColor = 0xFF00C853.toInt(),
                iconSymbol = "code"
            )
            val id = repository.insert(newProj)
            val inserted = repository.getProjectById(id.toInt())
            if (inserted != null) {
                selectProject(inserted)
            }
        }
    }

    fun deleteProject(project: Project) {
        viewModelScope.launch {
            repository.delete(project)
            if (activeProject?.id == project.id) {
                activeProject = null
            }
        }
    }

    /**
     * Simulation of premium 1-click cloud builder.
     * Progresses with comprehensive details and logs mimicking a real compile.
     */
    fun startApkBuild() {
        if (activeProject == null) return
        buildLogs.clear()
        buildStatus = "BUILDING"
        buildProgress = 0f

        viewModelScope.launch {
            val logs = listOf(
                "Initializing cloud compilation agent..." to 0.05f,
                "Packaging Web Application bundle..." to 0.12f,
                "Validating syntax integrity of index.html..." to 0.18f,
                "Analyzing and minifying CSS stylesheets..." to 0.25f,
                "Bundling and tree-shaking script.js asset pipelines..." to 0.32f,
                "Injecting secure Android WebView platform container..." to 0.40f,
                "Parsing target SDK AndroidManifest.xml (Setting target: Android 13+)..." to 0.48f,
                "Configuring app label: '$apkAppName'..." to 0.54f,
                "Binding applicationId namespaces: '$apkPackageName'..." to 0.60f,
                "Encoding icon resources and generating vectors..." to 0.68f,
                "Compiling intermediate DEX bytecode (AAPT2 compilation)..." to 0.76f,
                "Generating unsigned release build APK bundle..." to 0.85f,
                "Signing APK with default high-security Keystore alias 'upload'..." to 0.92f,
                "Zipaligning artifacts to optimize app launch speed..." to 0.97f,
                "Build success! Standalone Android APK ready for installation." to 1.0f
            )

            for ((log, progress) in logs) {
                buildLogs.add(log)
                buildProgress = progress
                delay(800) // Realistic compile timing
            }

            buildStatus = "SUCCESS"
        }
    }

    fun getCombinedHtml(): String {
        // Embed Style and Script directly in HTML for preview
        return """
            $htmlContent
            <style>
            $cssContent
            </style>
            <script>
            $jsContent
            </script>
        """.trimIndent()
    }

    fun exportProjectZip(context: Context, project: Project, uri: Uri) {
        viewModelScope.launch {
            try {
                val bytes = ZipUtils.exportProjectToZipBytes(project)
                context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                    outputStream.write(bytes)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun importProjectZip(context: Context, uri: Uri) {
        viewModelScope.launch {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                if (inputStream != null) {
                    val result = ZipUtils.importProjectFromZip(inputStream)
                    if (result != null) {
                        val (html, css, js) = result
                        val projName = "Imported Zip"
                        val pkg = "com.code2apk.imported"

                        val newProj = Project(
                            name = projName,
                            packageName = pkg,
                            htmlCode = html,
                            cssCode = css,
                            jsCode = js,
                            iconColor = 0xFF9C27B0.toInt(), // Purple
                            iconSymbol = "cloud_download"
                        )
                        val id = repository.insert(newProj)
                        val inserted = repository.getProjectById(id.toInt())
                        if (inserted != null) {
                            selectProject(inserted)
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

// Extension function for live list logs
private fun <T> mutableStateListOf() = androidx.compose.runtime.mutableStateListOf<T>()
