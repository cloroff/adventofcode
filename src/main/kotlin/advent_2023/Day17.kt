package advent_2023

import Direction
import Position
import java.io.FileReader
import java.util.PriorityQueue

private const val PART1_MIN_MOVES = 1
private const val PART1_MAX_MOVES = 3
private const val PART2_MIN_MOVES = 4
private const val PART2_MAX_MOVES = 10

class Day17 {
    private val startTime = System.currentTimeMillis()
    private val startNode = PathPosition(Position(0, 0), Direction.EAST, 0)

    fun process(args: Array<String>) {
        val cityMap = FileReader(args[0]).readLines()
        val heatMap = cityMap.map { line -> line.toCharArray().map { it.digitToInt() } }
        var pathHeat = findMinimumPath(heatMap, PART1_MIN_MOVES, PART1_MAX_MOVES)
        println("Part1: $pathHeat")
        println("Time: ${System.currentTimeMillis()-startTime}")

        pathHeat = findMinimumPath(heatMap, PART2_MIN_MOVES, PART2_MAX_MOVES)
        println("Part2: $pathHeat")
        println("Time: ${System.currentTimeMillis()-startTime}")
    }

    private fun findMinimumPath(heatMap: List<List<Int>>, minMoves: Int, maxMoves: Int): Int {
        val endPosition = Position(heatMap.first().lastIndex, heatMap.lastIndex)
        val startCrucible = Crucible(startNode, 0)
        val visitedNodes = mutableSetOf<PathPosition>()
        val pathMap = mutableMapOf(startNode to startCrucible)
        val queue = PriorityQueue<Crucible>()
        queue.add(startCrucible)

        while (queue.isNotEmpty()) {
            val currentCrucible = queue.poll()
            if (currentCrucible.pathPosition !in visitedNodes) {
                visitedNodes.add(currentCrucible.pathPosition)
                if (currentCrucible.pathPosition.position == endPosition) {
                    return currentCrucible.heat
                }
                val validNeighbours = findValidNeighbours(currentCrucible.pathPosition, visitedNodes, endPosition, minMoves, maxMoves)
                for (neighbour in validNeighbours) {
                    val nextCrucible = Crucible(neighbour, currentCrucible.heat + heatMap[neighbour.position.y][neighbour.position.x])
                    val existingCrucible = pathMap.getOrPut(neighbour) {
                        Crucible(neighbour, Int.MAX_VALUE)
                    }
                    if (nextCrucible.heat < existingCrucible.heat) {
                        pathMap[neighbour] = nextCrucible
                        queue.add(nextCrucible)
                    }
                }
            }
        }
        return -PART1_MIN_MOVES
    }

    private fun findValidNeighbours(
        currentNode: PathPosition,
        visitedNodes: MutableSet<PathPosition>,
        endPosition: Position,
        minMoves: Int,
        maxMoves: Int
    ): List<PathPosition> {
        val neighbours = mutableSetOf<PathPosition>()
        neighbours.add(
            PathPosition(
                Position(currentNode.position.x+1, currentNode.position.y),
                Direction.EAST,
                if (currentNode.direction == Direction.EAST)
                    currentNode.moves+1
                else
                    1
            )
        )
        neighbours.add(
            PathPosition(
                Position(currentNode.position.x-1, currentNode.position.y),
                Direction.WEST,
                if (currentNode.direction == Direction.WEST)
                    currentNode.moves+1
                else
                    1
            )
        )
        neighbours.add(
            PathPosition(
                Position(currentNode.position.x, currentNode.position.y+1),
                Direction.SOUTH,
                if (currentNode.direction == Direction.SOUTH)
                    currentNode.moves+1
                else
                    1
            )
        )
        neighbours.add(
            PathPosition(
                Position(currentNode.position.x, currentNode.position.y-1),
                Direction.NORTH,
                if (currentNode.direction == Direction.NORTH)
                    currentNode.moves+1
                else
                    1
            )
        )
        return neighbours
            .filter { it.position.x in 0..endPosition.x && it.position.y in 0..endPosition.y }
            .filter { it.moves in 1..maxMoves }
            .filter { it.direction == currentNode.direction || currentNode.moves in minMoves..maxMoves || currentNode == startNode }
            .filter { it !in visitedNodes }
            .filter { it.direction.opposite() != currentNode.direction }
    }
}

data class PathPosition (val position: Position, val direction: Direction, val moves: Int)

data class Crucible (val pathPosition: PathPosition, val heat: Int) : Comparable<Crucible> {
    override fun compareTo(other: Crucible): Int = heat.compareTo(other.heat)
}