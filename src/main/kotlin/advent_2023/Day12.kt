package advent_2023

import java.io.FileReader
import java.math.BigInteger
import kotlin.math.pow

class Day12 {
    fun process(args: Array<String>) {
        val conditionRecords = FileReader(args[0]).readLines()
        val damageLists = conditionRecords
            .map { record ->
                record
                    .split(" ")
                    .last()
                    .split(",")
                    .map { it.toInt() }
            }
        val conditionsList = conditionRecords.map { it.split(" ").first() }
        var variants = 0
        for (i in damageLists.indices) {
            variants += mapDamageGroups(conditionsList[i], damageLists[i])
        }
        println("Part1: $variants")

        val part2ConditionsList = conditionsList.map { "$it?$it?$it?$it?$it" }
        val part2DamageLists = damageLists.map { it.toMutableList() }
        for (i in 1..4) {
            part2DamageLists.forEachIndexed { index, it -> it.addAll(damageLists[index]) }
        }
        variants = 0
        for (i in part2DamageLists.indices) {
            variants += mapDamageGroups(part2ConditionsList[i], part2DamageLists[i])
            println("$i: $variants")
        }
        println("Part2: $variants")
    }

    private fun mapDamageGroups(conditions: String, damageList: List<Int>): Int {
        val conditionsArray = conditions.toCharArray()
        val markedDamaged = conditions.count { it == '#' }
        var variants = 0
        val regex = createRegex(damageList, conditions.length)
        val wildcardIndexes = mutableListOf<Int>()
        val damagedSprings = damageList.sum()
        conditions.forEachIndexed { index, c -> if (c == '?') wildcardIndexes.add(index) }

        for (i in 0..<2.toDouble().pow(wildcardIndexes.size.toDouble()).toInt()) {
            val binaryString = Integer.toBinaryString(i).padStart(wildcardIndexes.size, '0')
            if (binaryString.count { it == '1' } + markedDamaged == damagedSprings) {
                wildcardIndexes.mapIndexed { index, number ->
                    if (Integer.toBinaryString(i).padStart(wildcardIndexes.size, '0')[index] == '1')
                        conditionsArray[number] = '#'
                    else
                        conditionsArray[number] = '.'
                }
                if (regex.matches(String(conditionsArray)))
                    variants++
            }
        }
        return variants
    }

    private fun createRegex(damageList: List<Int>, length: Int): Regex {
        var regex = "[.]*"
        damageList.forEachIndexed { index, damage ->
            regex += "[#]{$damage}"
            if (index != damageList.size-1) {
                regex += "[.]+"
            }
        }
        regex += "[.]*"
        return regex.toRegex()
    }
}