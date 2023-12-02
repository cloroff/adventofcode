package advent_2023

import java.io.FileReader

class Day2 {
    val colors    = listOf("red", "green", "blue")
    val max_red   = 12
    val max_green = 13
    val max_blue  = 14


    fun process(args: Array<String>) {
        val gameRecords = FileReader(args[0]).readLines()
        println("Input: $gameRecords")
        println("Part1: " + calcPart1(processGameRecords(gameRecords)))
        println("Part2: " + calcPart2(processGameRecords(gameRecords)))
    }

    private fun processGameRecords(gameRecords: List<String>): List<Pair<Int, List<ColorDraw>>> {
        return gameRecords
            .map { it.split(':') }
            .map { Pair(it.first(), it.last()) }
            .map { Pair(it.first.substring("Game ".length).toInt(), parseGameResult(it.second.split(';'))) }
        }

    private fun calcPart1(gameRecords: List<Pair<Int, List<ColorDraw>>>): Int {
        return gameRecords
            .filter { resultPair -> resultPair.second
                .all{it.red <= max_red && it.green <= max_green && it.blue <= max_blue} }
            .sumOf { it.first }
    }

    private fun calcPart2(gameRecords: List<Pair<Int, List<ColorDraw>>>): Int {
        return gameRecords
            .map{ it.second }
            .sumOf { color -> color.maxOf { it.red } * color.maxOf { it.green } * color.maxOf { it.blue }}

    }

    private fun parseGameResult(games: List<String>): List<ColorDraw> {
        val colorDrawList = mutableListOf(ColorDraw())

        for (game in games) {
            val colorList = game.split(',')
                .map{ Pair (it, it.findAnyOf(colors)) }
                .map{ Pair (it.first.substring(0, it.second?.first?.minus(1) ?: 0).trim().toInt(), it.second!!.second) }
            colorDrawList.add(ColorDraw(colorList))
        }

        return colorDrawList
    }
}

data class ColorDraw(val red: Int = 0, val green: Int = 0, val blue: Int = 0) {
    companion object {
        operator fun invoke (colorList: List<Pair<Int, String>>): ColorDraw {
            var red = 0
            var green = 0
            var blue = 0
            for (color in colorList) {
                when (color.second) {
                    "red" -> red = color.first
                    "green" -> green = color.first
                    "blue" -> blue = color.first
                }
            }
            return ColorDraw(red, green, blue)
        }
    }
}