package com.sudoku.app.game

data class CellState(
    val value: Int = 0,
    val isGiven: Boolean = false,
    val isError: Boolean = false,
    val notes: Set<Int> = emptySet()
)

data class GameState(
    val puzzle: SudokuPuzzle,
    val cells: List<List<CellState>>,
    val selectedRow: Int = -1,
    val selectedCol: Int = -1,
    val isPencilMode: Boolean = false,
    val isComplete: Boolean = false,
    val isPaused: Boolean = false,
    val isTimeUp: Boolean = false,
    val isGameOver: Boolean = false,
    val elapsedSeconds: Int = 0,
    val timeLimitSeconds: Int = 0,
    val hintsUsed: Int = 0,
    val mistakeCount: Int = 0
) {
    val isTimedMode get() = timeLimitSeconds > 0
    val remainingSeconds get() = (timeLimitSeconds - elapsedSeconds).coerceAtLeast(0)
    val remainingHints get() = (3 - hintsUsed).coerceAtLeast(0)
}
