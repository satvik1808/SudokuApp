package com.sudoku.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sudoku.app.HapticManager
import com.sudoku.app.game.CellState
import com.sudoku.app.game.Difficulty
import com.sudoku.app.game.GameEvent
import com.sudoku.app.game.GameState
import com.sudoku.app.game.GameViewModel
import com.sudoku.app.ui.components.NumberPad
import com.sudoku.app.ui.components.SudokuGrid
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun GameScreen(
    difficulty: String,
    timedMode: Boolean,
    onBack: () -> Unit,
    vm: GameViewModel = viewModel()
) {
    val diff = Difficulty.valueOf(difficulty)
    val state by vm.state.collectAsState()
    val glowingCells = remember { mutableStateOf(emptySet<Pair<Int, Int>>()) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        vm.startGame(diff, timedMode)
    }

    // Correct / wrong entry haptics
    LaunchedEffect(Unit) {
        vm.gameEvents.collect { event ->
            when (event) {
                is GameEvent.CorrectEntry -> HapticManager.heavyClick()
                is GameEvent.WrongEntry   -> HapticManager.error()
            }
        }
    }

    // Completion wave — visual + haptic together
    LaunchedEffect(Unit) {
        vm.completionEvents.collect { event ->
            scope.launch {
                val total = event.cells.size
                event.cells.forEachIndexed { i, cell ->
                    delay(28L * i)
                    glowingCells.value = glowingCells.value + cell
                    HapticManager.wavePulse(i.toFloat() / total)
                }
                delay(380)
                event.cells.reversed().forEachIndexed { i, cell ->
                    delay(18L * i)
                    glowingCells.value = glowingCells.value - cell
                }
            }
        }
    }

    // Victory / game-over haptics on state transition
    val isComplete = state?.isComplete
    val isGameOver = state?.isGameOver
    val isTimeUp   = state?.isTimeUp
    LaunchedEffect(isComplete) { if (isComplete == true) HapticManager.victory() }
    LaunchedEffect(isGameOver) { if (isGameOver == true) HapticManager.gameOver() }
    LaunchedEffect(isTimeUp)   { if (isTimeUp   == true) HapticManager.gameOver() }

    state?.let { s ->
        if (s.isComplete) {
            CompletionDialog(
                won = true, elapsed = s.elapsedSeconds,
                hints = s.hintsUsed, mistakes = s.mistakeCount,
                onNewGame = { vm.newGame(diff, timedMode) }, onBack = onBack
            )
        }
        if (s.isTimeUp) {
            CompletionDialog(
                won = false, elapsed = s.elapsedSeconds,
                hints = s.hintsUsed, mistakes = s.mistakeCount,
                onNewGame = { vm.newGame(diff, timedMode) }, onBack = onBack
            )
        }
        if (s.isGameOver) {
            GameOverDialog(
                elapsed = s.elapsedSeconds,
                onNewGame = { vm.newGame(diff, timedMode) },
                onBack = onBack
            )
        }

        val validNumbers = remember(s.selectedRow, s.selectedCol, s.cells) {
            computeValidNumbers(s)
        }


        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(vertical = 20.dp)        // ← adds top & bottom padding
                .padding(horizontal = 1.dp)




        ) {
            // Top


            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(diff.displayName, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                    if (s.isTimedMode) {
                        val rem = s.remainingSeconds
                        Text(
                            formatTime(rem),
                            color = if (rem <= 60) MaterialTheme.colorScheme.error
                                    else MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.ExtraBold, fontSize = 20.sp
                        )
                    } else {
                        Text(
                            formatTime(s.elapsedSeconds),
                            color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 16.sp
                        )
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (s.mistakeCount > 0) {
                        Text(
                            "✕".repeat(s.mistakeCount.coerceAtMost(3)),
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 16.sp, fontWeight = FontWeight.Bold
                        )
                    }
                    IconButton(onClick = { HapticManager.click(); vm.togglePause() }) {
                        Icon(
                            if (s.isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                            contentDescription = if (s.isPaused) "Resume" else "Pause"
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            LinearProgressIndicator(
                progress = { completionProgress(s.cells) },
                modifier = Modifier.fillMaxWidth().height(4.dp),
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier.fillMaxWidth().weight(1f),
                contentAlignment = Alignment.Center
            ) {
                if (s.isPaused) {
                    PauseOverlay(onResume = { vm.togglePause() })
                } else {
                    SudokuGrid(
                        gameState = s,
                        onCellClick = vm::selectCell,
                        glowingCells = glowingCells.value,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            NumberPad(
                onNumber = vm::enterNumber,
                onErase = vm::eraseCell,
                onHint = vm::useHint,
                onUndo = vm::undo,
                isPencilMode = s.isPencilMode,
                hintsRemaining = s.remainingHints,
                validNumbers = validNumbers,
                onPencilToggle = vm::togglePencil,
                modifier = Modifier.fillMaxWidth()
            )
        }
    } ?: Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(12.dp))
            Text("Generating puzzle...", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

private fun computeValidNumbers(state: GameState): Set<Int> {
    val row = state.selectedRow
    val col = state.selectedCol
    // No cell selected or given cell → show all (used for number highlighting)
    if (row < 0 || col < 0 || state.cells[row][col].isGiven) return (1..9).toSet()

    val blocked = mutableSetOf<Int>()
    for (c in 0 until 9) if (c != col) state.cells[row][c].value.takeIf { it != 0 }?.let { blocked.add(it) }
    for (r in 0 until 9) if (r != row) state.cells[r][col].value.takeIf { it != 0 }?.let { blocked.add(it) }
    val br = row / 3 * 3; val bc = col / 3 * 3
    for (r in br until br + 3) for (c in bc until bc + 3)
        if (r != row || c != col) state.cells[r][c].value.takeIf { it != 0 }?.let { blocked.add(it) }
    // Numbers already placed 9 times are also blocked
    val counts = IntArray(10)
    for (r in 0 until 9) for (c in 0 until 9) state.cells[r][c].value.takeIf { it != 0 }?.let { counts[it]++ }
    for (n in 1..9) if (counts[n] >= 9) blocked.add(n)

    return (1..9).toSet() - blocked
}

@Composable
private fun GameOverDialog(elapsed: Int, onNewGame: () -> Unit, onBack: () -> Unit) {
    AlertDialog(
        onDismissRequest = {},
        icon = {
            Icon(
                Icons.Default.Close,
                null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(48.dp)
            )
        },
        title = {
            Text("Game Over", fontWeight = FontWeight.Bold, fontSize = 22.sp,
                color = MaterialTheme.colorScheme.error)
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("You made 3 mistakes!", fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(4.dp))
                StatRow("Time survived", formatTime(elapsed))
            }
        },
        confirmButton = {
            Button(
                onClick = onNewGame,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Icon(Icons.Default.Refresh, null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Try Again")
            }
        },
        dismissButton = { TextButton(onClick = onBack) { Text("Menu") } }
    )
}

@Composable
private fun PauseOverlay(onResume: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().aspectRatio(1f),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(Icons.Default.Pause, null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(12.dp))
            Text("PAUSED", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onResume, shape = RoundedCornerShape(14.dp)) {
                Icon(Icons.Default.PlayArrow, null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("RESUME", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun CompletionDialog(
    won: Boolean, elapsed: Int, hints: Int, mistakes: Int,
    onNewGame: () -> Unit, onBack: () -> Unit
) {
    AlertDialog(
        onDismissRequest = {},
        icon = {
            Icon(
                if (won) Icons.Default.EmojiEvents else Icons.Default.Timer,
                null,
                tint = if (won) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                modifier = Modifier.size(48.dp)
            )
        },
        title = { Text(if (won) "Puzzle Solved!" else "Time's Up!", fontWeight = FontWeight.Bold, fontSize = 22.sp) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                StatRow("Time", formatTime(elapsed))
                StatRow("Hints used", "$hints / 3")
                StatRow("Mistakes", mistakes.toString())
            }
        },
        confirmButton = {
            Button(onClick = onNewGame) {
                Icon(Icons.Default.Refresh, null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("New Game")
            }
        },
        dismissButton = { TextButton(onClick = onBack) { Text("Menu") } }
    )
}

@Composable
private fun StatRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, fontWeight = FontWeight.SemiBold)
    }
}

private fun formatTime(seconds: Int): String {
    val m = seconds / 60; val s = seconds % 60
    return "${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}"
}

private fun completionProgress(cells: List<List<CellState>>): Float {
    var filled = 0
    for (row in cells) for (cell in row) if (cell.value != 0) filled++
    return filled / 81f
}
