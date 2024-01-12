package advent_2023

import java.io.FileReader

class Day20 {
    private var lowCounter = 0L
    private var highCounter = 0L
    fun process(args: Array<String>) {
        val moduleStringList = FileReader(args[0]).readLines()
        var moduleMap = init(moduleStringList)

        val pulseQueue = ArrayDeque <Triple<String, Pulse, String>>()
        processPart1(pulseQueue, moduleMap)

        moduleMap = init(moduleStringList)
        processPart2(pulseQueue, moduleMap)
    }

    private fun init(moduleStringList: List<String>): Map<String, Module> {
        val moduleMap = moduleStringList
            .map { it.split(" -> ") }
            .map { Module(it[0], it[1].split(", ")) }
            .map {
                when (it.name.first()) {
                    '%' -> FlipFlop(name = it.name.drop(1), destinationModules = it.destinationModules)
                    '&' -> Conjunction(name = it.name.drop(1), destinationModules = it.destinationModules)
                    else -> Broadcaster(it.name, it.destinationModules)
                }
            }.associateBy { it.name }
        val conjunctionList: List<Conjunction> =
            moduleMap.filter { it.value is Conjunction }.values.toList() as List<Conjunction>

        conjunctionList.forEach { conjunction ->
            val inputSet = moduleMap.filter { mapEntry ->
                mapEntry.value.destinationModules.any { it == conjunction.name }
            }.keys
            inputSet.forEach {
                conjunction.inputModules.add(it)
                conjunction.memory[it] = Pulse.LOW
            }
        }
        return moduleMap
    }

    private fun processPart1(
        pulseQueue: ArrayDeque<Triple<String, Pulse, String>>,
        moduleMap: Map<String, Module>
    ) {
        for (i in 1..1000) {
            pulseQueue.addLast(Triple("broadcaster", Pulse.LOW, "button")) // push the button
            while (pulseQueue.isNotEmpty()) {
                val pulseEvent = pulseQueue.removeFirst()
                val module = moduleMap[pulseEvent.first]
                module?.processPulse(pulseEvent.second, pulseQueue, pulseEvent.third)
                when (pulseEvent.second) {
                    Pulse.LOW -> lowCounter++
                    Pulse.HIGH -> highCounter++
                }
            }
        }
        println("$lowCounter:$highCounter")
        println("Part1: ${lowCounter * highCounter}")
    }
    private fun processPart2(pulseQueue: ArrayDeque<Triple<String, Pulse, String>>, moduleMap: Map<String, Module>) {
        val kgvList = mutableListOf<Long>()
        for (i in 1..4000) {
            pulseQueue.addLast(Triple("broadcaster", Pulse.LOW, "button")) // push the button
            while (pulseQueue.isNotEmpty()) {
                val pulseEvent = pulseQueue.removeFirst()
                if (pulseEvent.first == "rx" && pulseEvent.second == Pulse.LOW) println("rx:${i}")
                val module = moduleMap[pulseEvent.first]
                module?.processPulse(pulseEvent.second, pulseQueue, pulseEvent.third)
                if (pulseEvent.first == "vr" && pulseEvent.second == Pulse.HIGH && module is Conjunction) {
                    println("$i:${module.memory}")
                    kgvList.add(i.toLong())
                }
            }
        }
        val kgv = kgvList.reduce { accumulator, element -> accumulator * element }
        println("Part2: $kgv")

    }
}


enum class Pulse { LOW, HIGH }
open class Module (open val name: String, open val destinationModules: List<String>) {
    open fun processPulse(pulse: Pulse, pulseQueue: ArrayDeque<Triple<String, Pulse, String>>, origin: String) {
        forwardDestinations(pulse, pulseQueue)
    }

    fun forwardDestinations(
        pulse: Pulse,
        pulseQueue: ArrayDeque<Triple<String, Pulse, String>>
    ) {
        destinationModules.forEach { pulseQueue.addLast(Triple(it, pulse, name)) }
    }
}
data class FlipFlop (override val name: String, var status: Boolean = false, override val destinationModules: List<String>) : Module(name, destinationModules) {
    override fun processPulse(pulse: Pulse, pulseQueue: ArrayDeque<Triple<String, Pulse, String>>, origin: String) {
        if (pulse == Pulse.LOW) {
            if (status) {
                status = false
                forwardDestinations(Pulse.LOW, pulseQueue)
            } else {
                status = true
                forwardDestinations(Pulse.HIGH, pulseQueue)
            }
        }
    }
}

data class Conjunction (override val name: String, val memory: MutableMap<String, Pulse> = mutableMapOf(), val inputModules: MutableList<String> = mutableListOf(), override val destinationModules: List<String>) : Module(name, destinationModules) {
    override fun processPulse(pulse: Pulse, pulseQueue: ArrayDeque<Triple<String, Pulse, String>>, origin: String) {
        memory[origin] = pulse
        val forwardPulse = if (memory.all { it.value == Pulse.HIGH })
            Pulse.LOW
        else
            Pulse.HIGH
        forwardDestinations(forwardPulse, pulseQueue)
    }
}

data class Broadcaster (override val name: String, override val destinationModules: List<String>) : Module(name, destinationModules)