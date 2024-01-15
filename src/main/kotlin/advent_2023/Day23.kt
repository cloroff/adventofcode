package advent_2023

import Direction
import Position
import java.io.FileReader
import java.util.PriorityQueue

class Day23 {
    private var movesMax = 0
    private var part1 = true

    fun process(args: Array<String>) {
        val areaMap = FileReader(args[0]).readLines()
        val hikingMap = areaMap.map { it.toCharArray() }
        val startIndex = hikingMap[0].indexOf('.')
        val startPosition = Position(startIndex, 0)
        val endIndex = hikingMap[hikingMap.lastIndex].indexOf('.')
        val endPosition = Position(endIndex, hikingMap.lastIndex)
        val visitedNodes = mutableSetOf(startPosition)
        processMove(PathPosition(startPosition, Direction.SOUTH, 0), visitedNodes, endPosition, hikingMap)
        println("Part1: $movesMax")

        part1 = false
        movesMax = 0
        val newVisitedNodes = arrayListOf<Position>()
        findMaximumPath(PathPosition(startPosition, Direction.SOUTH, 0), endPosition, newVisitedNodes, hikingMap)
        println("Part2: $movesMax")
    }

    private fun findMaximumPath(
        startPathPosition: PathPosition,
        endPosition: Position,
        visitedNodes: ArrayList<Position>,
        hikingMap: List<CharArray>
    ) {
        val queue = PriorityQueue<Tile>()
        queue.add(Tile(startPathPosition, visitedNodes))

        while (queue.isNotEmpty()) {
            val currentQueueItem = queue.poll()
            var currentPathPosition = currentQueueItem.pathPosition
            val currentVisitedNodes = currentQueueItem.visitedNodes.toMutableSet()
            var currentPosition = currentPathPosition.position
            while (currentPosition != endPosition) {
                if (currentPosition !in currentVisitedNodes) {
                    currentVisitedNodes.add(currentPosition)
                    val nextPosition = with(currentPathPosition) {
                        when(direction) {
                            Direction.SOUTH -> Position(position.x, position.y+1)
                            Direction.WEST -> Position(position.x-1, position.y)
                            Direction.NORTH -> Position(position.x, position.y-1)
                            Direction.EAST -> Position(position.x+1, position.y)
                        }
                    }
                    if (nextPosition == endPosition) {
                        currentPosition = nextPosition
                    }
                    else {
                        val validDirectionList = findValidDirections(nextPosition, currentPathPosition.direction, currentVisitedNodes, hikingMap)
                        validDirectionList.forEachIndexed { index, direction ->
                            val nextPathPosition = PathPosition(nextPosition, direction, currentPathPosition.moves+1)
                            if (index == 0) {
                                currentPathPosition = nextPathPosition
                                currentPosition = nextPosition
                            }
                            else {
                                queue.add(Tile(nextPathPosition, currentVisitedNodes.toList()))
                            }
                        }
                    }
                }
                else {
                    break
                }
            }
            if (currentPosition == endPosition) {
                movesMax = movesMax.coerceAtLeast(currentVisitedNodes.size)
            }
        }
    }

    private fun processMove(
        currentPathPosition: PathPosition,
        visitedNodes: MutableSet<Position>,
        endPosition: Position,
        hikingMap: List<CharArray>
    ) {
            val nextPosition = with(currentPathPosition) {
                when(direction) {
                    Direction.SOUTH -> Position(position.x, position.y+1)
                    Direction.WEST -> Position(position.x-1, position.y)
                    Direction.NORTH -> Position(position.x, position.y-1)
                    Direction.EAST -> Position(position.x+1, position.y)
                }
            }
        visitedNodes.add(nextPosition)
        if (nextPosition == endPosition) {
            println(visitedNodes.size-1)
            movesMax = movesMax.coerceAtLeast(visitedNodes.size-1)
        }
        else {
            val validDirectionList = findValidDirections(nextPosition, currentPathPosition.direction, visitedNodes, hikingMap)
            validDirectionList.forEach {
                val nextPathPosition = PathPosition(nextPosition, it, currentPathPosition.moves+1)
                processMove(nextPathPosition, visitedNodes, endPosition, hikingMap)
            }
        }
        visitedNodes.remove(nextPosition)
    }

    private fun findValidDirections(
        currentPosition: Position,
        currentDirection: Direction,
        visitedNodes: Set<Position>,
        hikingMap: List<CharArray>,
    ): List<Direction> {
        val validDirections = mutableSetOf<Direction>()
        val currentTile = hikingMap[currentPosition.y][currentPosition.x]
        if (part1) {
            when (currentTile) {
                'v' -> validDirections.add(Direction.SOUTH)
                '<' -> validDirections.add(Direction.WEST)
                '^' -> validDirections.add(Direction.NORTH)
                '>' -> validDirections.add(Direction.EAST)
                else -> {
                    processNormalPath(currentPosition, visitedNodes, hikingMap, validDirections)
                }
            }
        }
        else {
            processNormalPath(currentPosition, visitedNodes, hikingMap, validDirections)
        }
        return validDirections.filter { it != currentDirection.opposite() }
    }

    private fun processNormalPath(
        currentPosition: Position,
        visitedNodes: Set<Position>,
        hikingMap: List<CharArray>,
        validDirections: MutableSet<Direction>
    ) {
        val neighbours = mutableSetOf<Pair<Position, Direction>>()
        neighbours.add(Pair(Position(currentPosition.x, currentPosition.y + 1), Direction.SOUTH))
        neighbours.add(Pair(Position(currentPosition.x - 1, currentPosition.y), Direction.WEST))
        neighbours.add(Pair(Position(currentPosition.x, currentPosition.y - 1), Direction.NORTH))
        neighbours.add(Pair(Position(currentPosition.x + 1, currentPosition.y), Direction.EAST))
        val validNeighbours = neighbours
            .filter { it.first !in visitedNodes }
            .filter { it.first.y in hikingMap.indices && it.first.x in hikingMap.first().indices }
            .filter { hikingMap[it.first.y][it.first.x] != '#' }
        validNeighbours.forEach { validDirections.add(it.second) }
    }
}

data class Tile (val pathPosition: PathPosition, val visitedNodes: List<Position>) : Comparable<Tile> {
    override fun compareTo(other: Tile): Int = other.pathPosition.moves.compareTo(pathPosition.moves)
}
