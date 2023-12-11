package advent_2023

import java.io.FileReader

private const val GALAXY_CHAR = '#'

class Day11 {
    fun process(args: Array<String>) {
        val spaceChart = FileReader(args[0]).readLines()
        val emptyLines = spaceChart.findEmptyLines()
        val emptyColumns = spaceChart.findEmptyColumns()
        val galaxyLocations = spaceChart
            .mapIndexed { indexY, chars -> chars.mapIndexed { indexX, c -> Triple(indexX, indexY, c) } }
            .flatten()
            .filter { it.third == GALAXY_CHAR }

        println("Part1: " + galaxyLocations
            .mapIndexed { index, it -> calculateDistanceWithExpansion(
                index+1, it, galaxyLocations, emptyLines, emptyColumns, 1) }
            .sum())

        println("Part2: " + galaxyLocations
            .mapIndexed { index, it -> calculateDistanceWithExpansion(
                index+1, it, galaxyLocations, emptyLines, emptyColumns, 1000000-1) }
            .sum())
    }

    private fun calculateDistanceWithExpansion(
        startIndex: Int,
        galaxy: Triple<Int, Int, Char>,
        galaxyLocations: List<Triple<Int, Int, Char>>,
        emptyLines: List<Int>,
        emptyColumns: List<Int>,
        expansionFactor: Int
    ): Long {
        var result = 0L
        for (i in startIndex..<galaxyLocations.size) {
            val minX = galaxy.first.coerceAtMost(galaxyLocations[i].first)
            val maxX = galaxy.first.coerceAtLeast(galaxyLocations[i].first)
            val minY = galaxy.second.coerceAtMost(galaxyLocations[i].second)
            val maxY = galaxy.second.coerceAtLeast(galaxyLocations[i].second)
            result += maxX - minX + maxY - minY
            result += expansionFactor * emptyColumns.count { it in minX + 1..<maxX }
            result += expansionFactor * emptyLines.count { it in minY + 1..<maxY }
        }
        return result
    }
}

private fun List<String>.findEmptyColumns(): List<Int> {
    val galaxyColumns = mutableSetOf<Int>()
    this.forEach {
        it.forEachIndexed { index, char -> if (char == GALAXY_CHAR) galaxyColumns.add(index) }
    }
    val emptyColumns = (0..< this[0].length).toMutableList()
    emptyColumns.removeAll{it in galaxyColumns}
    return emptyColumns
}

private fun List<String>.findEmptyLines(): List<Int> {
    val emptyLines = mutableListOf<Int>()
    this.forEachIndexed { index, line ->
        if (!line.contains(GALAXY_CHAR)) emptyLines.add(index)
    }
    return emptyLines
}
