const val CARDS_PER_PLAYER = 5
const val NUM_PLAYERS = 3
const val N_EXPERIMENT = 10_000

fun main() {
    println(texasHoldEmMontecarlo(
        n = N_EXPERIMENT,
        nPlayers = NUM_PLAYERS,
        startingHand = ace of hearts and (two of spades),
        flop = CommunityCards(listOf(nine of hearts, two of diamonds, three of diamonds))
        )
    )
//    println(texasHoldEmMontecarlo(n = N_EXPERIMENT, nPlayers = NUM_PLAYERS, startingHand = two of hearts and (nine of spades)))

//    var deck = makeDeck().shuffled()
//    while (deck.size >= CARDS_PER_PLAYER * NUM_PLAYERS) {
//        val (hands, remainingDeck) = deck.dealPokerHands(CARDS_PER_PLAYER, NUM_PLAYERS)
//        println(hands)
//        println(hands.max())
//        deck = remainingDeck
//    }
//    println(deck)

//    for (i in 1..NUM_PLAYERS) {
//        val (cards, remainingDeck) = deck.deal(numCards = CARDS_PER_PLAYER)
//        val hand = PokerHand(cards)
//        print(hand)
//        deck = remainingDeck
//    }
}