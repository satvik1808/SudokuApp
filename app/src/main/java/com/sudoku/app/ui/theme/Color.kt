package com.sudoku.app.ui.theme

import androidx.compose.ui.graphics.Color

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)
val Purple40 = Color(0xFF6650A4)
val PurpleGrey40 = Color(0xFF625B71)
val Pink40 = Color(0xFF7D5260)

// Grid lines — light
val GridLineColor     = Color(0xFF1A1A3A)
val GridThinLineColor = Color(0xFFAAAAAA)

// Grid lines — dark (much brighter so boxes are obvious)
val GridLineColorDark     = Color(0xFFCCCCEE)
val GridThinLineColorDark = Color(0xFF666688)

// Grid lines — AMOLED
val GridLineColorHC     = Color(0xFFFFFFFF)
val GridThinLineColorHC = Color(0xFF888888)

// ── Light theme cells ──────────────────────────────────────────────────────
val CellNormal    = Color(0xFFFFFFFF)
val CellAlternate = Color(0xFFEBEFFF)
val CellRelated   = Color(0xFFD5DEFF)
val CellSameNum   = Color(0xFFB3C5FF)
val CellSelected  = Color(0xFF6699FF)

// ── Dark theme cells (much more contrast) ─────────────────────────────────
val CellNormalDark    = Color(0xFF1C1C2E)   // deep navy
val CellAlternateDark = Color(0xFF252540)   // slightly lighter box
val CellRelatedDark   = Color(0xFF2E2E55)   // clearly visible related
val CellSameNumDark   = Color(0xFF3A3A70)   // same-number highlight
val CellSelectedDark  = Color(0xFF3D5FA8)   // strong blue selected

// ── AMOLED cells ──────────────────────────────────────────────────────────
val CellNormalHC    = Color(0xFF000000)
val CellAlternateHC = Color(0xFF0E0E1E)
val CellRelatedHC   = Color(0xFF1A1A33)
val CellSelectedHC  = Color(0xFF003D99)

// ── Numbers — light ───────────────────────────────────────────────────────
val GivenNumberLight = Color(0xFF0D0D2B)
val UserNumberLight  = Color(0xFF1A44AA)

// ── Numbers — dark (bright and punchy) ───────────────────────────────────
val GivenNumberDark = Color(0xFFF0F0FF)   // near-white, very readable
val UserNumberDark  = Color(0xFF7EB3FF)   // bright cornflower blue

// ── Numbers — AMOLED ─────────────────────────────────────────────────────
val GivenNumberHC = Color(0xFFFFFFFF)
val UserNumberHC  = Color(0xFF55BBFF)

// ── Cosmic grid lines ─────────────────────────────────────────────────────
val GridLineColorCosmic     = Color(0xFFA78BFA)   // bright violet
val GridThinLineColorCosmic = Color(0xFF2D2B52)   // deep indigo

// ── Cosmic cells ──────────────────────────────────────────────────────────
val CellNormalCosmic    = Color(0xFF0F1021)
val CellAlternateCosmic = Color(0xFF141628)
val CellRelatedCosmic   = Color(0xFF1C1E3A)
val CellSameNumCosmic   = Color(0xFF252852)
val CellSelectedCosmic  = Color(0xFF5B21B6)   // deep violet

// ── Cosmic numbers ────────────────────────────────────────────────────────
val GivenNumberCosmic = Color(0xFFEDE9FE)   // near-white violet
val UserNumberCosmic  = Color(0xFFA78BFA)   // bright violet

// ── Vivid grid lines ──────────────────────────────────────────────────────
val GridLineColorVivid     = Color(0xFF6D28D9)   // deep violet
val GridThinLineColorVivid = Color(0xFFC4B5FD)   // soft lavender

// ── Vivid cells ───────────────────────────────────────────────────────────
val CellNormalVivid    = Color(0xFFFFFFFF)
val CellAlternateVivid = Color(0xFFF5F3FF)   // barely-there violet tint
val CellRelatedVivid   = Color(0xFFEDE9FE)   // lavender
val CellSameNumVivid   = Color(0xFFDDD6FE)   // medium lavender
val CellSelectedVivid  = Color(0xFF7C3AED)   // rich violet

// ── Vivid numbers ─────────────────────────────────────────────────────────
val GivenNumberVivid = Color(0xFF1E1B4B)   // deep indigo
val UserNumberVivid  = Color(0xFF6D28D9)   // violet

// ── Shared ────────────────────────────────────────────────────────────────
val ErrorNumber  = Color(0xFFFF4455)
val NoteColor    = Color(0xFF8888BB)
val NoteColorDark = Color(0xFFAAAADD)
val NoteColorVivid = Color(0xFFA78BFA)     // light violet
