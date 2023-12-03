package advent_2023

import java.io.FileReader

class Day3 {
    private val numbers = listOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9" )
    private lateinit var symbols: List<String>

    fun process(args: Array<String>) {
        val engineSchematic = FileReader(args[0]).readLines()
        symbols = engineSchematic
            .flatMap{ it.toList() }
            .filterNot{ it.isDigit() || it == '.' }
            .toSet()
            .map{ it.toString() }
        println(symbols)
        println(engineSchematic)

        val enginePartList = engineSchematic
            .flatMapIndexed{ idx, value -> parseSchematicLine(idx, value).first }

        val engineSymbolList = engineSchematic
            .flatMapIndexed{ idx, value -> parseSchematicLine(idx, value).second }

        println("Part1: " + enginePartList
            .filter{ it.isAdjacentToSymbol(engineSymbolList) }
            .sumOf{ it.number })

        println("Part2: " + engineSymbolList
            .filter{ it.symbol == "*" }
            .sumOf{ it.gearPower(enginePartList) })

    }

    private fun parseSchematicLine(index: Int, line: String): Pair<List<EnginePart>, List<EngineSymbol>> {
        val parts = findParts(line)
            .map{ EnginePart(index, it) }
        val symbols = findSymbols(line)
            .map{ EngineSymbol(index, it) }
        return Pair(parts, symbols)
    }

    private fun findParts(line: String): MutableList<Triple<Int, Int, Int>> {
        val partList = mutableListOf<Triple<Int, Int, Int>>()
        var index = 0
        while (index < line.length) {
            val partFinder = line.findAnyOf(numbers, index)
            var part: Triple<Int, Int, Int>
            partFinder?.let {
                val startIndex = partFinder.first
                var endIndex = startIndex
                while (endIndex < line.length-1 && line[endIndex+1].isDigit()) {
                    endIndex += 1
                }
                part = Triple(startIndex, endIndex, line.substring(startIndex, endIndex + 1).toInt())
                partList.add(part)
                index = endIndex + 2
            }
            if (partFinder == null) {
                index = line.length
            }
        }
        return partList
    }

    private fun findSymbols(line: String): MutableList<Pair<Int, String>> {
        val symbolList = mutableListOf<Pair<Int, String>>()
        var index = 0
        while (index < line.length) {
            val symbol = line.findAnyOf(symbols, index)
            symbol?.let {
                symbolList.add(symbol)
                index = symbol.first + 1
            }
            if (symbol == null) {
                index = line.length
            }
        }
        return symbolList
    }
}

data class EnginePart(val line: Int, val start: Int, val end: Int, val number: Int) {
    fun isAdjacentToSymbol(engineSymbolList: List<EngineSymbol>): Boolean {
        return engineSymbolList
            .any{ it.line in line - 1..line + 1
                    && it.index in start - 1..end + 1 }
    }

    constructor(line: Int, part: Triple<Int, Int, Int>) : this(line, part.first, part.second, part.third)
}

data class EngineSymbol(val line: Int, val index: Int, val symbol: String) {
    fun gearPower(enginePartList: List<EnginePart>): Int {
        var power = 0
        val gearPartList = enginePartList
            .filter{ it.line in line - 1..line + 1
                    && index in it.start - 1..it.end + 1 }
        if (gearPartList.size == 2) {
            power = gearPartList.first().number * gearPartList.last().number
        }
        return power
    }

    constructor(line: Int, symbol: Pair<Int, String>) : this(line, symbol.first, symbol.second)
}