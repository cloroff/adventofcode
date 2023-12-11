package advent_2023

import java.io.FileReader

class Day9 {
    fun process(args: Array<String>) {
        val oasisReport = FileReader(args[0]).readLines()
        val history = oasisReport.map{ it.split(' ').map { it.toLong() } }
        println("Part1: " + history.map { extrapolateNext(it) }.sum())
        println("Part2: " + history.map { extrapolatePrevious(it) }.sum())


    }

    private fun extrapolateNext(valueList: List<Long>): Long {
        var result: Long
        val differenceList = valueList.zipWithNext().map { it.second - it.first }
        if (differenceList.all { it == 0L }) {
            result = valueList.last()
        }
        else {
            val nextValue = extrapolateNext(differenceList)
            result = valueList.last() + nextValue
        }
        return result
    }

    private fun extrapolatePrevious(valueList: List<Long>): Long {
        var result: Long
        val differenceList = valueList.zipWithNext().map { it.second - it.first }
        if (differenceList.all { it == 0L }) {
            result = valueList.last()
        }
        else {
            val previousValue = extrapolatePrevious(differenceList)
            result = valueList.first() - previousValue
        }
        println("$result $valueList" )
        return result

    }

}