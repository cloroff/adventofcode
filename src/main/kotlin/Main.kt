import advent_2023.*

fun main(args: Array<String>) {
//    Day1().process(args)
//    Day2().process(args)
//    Day3().process(args)
//    Day4().process(args)
//    Day5().process(args)
//    Day6().process(args)
//    Day7().process(args)
//    Day8().process(args)
//    Day9().process(args)
//    Day10().process(args)
//    Day11().process(args)
//    Day12().process(args)
//    Day13().process(args)
//    Day14().process(args)
//    Day15().process(args)
//    Day16().process(args)
//    Day17().process(args)
//    Day18().process(args)
//    Day19().process(args)
    Day20().process(args)
//    Day21().process(args)
//    Day22().process(args)
}

enum class Direction {
    NORTH, EAST, SOUTH, WEST;

    fun opposite(): Direction {
        return when(this) {
            NORTH -> SOUTH
            EAST -> WEST
            SOUTH -> NORTH
            WEST -> EAST
        }
    }
}

data class Position (val x: Int, val y: Int)
