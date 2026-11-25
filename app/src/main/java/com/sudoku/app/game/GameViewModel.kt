package com.sudoku.app.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class CompletionEvent(val cells: List<Pair<Int, Int>>)

sealed class GameEvent {
    object CorrectEntry : GameEvent()
    object WrongEntry   : GameEvent()
}

class GameViewModel : ViewModel() {

    private val _state = MutableStateFlow<GameState?>(null)
    val state: StateFlow<GameState?> = _state.asStateFlow()

    private val _completionEvents = MutableSharedFlow<CompletionEvent>(extraBufferCapacity = 16)
    val completionEvents: SharedFlow<CompletionEvent> = _completionEvents.asSharedFlow()

    private val _gameEvents = MutableSharedFlow<GameEvent>(extraBufferCapacity = 16)
    val gameEvents: SharedFlow<GameEvent> = _gameEvents.asSharedFlow()

    private var timerJob: Job? = null
    private val undoStack = ArrayDeque<List<List<CellState>>>()
    private var started = false

    fun startGame(difficulty: Difficulty, timedMode: Boolean) {
        if (started) return
        started = true
        viewModelScope.launch {
            val puzzle = SudokuGenerator.generate(difficulty)
            val cells = List(9) { row ->
                List(9) { col ->
                    val v = puzzle.grid[row][col]
                    CellState(value = v, isGiven = v != 0)
                }
            }
            _state.value = GameState(
                puzzle = puzzle,
                cells = cells,
                timeLimitSeconds = if (timedMode) difficulty.timeLimitSeconds else 0
            )
            startTimer()
        }
    }

    fun newGame(difficulty: Difficulty, timedMode: Boolean) {
        started = false
        undoStack.clear()
        timerJob?.cancel()
        startGame(difficulty, timedMode)
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                _state.update { s ->
                    if (s == null || s.isComplete || s.isPaused || s.isTimeUp) return@update s
                    val elapsed = s.elapsedSeconds + 1
                    val timeUp = s.isTimedMode && elapsed >= s.timeLimitSeconds
                    s.copy(elapsedSeconds = elapsed, isTimeUp = timeUp)
                }
            }
        }
    }

    fun selectCell(row: Int, col: Int) {
        _state.update { it?.copy(selectedRow = row, selectedCol = col) }
    }

    fun enterNumber(num: Int) {
        val s = _state.value ?: return
        if (s.isComplete || s.isGameOver || s.isTimeUp) return
        val row = s.selectedRow; val col = s.selectedCol
        if (row < 0 || col < 0) return
        if (s.cells[row][col].isGiven) return

        val oldCells = s.cells
        pushUndo(oldCells)

        _state.update { st ->
            if (st == null) return@update null
            val newCells = st.cells.mapIndexed { r, rowList ->
                rowList.mapIndexed { c, cell ->
                    if (r == row && c == col) {
                        if (st.isPencilMode && num != 0) {
                            val notes = cell.notes.toMutableSet()
                            if (num in notes) notes.remove(num) else notes.add(num)
                            cell.copy(value = 0, notes = notes, isError = false)
                        } else {
                            val isError = num != 0 && num != st.puzzle.solution[row][col]
                            cell.copy(value = num, notes = emptySet(), isError = isError)
                        }
                    } else cell
                }
            }
            val mistakes = if (!st.isPencilMode && num != 0 && num != st.puzzle.solution[row][col])
                st.mistakeCount + 1 else st.mistakeCount
            val complete = !st.isPencilMode && checkComplete(newCells, st.puzzle.solution)
            val gameOver = mistakes >= 3
            st.copy(cells = newCells, mistakeCount = mistakes, isComplete = complete, isGameOver = gameOver)
        }

        if (!s.isPencilMode && num != 0) {
            viewModelScope.launch {
                val correct = num == s.puzzle.solution[s.selectedRow][s.selectedCol]
                _gameEvents.emit(if (correct) GameEvent.CorrectEntry else GameEvent.WrongEntry)
            }
            _state.value?.let { detectCompletions(oldCells, it.cells, it.puzzle.solution) }
        }
    }

    fun eraseCell() {
        val s = _state.value ?: return
        val row = s.selectedRow; val col = s.selectedCol
        if (row < 0 || col < 0) return
        if (s.cells[row][col].isGiven) return
        pushUndo(s.cells)
        _state.update { st ->
            if (st == null) return@update null
            val newCells = st.cells.mapIndexed { r, rowList ->
                rowList.mapIndexed { c, cell ->
                    if (r == row && c == col) CellState() else cell
                }
            }
            st.copy(cells = newCells)
        }
    }

    fun undo() {
        if (undoStack.isEmpty()) return
        val prev = undoStack.removeLast()
        _state.update { it?.copy(cells = prev) }
    }

    fun togglePencil() { _state.update { it?.copy(isPencilMode = !it.isPencilMode) } }
    fun togglePause() { _state.update { it?.copy(isPaused = !it.isPaused) } }

    fun useHint() {
        val s = _state.value ?: return
        if (s.hintsUsed >= 3) return
        val row = s.selectedRow; val col = s.selectedCol
        if (row < 0 || col < 0) return
        val cell = s.cells[row][col]
        if (cell.isGiven || cell.value == s.puzzle.solution[row][col]) return

        val oldCells = s.cells
        pushUndo(oldCells)
        _state.update { st ->
            if (st == null) return@update null
            val newCells = st.cells.mapIndexed { r, rowList ->
                rowList.mapIndexed { c, cell ->
                    if (r == row && c == col)
                        CellState(value = st.puzzle.solution[row][col], isGiven = true)
                    else cell
                }
            }
            val complete = checkComplete(newCells, st.puzzle.solution)
            st.copy(cells = newCells, hintsUsed = st.hintsUsed + 1, isComplete = complete)
        }
        _state.value?.let { detectCompletions(oldCells, it.cells, it.puzzle.solution) }
    }

    private fun detectCompletions(
        old: List<List<CellState>>,
        new: List<List<CellState>>,
        solution: Array<IntArray>
    ) {
        viewModelScope.launch {
            // Rows (wave left→right)
            for (row in 0 until 9) {
                val pos = (0 until 9).map { col -> row to col }
                if (!regionDone(old, solution, pos) && regionDone(new, solution, pos))
                    _completionEvents.emit(CompletionEvent(pos))
            }
            // Columns (wave top→bottom)
            for (col in 0 until 9) {
                val pos = (0 until 9).map { row -> row to col }
                if (!regionDone(old, solution, pos) && regionDone(new, solution, pos))
                    _completionEvents.emit(CompletionEvent(pos))
            }
            // 3×3 boxes (wave top-left→bottom-right)
            for (box in 0 until 9) {
                val br = (box / 3) * 3; val bc = (box % 3) * 3
                val pos = (br until br + 3).flatMap { r -> (bc until bc + 3).map { c -> r to c } }
                if (!regionDone(old, solution, pos) && regionDone(new, solution, pos))
                    _completionEvents.emit(CompletionEvent(pos))
            }
        }
    }

    private fun regionDone(
        cells: List<List<CellState>>,
        solution: Array<IntArray>,
        positions: List<Pair<Int, Int>>
    ) = positions.all { (r, c) -> cells[r][c].value == solution[r][c] && !cells[r][c].isError }

    private fun pushUndo(cells: List<List<CellState>>) {
        undoStack.addLast(cells)
        if (undoStack.size > 50) undoStack.removeFirst()
    }

    private fun checkComplete(cells: List<List<CellState>>, solution: Array<IntArray>): Boolean {
        for (row in 0 until 9) for (col in 0 until 9)
            if (cells[row][col].value != solution[row][col]) return false
        return true
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
