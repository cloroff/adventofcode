package advent_2023

import java.io.FileReader

class Day5 {
    fun process(args: Array<String>) {
        val almanac = FileReader(args[0]).readLines()
        val seeds = almanac.first().substring(7).split(' ').map{ it.toLong() }.map { it to it }.toMap().toMutableMap()
        println(seeds)

        calculateLocation(almanac, seeds)
        for (seed in seeds) {
            println("${seed.key} : ${seed.value}")
        }
        println("Part1: " + seeds.minOf{ it.value })

        val seedRanges = almanac.first().substring(7).split(' ').map{ it.toLong() }
        val testMap = mutableMapOf(Pair(0L, 0L))
        calculateSeed(almanac.reversed(), testMap)
        println(testMap)
        for (seed in seedRanges.chunked(2)) {
            if (testMap.get(0L) in seed[0] .. seed[0]+seed[1]-1){
                println("seed:" + testMap)
            }

        }

//        val newSeedMap = mutableMapOf<Long, Long>()
//        newSeedMap.put(3527932771, 3527932771)
//        calculateLocation(almanac, newSeedMap)
//        val location = newSeedMap.minOf { it.value }
//        println(location)

//        println("Part2: " + minNewLocation)


        var minNewLocation = Long.MAX_VALUE
        for (seed in seedRanges.chunked(2)) {
            for (i in 0..< seed[1]) {
                val newSeedMap = mutableMapOf<Long, Long>()
                newSeedMap[seed[0]+i] = seed[0]+i
                calculateLocation(almanac, newSeedMap)
                val location = newSeedMap.minOf { it.value }
                minNewLocation = Math.min(minNewLocation, location)
            }
            println(minNewLocation)
        }
        println("Part2: " + minNewLocation)

    }

    private fun calculateLocation(almanac: List<String>, seeds: MutableMap<Long, Long>) {
        val mapTitles = mutableListOf<String>()
        var mappedSeeds = mutableListOf<MutableMap.MutableEntry<Long, Long>>()
        for (line in almanac) {

            if (line != almanac.first() && line.isNotEmpty()) {
                if (line.contains(":")) {
                    mappedSeeds = mutableListOf<MutableMap.MutableEntry<Long, Long>>()
                    mapTitles.add(line)
                } else if (line[0].isDigit()) {
                    val mapping = line.split(' ').map { it.toLong() }
                    for (seed in seeds) {
                        if (!mappedSeeds.contains(seed) && seed.value in mapping[1]..mapping[1] + mapping[2] - 1) {
                            seed.setValue(seed.value - mapping[1] + mapping[0])
                            mappedSeeds.add(seed)
                        }
                    }
                }
            }
        }
    }


    private fun calculateSeed(almanac: List<String>, locations: MutableMap<Long, Long>) {
        val mapTitles = mutableListOf<String>()
        var mappedLocations = mutableListOf<MutableMap.MutableEntry<Long, Long>>()

        for (line in almanac.reversed()) {
            if (line != almanac.first() && line.isNotEmpty()) {
                if (line.contains(":")) {
                    mappedLocations = mutableListOf<MutableMap.MutableEntry<Long, Long>>()
                    mapTitles.add(line)
                } else if (line[0].isDigit()) {
                    val mapping = line.split(' ').map { it.toLong() }
                    for (location in locations) {
                        if (!mappedLocations.contains(location) && location.value in mapping[0]..mapping[0] + mapping[2] - 1) {
                            location.setValue(location.value - mapping[0] + mapping[1])
                            mappedLocations.add(location)
                        }
                    }
                }
            }
        }
    }
}