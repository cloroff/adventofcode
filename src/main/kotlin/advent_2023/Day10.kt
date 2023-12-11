package advent_2023

import java.io.FileReader
import kotlin.math.E

val NORTH_ELEMENTS = listOf("7", "|", "F", "S")
val EAST_ELEMENTS = listOf("J", "-", "7", "S")
val SOUTH_ELEMENTS = listOf("L", "|", "J", "S")
val WEST_ELEMENTS = listOf("F", "-", "L", "S")
class Day10 {
    fun process(args: Array<String>) {
        val pipeSketch = FileReader(args[0]).readLines()
        val pipeMap = mutableMapOf<Pair<Int, Int>, PipeElement>()
        pipeSketch.forEachIndexed { indexY, pipeLine -> pipeLine.forEachIndexed { indexX, pipe -> pipeMap.put(Pair(indexX, indexY), PipeElement(indexX, indexY, pipe.toString())) } }
        val startEntry = pipeMap.filter{it.value.element == "S"}.entries.first()
        val startElement = startEntry.value
        var connecting = true
        var element = startElement
        var distance = 0
        while (connecting) {
            connecting = connectElement(element, pipeMap)
            distance++
            element = element.nextElement
            if (element == startElement) {
                connecting = false
            }
        }
        println("$distance ${distance/2}")
    }

    private fun connectElement(element: PipeElement, map: Map<Pair<Int, Int>, PipeElement>): Boolean {
        var connected = false
        val neighborList = mutableListOf<PipeElement>()
        val northElement = checkNorthLocation(Pair(element.xPos, element.yPos-1), map)
        if (northElement != null && element.element in SOUTH_ELEMENTS) {
            neighborList.add(northElement)
        }
        val eastElement = checkEastLocation(Pair(element.xPos+1, element.yPos), map)
        if (eastElement != null && element.element in WEST_ELEMENTS) {
            neighborList.add(eastElement)
        }
        val southElement = checkSouthLocation(Pair(element.xPos, element.yPos+1), map)
        if (southElement != null && element.element in NORTH_ELEMENTS) {
            neighborList.add(southElement)
        }
        val westElement = checkWestLocation(Pair(element.xPos-1, element.yPos), map)
        if (westElement != null && element.element in EAST_ELEMENTS) {
            neighborList.add(westElement)
        }
        if (element.nextElement == element && neighborList.first().nextElement != element) {
            element.nextElement = neighborList.first()
            element.nextElement.previousElement = element
            connected = true
        }
        else {
            element.nextElement = neighborList.last()
            element.nextElement.previousElement = element
            connected = true
        }
        return connected
    }

    private fun checkWestLocation(location: Pair<Int, Int>, map: Map<Pair<Int, Int>, PipeElement>): PipeElement? {
        var result: PipeElement? = null
        val pipe = map.getOrDefault(location, PipeElement(-1, -1, "."))
        if (WEST_ELEMENTS.contains(pipe.element)) {
            result = pipe
        }
        return result
    }

    private fun checkSouthLocation(location: Pair<Int, Int>, map: Map<Pair<Int, Int>, PipeElement>): PipeElement? {
        var result: PipeElement? = null
        val pipe = map.getOrDefault(location, PipeElement(-1, -1, "."))
        if (SOUTH_ELEMENTS.contains(pipe.element)) {
            result = pipe
        }
        return result
    }

    private fun checkEastLocation(location: Pair<Int, Int>, map: Map<Pair<Int, Int>, PipeElement>): PipeElement? {
        var result: PipeElement? = null
        val pipe = map.getOrDefault(location, PipeElement(-1, -1, "."))
        if (EAST_ELEMENTS.contains(pipe.element)) {
            result = pipe
        }
        return result
    }

    private fun checkNorthLocation(location: Pair<Int, Int>, map: Map<Pair<Int, Int>, PipeElement>): PipeElement? {
        var result: PipeElement? = null
        val pipe = map.getOrDefault(location, PipeElement(-1, -1, "."))
        if (NORTH_ELEMENTS.contains(pipe.element)) {
            result = pipe
        }
        return result
    }
}

data class PipeElement (val xPos: Int, val yPos: Int, val element: String){
    var nextElement = this
    var previousElement = this
}
