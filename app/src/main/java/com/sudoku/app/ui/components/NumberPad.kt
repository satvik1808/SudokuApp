package com.sudoku.app.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import com.sudoku.app.HapticManager
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun NumberPad(
    onNumber: (Int) -> Unit,
    onErase: () -> Unit,
    onHint: () -> Unit,
    onUndo: () -> Unit,
    isPencilMode: Boolean,
    hintsRemaining: Int,
    validNumbers: Set<Int>,
    onPencilToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        // Tool row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ToolButton(Icons.AutoMirrored.Filled.Undo, "Undo",
                onClick = { HapticManager.click(); onUndo() })
            ToolButton(
                icon = if (isPencilMode) Icons.Default.EditNote else Icons.Default.Edit,
                label = "Notes",
                onClick = { HapticManager.click(); onPencilToggle() },
                isActive = isPencilMode
            )
            ToolButton(Icons.Default.Backspace, "Erase",
                onClick = { HapticManager.click(); onErase() })
            ToolButton(
                icon = Icons.Default.Lightbulb,
                label = "Hint ($hintsRemaining)",
                onClick = { HapticManager.hint(); onHint() },
                enabled = hintsRemaining > 0
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Number buttons 1–9 in a 3x3 grid
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            for (row in 0..2) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    for (col in 1..3) {
                        val num = row * 3 + col
                        val valid = validNumbers.isEmpty() || num in validNumbers
                        val interactionSource = remember { MutableInteractionSource() }
                        val isPressed by interactionSource.collectIsPressedAsState()
                        val scale by animateFloatAsState(
                            targetValue = if (isPressed) 0.85f else 1f,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessHigh
                            ),
                            label = "numScale"
                        )
                        Box(
                            modifier = Modifier
                                .alpha(if (valid) 1f else 0.15f)
                                .scale(scale)
                        ) {
                            Button(
                                onClick = { if (valid) { HapticManager.heavyClick(); onNumber(num) } },
                                enabled = valid,
                                interactionSource = interactionSource,
                                modifier = Modifier.size(64.dp),
                                contentPadding = PaddingValues(0.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.filledTonalButtonColors(),
                                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                            ) {
                                Text(
                                    text = num.toString(),
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ToolButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    isActive: Boolean = false,
    enabled: Boolean = true
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.85f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "toolScale"
    )
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.scale(scale)
    ) {
        FilledTonalIconButton(
            onClick = onClick,
            enabled = enabled,
            interactionSource = interactionSource,
            colors = if (isActive)
                IconButtonDefaults.filledIconButtonColors()
            else
                IconButtonDefaults.filledTonalIconButtonColors()
        ) {
            Icon(icon, contentDescription = label)
        }
        Text(
            label,
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1
        )
    }
}
