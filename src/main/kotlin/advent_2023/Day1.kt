package advent_2023

import java.io.FileReader

class Day1 {
    private val spelledDigits = listOf("one", "two", "three", "four", "five", "six", "seven", "eight", "nine")

    fun process(args: Array<String>) {
        val calibrationDocument = FileReader(args[0]).readLines()
        println("Input: $calibrationDocument")

        println("Part1: " + calibrationDocument
            .map { character -> character.filter { it.isDigit() } }
            .sumOf { ("" + it.first() + it.last()).toInt() })

        println("Part2: " + calibrationDocument
            .map { insertNumbers(it) }
            .map { character -> character.filter { it.isDigit() } }
            .sumOf { ("" + it.first() + it.last()).toInt() })
    }

    private fun insertNumbers(input: String) : String {
        var output = input
        val firstFind = output.findAnyOf(spelledDigits)
        firstFind?.let {
            output = output.addStringAtIndex((spelledDigits.indexOf(it.second)+1).toString(), it.first) }
        val lastFind = output.findLastAnyOf(spelledDigits)
        lastFind?.let {
            output = output.addStringAtIndex(
                (spelledDigits.indexOf(it.second)+1).toString(), it.first + it.second.length) }
        return output
    }

    fun String.addStringAtIndex(string: String, index: Int) =
        StringBuilder(this).apply { insert(index, string) }.toString()
}