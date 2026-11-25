package com.sudoku.app.game

import kotlin.random.Random

enum class Difficulty(val displayName: String, val cellsToRemove: Int, val timeLimitSeconds: Int) {
    EASY("Easy", 36, 600),
    MEDIUM("Medium", 46, 900),
    HARD("Hard", 52, 1200),
    EXPERT("Expert", 57, 1800)
}

data class SudokuPuzzle(
    val grid: Array<IntArray>,
    val solution: Array<IntArray>,
    val difficulty: Difficulty
) {
    override fun equals(other: Any?) = false
    override fun hashCode() = grid.contentDeepHashCode()
}

object SudokuGenerator {

    fun generate(difficulty: Difficulty): SudokuPuzzle {
        val solution = generateSolution()
        val grid = Array(9) { row -> solution[row].copyOf() }
        removeCells(grid, difficulty.cellsToRemove, solution)
        return SudokuPuzzle(grid, solution, difficulty)
    }

    private fun generateSolution(): Array<IntArray> {
        val grid = Array(9) { IntArray(9) }
        for (box in 0..2) fillBox(grid, box * 3, box * 3)
        solveSudoku(grid, randomize = true)
        return grid
    }

    private fun fillBox(grid: Array<IntArray>, row: Int, col: Int) {
        val nums = (1..9).shuffled(Random)
        var idx = 0
        for (r in row until row + 3) {
            for (c in col until col + 3) {
                grid[r][c] = nums[idx++]
            }
        }
    }

    private fun solveSudoku(grid: Array<IntArray>, randomize: Boolean = false): Boolean {
        for (row in 0 until 9) {
            for (col in 0 until 9) {
                if (grid[row][col] == 0) {
                    val nums = if (randomize) (1..9).shuffled(Random) else (1..9).toList()
                    for (num in nums) {
                        if (isValid(grid, row, col, num)) {
                            grid[row][col] = num
                            if (solveSudoku(grid, randomize)) return true
                            grid[row][col] = 0
                        }
                    }
                    return false
                }
            }
        }
        return true
    }

    fun isValid(grid: Array<IntArray>, row: Int, col: Int, num: Int): Boolean {
        for (c in 0 until 9) if (grid[row][c] == num) return false
        for (r in 0 until 9) if (grid[r][col] == num) return false
        val br = row - row % 3
        val bc = col - col % 3
        for (r in br until br + 3) for (c in bc until bc + 3) if (grid[r][c] == num) return false
        return true
    }

    private fun removeCells(grid: Array<IntArray>, count: Int, solution: Array<IntArray>) {
        val positions = (0 until 81).shuffled(Random)
        var removed = 0
        for (pos in positions) {
            if (removed >= count) break
            val row = pos / 9
            val col = pos % 9
            if (grid[row][col] == 0) continue
            val backup = grid[row][col]
            grid[row][col] = 0
            val testGrid = Array(9) { r -> grid[r].copyOf() }
            if (countSolutions(testGrid, 2) == 1) {
                removed++
            } else {
                grid[row][col] = backup
            }
        }
    }

    private fun countSolutions(grid: Array<IntArray>, limit: Int): Int {
        for (row in 0 until 9) {
            for (col in 0 until 9) {
                if (grid[row][col] == 0) {
                    var count = 0
                    for (num in 1..9) {
                        if (isValid(grid, row, col, num)) {
                            grid[row][col] = num
                            count += countSolutions(grid, limit)
                            grid[row][col] = 0
                            if (count >= limit) return count
                        }
                    }
                    return count
                }
            }
        }
        return 1
    }
}
