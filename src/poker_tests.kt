import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.StringSpec

class PokerCardTests : StringSpec({
    "ace beats all" {
        (ace of spades > king of spades) shouldBe true
    }
    "jack beats 10" {
        (jack of clubs > ten of spades) shouldBe true
    }
    "10 beats 9" {
        (nine of spades < ten of spades)
    }
    "suits don't count in ranking but they do in equality" {
        (ace of spades > ace of clubs) shouldBe false
        (ace of spades < ace of clubs) shouldBe false
        (ace of spades == ace of spades) shouldBe true
        (ace of spades == ace of clubs) shouldBe false
    }
})

class PokerHandTests : StringSpec({
    "merging two cards into hand" {
        ace of hearts and (jack of diamonds) shouldBe PokerHand(listOf(ace of hearts, jack of diamonds))
    }
    "merging three cards into hand" {
        ace of hearts and (jack of diamonds) and (seven of spades) shouldBe
                PokerHand(listOf(ace of hearts, jack of diamonds, seven of spades))
    }
    "merging two hands into hand" {
        (ace of hearts and (jack of diamonds)) and (queen of spades and (five of clubs)) shouldBe
                PokerHand(listOf(ace of hearts, jack of diamonds, queen of spades, five of clubs))
    }
    "order shouldn't matter" {
        queen of hearts and (king of hearts) shouldBe ((king of hearts) and (queen of hearts))
    }
    "high cards" {
        (PokerHand(ace of hearts) > (king of clubs) and (queen of spades)) shouldBe true
        (PokerHand(jack of hearts) < (king of clubs) and (queen of spades)) shouldBe true
    }
    "pair beats high card" {
        ((two of clubs) and (three of clubs) < PokerHand(ace of spades)) shouldBe true
        ((two of clubs) and (two of spades) > PokerHand(ace of spades)) shouldBe true
    }
    "double pair beats pair" {
        ((ten of clubs) and (ten of diamonds) and (six of spades) and (six of clubs) >
                ((ace of spades) and (ace of hearts))) shouldBe true
    }
    "three of a kind beats double pair" {
        ((two of hearts) and (two of clubs) and (two of spades) >
                ((ace of hearts) and (ace of clubs) and (king of hearts) and (king of clubs))) shouldBe true
    }
    "straight beats three of a kind" {
        ((seven of hearts) and (eight of diamonds) and (ten of spades) and (nine of hearts) and (jack of hearts) >
                ((ace of hearts) and (ace of clubs) and (king of hearts) and (king of clubs))) shouldBe true
    }
    "low ace straight loses to other straights" {
        ((ace of spades) and (two of hearts) and (three of hearts) and (four of spades) and (five of hearts) <
                ((two of spades) and (three of hearts) and (four of spades) and (five of hearts) and (six of spades))) shouldBe true
    }
    "high ace straight beats other straights" {
        ((ten of hearts) and (jack of hearts) and (queen of spades) and (king of spades) and (ace of hearts) >
                ((nine of spades) and (ten of hearts) and (jack of hearts) and (queen of spades) and (king of spades))) shouldBe true
    }
    "flush beats straight" {
        ((two of hearts) and (jack of hearts) and (three of hearts) and (seven of hearts) and (ace of hearts) >
                ((ten of hearts) and (jack of hearts) and (queen of spades) and (king of spades) and (ace of hearts))) shouldBe true
    }
    "flush with high card wins" {
        ((two of hearts) and (jack of hearts) and (three of hearts) and (seven of hearts) and (ace of hearts) >
                ((two of hearts) and (jack of hearts) and (three of hearts) and (seven of hearts) and (king of hearts))) shouldBe true
    }
    "full house beats flush" {
        ((two of clubs) and (two of spades) and (two of diamonds) and (three of clubs) and (three of hearts) >
                ((two of hearts) and (jack of hearts) and (three of hearts) and (seven of hearts) and (ace of hearts))) shouldBe true
    }
    "four of a kind beats full house" {
        ((three of clubs) and (three of diamonds) and (three of hearts) and (three of spades) >
                ((two of clubs) and (two of spades) and (two of diamonds) and (three of clubs) and (three of hearts))) shouldBe true
    }
    "straight flush beats four of a kind" {
        ((ten of hearts) and (jack of hearts) and (queen of hearts) and (king of hearts) and (ace of hearts) >
                ((three of clubs) and (three of diamonds) and (three of hearts) and (three of spades))) shouldBe true
    }
    "five of a kind beats straight flush" {
        // Not implemented. Needs wildcards
    }
    "weird case" {
        val badHand = (four of hearts) and (eight of diamonds) and (jack of clubs) and (queen of hearts) and (ace of clubs)
        val pairHand = (five of clubs) and (six of diamonds) and (nine of diamonds) and (nine of spades) and (king of diamonds)
        ((four of hearts) and (eight of diamonds) and (jack of clubs) and (queen of hearts) and (ace of clubs) <
        ((five of clubs) and (six of diamonds) and (nine of diamonds) and (nine of spades) and (king of diamonds))) shouldBe true
        (badHand < pairHand) shouldBe true
        (pairHand > badHand) shouldBe true
        (badHand > pairHand) shouldBe false
        (pairHand < badHand) shouldBe false
        listOf(badHand, pairHand).max() shouldBe pairHand
    }
})
