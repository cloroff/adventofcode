package advent_2023

import java.io.FileReader

private const val PATTER_SEARCH_START_INDEX = 2000

private const val PATTERN_SEARCH_END_INDEX = 4000

private const val TARGET_CYCLES = 1000000000

private const val MIN_PATTERN_LENGTH = 3

private const val MAX_PATTERN_LENGTH = 100

class Day14 {
    fun process(args: Array<String>) {
        val metalPlatform = FileReader(args[0]).readLines()
        solvePart1(metalPlatform)
        val platformGrid = mutableMapOf<PlatformPosition, Rock?>()
        val maxX = metalPlatform[0].length
        val maxY = metalPlatform.size
        metalPlatform.forEachIndexed { indexY, line ->
            line.forEachIndexed { indexX, character ->
                platformGrid[PlatformPosition(indexX+1, metalPlatform.size-indexY)] =
                    when (character) {
                        'O' -> Rock.ROUND
                        '#' -> Rock.CUBE
                        else -> null
                    }
            }
        }
        val loadList = mutableListOf<Int>()
        for (i in 1..PATTERN_SEARCH_END_INDEX) {
            cycle(platformGrid, maxX, maxY)
            if (i >= PATTER_SEARCH_START_INDEX) loadList.add(platformGrid.calcLoad())
        }
        println("Loop list: ${findLoops(loadList)}")
    }

    private fun solvePart1(metalPlatform: List<String>) {
        val platformGrid = mutableMapOf<PlatformPosition, Rock?>()
        val maxX = metalPlatform[0].length
        val maxY = metalPlatform.size
        metalPlatform.forEachIndexed { indexY, line ->
            line.forEachIndexed { indexX, character ->
                platformGrid[PlatformPosition(indexX+1, metalPlatform.size-indexY)] =
                    when (character) {
                        'O' -> Rock.ROUND
                        '#' -> Rock.CUBE
                        else -> null
                    }
            }
        }
        platformGrid.tiltNorth(maxX, maxY)
        platformGrid.visualize(maxX, maxY)
        println("Part1: ${platformGrid.calcLoad()}")
    }

    private fun findLoops(loadList: MutableList<Int>): List<Int> {
        var resultList = listOf<Int>()
        for (patternLength in MIN_PATTERN_LENGTH..MAX_PATTERN_LENGTH) {
            var pattern = false
            for (i in 0..loadList.lastIndex-2*patternLength step patternLength) {
                pattern = loadList.subList(i, i + patternLength - 1) ==
                        loadList.subList(i+patternLength, i + 2*patternLength - 1)
            }
            if (pattern) {
                println("Pattern length: $patternLength")
                val offset = (TARGET_CYCLES - PATTER_SEARCH_START_INDEX)%patternLength
                resultList = loadList.subList(0, patternLength)
                println("Part2: ${resultList[offset]}")
                break
            }
        }
        return resultList
    }

    private fun cycle(
        platformGrid: MutableMap<PlatformPosition, Rock?>,
        maxX: Int,
        maxY: Int
    ) {
        platformGrid.tiltNorth(maxX, maxY)
        platformGrid.tiltWest(maxX, maxY)
        platformGrid.tiltSouth(maxX, maxY)
        platformGrid.tiltEast(maxX, maxY)
    }
}

private fun MutableMap<PlatformPosition, Rock?>.visualize(maxX: Int, maxY: Int) {
    for (y in maxY downTo 1) {
        for (x in 1..maxX) {
            val displayCharacter = when (this[PlatformPosition(x, y)]) {
                Rock.ROUND -> 'O'
                Rock.CUBE -> '#'
                else -> '.'
            }
            print(displayCharacter)
        }
        println()
    }
    println()
}

private fun MutableMap<PlatformPosition, Rock?>.tiltNorth(maxX: Int, maxY: Int) {
    val freeEdgePositions = this.filter{ it.key.posY == maxY && it.value == null }.mapValues { it.key.posY }.mapKeys { it.key.posX }.toMutableMap()
    for (y in maxY downTo 1) {
        for (x in 1..maxX) {
            val freePosition = freeEdgePositions[x]
            when (this[PlatformPosition(x, y)]) {
                Rock.ROUND -> {
                    freePosition?.let {
                        this[PlatformPosition(x, freePosition)] = Rock.ROUND
                        this[PlatformPosition(x, y)] = null
                        freeEdgePositions[x] = freePosition - 1
                    }
                }
                Rock.CUBE -> freePosition?.let {
                    freeEdgePositions.remove(x)
                }
                else -> if (freePosition == null) freeEdgePositions[x] = y
            }
        }
    }
}

private fun MutableMap<PlatformPosition, Rock?>.tiltWest(maxX: Int, maxY: Int) {
    val freeEdgePositions = this.filter{ it.key.posX == 1 && it.value == null }.mapValues { it.key.posX }.mapKeys { it.key.posY }.toMutableMap()
    for (x in 1..maxX) {
        for (y in 1..maxY) {
            val freePosition = freeEdgePositions[y]
            when (this[PlatformPosition(x, y)]) {
                Rock.ROUND -> {
                    freePosition?.let {
                        this[PlatformPosition(freePosition, y)] = Rock.ROUND
                        this[PlatformPosition(x, y)] = null
                        freeEdgePositions[y] = freePosition + 1
                    }
                }
                Rock.CUBE -> freePosition?.let {
                    freeEdgePositions.remove(y)
                }
                else -> if (freePosition == null) freeEdgePositions[y] = x
            }
        }
    }
}

private fun MutableMap<PlatformPosition, Rock?>.tiltSouth(maxX: Int, maxY: Int) {
    val freeEdgePositions = this.filter{ it.key.posY == 1 && it.value == null }.mapValues { it.key.posY }.mapKeys { it.key.posX }.toMutableMap()
    for (y in 1..maxY) {
        for (x in 1..maxX) {
            val freePosition = freeEdgePositions[x]
            when (this[PlatformPosition(x, y)]) {
                Rock.ROUND -> {
                    freePosition?.let {
                        this[PlatformPosition(x, freePosition)] = Rock.ROUND
                        this[PlatformPosition(x, y)] = null
                        freeEdgePositions[x] = freePosition + 1
                    }
                }
                Rock.CUBE -> freePosition?.let {
                    freeEdgePositions.remove(x)
                }
                else -> if (freePosition == null) freeEdgePositions[x] = y
            }
        }
    }
}

private fun MutableMap<PlatformPosition, Rock?>.tiltEast(maxX: Int, maxY: Int) {
    val freeEdgePositions = this.filter{ it.key.posX == maxX && it.value == null }.mapValues { it.key.posX }.mapKeys { it.key.posY }.toMutableMap()
    for (x in maxX downTo 1) {
        for (y in 1..maxY) {
            val freePosition = freeEdgePositions[y]
            when (this[PlatformPosition(x, y)]) {
                Rock.ROUND -> {
                    freePosition?.let {
                        this[PlatformPosition(freePosition, y)] = Rock.ROUND
                        this[PlatformPosition(x, y)] = null
                        freeEdgePositions[y] = freePosition - 1
                    }
                }
                Rock.CUBE -> freePosition?.let {
                    freeEdgePositions.remove(y)
                }
                else -> if (freePosition == null) freeEdgePositions[y] = x
            }
        }
    }
}

private fun MutableMap<PlatformPosition, Rock?>.calcLoad(): Int {
    return this.map { if (it.value == Rock.ROUND) it.key.posY else 0 }.sum()
}


data class PlatformPosition (val posX: Int, val posY: Int)

enum class Rock {
    ROUND, CUBE
}