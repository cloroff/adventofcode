import java.io.FileReader

private const val STEP_REGEX_STRING = "(\\w+)([-=])(\\d*)"

class Day15 {
    fun process(args: Array<String>) {
        val initializationSequence = FileReader(args[0]).readLines()
        val stepList = initializationSequence[0].split(',')
        val hashList = stepList.map { hash(it) }
        println("Part1: ${hashList.sum()}")
        val stepOperations = mutableListOf<Step>()
        stepList.forEach { step ->
            val stepParts = STEP_REGEX_STRING.toRegex().matchEntire(step)?.groupValues
            stepParts.let {
                if (it != null && it.size > 3) {
                    val operation = when (it[2]) {
                        "-" -> Operation.REMOVE
                        else -> Operation.INSERT
                    }
                    val focalLength = if (it[3].isNotEmpty()) it[3].toInt() else null
                    stepOperations.add(Step(it[1], hash(it[1]), operation, focalLength))
                }
            }
        }
        val boxes = mutableListOf<MutableList<Lens>>()
        for (i in 0..255) {
            boxes.add(mutableListOf())
        }
        stepOperations.forEach { step ->
            val currentBox = boxes[step.box]
            when (step.operation) {
                Operation.REMOVE -> currentBox.removeIf { it.label == step.label }
                Operation.INSERT -> {
                    step.focalLength?.let {
                        val newLens = Lens(step.label, step.focalLength)
                        val lensIndex = currentBox.indexOfFirst { it.label == step.label }
                        if (lensIndex >= 0) {
                            currentBox.set(lensIndex, newLens)
                        }
                        else {
                            currentBox.add(newLens)
                        }
                    }
                }
            }

        }
        var focusingPower = 0
        boxes.forEachIndexed { boxIndex, box ->
            box.forEachIndexed { lensIndex, lens ->
                focusingPower += (1+boxIndex) * (1+lensIndex) * lens.focalLength
            }
        }
        println("Part2: $focusingPower")
    }

    private fun hash(step: String): Int {
        var currentValue = 0
        for (character in step.chars().iterator()) {
            currentValue += character
            currentValue *= 17
            currentValue %= 256
        }
        return currentValue
    }

    data class Step(val label: String, val box: Int, val operation: Operation, val focalLength: Int?)

    data class Lens(val label: String, val focalLength: Int)

    enum class Operation {
        REMOVE, INSERT
    }
}