package advent_2023

import java.io.FileReader

class Day22 {
    fun process(args: Array<String>) {
        val brickSnapshot = FileReader(args[0]).readLines()
        val brickList = brickSnapshot
            .map { brickDescription ->
                brickDescription.split("~")
                    .map { it.split(",") }
                    .map { Coordinate(it[0].toInt(), it[1].toInt(), it[2].toInt()) }
            }
            .map { Brick(it[0], it[1]) }
        val maxCoordinate = Coordinate(
            brickList.maxOf { it.startCoordinate.x.coerceAtLeast(it.endCoordinate.x) },
            brickList.maxOf { it.startCoordinate.y.coerceAtLeast(it.endCoordinate.y) },
            brickList.maxOf { it.startCoordinate.z.coerceAtLeast(it.endCoordinate.z) })
        println("Max coordinates: $maxCoordinate")

        val baseGrid = mutableMapOf<Position, Content>()
        val ground = Content(0, null)
        for (x in 0..maxCoordinate.x) {
            for (y in 0..maxCoordinate.y) {
                baseGrid[Position(x, y)] = ground
            }
        }
        bricksFallingDown(maxCoordinate, brickList, baseGrid)

        processPart1(brickList)

        processPart2(brickList)
    }

    private fun processPart1(brickList: List<Brick>) {
        val disintegrateableBricks =
            brickList.count { brick -> 
                brick.bricksOnTop.size == 0 || brick.bricksOnTop.all { it.bricksBelow.size > 1 } }
        println("Part1: $disintegrateableBricks")
    }

    private fun processPart2(brickList: List<Brick>) {
        var fallingBricks = 0
        brickList.forEach { brick ->
            val fallingTree = brick.getBricksOnTopTree()
            val stableBricks = fallingTree.filter { currentBrick ->
                currentBrick != brick && currentBrick.bricksBelow.any { it !in fallingTree } }
            val stableTree = mutableSetOf<Brick>()
            stableBricks.forEach { stableTree.addAll(it.getBricksOnTopTree()) }
            fallingTree.removeAll(stableTree)
            fallingBricks += fallingTree.size - 1
        }
        println("Part2: $fallingBricks")
    }

    private fun bricksFallingDown(
        maxCoordinate: Coordinate,
        brickList: List<Brick>,
        baseGrid: MutableMap<Position, Content>
    ) {
        for (z in 1..maxCoordinate.z) {
            val bricksOnZ = brickList.filter { !it.isLanded && z in it.startCoordinate.z..it.endCoordinate.z }
            bricksOnZ.forEach { brick ->
                val baseGridCoordinates = baseGrid
                    .filter {
                        it.key.x in brick.startCoordinate.x..brick.endCoordinate.x &&
                                it.key.y in brick.startCoordinate.y..brick.endCoordinate.y
                    }
                val zBaseMax = baseGridCoordinates.maxOf { it.value.z }
                val highestBaseGridCoordinates = baseGridCoordinates.filter { it.value.z == zBaseMax }
                val zDifference = brick.startCoordinate.z.coerceAtMost(brick.endCoordinate.z) - zBaseMax - 1
                brick.startCoordinate.z -= zDifference
                brick.endCoordinate.z -= zDifference
                brick.isLanded = true
                highestBaseGridCoordinates.forEach { (_, content) ->
                    if (content.z == zBaseMax) {
                        content.brick?.let {
                            it.bricksOnTop.add(brick)
                            brick.bricksBelow.add(it)
                        }
                    }
                }
                baseGridCoordinates.forEach { (position, _) ->
                    baseGrid[position] = Content(brick.startCoordinate.z.coerceAtLeast(brick.endCoordinate.z), brick)
                }
            }
        }
    }
}

data class Brick (
    val startCoordinate: Coordinate, 
    val endCoordinate: Coordinate, 
    var isLanded: Boolean = false, 
    val bricksOnTop: MutableSet<Brick> = mutableSetOf(), 
    val bricksBelow: MutableSet<Brick> = mutableSetOf()
) {
    override fun toString(): String {
        return "Brick(${startCoordinate}, ${endCoordinate}, isLanded=$isLanded, " +
                "bricksOnTop=${bricksOnTop.map{ "" + it.startCoordinate + ", " + it.endCoordinate }}, " +
                "bricksBelow=${bricksBelow.map{ "" + it.startCoordinate + ", " + it.endCoordinate }}"
    }

    override fun hashCode(): Int {
        return startCoordinate.x * 10000 + startCoordinate.y * 1000 + startCoordinate.z
    }

    fun getBricksOnTopTree(): MutableSet<Brick> {
        val tree = mutableSetOf(this)
        tree.addAll(bricksOnTop)
        bricksOnTop.forEach { tree.addAll(it.getBricksOnTopTree()) }
        return tree
    }
}

data class Coordinate (val x: Int, val y: Int, var z: Int)

data class Position (val x: Int, val y: Int)

data class Content (var z: Int, var brick: Brick?)

