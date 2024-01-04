package advent_2023

import java.io.FileReader

class Day16 {

    private val loopDetector = mutableSetOf<Pair<Pair<Int, Int>, Direction>>()
    private val energized = mutableSetOf<Pair<Int, Int>>()
    private var firstBeam = true
    fun process(args: Array<String>) {
        val mirrorGrid = FileReader(args[0]).readLines()
        val beamStack = ArrayDeque <Pair<Pair<Int,Int>, Direction>>()
        initBeam(beamStack, Pair(Pair(-1, 0), Direction.EAST), mirrorGrid)
        println("Part1: ${energized.size}")

        val maxEnergized = processPart2(mirrorGrid, beamStack)
        println("Part2: $maxEnergized")
    }

    private fun processPart2(
        mirrorGrid: List<String>,
        beamStack: ArrayDeque<Pair<Pair<Int, Int>, Direction>>
    ): Int {
        var maxEnergized = 0
        for (x in mirrorGrid[0].indices) {
            maxEnergized = maxEnergized.coerceAtLeast(
                initBeam(beamStack, Pair(Pair(x, -1), Direction.SOUTH), mirrorGrid)
            )
            maxEnergized = maxEnergized.coerceAtLeast(
                initBeam(beamStack, Pair(Pair(x, mirrorGrid.size), Direction.NORTH), mirrorGrid)
            )
        }
        for (y in mirrorGrid.indices) {
            maxEnergized = maxEnergized.coerceAtLeast(
                initBeam(beamStack, Pair(Pair(-1, y), Direction.EAST), mirrorGrid)
            )
            maxEnergized = maxEnergized.coerceAtLeast(
                initBeam(beamStack, Pair(Pair(mirrorGrid[0].length, y), Direction.WEST), mirrorGrid)
            )
        }
        return maxEnergized
    }

    private fun initBeam(
        beamStack: ArrayDeque<Pair<Pair<Int, Int>, Direction>>,
        startBeam: Pair<Pair<Int, Int>, Direction>,
        mirrorGrid: List<String>
    ): Int {
        loopDetector.clear()
        energized.clear()
        firstBeam = true
        beamStack.addFirst(startBeam)

        while (beamStack.isNotEmpty()) {
            val beam = beamStack.removeFirst()
            val startX = beam.first.first
            val startY = beam.first.second
            val direction = beam.second
            processBeam(mirrorGrid, beamStack, startX, startY, direction)
        }
        return energized.size
    }

    private fun processBeam(
        mirrorGrid: List<String>,
        beamStack: ArrayDeque<Pair<Pair<Int, Int>, Direction>>,
        startX: Int,
        startY: Int,
        direction: Direction
    ) {
        var nextX = startX
        var nextY = startY
        var nextDirection = direction
        while (firstBeam || (nextX in mirrorGrid[0].indices && nextY in mirrorGrid.indices &&
            Pair(Pair(nextX, nextY), nextDirection) !in loopDetector))
        {
            when (firstBeam) {
                true -> firstBeam = false
                false -> {
                    loopDetector.add(Pair(Pair(nextX, nextY), nextDirection))
                    energized.add(Pair(nextX, nextY))
                }
            }
            when (nextDirection) {
                Direction.NORTH -> nextY -= 1
                Direction.EAST  -> nextX += 1
                Direction.SOUTH -> nextY += 1
                Direction.WEST  -> nextX -= 1
            }
            if (nextX in mirrorGrid[0].indices && nextY in mirrorGrid.indices) {
                nextDirection = enterTile(mirrorGrid, beamStack, nextX, nextY, nextDirection)
            }
        }
    }

    private fun enterTile(
        mirrorGrid: List<String>,
        beamStack: ArrayDeque<Pair<Pair<Int, Int>, Direction>>,
        x: Int,
        y: Int,
        direction: Direction
    ): Direction {
        var newDirection = direction
        when (mirrorGrid[y][x]) {
            '|' -> {
                when (direction) {
                    Direction.EAST, Direction.WEST -> {
                        newDirection = Direction.NORTH
                        beamStack.addFirst(Pair(Pair(x, y), Direction.SOUTH))
                    }
                    else -> {}
                }
            }
            '-' -> {
                when (direction) {
                    Direction.NORTH, Direction.SOUTH -> {
                        newDirection = Direction.EAST
                        beamStack.addFirst(Pair(Pair(x, y), Direction.WEST))
                    }
                    else -> {}
                }
            }
            '/' -> newDirection = when (direction) {
                Direction.NORTH -> Direction.EAST
                Direction.EAST -> Direction.NORTH
                Direction.SOUTH -> Direction.WEST
                Direction.WEST -> Direction.SOUTH
            }
            '\\' -> newDirection = when (direction) {
                Direction.NORTH -> Direction.WEST
                Direction.EAST -> Direction.SOUTH
                Direction.SOUTH -> Direction.EAST
                Direction.WEST -> Direction.NORTH
            }
        }
        return newDirection
    }
}

enum class Direction {
    NORTH, EAST, SOUTH, WEST
}
