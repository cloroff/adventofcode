package advent_2023

import java.io.FileReader

class Day13 {
    fun process(args: Array<String>) {
        val mirrorField = FileReader(args[0]).readLines()
        val resultListPart1 = mutableListOf<Pair<Int, Char>>()
        val mirrorPatterns = initData(mirrorField)
        var resultCount = 0
        mirrorPatterns.forEach {
            resultCount += processPatternPart1(it, resultListPart1)
        }
        println("Part1: $resultCount")

        resultCount = 0
        val resultListPart2 = mutableListOf<Int>()
        mirrorPatterns.forEachIndexed { index, pattern ->
            for (x in pattern.first().indices) {
                for (y in pattern.indices) {
                    if (resultCount == 0) {
                        resultCount = processPatternPart2(
                            pattern.mapIndexed { indexY, line ->
                                if (indexY == y)
                                    String(line.mapIndexed { indexX, character ->
                                        if (indexX == x)
                                            reverseCharacter(character)
                                        else
                                            character
                                    }.toCharArray())
                                else
                                    line
                            },
                            resultListPart1,
                            index
                        )
                    }
                }
            }
            resultListPart2.add(resultCount)
            resultCount = 0
        }
        resultListPart2.forEachIndexed { index, it ->  println("$index:$it") }
        println("Part2: ${resultListPart2.sum()}")
    }

    private fun initData(mirrorField: List<String>): MutableList<List<String>> {
        val mirrorPatterns = mutableListOf<List<String>>()
        var singleMirrorPattern = mutableListOf<String>()
        mirrorField.forEach {
            if (it.isNotEmpty()) {
                singleMirrorPattern.add(it)
            } else {
                mirrorPatterns.add(singleMirrorPattern)
                singleMirrorPattern = mutableListOf()
            }
        }
        // add last pattern
        mirrorPatterns.add(singleMirrorPattern)
        return mirrorPatterns
    }

    private fun processPatternPart1(
        it: List<String>,
        resultList: MutableList<Pair<Int, Char>>,
    ): Int {
        var resultCount = 0
        var axisRange = (0..<it[0].length - 1).toMutableSet()
        it.forEach {
            if (axisRange.isNotEmpty()) {
                axisRange = findVerticalMirrorPattern(it.toCharArray(), axisRange)
            }
        }
        if (axisRange.size == 0) {
            axisRange = (0..<it.size - 1).toMutableSet()
            axisRange = findHorizontalMirrorPattern(it, axisRange)
            if (axisRange.size > 0) {
                resultCount += (axisRange.sum() + 1) * 100
                resultList.add(Pair(axisRange.sum() + 1, 'h'))
            }
        } else {
            resultCount += axisRange.sum() + 1
            resultList.add(Pair(axisRange.sum() + 1, 'v'))
        }
        return resultCount
    }

    private fun processPatternPart2(
        it: List<String>,
        resultList: MutableList<Pair<Int, Char>>,
        index: Int
    ): Int {
        var resultCount = 0
        var axisRange = (0..<it[0].length - 1).toMutableSet()
        it.forEach {
            if (axisRange.isNotEmpty()) {
                axisRange = findVerticalMirrorPattern(it.toCharArray(), axisRange)
            }
        }
        if (resultList[index].second == 'v') {
            axisRange.remove(resultList[index].first-1)
        }
        if (axisRange.size == 0) {
            axisRange = (0..<it.size - 1).toMutableSet()
            axisRange = findHorizontalMirrorPattern(it, axisRange)
            if (resultList[index].second == 'h') {
                axisRange.remove(resultList[index].first-1)
            }
            if (axisRange.size > 0) {
                resultCount += (axisRange.sum() + 1) * 100
            }
        } else {
            resultCount += axisRange.sum() + 1
        }
        return resultCount
    }

    private fun reverseCharacter(character: Char): Char {
        return if (character == '.') 
            '#' 
        else
            '.'
    }

    private fun findHorizontalMirrorPattern(patternList: List<String>, horizontalAxisList: MutableSet<Int>): MutableSet<Int> {
        val possibleAxis = mutableSetOf<Int>()
        for (i in horizontalAxisList) {
            if (patternList[i] == patternList[i+1]) {
                for (j in 0..i) {
                    if (i + j + 1 < patternList.size) {
                        if (patternList[i-j] == patternList[i+j+1]) {
                            possibleAxis.add(i)
                        }
                        else {
                            possibleAxis.remove(i)
                            break
                        }
                    }
                }
            }
        }
        return possibleAxis
    }

    private fun findVerticalMirrorPattern(line: CharArray, verticalAxisList: MutableSet<Int>): MutableSet<Int> {
        val possibleAxis = mutableSetOf<Int>()
        for (i in verticalAxisList) {
            if (line[i] == line[i+1]) {
                for (j in 0..i) {
                    if (i + j + 1 < line.size) {
                        if (line[i-j] == line[i+j+1]) {
                            possibleAxis.add(i)
                        }
                        else {
                            possibleAxis.remove(i)
                            break
                        }
                    }
                }
            }
        }
        return possibleAxis
    }
}