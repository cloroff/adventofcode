package advent_2023

import java.io.FileReader

class Day8 {
    final val START = "AAA"
    final val TARGET = "ZZZ"
    fun process(args: Array<String>) {
        var instructionList = listOf<String>()
        val networkList = FileReader(args[0]).readLines()
        val networkMap = mutableMapOf<String, NetworkNode>()
        for (line in networkList) {
            if (line == networkList.first()) {
                instructionList = line.chunked(1)
            }
            else {
                if (line.isNotEmpty()) {
                    val nodeList = line.split('=', '(', ',', ')').map { it.trim() }.filter { it.isNotEmpty() }
                    networkMap[nodeList[0]] = NetworkNode(nodeList[0], nodeList[1], nodeList[2])
                }
            }
        }

        val startNode = networkMap[START]
        val targetNode = networkMap[TARGET]
        var currentNode = startNode
        var stepCounter = 0L
        var instructionIterator: ListIterator<String>? = null
        while (currentNode != targetNode) {
            stepCounter++
            if (instructionIterator == null || !instructionIterator.hasNext()) {
                instructionIterator = instructionList.listIterator()
            }
            val instruction = instructionIterator.next()
            if (currentNode != null) {
                currentNode = networkMap[currentNode.get(instruction)]
            }
        }
        println("Part1: $stepCounter")


        val startNodes = networkMap.filter { it.key.endsWith("A") }
        println("Start nodes: ${startNodes.size}")
        val targetNodes = networkMap.filter { it.key.endsWith("Z") }
        println("Target nodes ${targetNodes.size}")
        var currentNodes = startNodes
        stepCounter = 0
        while (currentNodes != targetNodes) {
            stepCounter++
//            if (currentNodes.filter { targetNodes.contains(it.key) }.size > 2) {
//                println(currentNodes.filter { targetNodes.contains(it.key) }.size)
//            }
//            if (stepCounter % 1000000 == 0L) {
//                println(stepCounter)
//            }
            if (instructionIterator == null || !instructionIterator.hasNext()) {
                instructionIterator = instructionList.listIterator()
            }
            val instruction = instructionIterator.next()
            currentNodes = networkMap.filter{ nodeEntry -> nodeEntry.key in currentNodes.map{ it.value.get(instruction) } }
        }
        println("Part2: $stepCounter")
    }
}

data class NetworkNode(val name: String, val left: String, val right: String) {
    fun get(instruction: String): String {
        return if (instruction == "L") left else right
    }
}