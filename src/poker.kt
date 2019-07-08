import com.marcinmoskala.math.combinations

val LOW_ACE = Rank.NumberRank(1) of hearts  // Could be of any suit, we just need it as a special case constant

val Card.pokerRankValue: Int
get () {
    return when (this.rank) {
        is Rank.NumberRank -> this.rank.number
        is Rank.LetterRank -> when (this.rank.letter) {
            RankLetter.J -> 11
            RankLetter.Q -> 12
            RankLetter.K -> 13
            RankLetter.A -> 14
        }
    }
}


operator fun Card.compareTo(other: Card): Int = this.pokerRankValue.compareTo(other.pokerRankValue)
infix fun Card.and(other: Card): PokerHand = PokerHand(listOf(this, other))

enum class HandRanking(val value: Int) {
    HIGHEST_CARD(0),
    PAIR(1),
    TWO_PAIR(2),
    THREE_OF_A_KIND(3),
    STRAIGHT(4),
    FLUSH(5),
    FULL_HOUSE(6),
    FOUR_OF_A_KIND(7),
    STRAIGHT_FLUSH(8),
    FIVE_OF_A_KIND(9),
}

data class PokerHand(var cards: Cards): Comparable<PokerHand> {
    val size get() = cards.size

    constructor(card: Card): this(listOf(card))
    init { cards = cards.sortedBy { card -> card.pokerRankValue } }

    infix fun and(other: Cards): PokerHand = PokerHand(cards + other)
    infix fun and(other: PokerHand): PokerHand = this and other.cards
    infix fun and(card: Card): PokerHand = PokerHand(cards + card)

    private val handRankValue: Pair<HandRanking, Cards>
    get() {
        assert(cards.isNotEmpty())

        nOfAKind(5)?.let { highest -> return HandRanking.FIVE_OF_A_KIND to highest }
        straight()?.let {
                highestStraight -> flush()?.let { highestFlush -> return HandRanking.STRAIGHT_FLUSH to listOf(highestStraight, highestFlush) }
        }
        nOfAKind(4)?.let { highest -> return HandRanking.FOUR_OF_A_KIND to highest }
        nOfAKind(3)?.let {
                highestThree -> nOfAKind(2)?.let { highestTwo -> return HandRanking.FULL_HOUSE to (highestThree + highestTwo) }
        }
        flush()?.let { highest -> return HandRanking.FLUSH to listOf(highest) }
        straight()?.let { highest -> return HandRanking.STRAIGHT to listOf(highest) }
        nOfAKind(3)?.let { highest -> return HandRanking.THREE_OF_A_KIND to highest }
        twoPair()?.let { highest -> return HandRanking.TWO_PAIR to highest }
        nOfAKind(2)?.let { highest -> return HandRanking.PAIR to highest }
        return HandRanking.HIGHEST_CARD to listOf(cards.maxBy { it.pokerRankValue }!!)
    }

    override operator fun compareTo(other: PokerHand): Int {
        val (myHandRanking, myHighestCards) = this.handRankValue
        val (otherHandRanking, otherHighestCards) = other.handRankValue
        if (myHandRanking == otherHandRanking) {
            for ((myCard, hisCard) in myHighestCards zip otherHighestCards) {
                if (myCard.pokerRankValue != hisCard.pokerRankValue) return myCard.compareTo(hisCard)
            }
            return 0
        } else {
            return myHandRanking.value.compareTo(otherHandRanking.value)
        }
    }

    private fun straight(): Card? {
        return if (cards.toSet().size != 5) null
        else if (cards.last().pokerRankValue - cards.first().pokerRankValue == 4) cards.first()
        else if (
            cards.last().rank == ace &&
            (cards.last().pokerRankValue - cards.first().pokerRankValue == 12) &&
            cards.takeLast(2).first().pokerRankValue - cards.first().pokerRankValue == 3
        ) LOW_ACE
        else null
    }

    private fun flush(): Card? {
        return if (cards.size == 5 && cards.all { card -> card.suit.color == cards.first().suit.color }) cards.maxBy { it.pokerRankValue } else null
    }

    private fun nOfAKind(n: Int): Cards? {
        val frequencies = cards.groupingBy { it.rank }.eachCount()
        val cardsInN = frequencies.filter { x -> x.value == n }
        return if (cardsInN.isNotEmpty()) cards.filter { it.rank == cardsInN.keys.first() } else null
    }

    private fun twoPair(): Cards? {
        val frequencies = cards.groupingBy { it.rank }.eachCount()
        val pairsRank = frequencies.filter { x -> x.value == 2 }.keys
        val pairs = cards.filter { it.rank in pairsRank }.sortedBy { card -> card.pokerRankValue }
        return if (pairs.size == 4) pairs else null
    }
}

fun Deck.dealPokerHands(cardsPerPlayer: Int, numPlayers: Int): Pair<List<PokerHand>, Deck> {
    val (cards, remainingDeck) = this.deal(cardsPerPlayer * numPlayers)
    val hands = cards.chunked(cardsPerPlayer).map { PokerHand(it) }
    return Pair(hands, remainingDeck)
}
fun Deck.dealPokerHand(cardsPerPlayer: Int): Pair<PokerHand, Deck> {
    val (hands, remainingDeck) = this.dealPokerHands(cardsPerPlayer = cardsPerPlayer, numPlayers = 1)
    return Pair(hands[0], remainingDeck)
}

inline class CommunityCards(val cards: Cards) {
    val size get() = cards.size

    operator fun plus(otherCards: Cards): CommunityCards = CommunityCards(cards + otherCards)
    operator fun plus(hand: PokerHand): PokerHand {
        assert(hand.size == 2)

        val combinations = cards.toSet().combinations(3).map { hand and it.toList() }
        return combinations.max()!!
    }
}

fun Deck.dealCommunityCards(communityCards: CommunityCards? = null): Pair<CommunityCards, Deck> {
    return if (communityCards == null) {
        val (cards, remainingDeck) = this.deal(3)
        Pair(CommunityCards(cards), remainingDeck)
    } else {
        val (cards, remainingDeck) = this.deal(1)
        Pair(communityCards + cards, remainingDeck)
    }
}
fun Deck.dealFlop() = dealCommunityCards()
fun Deck.dealTurn(communityCards: CommunityCards): Pair<CommunityCards, Deck> {
    assert(communityCards.size == 3)
    return dealCommunityCards(communityCards)
}
fun Deck.dealRiver(communityCards: CommunityCards): Pair<CommunityCards, Deck> {
    assert(communityCards.size == 4)
    return dealCommunityCards(communityCards)
}

data class HoldemPokerState(
    val givenHand: PokerHand? = null,
    val givenRivalHands: List<PokerHand?>,
    val givenFlop: Triple<Card, Card, Card>? = null,
    val givenTurn: Card?,
    val givenRiver: Card?
) {
    private var deck: Deck = makeDeck().shuffled()
    private var communityCards: CommunityCards
    private val myHand: PokerHand
    private val rivalHands: List<PokerHand>

    constructor(
        givenHand: PokerHand? = null,
        val givenFlop: Triple<Card, Card, Card>? = null,
        val givenTurn: Card?,
        val givenRiver: Card?
        numPlayers: int,
    ): this(
        givenHand = givenHand,
        givenRivalHands = arrayOfNulls<PokerHand>(numPlayers).toList(),
        givenFlop = givenFlop,
        givenTurn = givenTurn,
        givenRiver = givenRiver
    )

    init {
        this.myHand = givenHand ?: this.dealHand()
        this.rivalHands = sequence {
            for (hand in givenRivalHands) if (hand == null) {
                yield(dealHand())
            } else yield(hand)
        }.toList()
        val flop = if (givenFlop != null) CommunityCards(givenFlop.toList()) else dealFlop()
        val turn = if (givenTurn != null) CommunityCards(flop.cards + givenTurn) else dealTurn(flop)
        this.communityCards = if (givenRiver != null) CommunityCards(turn.cards + givenRiver) else dealRiver(turn)
    }

    private fun dealHand(): PokerHand {
        val (hand, remainingDeck) = this.deck.dealPokerHand(2)
        this.deck = remainingDeck
        return hand
    }

    private fun dealFlop(): CommunityCards {
        val (flop, remainingDeck) = this.deck.dealFlop()
        return flop
    }

    private fun dealTurn(flop: CommunityCards): CommunityCards {
        val (turn, remainingDeck) = this.deck.dealTurn(flop)
        return turn
    }

    private fun dealRiver(turn: CommunityCards): CommunityCards {
        val (river, remainingDeck) = this.deck.dealTurn(turn)
        return river
    }
}
