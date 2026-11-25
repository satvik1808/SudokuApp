package com.sudoku.app.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sudoku.app.HapticManager
import com.sudoku.app.game.Difficulty
import com.sudoku.app.ui.theme.AppTheme
import com.sudoku.app.ui.theme.ThemeState

@Composable
fun HomeScreen(
    onStartGame: (String, Boolean) -> Unit,
    onShowStats: () -> Unit,
    onEditCustomTheme: () -> Unit
) {
    var selectedDifficulty by remember { mutableStateOf(Difficulty.EASY) }
    var timedMode by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 28.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        // Theme picker row
        ThemePicker(
            onEditCustomTheme = onEditCustomTheme,
            modifier = Modifier.align(Alignment.End).padding(bottom = 8.dp)
        )

        // Logo + title
        Icon(
            Icons.Default.GridOn, null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            "SUDOKU",
            fontSize = 42.sp,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.primary,
            letterSpacing = 8.sp
        )
        Text(
            "Classic Number Puzzle",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Difficulty selector
        Text(
            "SELECT DIFFICULTY",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            letterSpacing = 2.sp,
            modifier = Modifier.align(Alignment.Start).padding(bottom = 8.dp)
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                Difficulty.entries.forEach { diff ->
                    DifficultyItem(
                        difficulty = diff,
                        isSelected = selectedDifficulty == diff,
                        onClick = { HapticManager.click(); selectedDifficulty = diff }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Timed mode
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Timer, null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(26.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Timed Mode", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                        Text("Race against the clock", fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                Switch(checked = timedMode, onCheckedChange = { timedMode = it })
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { HapticManager.heavyClick(); onStartGame(selectedDifficulty.name, timedMode) },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(18.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
        ) {
            Icon(Icons.Default.PlayArrow, null, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("START GAME", fontSize = 17.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
        }

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedButton(
            onClick = onShowStats,
            modifier = Modifier.fillMaxWidth().height(48.dp),
            shape = RoundedCornerShape(18.dp)
        ) {
            Icon(Icons.Default.BarChart, null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("STATISTICS", letterSpacing = 1.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun ThemePicker(modifier: Modifier = Modifier, onEditCustomTheme: () -> Unit) {
    val themes = listOf(
        AppTheme.SYSTEM to Icons.Default.BrightnessAuto,
        AppTheme.LIGHT  to Icons.Default.LightMode,
        AppTheme.DARK   to Icons.Default.DarkMode,
        AppTheme.HIGH_CONTRAST to Icons.Default.Contrast,
        AppTheme.VIVID   to Icons.Default.AutoAwesome,
        AppTheme.COSMIC  to Icons.Default.Bedtime,
        AppTheme.CUSTOM  to Icons.Default.Palette
    )
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        themes.forEach { (theme, icon) ->
            val selected = ThemeState.theme == theme
            IconButton(
                onClick = { 
                    HapticManager.tick()
                    ThemeState.theme = theme
                    if (theme == AppTheme.CUSTOM) {
                        onEditCustomTheme()
                    }
                },
                modifier = Modifier.size(36.dp),
                colors = if (selected)
                    IconButtonDefaults.filledIconButtonColors()
                else
                    IconButtonDefaults.iconButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
            ) {
                Icon(icon, contentDescription = theme.label, modifier = Modifier.size(18.dp))
            }
        }
    }
}

@Composable
private fun DifficultyItem(difficulty: Difficulty, isSelected: Boolean, onClick: () -> Unit) {
    val timeHint = when (difficulty) {
        Difficulty.EASY   -> "~10 min • 46+ givens"
        Difficulty.MEDIUM -> "~20 min • 36+ givens"
        Difficulty.HARD   -> "~30 min • 29+ givens"
        Difficulty.EXPERT -> "~45 min • 24+ givens"
    }
    val bgColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primaryContainer
                      else MaterialTheme.colorScheme.surface,
        label = "diffBg"
    )
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        color = bgColor,
        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = isSelected, onClick = onClick)
                Spacer(modifier = Modifier.width(6.dp))
                Text(difficulty.displayName,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    fontSize = 15.sp)
            }
            Text(timeHint, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}


