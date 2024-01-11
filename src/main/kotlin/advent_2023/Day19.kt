package advent_2023

import java.io.FileReader

private const val NO_CONDITION = "noCondition"

class Day19 {
    private var acceptedCombinations = 0L
    fun process(args: Array<String>) {
        val workflowPartList = FileReader(args[0]).readLines()
        val workflowStringList = workflowPartList.filter { it.isNotEmpty() && !it.startsWith('{') }
        val partStringList = workflowPartList.filter { it.startsWith('{') }
        val workflowMap = workflowStringList
            .map {
                Pair(
                    it.substringBefore('{'),
                    it.substringAfter('{')
                        .dropLast(1)
                        .split(',')
                )
            }
            .map { workflowElement ->
                Pair(
                    workflowElement.first,
                    workflowElement.second
                        .map { it.split(':') }
                        .map {
                            if (it.first() == it.last()) Pair(NO_CONDITION, it.last()) else Pair(
                                it.first(),
                                it.last()
                            )
                        }
                        .map { Rule(it.first, it.second) })
            }.associate { it.first to Workflow(it.first, it.second) }
        val partList = partStringList
            .map { it.substring(1..<it.lastIndex) }
            .map { it.split(',') }
            .map { partParameter -> partParameter.map { it.substringAfter('=').toInt() } }
            .map { Part(it[0], it[1], it[2], it[3]) }
        processPart1(workflowMap, partList)
        processPart2(workflowMap)
    }

    private fun processPart1(
        workflowMap: Map<String, Workflow>,
        partList: List<Part>
    ) {
        val inWorkflow = workflowMap["in"]
        val acceptedList = mutableListOf<Part>()
        partList.forEach { part ->
            var currentWorkflow = inWorkflow
            while (currentWorkflow != null) {
                for (rule in currentWorkflow?.rules!!) {
                    val valueToCheck = when (rule.category) {
                        Category.x -> part.x
                        Category.m -> part.m
                        Category.a -> part.a
                        Category.s -> part.s
                        Category.NONE -> -1
                    }
                    val forwardPart = checkValue(valueToCheck, rule.operator, rule.compareValue)
                    if (forwardPart) {
                        when (rule.destination) {
                            "A" -> {
                                acceptedList.add(part)
                                currentWorkflow = null
                            }
                            "R" -> currentWorkflow = null
                            else -> currentWorkflow = workflowMap[rule.destination]
                        }
                        break
                    }
                }
            }
        }
        println("Part1: ${acceptedList.sumOf { it.x + it.m + it.a + it.s }}")
    }

    private fun checkValue(valueToCheck: Int, operator: Operator, compareValue: Int): Boolean {
        return when (operator) {
            Operator.GT -> valueToCheck > compareValue
            Operator.LT -> valueToCheck < compareValue
            Operator.NONE -> true
        }

    }

    private fun processPart2(workflowMap: Map<String, Workflow>) {
        val inWorkflow = workflowMap["in"]
        val baseRange = (1..4000).toList()
        val genericPart = GenericPart(baseRange, baseRange, baseRange, baseRange)
        processWorkflow(genericPart, inWorkflow, workflowMap)
        println("Part2: $acceptedCombinations")
    }

    private fun processWorkflow(
        genericPart: GenericPart,
        currentWorkflow: Workflow?,
        workflowMap: Map<String, Workflow>
    ) {
        var part = genericPart.copy()
        currentWorkflow?.rules?.forEach { rule ->
            ruleApplies(rule, part, workflowMap)
            part = ruleAppliesNot(rule, part)
        }
    }

    private fun ruleApplies(
        rule: Rule,
        genericPart: GenericPart,
        workflowMap: Map<String, Workflow>
    ) {
        val part = genericPart.copy()
        when (rule.category) {
            Category.x -> part.x = part.x.filter { processRule(rule, it) }
            Category.m -> part.m = part.m.filter { processRule(rule, it) }
            Category.a -> part.a = part.a.filter { processRule(rule, it) }
            Category.s -> part.s = part.s.filter { processRule(rule, it) }
            Category.NONE -> 0..0
        }
        when (rule.destination) {
            "A"  -> acceptedCombinations +=
                part.x.size.toLong() * part.m.size.toLong() * part.a.size.toLong() * part.s.size.toLong()
            "R"  -> return
            else -> processWorkflow(part, workflowMap[rule.destination], workflowMap)
        }
    }

    private fun ruleAppliesNot(rule: Rule, genericPart: GenericPart): GenericPart {
        val part = genericPart.copy()
        when (rule.category) {
            Category.x -> part.x = part.x.filter { processInverseRule(rule, it) }
            Category.m -> part.m = part.m.filter { processInverseRule(rule, it) }
            Category.a -> part.a = part.a.filter { processInverseRule(rule, it) }
            Category.s -> part.s = part.s.filter { processInverseRule(rule, it) }
            Category.NONE -> 0..0
        }
        return part
    }

    private fun processRule(rule: Rule, it: Int) = when (rule.operator) {
        Operator.LT -> it < rule.compareValue
        Operator.GT -> it > rule.compareValue
        Operator.NONE -> true
    }

    private fun processInverseRule(rule: Rule, it: Int) = when (rule.operator) {
        Operator.LT -> it >= rule.compareValue
        Operator.GT -> it <= rule.compareValue
        Operator.NONE -> false
    }
}

data class Part (val x: Int, val m: Int, val a: Int, val s: Int)

data class GenericPart (var x: List<Int>, var m: List<Int>, var a: List<Int>, var s: List<Int>)
data class Workflow (val name: String, val rules: List<Rule>)

data class Rule (val category: Category, val operator: Operator, val compareValue: Int, val destination: String) {
    companion object {
        operator fun invoke (condition: String, destination: String): Rule {
            val operator = if (condition.contains('<'))
                Operator.LT
            else if (condition.contains('>'))
                Operator.GT
            else
                Operator.NONE
            val operandList = condition.split('<', '>')

            val category = when (operandList.first()) {
                "x" -> Category.x
                "m" -> Category.m
                "a" -> Category.a
                "s" -> Category.s
                else -> Category.NONE
            }
            val compareValue = if (operator != Operator.NONE)
                operandList.last().toInt()
            else
                -1
            return Rule(category, operator, compareValue, destination)
        }
    }
}

enum class Category {
    x, m, a, s, NONE
}

enum class Operator {
    LT, GT, NONE
}