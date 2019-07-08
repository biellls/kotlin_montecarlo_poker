import kotlin.random.Random

// Constants for convenience
val jack = Rank.LetterRank(RankLetter.J)
val queen = Rank.LetterRank(RankLetter.Q)
val king = Rank.LetterRank(RankLetter.K)
val ace = Rank.LetterRank(RankLetter.A)
val two = Rank.NumberRank(2)
val three = Rank.NumberRank(3)
val four = Rank.NumberRank(4)
val five = Rank.NumberRank(5)
val six = Rank.NumberRank(6)
val seven = Rank.NumberRank(7)
val eight = Rank.NumberRank(8)
val nine = Rank.NumberRank(9)
val ten = Rank.NumberRank(10)
val hearts = Suit.HEARTS
val spades = Suit.SPADES
val diamonds = Suit.DIAMONDS
val clubs = Suit.CLUBS

enum class Color { RED, BLACK }

enum class Suit(val color: Color) {
    HEARTS(Color.RED), DIAMONDS(Color.RED), SPADES(Color.BLACK), CLUBS(Color.BLACK)
}

enum class RankLetter(private val value: Char) {
    A('A'), J('J'), Q('Q'), K('K');

    override fun toString() = value.toString()
}

sealed class Rank {
    class NumberRank(val number: Int): Rank()
    class LetterRank(val letter: RankLetter): Rank()

    infix fun of(suit: Suit): Card = Card(suit, this)
    override fun equals(other: Any?): Boolean {
        return when (this) {
            is NumberRank -> if (other is NumberRank) this.number == other.number else false
            is LetterRank -> if (other is LetterRank) this.letter == other.letter else false
        }
    }

    override fun hashCode(): Int {
        return when (this) {
            is NumberRank -> this.number.hashCode()
            is LetterRank -> this.letter.hashCode()
        }
    }
}

data class Card(val suit: Suit, val rank: Rank) {
    override fun toString(): String {
        val r = when (rank) {
            is Rank.NumberRank -> rank.number.toString()
            is Rank.LetterRank -> rank.letter.toString()
        }
        return "$r of ${suit.toString().toLowerCase()}"
    }
}

typealias Cards = List<Card>

inline class Deck(private val cards: Cards) {
    val size get() = cards.size

    fun deal(numCards: Int): Pair<Cards, Deck> {
        return Pair(this.cards.take(numCards), Deck(this.cards.drop(numCards)))
    }

    fun removeCards(otherCards: Cards): Deck = Deck(cards.filter { it !in otherCards })

//    fun shuffled(): Deck = Deck(cards.shuffled(Random(seed = 10)))
    fun shuffled(): Deck = Deck(cards.shuffled())
}

fun makeDeck(): Deck {
    val cards = sequence {
        for (suit in Suit.values()) {
            for (rank in 2..10) yield(Card(suit, Rank.NumberRank(rank)))
            for (rank in RankLetter.values()) yield(Card(suit, Rank.LetterRank(rank)))
        }
    }

    return Deck(cards.toList())
}
