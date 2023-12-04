package advent_2023

import java.io.FileReader
import kotlin.math.pow

private const val base = 2.0

class Day4 {
    fun process(args: Array<String>) {
        val scratchCardTable = FileReader(args[0]).readLines()
        val scratchCards = scratchCardTable
            .map{ it.split(':', '|') }
            .map{ cardContent ->
                Triple(
                    cardContent[0]  // card number
                        .substring(5).trim().toInt(),
                    cardContent[1]  // winning numbers
                        .split(' ')
                        .filter{ it.isNotEmpty() }
                        .map{ it.toInt() },
                    cardContent[2]  // own numbers
                        .split(' ')
                        .filter{ it.isNotEmpty() }
                        .map{ it.toInt() }) }
        val pointList = scratchCards.map{ it.second.intersect(it.third.toSet()).size }

        println("Part 1: " + calculatePart1(pointList))

        println("Part 2: " + calculatePart2(pointList))
    }

    private fun calculatePart1(pointList: List<Int>) =
        pointList
            .map { it - 1 }
            .filter { it >= 0 }
            .sumOf{ base.pow(it).toInt() }

    private fun calculatePart2(pointList: List<Int>): Int {
        val stackList = mutableListOf<Int>()
        val countList = mutableListOf<Int>()
        pointList.forEach { points ->
            stackList.removeIf{ it < 1 }
            val count = 1 + stackList.size
            countList.add(count)
            stackList.replaceAll{ it - 1 }
            for (i in 1..count) {
                stackList.add(points)
            }
        }
        return countList.sum()
    }
}