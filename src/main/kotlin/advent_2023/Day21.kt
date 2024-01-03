package advent_2023

import java.io.FileReader
import kotlin.math.abs

private const val ROCK = '#'
private const val STEPS_PART1 = 64
private const val STEPS_PART2 = 26501365
private const val STEPS_SIMULATION = 400

class Day21 {
    fun process(args: Array<String>) {
        val gardenPlan = FileReader(args[0]).readLines()
        val yStart = gardenPlan.indexOfFirst { it.contains('S') }
        val xStart = gardenPlan[yStart].indexOfFirst { it == 'S' }
        val stepSets = mutableListOf(mutableSetOf(Pair(xStart, yStart)))
        calculatePart1(stepSets, gardenPlan)
        println("Part1: ${stepSets[STEPS_PART1].size}")

        stepSets.clear()
        stepSets.add(mutableSetOf(Pair(xStart, yStart)))
        val startTime = System.currentTimeMillis()
        val squareSize = gardenPlan.size
        val plots = calculatePart2(stepSets, gardenPlan, squareSize)
        println("Time: ${System.currentTimeMillis() - startTime}")
        println("Part2: $plots")
    }

    private fun calculatePart2(
        stepSets: MutableList<MutableSet<Pair<Int, Int>>>,
        gardenPlan: List<String>,
        squareSize: Int
    ): Long {
        var stepCounter = 0L
        var step10List = mutableListOf<Int>()
        var step11List = mutableListOf<Int>()
        var step01List = mutableListOf<Int>()
        var step_11List = mutableListOf<Int>()
        var step_10List = mutableListOf<Int>()
        var step_1_1List = mutableListOf<Int>()
        var step0_1List = mutableListOf<Int>()
        var step1_1List = mutableListOf<Int>()
        for (i in 1..STEPS_SIMULATION) {
            val step = mutableSetOf<Pair<Int, Int>>()
            if (i > 1) stepSets[i-2].clear()
            stepSets[i - 1].forEach {
                stepCounter++
                val x = it.first
                val y = it.second
                if (gardenPlan[Math.floorMod(x, squareSize)][Math.floorMod(y-1, squareSize)] != ROCK)
                    step.add(Pair(x, y - 1))
                if (gardenPlan[Math.floorMod(x+1, squareSize)][Math.floorMod(y, squareSize)] != ROCK)
                    step.add(Pair(x + 1, y))
                if (gardenPlan[Math.floorMod(x, squareSize)][Math.floorMod(y+1, squareSize)] != ROCK)
                    step.add(Pair(x, y + 1))
                if (gardenPlan[Math.floorMod(x-1, squareSize)][Math.floorMod(y, squareSize)] != ROCK)
                    step.add(Pair(x - 1, y))
            }
            stepSets.add(step)
            val step10 = step.filter { it.first in 1*squareSize..<2*squareSize && it.second in 0*squareSize..<1*squareSize }
            val step11 = step.filter { it.first in 1*squareSize..<2*squareSize && it.second in 1*squareSize..<2*squareSize }
            val step01 = step.filter { it.first in 0*squareSize..<1*squareSize && it.second in 1*squareSize..<2*squareSize }
            val step_11 = step.filter { it.first in -1*squareSize..<0*squareSize && it.second in 1*squareSize..<2*squareSize }
            val step_10 = step.filter { it.first in-1*squareSize..<0*squareSize && it.second in 0*squareSize..<1*squareSize }
            val step_1_1 = step.filter { it.first in -1*squareSize..<0*squareSize && it.second in -1*squareSize..<0*squareSize }
            val step0_1 = step.filter { it.first in 0*squareSize..<1*squareSize && it.second in -1*squareSize..<0*squareSize }
            val step1_1 = step.filter { it.first in 1*squareSize..<2*squareSize && it.second in -1*squareSize..<0*squareSize }
            step10List.add(step10.size)
            step11List.add(step11.size)
            step01List.add(step01.size)
            step_11List.add(step_11.size)
            step_10List.add(step_10.size)
            step_1_1List.add(step_1_1.size)
            step0_1List.add(step0_1.size)
            step1_1List.add(step1_1.size)
        }
        step10List = step10List.filter { it > 0 }.toMutableList()
        step11List = step11List.filter { it > 0 }.toMutableList()
        step01List = step01List.filter { it > 0 }.toMutableList()
        step_11List = step_11List.filter { it > 0 }.toMutableList()
        step_10List = step_10List.filter { it > 0 }.toMutableList()
        step_1_1List = step_1_1List.filter { it > 0 }.toMutableList()
        step0_1List = step0_1List.filter { it > 0 }.toMutableList()
        step1_1List = step1_1List.filter { it > 0 }.toMutableList()

        var result = 0L
        val fieldMax = STEPS_PART2 / squareSize
        for (x in -fieldMax..fieldMax) {
            for (y in -fieldMax..fieldMax) {
                var start: Int
                var stable: Int
                if (x == 0 && y == 0) {
                    start = 0
                    stable = 129
                }
                else if (x == 0 && y != 0) {
                    start = abs(y) * 131 - 65
                    stable = start + 194
                }
                else if (x != 0 && y == 0) {
                    start = abs(x) * 131 - 65
                    stable = start + 194
                }
                else {
                    start = (abs(x) + abs(y) - 1) * 131 + 1
                    stable = start + 259
                }
                var plots = 0
                if (STEPS_PART2 in start..<stable) {
                    if (x < 0 && y < 0) {
                        plots = step_1_1List[STEPS_PART2-start]
                    }
                    else if (x == 0 && y < 0) {
                        plots = step0_1List[STEPS_PART2-start]
                    }
                    else if (x > 0 && y < 0) {
                        plots = step1_1List[STEPS_PART2-start]
                    }
                    else if (x > 0 && y == 0) {
                        plots = step10List[STEPS_PART2-start]
                    }
                    else if (x > 0 && y > 0) {
                        plots = step11List[STEPS_PART2-start]
                    }
                    else if (x == 0 && y > 0) {
                        plots = step01List[STEPS_PART2-start]
                    }
                    else if (x < 0 && y > 0) {
                        plots = step_11List[STEPS_PART2-start]
                    }
                    else if (x < 0 && y == 0) {
                        plots = step_10List[STEPS_PART2-start]
                    }
                }
                else if (STEPS_PART2 >= stable) {
                    plots = if (((abs(x) + abs(y)) % 2) == 0)
                        7483
                    else
                        7541
                }
                result += plots
            }
        }
        return result
    }

    private fun calculatePart1(
        stepSets: MutableList<MutableSet<Pair<Int, Int>>>,
        gardenPlan: List<String>
    ) {
        for (i in 1..STEPS_PART1) {
            val step = mutableSetOf<Pair<Int, Int>>()
            stepSets[i - 1].forEach {
                val x = it.first
                val y = it.second
                if (y - 1 in gardenPlan[0].indices && gardenPlan[x][y - 1] != ROCK)
                    step.add(Pair(x, y - 1))
                if (x + 1 in gardenPlan.indices && gardenPlan[x + 1][y] != ROCK)
                    step.add(Pair(x + 1, y))
                if (y + 1 in gardenPlan[0].indices && gardenPlan[x][y + 1] != ROCK)
                    step.add(Pair(x, y + 1))
                if (x - 1 in gardenPlan.indices && gardenPlan[x - 1][y] != ROCK)
                    step.add(Pair(x - 1, y))
            }
            stepSets.add(step)
        }
    }

}