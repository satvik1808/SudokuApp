package com.sudoku.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.ColorUtils
import com.sudoku.app.HapticManager
import com.sudoku.app.ui.theme.ThemeState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeBuilderScreen(onBack: () -> Unit) {
    var enforceContrast by remember { mutableStateOf(true) }
    var selectedColorRole by remember { mutableStateOf<String?>(null) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Theme Builder") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // Preview Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clickable { selectedColorRole = "Background" },
                colors = CardDefaults.cardColors(containerColor = ThemeState.customBackground)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Theme Preview", color = ThemeState.customOnBackground, fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.clickable { selectedColorRole = "On Background" })
                    Text("Tap elements to edit their color", color = ThemeState.customOnBackground.copy(alpha = 0.7f), fontSize = 12.sp, modifier = Modifier.padding(bottom = 16.dp))
                    
                    // Surface Card
                    Card(
                        colors = CardDefaults.cardColors(containerColor = ThemeState.customSurface),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp).clickable { selectedColorRole = "Surface" }
                    ) {
                        Text("This is text on a surface element.", color = ThemeState.customOnSurface, modifier = Modifier.padding(16.dp).clickable { selectedColorRole = "On Surface" })
                    }
                    
                    // Surface Variant Card
                    Card(
                        colors = CardDefaults.cardColors(containerColor = ThemeState.customSurfaceVariant),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp).border(1.dp, ThemeState.customOutline, RoundedCornerShape(12.dp)).clickable { selectedColorRole = "Surface Variant" }
                    ) {
                        Text("Text on surface variant with outline.", color = ThemeState.customOnSurfaceVariant, modifier = Modifier.padding(16.dp).clickable { selectedColorRole = "On Surface Variant" })
                    }
                    
                    // Primary and Secondary Buttons
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { selectedColorRole = "Primary" }, 
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ThemeState.customPrimary,
                                contentColor = ThemeState.customOnPrimary
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Primary", modifier = Modifier.clickable { selectedColorRole = "On Primary" })
                        }
                        Button(
                            onClick = { selectedColorRole = "Secondary" }, 
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ThemeState.customSecondary,
                                contentColor = ThemeState.customOnSecondary
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Secondary", modifier = Modifier.clickable { selectedColorRole = "On Secondary" })
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    Text("Grid Preview", color = ThemeState.customOnBackground, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
                    
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .background(ThemeState.customGridLine)
                        .padding(4.dp)
                        .clickable { selectedColorRole = "Grid Thick Line" }
                    ) {
                        Column(modifier = Modifier
                            .fillMaxSize()
                            .background(ThemeState.customGridThinLine)
                        ) {
                            for (r in 0..2) {
                                Row(modifier = Modifier.weight(1f)) {
                                    for (c in 0..2) {
                                        val isAlt = (r + c) % 2 == 1
                                        val bgRole = when {
                                            r == 1 && c == 0 -> "Cell Selected"
                                            r == 1 && c == 1 -> "Cell Related"
                                            r == 1 && c == 2 -> "Cell Same Number"
                                            else -> if (isAlt) "Cell Alternate" else "Cell Normal"
                                        }
                                        val bg = when(bgRole) {
                                            "Cell Selected" -> ThemeState.customCellSelected
                                            "Cell Related" -> ThemeState.customCellRelated
                                            "Cell Same Number" -> ThemeState.customCellSameNum
                                            "Cell Alternate" -> ThemeState.customCellAlternate
                                            else -> ThemeState.customCellNormal
                                        }
                                        
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .fillMaxHeight()
                                                .padding(1.dp)
                                                .background(bg)
                                                .clickable { selectedColorRole = bgRole },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            if (r == 0 && c == 0) {
                                                Text("1", color = ThemeState.customGivenNumber, fontWeight = FontWeight.ExtraBold, fontSize = 32.sp, modifier = Modifier.clickable { selectedColorRole = "Given Number Text" })
                                            } else if (r == 0 && c == 1) {
                                                Text("5", color = ThemeState.customUserNumber, fontWeight = FontWeight.SemiBold, fontSize = 32.sp, modifier = Modifier.clickable { selectedColorRole = "User Number Text" })
                                            } else if (r == 0 && c == 2) {
                                                Text("9", color = ThemeState.customErrorNumber, fontWeight = FontWeight.SemiBold, fontSize = 32.sp, modifier = Modifier.clickable { selectedColorRole = "Error Number Text" })
                                            } else if (r == 1 && c == 2) {
                                                Text("1", color = ThemeState.customGivenNumber, fontWeight = FontWeight.ExtraBold, fontSize = 32.sp, modifier = Modifier.clickable { selectedColorRole = "Given Number Text" })
                                            } else if (r == 2 && c == 0) {
                                                Text("1 2 3", color = ThemeState.customNoteColor, fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.clickable { selectedColorRole = "Note Color" })
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            // Toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Show Contrast Warnings", fontWeight = FontWeight.Medium)
                Switch(checked = enforceContrast, onCheckedChange = { enforceContrast = it })
            }
            
            // (Color list has been replaced by the interactive preview above)
        }
    }
    
    // Dialog handling
    selectedColorRole?.let { role ->
        val initialColor = when(role) {
            "Primary" -> ThemeState.customPrimary
            "On Primary" -> ThemeState.customOnPrimary
            "Secondary" -> ThemeState.customSecondary
            "On Secondary" -> ThemeState.customOnSecondary
            "Background" -> ThemeState.customBackground
            "On Background" -> ThemeState.customOnBackground
            "Surface" -> ThemeState.customSurface
            "On Surface" -> ThemeState.customOnSurface
            "Surface Variant" -> ThemeState.customSurfaceVariant
            "On Surface Variant" -> ThemeState.customOnSurfaceVariant
            "Outline" -> ThemeState.customOutline
            "Grid Thick Line" -> ThemeState.customGridLine
            "Grid Thin Line" -> ThemeState.customGridThinLine
            "Cell Normal" -> ThemeState.customCellNormal
            "Cell Alternate" -> ThemeState.customCellAlternate
            "Cell Selected" -> ThemeState.customCellSelected
            "Cell Related" -> ThemeState.customCellRelated
            "Cell Same Number" -> ThemeState.customCellSameNum
            "Given Number Text" -> ThemeState.customGivenNumber
            "User Number Text" -> ThemeState.customUserNumber
            "Note Color" -> ThemeState.customNoteColor
            "Error Number Text" -> ThemeState.customErrorNumber
            else -> Color.Black
        }
        
        val contrastAgainst = when(role) {
            "Primary" -> ThemeState.customOnPrimary
            "On Primary" -> ThemeState.customPrimary
            "Secondary" -> ThemeState.customOnSecondary
            "On Secondary" -> ThemeState.customSecondary
            "Background" -> ThemeState.customOnBackground
            "On Background" -> ThemeState.customBackground
            "Surface" -> ThemeState.customOnSurface
            "On Surface" -> ThemeState.customSurface
            "Surface Variant" -> ThemeState.customOnSurfaceVariant
            "On Surface Variant" -> ThemeState.customSurfaceVariant
            "Given Number Text" -> ThemeState.customCellNormal
            "User Number Text" -> ThemeState.customCellNormal
            "Error Number Text" -> ThemeState.customCellNormal
            "Note Color" -> ThemeState.customCellNormal
            else -> null
        }
        
        CustomColorPickerDialog(
            initialColor = initialColor,
            title = "Edit $role",
            enforceContrast = enforceContrast,
            contrastAgainst = contrastAgainst,
            onDismiss = { selectedColorRole = null },
            onColorSelected = { color ->
                when(role) {
                    "Primary" -> ThemeState.customPrimary = color
                    "On Primary" -> ThemeState.customOnPrimary = color
                    "Secondary" -> ThemeState.customSecondary = color
                    "On Secondary" -> ThemeState.customOnSecondary = color
                    "Background" -> ThemeState.customBackground = color
                    "On Background" -> ThemeState.customOnBackground = color
                    "Surface" -> ThemeState.customSurface = color
                    "On Surface" -> ThemeState.customOnSurface = color
                    "Surface Variant" -> ThemeState.customSurfaceVariant = color
                    "On Surface Variant" -> ThemeState.customOnSurfaceVariant = color
                    "Outline" -> ThemeState.customOutline = color
                    "Grid Thick Line" -> ThemeState.customGridLine = color
                    "Grid Thin Line" -> ThemeState.customGridThinLine = color
                    "Cell Normal" -> ThemeState.customCellNormal = color
                    "Cell Alternate" -> ThemeState.customCellAlternate = color
                    "Cell Selected" -> ThemeState.customCellSelected = color
                    "Cell Related" -> ThemeState.customCellRelated = color
                    "Cell Same Number" -> ThemeState.customCellSameNum = color
                    "Given Number Text" -> ThemeState.customGivenNumber = color
                    "User Number Text" -> ThemeState.customUserNumber = color
                    "Note Color" -> ThemeState.customNoteColor = color
                    "Error Number Text" -> ThemeState.customErrorNumber = color
                }
                selectedColorRole = null
            }
        )
    }
}

@Composable
fun CustomColorPickerDialog(
    initialColor: Color,
    title: String,
    enforceContrast: Boolean,
    contrastAgainst: Color?,
    onDismiss: () -> Unit,
    onColorSelected: (Color) -> Unit
) {
    val hsl = FloatArray(3)
    ColorUtils.colorToHSL(initialColor.toArgb(), hsl)
    var hue by remember { mutableFloatStateOf(hsl[0]) }
    var saturation by remember { mutableFloatStateOf(hsl[1]) }
    var lightness by remember { mutableFloatStateOf(hsl[2]) }
    
    val currentColor = Color(ColorUtils.HSLToColor(floatArrayOf(hue, saturation, lightness)))
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title, fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(currentColor)
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text("Hue: ${hue.toInt()}°", fontSize = 14.sp)
                Box(contentAlignment = Alignment.Center) {
                    Box(modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)).background(
                        Brush.horizontalGradient(
                            colors = listOf(Color.Red, Color.Yellow, Color.Green, Color.Cyan, Color.Blue, Color.Magenta, Color.Red)
                        )
                    ))
                    Slider(
                        value = hue, 
                        onValueChange = { hue = it },
                        valueRange = 0f..360f,
                        colors = SliderDefaults.colors(
                            thumbColor = Color(ColorUtils.HSLToColor(floatArrayOf(hue, 1f, 0.5f))),
                            activeTrackColor = Color.Transparent,
                            inactiveTrackColor = Color.Transparent
                        )
                    )
                }
                
                Text("Saturation: ${(saturation * 100).toInt()}%", fontSize = 14.sp)
                Slider(
                    value = saturation, 
                    onValueChange = { saturation = it }, 
                    colors = SliderDefaults.colors(thumbColor = currentColor, activeTrackColor = currentColor)
                )
                
                Text("Lightness: ${(lightness * 100).toInt()}%", fontSize = 14.sp)
                Slider(
                    value = lightness, 
                    onValueChange = { lightness = it }, 
                    colors = SliderDefaults.colors(thumbColor = Color(ColorUtils.HSLToColor(floatArrayOf(0f, 0f, lightness))), activeTrackColor = Color.Gray)
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                val argb = currentColor.toArgb()
                val hexCode = String.format("#%02X%02X%02X", (argb shr 16) and 0xFF, (argb shr 8) and 0xFF, argb and 0xFF)
                Text("Hex: $hexCode", fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.CenterHorizontally))
                
                if (enforceContrast && contrastAgainst != null) {
                    val currentArgb = currentColor.toArgb()
                    val againstArgb = contrastAgainst.toArgb()
                    val contrast = ColorUtils.calculateContrast(currentArgb, againstArgb)
                    
                    if (contrast < 4.5) {
                        Text(
                            "Warning: Low Contrast (${String.format("%.1f", contrast)}:1).\nMinimum recommended for readability is 4.5:1.",
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(top = 16.dp)
                        )
                    } else {
                         Text(
                            "Contrast OK (${String.format("%.1f", contrast)}:1).",
                            color = Color(0xFF388E3C), // A nice green
                            fontSize = 13.sp,
                            modifier = Modifier.padding(top = 16.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onColorSelected(currentColor) }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
