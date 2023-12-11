package advent_2023

import java.io.FileReader

class Day6 {
    fun process(args: Array<String>) {
        val races = FileReader(args[0]).readLines()
        var time = races[0]
            .split(Regex("[' ']+"))
            .filterIndexed{ index, _ -> index >  0 }
            .map{ it.toLong() }
        var distance = races[1]
            .split(Regex("[' ']+"))
            .filterIndexed{ index, _ -> index >  0 }
            .map{ it.toLong() }

        var result = calculateRaceResults(time, distance)
        println("Part1: $result")

        time =races[0]
            .split(':')
            .filterIndexed{ index, _ -> index >  0 }
            .map{ it.replace(" ", "").toLong() }
        distance =races[1]
            .split(':')
            .filterIndexed{ index, _ -> index >  0 }
            .map{ it.replace(" ", "").toLong() }

        result = calculateRaceResults(time, distance)
        println("Part2: $result")
    }

    private fun calculateRaceResults(time: List<Long>, distance: List<Long>): Int {
        var result = 1
        for (i in time.indices) {
            var beat = 0
            for (ms in 1..time[i]) {
                if ((time[i] - ms) * ms > distance[i]) {
                    beat++
                }
            }
            result *= beat
        }
        return result
    }
}