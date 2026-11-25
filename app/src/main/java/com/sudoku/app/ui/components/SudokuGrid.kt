package com.sudoku.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text
import com.sudoku.app.HapticManager
import com.sudoku.app.game.CellState
import com.sudoku.app.game.GameState
import com.sudoku.app.ui.theme.*
import com.sudoku.app.ui.theme.AppTheme
import com.sudoku.app.ui.theme.ThemeState

@Composable
fun SudokuGrid(
    gameState: GameState,
    onCellClick: (Int, Int) -> Unit,
    glowingCells: Set<Pair<Int, Int>> = emptySet(),
    modifier: Modifier = Modifier
) {
    val currentDensity = LocalDensity.current
    val customDensity = remember(currentDensity) {
        Density(
            density = currentDensity.density,
            fontScale = 1f
        )
    }

    CompositionLocalProvider(LocalDensity provides customDensity) {
        val theme = ThemeState.theme
        val isHC = theme == AppTheme.HIGH_CONTRAST
        val isVivid = theme == AppTheme.VIVID
        val isCosmic = theme == AppTheme.COSMIC
        val isDark = theme == AppTheme.DARK || theme == AppTheme.HIGH_CONTRAST ||
            theme == AppTheme.COSMIC || (theme == AppTheme.SYSTEM && isSystemDarkTheme())

        val boxBorderColor = when (theme) {
            AppTheme.HIGH_CONTRAST -> GridLineColorHC
            AppTheme.DARK -> GridLineColorDark
            AppTheme.VIVID -> GridLineColorVivid
            AppTheme.COSMIC -> GridLineColorCosmic
            AppTheme.CUSTOM -> ThemeState.customGridLine
            else -> GridLineColor
        }
        val thinLineColor = when (theme) {
            AppTheme.HIGH_CONTRAST -> GridThinLineColorHC
            AppTheme.DARK -> GridThinLineColorDark
            AppTheme.VIVID -> GridThinLineColorVivid
            AppTheme.COSMIC -> GridThinLineColorCosmic
            AppTheme.CUSTOM -> ThemeState.customGridThinLine
            else -> GridThinLineColor
        }

        BoxWithConstraints(
            modifier = modifier
                .aspectRatio(1f)
                .border(3.dp, boxBorderColor)
        ) {
            val cellSize = maxWidth / 9
            Column {
                for (row in 0 until 9) {
                    Row {
                        for (col in 0 until 9) {
                            val cell = gameState.cells[row][col]
                            val selRow = gameState.selectedRow
                            val selCol = gameState.selectedCol
                            val isSelected = row == selRow && col == selCol
                            val isRelated = selRow >= 0 && selCol >= 0 &&
                                (row == selRow || col == selCol ||
                                 (row / 3 == selRow / 3 && col / 3 == selCol / 3))
                            val selCell = if (selRow >= 0 && selCol >= 0) gameState.cells[selRow][selCol] else null
                            val isSameNum = cell.value != 0 && selCell != null && cell.value == selCell.value
                            val isGlowing = (row to col) in glowingCells
                            val isAltBox = (row / 3 + col / 3) % 2 == 1

                            SudokuCell(
                                cell = cell,
                                isSelected = isSelected,
                                isRelated = isRelated && !isSelected,
                                isSameNum = isSameNum && !isSelected,
                                isGlowing = isGlowing,
                                isAltBox = isAltBox,
                                size = cellSize,
                                isDark = isDark,
                                isHC = isHC,
                                isVivid = isVivid,
                                isCosmic = isCosmic,
                                onClick = { HapticManager.tick(); onCellClick(row, col) }
                            )
                        }
                    }
                }
            }
            // Grid lines drawn on top
            Canvas(modifier = Modifier.fillMaxSize()) {
                val cellPx = size.width / 9f
                val boxStroke = if (isDark || isHC) 6f else 5f
                val thinStroke = if (isDark || isHC) 1.8f else 1.2f
                for (i in 0..9) {
                    val pos = i * cellPx
                    val isBox = i % 3 == 0
                    val stroke = if (isBox) boxStroke else thinStroke
                    val color = if (isBox) boxBorderColor else thinLineColor
                    drawLine(color, Offset(pos, 0f), Offset(pos, size.height), stroke)
                    drawLine(color, Offset(0f, pos), Offset(size.width, pos), stroke)
                }
            }
        }
    }
}

// Reads system dark mode without CompositionLocal (used outside of composable context)
private fun isSystemDarkTheme(): Boolean = false  // fallback; actual value comes from ThemeState

@Composable
fun SudokuCell(
    cell: CellState,
    isSelected: Boolean,
    isRelated: Boolean,
    isSameNum: Boolean,
    isGlowing: Boolean,
    isAltBox: Boolean,
    size: Dp,
    isDark: Boolean,
    isHC: Boolean,
    isVivid: Boolean = false,
    isCosmic: Boolean = false,
    onClick: () -> Unit
) {
    val normalBg = when {
        ThemeState.theme == AppTheme.CUSTOM -> if (isAltBox) ThemeState.customCellAlternate else ThemeState.customCellNormal
        isHC     -> if (isAltBox) CellAlternateHC else CellNormalHC
        isCosmic -> if (isAltBox) CellAlternateCosmic else CellNormalCosmic
        isVivid  -> if (isAltBox) CellAlternateVivid else CellNormalVivid
        isDark   -> if (isAltBox) CellAlternateDark else CellNormalDark
        else     -> if (isAltBox) CellAlternate else CellNormal
    }
    val targetBg = when {
        isGlowing  -> when { isCosmic -> Color(0xFF7C3AED); isDark -> Color(0xFFB8860B); isVivid -> Color(0xFFF59E0B); else -> Color(0xFFFFD54F) }
        isSelected -> when { ThemeState.theme == AppTheme.CUSTOM -> ThemeState.customCellSelected; isHC -> CellSelectedHC; isCosmic -> CellSelectedCosmic; isVivid -> CellSelectedVivid; isDark -> CellSelectedDark; else -> CellSelected }
        isSameNum  -> when { ThemeState.theme == AppTheme.CUSTOM -> ThemeState.customCellSameNum; isCosmic -> CellSameNumCosmic; isVivid -> CellSameNumVivid; isDark -> CellSameNumDark; else -> CellSameNum }
        isRelated  -> when { ThemeState.theme == AppTheme.CUSTOM -> ThemeState.customCellRelated; isHC -> CellRelatedHC; isCosmic -> CellRelatedCosmic; isVivid -> CellRelatedVivid; isDark -> CellRelatedDark; else -> CellRelated }
        else       -> normalBg
    }
    val bg by animateColorAsState(
        targetValue = targetBg,
        animationSpec = tween(durationMillis = 110),
        label = "cellBg"
    )

    Box(
        modifier = Modifier
            .size(size)
            .background(bg)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        when {
            cell.value != 0 -> {
                val textColor = when {
                    ThemeState.theme == AppTheme.CUSTOM -> if (cell.isError) ThemeState.customErrorNumber else if (cell.isGiven) ThemeState.customGivenNumber else ThemeState.customUserNumber
                    cell.isError -> ErrorNumber
                    cell.isGiven -> when { isHC -> GivenNumberHC; isCosmic -> GivenNumberCosmic; isVivid -> GivenNumberVivid; isDark -> GivenNumberDark; else -> GivenNumberLight }
                    else         -> when { isHC -> UserNumberHC;  isCosmic -> UserNumberCosmic;  isVivid -> UserNumberVivid;  isDark -> UserNumberDark;  else -> UserNumberLight }
                }
                Text(
                    text = cell.value.toString(),
                    fontSize = (size.value * 0.52f).sp,
                    fontWeight = if (cell.isGiven) FontWeight.ExtraBold else FontWeight.SemiBold,
                    color = textColor,
                    textAlign = TextAlign.Center
                )
            }
            cell.notes.isNotEmpty() -> {
                val notesList = cell.notes.toList().sorted()
                androidx.compose.ui.layout.Layout(
                    content = {
                        notesList.forEach { n ->
                            Text(
                                text = n.toString(),
                                fontSize = (size.value * 0.30f).sp,
                                color = when { ThemeState.theme == AppTheme.CUSTOM -> ThemeState.customNoteColor; isCosmic -> NoteColorVivid; isVivid -> NoteColorVivid; isDark -> NoteColorDark; else -> NoteColor },
                                fontWeight = FontWeight.Bold
                            )
                        }
                    },
                    modifier = Modifier.fillMaxSize().padding(1.dp)
                ) { measurables, constraints ->
                    val cellW = constraints.maxWidth
                    val cellH = constraints.maxHeight
                    val slotW = cellW / 3f
                    val slotH = cellH / 3f

                    val placeables = measurables.map { it.measure(androidx.compose.ui.unit.Constraints()) }

                    layout(cellW, cellH) {
                        placeables.forEachIndexed { index, placeable ->
                            val n = notesList[index]
                            val nr = (n - 1) / 3
                            val nc = (n - 1) % 3
                            val cx = nc * slotW + slotW / 2f
                            val cy = nr * slotH + slotH / 2f
                            placeable.place(
                                x = (cx - placeable.width / 2f).toInt(),
                                y = (cy - placeable.height / 2f).toInt()
                            )
                        }
                    }
                }
            }
        }
    }
}
