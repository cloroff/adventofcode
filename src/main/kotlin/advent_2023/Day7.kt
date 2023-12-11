package advent_2023

import java.io.FileReader

class Day7 {
    private val cardList      = listOf("2", "3", "4", "5", "6", "7", "8", "9", "T", "J", "Q", "K", "A")
    private val jokerCardList = listOf("J", "2", "3", "4", "5", "6", "7", "8", "9", "T", "Q", "K", "A")

    fun process(args: Array<String>) {
        val camelCards = FileReader(args[0]).readLines()
        val hands = camelCards
            .map{ it.split(' ') }
            .map { Hand(it[0], calculateType(it[0]), it[1].toInt(), cardList) }
            .sorted()
        println("Part1: " + hands.mapIndexed{ index, it -> it.bid * (index + 1) }.sum())

        val jokerHands = camelCards
            .map{ it.split(' ') }
            .map { Hand(it[0], calculateTypeWithJokers(it[0]), it[1].toInt(), jokerCardList) }
            .sorted()
        println("Part2: " + jokerHands.mapIndexed{ index, it -> it.bid * (index + 1) }.sum())
    }

    private fun calculateType(allCards: String): Type {
        var resultType = Type.HIGH_CARD
        var pairs = 0
        var threeOfAKind = false
        val distinctCards = allCards.toSet()
        for (card in distinctCards) {
            when (allCards.count{ it == card }) {
                2 -> pairs++
                3 -> threeOfAKind = true
                4 -> resultType = Type.FOUR_OF_A_KIND
                5 -> resultType = Type.FIVE_OF_A_KIND
            }
        }
        when (pairs) {
            0 -> if (threeOfAKind) resultType = Type.THREE_OF_A_KIND
            1 -> if (threeOfAKind) resultType = Type.FULL_HOUSE else resultType = Type.ONE_PAIR
            2 -> resultType = Type.TWO_PAIR
        }
        return resultType
    }

    private fun calculateTypeWithJokers(allCards: String): Type {
        var resultType = Type.HIGH_CARD
        val jokers = allCards.count { it == 'J' }
        var pairs = 0
        var highestNumber = 0
        var threeOfAKind = false
        val distinctCards = allCards.filterNot { it == 'J' }.toSet()
        for (card in distinctCards) {
            val count = allCards.count{ it == card }
            highestNumber = highestNumber.coerceAtLeast(count)
            if (count == 2) pairs++
        }
        if (highestNumber == 1 && jokers == 1) pairs++
        if (highestNumber == 2 && jokers > 0) pairs--
        highestNumber += jokers
        when (highestNumber) {
            3 -> threeOfAKind = true
            4 -> resultType = Type.FOUR_OF_A_KIND
            5 -> resultType = Type.FIVE_OF_A_KIND
        }
        when (pairs) {
            0 -> if (threeOfAKind) resultType = Type.THREE_OF_A_KIND
            1 -> if (threeOfAKind) resultType = Type.FULL_HOUSE else resultType = Type.ONE_PAIR
            2 -> resultType = Type.TWO_PAIR
        }
        return resultType
    }

}

enum class Type {
    HIGH_CARD, ONE_PAIR, TWO_PAIR, THREE_OF_A_KIND, FULL_HOUSE, FOUR_OF_A_KIND, FIVE_OF_A_KIND
}

data class Hand (val cards: String, val type: Type, val bid: Int, val cardList: List<String>) : Comparable<Hand> {
    override fun compareTo(other: Hand): Int {
        var compareResult = 0
        if (type > other.type) compareResult = 1
        if (type < other.type) compareResult = -1
        if (type == other.type) compareResult = compareCardStrengths(cards, other.cards)
        return compareResult
    }

    private fun compareCardStrengths(cards: String, otherCards: String): Int {
        for (i in cards.indices) {
            if (cardList.indexOf(cards[i].toString()) > cardList.indexOf(otherCards[i].toString())) return 1
            if (cardList.indexOf(cards[i].toString()) < cardList.indexOf(otherCards[i].toString())) return -1
        }
        return 0
    }
}
