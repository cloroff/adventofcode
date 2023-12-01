package advent_2023

import java.io.FileReader

class Day1 {
    private val spelledDigits = listOf("one", "two", "three", "four", "five", "six", "seven", "eight", "nine")

    fun process(args: Array<String>) {
        val calibrationDocument = FileReader(args[0]).readLines()
        println("Input: $calibrationDocument")

        println("Part1: " + calibrationDocument.map { character -> character.filter { it.isDigit() } }
            .sumOf { ("" + it.first() + it.last()).toInt() })

        println("Part2: " + calibrationDocument.map { replaceNumbers(it) }
            .map { character -> character.filter { it.isDigit() } }
            .sumOf { ("" + it.first() + it.last()).toInt() })
    }

    private fun replaceNumbers(input: String) : String {
        var output = input
        val firstFind = output.findAnyOf(spelledDigits)
        firstFind?.let { output = output.replace(it.second, (spelledDigits.indexOf(it.second)+1).toString()) }
        val lastFind = output.findLastAnyOf(spelledDigits)
        lastFind?.let { output = output.replace(it.second, (spelledDigits.indexOf(it.second)+1).toString()) }
        return output
    }
}