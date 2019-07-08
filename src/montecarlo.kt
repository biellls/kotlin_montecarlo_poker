fun Boolean.toInt() = if (this) 1 else 0

fun montecarlo(experiment: () -> Boolean, n: Int = 1000): Float {
    val results = sequence { while (true) yield(experiment()) }.take(n)
    return results.map { it.toInt() }.sum().toFloat() / n
}

fun texasHoldEmMontecarloSim(
    startingHand: PokerHand,
    river: CommunityCards,
    rivalHands: List<PokerHand?>,
    excludedCards: Cards = listOf(),
    n: Int = 1000
): Float {
    fun texasHoldEmSim(): Boolean {
//        val (rivalHands, deck) = makeDeck().removeCards(flop.cards).removeCards(startingHand.cards).shuffled()
//            .dealPokerHands(2, nPlayers - 1)
//        val (turn, remainingDeck) = deck.dealTurn(communityCards = flop)
//        val (river, _) = remainingDeck.dealRiver(communityCards = turn)
        var deck = makeDeck().removeCards(startingHand.cards + river.cards + excludedCards).
            removeCards(rivalHands.filterNotNull().map { it.cards }.flatten()).shuffled()
        val dealtHands = rivalHands.map {
            if (it == null) {
                val (hand, remainingDeck) = deck.dealPokerHand(cardsPerPlayer = 2)
                deck = remainingDeck
                hand
            } else {
                it
            }
        }

        val myBestHand = river + startingHand
        val bestRivalHand = rivalHands.map { river + it }.max()!!

        return myBestHand > bestRivalHand
    }

    return montecarlo(experiment = { texasHoldEmSim(startingHand, nPlayers) }, n = n)
}

