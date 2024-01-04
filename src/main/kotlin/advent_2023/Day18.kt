package advent_2023

import java.io.FileReader

class Day18 {
    fun process(args: Array<String>) {
        val digPlan = FileReader(args[0]).readLines()
        var digMap = initDigMapPart1(digPlan)

        var startTime = System.currentTimeMillis()
        var cubicMeters = calculateVolume(digMap)
        println("Time: ${System.currentTimeMillis() - startTime}ms")
        println("Part1: $cubicMeters")

        digMap = initDigMapPart2(digPlan)

        startTime = System.currentTimeMillis()
        cubicMeters = calculateVolume(digMap)
        println("Time: ${System.currentTimeMillis() - startTime}ms")
        println("Part2: $cubicMeters")
    }

    private fun initDigMapPart1(digPlan: List<String>): MutableList<Trench> {
        val instructionList = digPlan
            .map { it.split(' ') }
            .map { DigInstruction(it[0], it[1].toLong()) }
        return initDigMap(instructionList)
    }

    private fun initDigMapPart2(digPlan: List<String>): MutableList<Trench> {
        val instructionList = digPlan
            .map { it.split(' ') }
            .map { listOf(it[2].substring(2, 7), it[2].substring(7, 8)) }
            .map { DigInstruction(getDirection(it[1].toInt()), it[0].toLong(radix = 16)) }
        return initDigMap(instructionList)
    }

    private fun initDigMap(instructionList: List<DigInstruction>): MutableList<Trench> {
        val digMap = mutableListOf<Trench>()
        var currentX = 0L
        var currentY = 0L
        instructionList.forEach { instruction ->
            when (instruction.direction) {
                "U" -> {
                    digMap.add(Trench(currentX..currentX, currentY..currentY + instruction.length))
                    currentY += instruction.length
                }

                "D" -> {
                    digMap.add(Trench(currentX..currentX, currentY - instruction.length..currentY))
                    currentY -= instruction.length
                }

                "R" -> {
                    digMap.add(Trench(currentX..currentX + instruction.length, currentY..currentY))
                    currentX += instruction.length
                }

                "L" -> {
                    digMap.add(Trench(currentX - instruction.length..currentX, currentY..currentY))
                    currentX -= instruction.length
                }
            }
        }
        return digMap
    }

    private fun getDirection(directionNumber: Int): String {
        return when (directionNumber) {
            0 -> "R"
            1 -> "D"
            2 -> "L"
            3 -> "U"
            else -> throw Exception("Unexpected direction argument")
        }
    }

    private fun calculateVolume(digMap: List<Trench>): Long {
        var cubicMeters = 0L
        var inside = false
        var trench = false
        println(digMap)
        val minX = digMap.minOf { it.posX.minBy { it } }
        val maxX = digMap.maxOf { it.posX.maxBy { it } }
        val minY = digMap.minOf { it.posY.minBy { it } }
        val maxY = digMap.maxOf { it.posY.maxBy { it } }
        println("minX: $minX")
        println("maxX: $maxX")
        println("minY: $minY")
        println("maxY: $maxY")
        var previousLine: List<Trench> = listOf()
        var previousCubicMeters = 0L
        for (y in maxY downTo minY) {
            val time = System.currentTimeMillis()
            val line = digMap.filter { y in it.posY }
            if (line == previousLine) cubicMeters += previousCubicMeters
            else {
                val localMinX = line.minBy { it.posX.first }.posX.first
                val localMaxX = line.maxBy { it.posX.last }.posX.last
                previousCubicMeters = 0L
                for (x in localMinX..localMaxX) {
                    if (line.any{ x in it.posX}) {
//                    print("#")
                        cubicMeters += 1
                        previousCubicMeters += 1
                        inside = false
                        trench = true
                    } else {
                        if (inside || (trench && isInsideTrench(
                                x, y, digMap, line, maxX, maxY, minX, minY
                            ))
                        ) {
                            cubicMeters += 1
                            previousCubicMeters += 1
//                        print("#")
                            inside = true
                            trench = false
                        } else {
//                        print(" ")
                            inside = false
                            trench = false
                        }
                    }
                }
            }

//            println()
            println("$y: ${System.currentTimeMillis()-time}")
            previousLine = line
        }
        return cubicMeters
    }

    private fun isInsideTrench(
        x: Long,
        y: Long,
        digMap: List<Trench>,
        line: List<Trench>,
        maxX: Long,
        maxY: Long,
        minX: Long,
        minY: Long
    ): Boolean {
        var result = false
        if (line.any{ x in it.posX })
            result = false
        else
            if (checkOddTraversals(x, y, digMap, line, maxX, maxY, minX, minY))
                result = true
        return result
    }

    private fun checkOddTraversals(
        xStart: Long,
        yStart: Long,
        digMap: List<Trench>,
        line: List<Trench>,
        maxX: Long,
        maxY: Long,
        minX: Long,
        minY: Long
    ): Boolean {
        var result = false
        if ((xStart <= maxX / 2) && (yStart > maxY / 2)) {
            if (xStart - minX > maxY - yStart) {
                var counter = checkNorth(xStart, yStart, digMap, maxY)
                if (counter %2 == 1) {
                    counter = checkEast(xStart, yStart, digMap, line, minX)
                    if (counter %2 == 1)
                        result = true
                }
            }
            else {
                var counter = checkEast(xStart, yStart, digMap, line, minX)
                if (counter %2 == 1) {
                    counter = checkNorth(xStart, yStart, digMap, maxY)
                    if (counter % 2 == 1)
                        result = true
                }
            }
        }
        else if ((xStart > maxX / 2) && (yStart > maxY / 2)) {
            if (maxX - xStart > maxY - yStart) {
                var counter = checkNorth(xStart, yStart, digMap, maxY)
                if (counter % 2 == 1) {
                    counter = checkWest(xStart, yStart, digMap, line, maxX)
                    if (counter % 2 == 1)
                        result = true
                }
            }
            else {
                var counter = checkWest(xStart, yStart, digMap, line, maxX)
                if (counter % 2 == 1) {
                    counter = checkNorth(xStart, yStart, digMap, maxY)
                    if (counter % 2 == 1)
                        result = true
                }
            }
        }
        else if ((xStart > maxX / 2) && (yStart <= maxY / 2)) {
            if (maxX - xStart > yStart - minY) {
                var counter = checkSouth(xStart, yStart, digMap, minY)
                if (counter %2 == 1) {
                    counter = checkWest(xStart, yStart, digMap, line, maxX)
                    if (counter %2 == 1)
                        result = true
                }
            }
            else {
                var counter = checkWest(xStart, yStart, digMap, line, maxX)
                if (counter %2 == 1) {
                    counter = checkSouth(xStart, yStart, digMap, minY)
                    if (counter %2 == 1)
                        result = true
                }
            }
        }
        else if ((xStart <= maxX / 2) && (yStart <= maxY / 2)) {
            if (xStart - minX > yStart - minY) {
                var counter = checkSouth(xStart, yStart, digMap, minY)
                if (counter % 2 == 1) {
                    counter = checkEast(xStart, yStart, digMap, line, minX)
                    if (counter % 2 == 1)
                        result = true
                }
            }
            else {
                var counter = checkEast(xStart, yStart, digMap, line, minX)
                if (counter % 2 == 1) {
                    counter = checkSouth(xStart, yStart, digMap, minY)
                    if (counter % 2 == 1)
                        result = true
                }
            }
        }
        return result
    }

    private fun checkNorth(
        xStart: Long,
        yStart: Long,
        digMap: List<Trench>,
        maxY: Long
    ): Int {
        var result = 0
        var previousWasTrench = false
        var leftCorner = false
        var rightCorner = false
        for (y in yStart + 1..maxY) {
            if (digMap.any{ xStart in it.posX && y in it.posY }) {
                if (!previousWasTrench) {
                    result++
                    previousWasTrench = true
                } else if (!rightCorner && !leftCorner) {
                    leftCorner = digMap.any{ xStart-1 in it.posX && y-1 in it.posY }
                    rightCorner = digMap.any{ xStart+1 in it.posX && y-1 in it.posY }
                }
            } else {
                if (previousWasTrench && leftCorner && digMap.any{ xStart-1 in it.posX && y-1 in it.posY }) {
                    result--
                }
                if (previousWasTrench && rightCorner && digMap.any{ xStart+1 in it.posX && y-1 in it.posY }) {
                    result--
                }
                leftCorner = false
                rightCorner = false
                previousWasTrench = false
            }
        }
        return result
    }

    private fun checkSouth(
        xStart: Long,
        yStart: Long,
        digMap: List<Trench>,
        minY: Long
    ): Int {
        var result = 0
        var previousWasTrench = false
        var leftCorner = false
        var rightCorner = false
        for (y in yStart - 1 downTo minY) {
            if (digMap.any{ xStart in it.posX && y in it.posY }) {
                if (!previousWasTrench) {
                    result++
                    previousWasTrench = true
                } else if (!rightCorner && !leftCorner) {
                    leftCorner = digMap.any{ xStart+1 in it.posX && y+1 in it.posY }
                    rightCorner = digMap.any{ xStart-1 in it.posX && y+1 in it.posY }
                }
            } else {
                if (previousWasTrench && leftCorner && digMap.any{ xStart+1 in it.posX && y+1 in it.posY }) {
                    result--
                }
                if (previousWasTrench && rightCorner && digMap.any{ xStart-1 in it.posX && y+1 in it.posY }) {
                    result--
                }
                leftCorner = false
                rightCorner = false
                previousWasTrench = false
            }
        }
        return result
    }

    private fun checkWest(
        xStart: Long,
        yStart: Long,
        digMap: List<Trench>,
        line: List<Trench>,
        maxX: Long
    ): Int {
        var result = 0
        var previousWasTrench = false
        var leftCorner = false
        var rightCorner = false
        for (x in xStart + 1..maxX) {
            if (line.any{ x in it.posX }) {
                if (!previousWasTrench) {
                    result++
                    previousWasTrench = true
                } else if (!rightCorner && !leftCorner) {
                    leftCorner = digMap.any{ x-1 in it.posX && yStart+1 in it.posY }
                    rightCorner = digMap.any{ x-1 in it.posX && yStart-1 in it.posY }
                }
            } else {
                if (previousWasTrench && leftCorner && digMap.any{ x-1 in it.posX && yStart+1 in it.posY }) {
                    result--
                }
                if (previousWasTrench && rightCorner && digMap.any{ x-1 in it.posX && yStart-1 in it.posY }) {
                    result--
                }
                leftCorner = false
                rightCorner = false
                previousWasTrench = false
            }
        }
        return result
    }

    private fun checkEast(
        xStart: Long,
        yStart: Long,
        digMap: List<Trench>,
        line: List<Trench>,
        minX: Long
    ): Int {
        var result = 0
        var previousWasTrench = false
        var leftCorner = false
        var rightCorner = false
        for (x in xStart - 1 downTo minX) {
            if (line.any{ x in it.posX }) {
                if (!previousWasTrench) {
                    result++
                    previousWasTrench = true
                } else if (!rightCorner && !leftCorner) {
                    leftCorner = digMap.any{ x+1 in it.posX && yStart-1 in it.posY }
                    rightCorner = digMap.any{ x+1 in it.posX && yStart+1 in it.posY }
                }
            } else {
                if (previousWasTrench && leftCorner && digMap.any{ x+1 in it.posX && yStart-1 in it.posY }) {
                    result--
                }
                if (previousWasTrench && rightCorner && digMap.any{ x+1 in it.posX && yStart+1 in it.posY }) {
                    result--
                }
                leftCorner = false
                rightCorner = false
                previousWasTrench = false
            }
        }
        return result
    }

    data class DigInstruction (val direction: String, val length: Long)
    data class Trench (val posX: LongRange, val posY: LongRange)
}
