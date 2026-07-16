package com.example.data

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "projects")
data class Project(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val packageName: String,
    val htmlCode: String,
    val cssCode: String,
    val jsCode: String,
    val iconColor: Int, // Hex color value (e.g. 0xFF00C853)
    val iconSymbol: String, // E.g., "code", "calculate", "check", "sports_esports"
    val lastUpdated: Long = System.currentTimeMillis()
)

@Dao
interface ProjectDao {
    @Query("SELECT * FROM projects ORDER BY lastUpdated DESC")
    fun getAllProjects(): Flow<List<Project>>

    @Query("SELECT * FROM projects WHERE id = :id")
    suspend fun getProjectById(id: Int): Project?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: Project): Long

    @Update
    suspend fun updateProject(project: Project)

    @Delete
    suspend fun deleteProject(project: Project)

    @Query("DELETE FROM projects WHERE id = :id")
    suspend fun deleteProjectById(id: Int)
}

@Database(entities = [Project::class], version = 1, exportSchema = false)
abstract class ProjectDatabase : RoomDatabase() {
    abstract fun projectDao(): ProjectDao

    companion object {
        @Volatile
        private var INSTANCE: ProjectDatabase? = null

        fun getDatabase(context: Context): ProjectDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ProjectDatabase::class.java,
                    "code2apk_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

class ProjectRepository(private val projectDao: ProjectDao) {
    val allProjects: Flow<List<Project>> = projectDao.getAllProjects()

    suspend fun getProjectById(id: Int): Project? {
        return projectDao.getProjectById(id)
    }

    suspend fun insert(project: Project): Long {
        return projectDao.insertProject(project)
    }

    suspend fun update(project: Project) {
        projectDao.updateProject(project)
    }

    suspend fun delete(project: Project) {
        projectDao.deleteProject(project)
    }

    suspend fun deleteById(id: Int) {
        projectDao.deleteProjectById(id)
    }
}

object Templates {
    val Portfolio = Project(
        name = "My Portfolio",
        packageName = "com.code2apk.portfolio",
        iconColor = 0xFF2196F3.toInt(), // Blue
        iconSymbol = "person",
        htmlCode = """
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Portfolio</title>
    <link rel="stylesheet" href="style.css">
</head>
<body>
    <header>
        <div class="logo">⚡ Code2APK Pro</div>
        <button id="theme-toggle" class="btn-toggle">🌙 Theme</button>
    </header>

    <main class="container">
        <section class="hero">
            <div class="avatar">👨‍💻</div>
            <h1>Alex Johnson</h1>
            <p class="subtitle">Creative Full-Stack Web Developer</p>
            <p class="description">
                Building responsive, gorgeous, and fully optimized web experiences. Powered by Code2APK Studio!
            </p>
            <div class="social-links">
                <button class="btn accent" onclick="alert('Contacting Alex...')">Hire Me</button>
                <button class="btn secondary" onclick="alert('Viewing Resume...')">My Resume</button>
            </div>
        </section>

        <section class="skills">
            <h2>My Skills</h2>
            <div class="skills-grid">
                <div class="skill-card">HTML5</div>
                <div class="skill-card">CSS3</div>
                <div class="skill-card">JavaScript</div>
                <div class="skill-card">Android WebView</div>
            </div>
        </section>

        <section class="projects">
            <h2>Recent Work</h2>
            <div class="project-card">
                <h3>🎮 Astro Battle</h3>
                <p>A web canvas action game bundled with Code2APK Studio.</p>
                <span class="tag">HTML5 / Canvas</span>
            </div>
            <div class="project-card">
                <h3>📊 Finance Tracker</h3>
                <p>Personal finance assistant using localStorage.</p>
                <span class="tag">JavaScript</span>
            </div>
        </section>
    </main>

    <footer>
        <p>&copy; 2026 Alex Johnson. Made on Android with Code2APK.</p>
    </footer>

    <script src="script.js"></script>
</body>
</html>
        """.trimIndent(),
        cssCode = """
:root {
    --bg-primary: #f8f9fa;
    --bg-secondary: #ffffff;
    --text-primary: #212529;
    --text-secondary: #6c757d;
    --accent: #00C853;
    --accent-hover: #00a442;
    --border: #e9ecef;
}

[data-theme="dark"] {
    --bg-primary: #121212;
    --bg-secondary: #1e1e1e;
    --text-primary: #e0e0e0;
    --text-secondary: #a0a0a0;
    --border: #333333;
}

body {
    margin: 0;
    font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif;
    background-color: var(--bg-primary);
    color: var(--text-primary);
    transition: background-color 0.3s, color 0.3s;
}

header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 1rem 1.5rem;
    background-color: var(--bg-secondary);
    border-bottom: 1px solid var(--border);
}

.logo {
    font-size: 1.2rem;
    font-weight: bold;
    color: var(--accent);
}

.btn-toggle {
    background: transparent;
    border: 1px solid var(--border);
    color: var(--text-primary);
    padding: 0.5rem 1rem;
    border-radius: 20px;
    cursor: pointer;
}

.container {
    max-width: 800px;
    margin: 2rem auto;
    padding: 0 1.5rem;
}

.hero {
    text-align: center;
    padding: 3rem 1.5rem;
    background-color: var(--bg-secondary);
    border-radius: 12px;
    border: 1px solid var(--border);
    box-shadow: 0 4px 6px rgba(0,0,0,0.05);
}

.avatar {
    font-size: 5rem;
    margin-bottom: 1rem;
}

h1 {
    margin: 0.5rem 0;
}

.subtitle {
    font-size: 1.2rem;
    color: var(--accent);
    margin-bottom: 1rem;
}

.description {
    color: var(--text-secondary);
    line-height: 1.6;
    max-width: 600px;
    margin: 0 auto 1.5rem;
}

.btn {
    padding: 0.75rem 1.5rem;
    font-size: 1rem;
    font-weight: bold;
    border-radius: 8px;
    cursor: pointer;
    border: none;
    margin: 0.5rem;
}

.btn.accent {
    background-color: var(--accent);
    color: white;
}

.btn.accent:hover {
    background-color: var(--accent-hover);
}

.btn.secondary {
    background-color: var(--border);
    color: var(--text-primary);
}

.skills {
    margin: 3rem 0;
}

.skills-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(120px, 1fr));
    gap: 1rem;
    margin-top: 1rem;
}

.skill-card {
    background-color: var(--bg-secondary);
    border: 1px solid var(--border);
    padding: 1rem;
    text-align: center;
    border-radius: 8px;
    font-weight: bold;
}

.projects {
    margin: 3rem 0;
}

.project-card {
    background-color: var(--bg-secondary);
    border: 1px solid var(--border);
    padding: 1.5rem;
    border-radius: 8px;
    margin-bottom: 1rem;
}

.project-card h3 {
    margin-top: 0;
}

.tag {
    display: inline-block;
    font-size: 0.8rem;
    background-color: var(--border);
    color: var(--text-secondary);
    padding: 0.25rem 0.5rem;
    border-radius: 4px;
    margin-top: 0.5rem;
}

footer {
    text-align: center;
    padding: 2rem;
    color: var(--text-secondary);
    font-size: 0.9rem;
    border-top: 1px solid var(--border);
    background-color: var(--bg-secondary);
}
        """.trimIndent(),
        jsCode = """
// Live dark mode toggle
const themeToggle = document.getElementById('theme-toggle');

themeToggle.addEventListener('click', () => {
    const currentTheme = document.body.getAttribute('data-theme');
    if (currentTheme === 'dark') {
        document.body.removeAttribute('data-theme');
        themeToggle.textContent = '🌙 Theme';
    } else {
        document.body.setAttribute('data-theme', 'dark');
        themeToggle.textContent = '☀️ Theme';
    }
});
        """.trimIndent()
    )

    val Calculator = Project(
        name = "Neon Calculator",
        packageName = "com.code2apk.neoncalc",
        iconColor = 0xFFFF9800.toInt(), // Orange
        iconSymbol = "calculate",
        htmlCode = """
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Neon Calculator</title>
    <link rel="stylesheet" href="style.css">
</head>
<body>
    <div class="calculator">
        <div class="display">
            <div class="previous" id="prev-val"></div>
            <div class="current" id="curr-val">0</div>
        </div>
        <div class="buttons">
            <button class="btn action" onclick="clearDisplay()">C</button>
            <button class="btn action" onclick="deleteChar()">DEL</button>
            <button class="btn op" onclick="appendOp('/')">÷</button>
            <button class="btn op" onclick="appendOp('*')">×</button>

            <button class="btn" onclick="appendNum('7')">7</button>
            <button class="btn" onclick="appendNum('8')">8</button>
            <button class="btn" onclick="appendNum('9')">9</button>
            <button class="btn op" onclick="appendOp('-')">-</button>

            <button class="btn" onclick="appendNum('4')">4</button>
            <button class="btn" onclick="appendNum('5')">5</button>
            <button class="btn" onclick="appendNum('6')">6</button>
            <button class="btn op" onclick="appendOp('+')">+</button>

            <button class="btn" onclick="appendNum('1')">1</button>
            <button class="btn" onclick="appendNum('2')">2</button>
            <button class="btn" onclick="appendNum('3')">3</button>
            <button class="btn eval" onclick="calculate()">=</button>

            <button class="btn zero" onclick="appendNum('0')">0</button>
            <button class="btn" onclick="appendNum('.')">.</button>
        </div>
    </div>
    <script src="script.js"></script>
</body>
</html>
        """.trimIndent(),
        cssCode = """
body {
    background-color: #0d0e12;
    color: white;
    font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
    display: flex;
    justify-content: center;
    align-items: center;
    height: 100vh;
    margin: 0;
}

.calculator {
    width: 320px;
    background: #171923;
    border-radius: 20px;
    padding: 20px;
    box-shadow: 0 10px 25px rgba(0,0,0,0.5), 0 0 10px rgba(0, 200, 83, 0.2);
    border: 1px solid #2d3748;
}

.display {
    background: #0f111a;
    padding: 20px;
    border-radius: 12px;
    text-align: right;
    margin-bottom: 20px;
    min-height: 80px;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    border: 1px solid #1e2235;
}

.previous {
    color: #00C853;
    font-size: 1rem;
    opacity: 0.7;
    word-break: break-all;
}

.current {
    font-size: 2.2rem;
    font-weight: bold;
    word-break: break-all;
    overflow-x: auto;
}

.buttons {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 12px;
}

.btn {
    border: none;
    border-radius: 12px;
    padding: 15px;
    font-size: 1.25rem;
    font-weight: bold;
    background: #1e2235;
    color: #e2e8f0;
    cursor: pointer;
    transition: all 0.1s ease;
}

.btn:active {
    transform: scale(0.95);
    background: #2d3748;
}

.btn.op {
    background: #2b324f;
    color: #00C853;
}

.btn.action {
    background: #f56565;
    color: white;
}

.btn.eval {
    grid-row: span 2;
    background: #00C853;
    color: white;
    height: 100%;
}

.btn.eval:active {
    background: #00a442;
}

.btn.zero {
    grid-column: span 2;
}
        """.trimIndent(),
        jsCode = """
let currentInput = '';
let previousInput = '';
let operator = null;

const currVal = document.getElementById('curr-val');
const prevVal = document.getElementById('prev-val');

function appendNum(num) {
    if (num === '.' && currentInput.includes('.')) return;
    if (currentInput === '0' && num !== '.') {
        currentInput = num;
    } else {
        currentInput += num;
    }
    updateUI();
}

function appendOp(op) {
    if (currentInput === '') return;
    if (previousInput !== '') {
        calculate();
    }
    operator = op;
    previousInput = currentInput;
    currentInput = '';
    updateUI();
}

function calculate() {
    let result;
    const prev = parseFloat(previousInput);
    const curr = parseFloat(currentInput);
    if (isNaN(prev) || isNaN(curr)) return;

    switch (operator) {
        case '+': result = prev + curr; break;
        case '-': result = prev - curr; break;
        case '*': result = prev * curr; break;
        case '/': result = curr === 0 ? 'Error' : prev / curr; break;
        default: return;
    }

    currentInput = result.toString();
    operator = null;
    previousInput = '';
    updateUI();
}

function clearDisplay() {
    currentInput = '0';
    previousInput = '';
    operator = null;
    updateUI();
}

function deleteChar() {
    if (currentInput.length <= 1) {
        currentInput = '0';
    } else {
        currentInput = currentInput.slice(0, -1);
    }
    updateUI();
}

function updateUI() {
    currVal.textContent = currentInput || '0';
    if (operator) {
        prevVal.textContent = `${'$'}{previousInput} ${'$'}{operator}`;
    } else {
        prevVal.textContent = '';
    }
}
        """.trimIndent()
    )

    val Todo = Project(
        name = "Task Master",
        packageName = "com.code2apk.todo",
        iconColor = 0xFF4CAF50.toInt(), // Green
        iconSymbol = "check_circle",
        htmlCode = """
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Task Master</title>
    <link rel="stylesheet" href="style.css">
</head>
<body>
    <div class="todo-app">
        <header>
            <h1>📝 Task Master</h1>
            <p>Made in Code2APK Studio</p>
        </header>

        <form id="todo-form">
            <input type="text" id="todo-input" placeholder="What needs to be done?" required autocomplete="off">
            <button type="submit" class="btn-add">Add</button>
        </form>

        <div class="filters">
            <button class="filter-btn active" id="filter-all" onclick="filterTasks('all')">All</button>
            <button class="filter-btn" id="filter-active" onclick="filterTasks('active')">Active</button>
            <button class="filter-btn" id="filter-completed" onclick="filterTasks('completed')">Completed</button>
        </div>

        <ul class="todo-list" id="todo-list">
            <!-- Tasks loaded from JS -->
        </ul>
    </div>
    <script src="script.js"></script>
</body>
</html>
        """.trimIndent(),
        cssCode = """
body {
    background-color: #f3f4f6;
    color: #1f2937;
    font-family: system-ui, -apple-system, sans-serif;
    padding: 20px;
    margin: 0;
    display: flex;
    justify-content: center;
}

.todo-app {
    width: 100%;
    max-width: 450px;
    background: white;
    border-radius: 16px;
    padding: 24px;
    box-shadow: 0 4px 10px rgba(0,0,0,0.06);
    margin-top: 40px;
}

header h1 {
    margin: 0;
    font-size: 1.8rem;
    color: #10b981;
}

header p {
    margin: 4px 0 20px;
    font-size: 0.9rem;
    color: #6b7280;
}

#todo-form {
    display: flex;
    gap: 10px;
    margin-bottom: 20px;
}

#todo-input {
    flex: 1;
    border: 1px solid #d1d5db;
    border-radius: 8px;
    padding: 12px;
    font-size: 1rem;
    outline: none;
    transition: border 0.2s;
}

#todo-input:focus {
    border-color: #10b981;
}

.btn-add {
    background: #10b981;
    color: white;
    border: none;
    border-radius: 8px;
    padding: 12px 20px;
    font-size: 1rem;
    font-weight: bold;
    cursor: pointer;
}

.filters {
    display: flex;
    gap: 8px;
    margin-bottom: 16px;
}

.filter-btn {
    border: 1px solid #e5e7eb;
    background: white;
    border-radius: 20px;
    padding: 6px 14px;
    cursor: pointer;
    font-size: 0.85rem;
    color: #4b5563;
}

.filter-btn.active {
    background: #10b981;
    color: white;
    border-color: #10b981;
}

.todo-list {
    list-style: none;
    padding: 0;
    margin: 0;
}

.todo-item {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 12px;
    border-bottom: 1px solid #f3f4f6;
    gap: 12px;
}

.todo-item.completed span {
    text-decoration: line-through;
    color: #9ca3af;
}

.todo-item input[type="checkbox"] {
    width: 20px;
    height: 20px;
    accent-color: #10b981;
    cursor: pointer;
}

.todo-item span {
    flex: 1;
    font-size: 1rem;
}

.btn-delete {
    background: transparent;
    border: none;
    color: #ef4444;
    cursor: pointer;
    font-size: 1.1rem;
}
        """.trimIndent(),
        jsCode = """
let todos = JSON.parse(localStorage.getItem('todos')) || [
    { id: 1, text: "Explore Code2APK features", completed: false },
    { id: 2, text: "Build index.html offline", completed: true },
    { id: 3, text: "Simulate a premium 1-click cloud build", completed: false }
];
let currentFilter = 'all';

const form = document.getElementById('todo-form');
const input = document.getElementById('todo-input');
const list = document.getElementById('todo-list');

form.addEventListener('submit', (e) => {
    e.preventDefault();
    const text = input.value.trim();
    if (!text) return;
    todos.push({
        id: Date.now(),
        text: text,
        completed: false
    });
    input.value = '';
    saveAndRender();
});

function toggleTodo(id) {
    todos = todos.map(todo => {
        if (todo.id === id) {
            return { ...todo, completed: !todo.completed };
        }
        return todo;
    });
    saveAndRender();
}

function deleteTodo(id) {
    todos = todos.filter(todo => todo.id !== id);
    saveAndRender();
}

function filterTasks(type) {
    currentFilter = type;
    document.querySelectorAll('.filter-btn').forEach(btn => btn.classList.remove('active'));
    document.getElementById(`filter-` + type).classList.add('active');
    render();
}

function saveAndRender() {
    localStorage.setItem('todos', JSON.stringify(todos));
    render();
}

function render() {
    list.innerHTML = '';
    const filtered = todos.filter(todo => {
        if (currentFilter === 'active') return !todo.completed;
        if (currentFilter === 'completed') return todo.completed;
        return true;
    });

    filtered.forEach(todo => {
        const li = document.createElement('li');
        li.className = `todo-item ` + (todo.completed ? 'completed' : '');
        li.innerHTML = `
            <input type="checkbox" ` + (todo.completed ? 'checked' : '') + ` onclick="toggleTodo(` + todo.id + `)">
            <span>` + todo.text + `</span>
            <button class="btn-delete" onclick="deleteTodo(` + todo.id + `)">🗑️</button>
        `;
        list.appendChild(li);
    });
}

// Initial render
render();
        """.trimIndent()
    )

    val RetroGame = Project(
        name = "Retro Snake",
        packageName = "com.code2apk.retrosnake",
        iconColor = 0xFFE91E63.toInt(), // Pink
        iconSymbol = "sports_esports",
        htmlCode = """
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Retro Snake</title>
    <link rel="stylesheet" href="style.css">
</head>
<body>
    <div class="game-container">
        <header>
            <h1>🐍 Retro Snake</h1>
            <div class="score-container">
                <div>Score: <span id="score">0</span></div>
                <div>High Score: <span id="high-score">0</span></div>
            </div>
        </header>

        <canvas id="gameBoard" width="300" height="300"></canvas>

        <div class="controls-overlay" id="start-overlay">
            <button class="btn-play" onclick="startGame()">TAP TO PLAY</button>
        </div>

        <div class="mobile-controls">
            <div class="row"><button class="btn-nav" onclick="changeDirection('UP')">▲</button></div>
            <div class="row">
                <button class="btn-nav" onclick="changeDirection('LEFT')">◀</button>
                <div class="spacer"></div>
                <button class="btn-nav" onclick="changeDirection('RIGHT')">▶</button>
            </div>
            <div class="row"><button class="btn-nav" onclick="changeDirection('DOWN')">▼</button></div>
        </div>
    </div>
    <script src="script.js"></script>
</body>
</html>
        """.trimIndent(),
        cssCode = """
body {
    background-color: #121824;
    color: white;
    font-family: 'Courier New', Courier, monospace;
    display: flex;
    justify-content: center;
    align-items: center;
    min-height: 100vh;
    margin: 0;
    padding: 10px;
}

.game-container {
    width: 100%;
    max-width: 340px;
    display: flex;
    flex-direction: column;
    align-items: center;
    background: #1e293b;
    border-radius: 12px;
    padding: 16px;
    box-shadow: 0 8px 24px rgba(0,0,0,0.5);
    position: relative;
}

header {
    width: 100%;
    display: flex;
    flex-direction: column;
    align-items: center;
    margin-bottom: 12px;
}

header h1 {
    font-size: 1.5rem;
    margin: 0 0 8px;
    color: #4ade80;
}

.score-container {
    display: flex;
    justify-content: space-between;
    width: 100%;
    font-size: 0.9rem;
    color: #cbd5e1;
}

#gameBoard {
    background: #0f172a;
    border: 3px solid #334155;
    border-radius: 8px;
}

.controls-overlay {
    position: absolute;
    top: 60px;
    left: 16px;
    right: 16px;
    height: 300px;
    background: rgba(15, 23, 42, 0.85);
    display: flex;
    justify-content: center;
    align-items: center;
    border-radius: 8px;
    z-index: 10;
}

.btn-play {
    background: #4ade80;
    color: #0f172a;
    border: none;
    font-weight: bold;
    font-size: 1.2rem;
    padding: 12px 24px;
    border-radius: 6px;
    cursor: pointer;
    font-family: inherit;
    box-shadow: 0 0 10px rgba(74, 222, 128, 0.5);
}

.mobile-controls {
    display: flex;
    flex-direction: column;
    align-items: center;
    margin-top: 16px;
}

.row {
    display: flex;
    justify-content: center;
}

.btn-nav {
    width: 50px;
    height: 50px;
    font-size: 1.2rem;
    background: #334155;
    color: white;
    border: none;
    border-radius: 8px;
    margin: 4px;
    cursor: pointer;
}

.btn-nav:active {
    background: #4ade80;
    color: #0f172a;
}

.spacer {
    width: 50px;
    height: 50px;
    margin: 4px;
}
        """.trimIndent(),
        jsCode = """
const canvas = document.getElementById("gameBoard");
const ctx = canvas.getContext("2d");

const grid = 15;
let count = 0;
let score = 0;
let highScore = localStorage.getItem("high-score") || 0;
document.getElementById("high-score").textContent = highScore;

let snake = {
    x: 150,
    y: 150,
    dx: grid,
    dy: 0,
    cells: [],
    maxCells: 4
};

let apple = {
    x: 300,
    y: 300
};

let gameLoopId = null;
let active = false;

function getRandomInt(min, max) {
    return Math.floor(Math.random() * (max - min)) + min;
}

function resetApple() {
    apple.x = getRandomInt(0, 20) * grid;
    apple.y = getRandomInt(0, 20) * grid;
}

function startGame() {
    document.getElementById("start-overlay").style.display = "none";
    score = 0;
    document.getElementById("score").textContent = score;
    snake.x = 150;
    snake.y = 150;
    snake.cells = [];
    snake.maxCells = 4;
    snake.dx = grid;
    snake.dy = 0;
    resetApple();
    active = true;

    if (gameLoopId) cancelAnimationFrame(gameLoopId);
    gameLoopId = requestAnimationFrame(loop);
}

function loop() {
    if (!active) return;
    gameLoopId = requestAnimationFrame(loop);

    if (++count < 6) return; // Cap speeds
    count = 0;

    ctx.clearRect(0, 0, canvas.width, canvas.height);

    snake.x += snake.dx;
    snake.y += snake.dy;

    // Wrap-around coordinates
    if (snake.x < 0) snake.x = canvas.width - grid;
    else if (snake.x >= canvas.width) snake.x = 0;

    if (snake.y < 0) snake.y = canvas.height - grid;
    else if (snake.y >= canvas.height) snake.y = 0;

    // Keep track of where snake has been
    snake.cells.unshift({ x: snake.x, y: snake.y });

    if (snake.cells.length > snake.maxCells) {
        snake.cells.pop();
    }

    // Draw apple
    ctx.fillStyle = '#f87171';
    ctx.beginPath();
    ctx.arc(apple.x + grid/2, apple.y + grid/2, grid/2 - 1, 0, 2*Math.PI);
    ctx.fill();

    // Draw snake
    ctx.fillStyle = '#4ade80';
    snake.cells.forEach(function(cell, index) {
        ctx.fillRect(cell.x, cell.y, grid - 1, grid - 1);

        // Apple collision
        if (cell.x === apple.x && cell.y === apple.y) {
            snake.maxCells++;
            score++;
            document.getElementById("score").textContent = score;
            if (score > highScore) {
                highScore = score;
                localStorage.setItem("high-score", highScore);
                document.getElementById("high-score").textContent = highScore;
            }
            resetApple();
        }

        // Check self-collision
        for (let i = index + 1; i < snake.cells.length; i++) {
            if (cell.x === snake.cells[i].x && cell.y === snake.cells[i].y) {
                gameOver();
            }
        }
    });
}

function gameOver() {
    active = false;
    document.getElementById("start-overlay").style.display = "flex";
    document.querySelector(".btn-play").textContent = "REPLAY";
}

function changeDirection(dir) {
    if (!active) return;
    if (dir === 'LEFT' && snake.dx === 0) {
        snake.dx = -grid;
        snake.dy = 0;
    } else if (dir === 'UP' && snake.dy === 0) {
        snake.dx = 0;
        snake.dy = -grid;
    } else if (dir === 'RIGHT' && snake.dx === 0) {
        snake.dx = grid;
        snake.dy = 0;
    } else if (dir === 'DOWN' && snake.dy === 0) {
        snake.dx = 0;
        snake.dy = grid;
    }
}

// Arrow key controls
document.addEventListener('keydown', function(e) {
    if (e.which === 37) changeDirection('LEFT');
    else if (e.which === 38) changeDirection('UP');
    else if (e.which === 39) changeDirection('RIGHT');
    else if (e.which === 40) changeDirection('DOWN');
});
        """.trimIndent()
    )
}
