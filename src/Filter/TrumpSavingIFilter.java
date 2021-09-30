package Filter;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;
import game.Information;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


// filter the hand to trump + lead while saving trump as much as possible. Only returns trumps if no lead is available
public class TrumpSavingIFilter implements IFilter {
    @Override
    public ArrayList<Card> filterList(Hand hand) {
        List<Card> trumpSuitList = hand.getCardList().stream().filter(card -> card.getSuit().equals(Information.trumpSuit)).collect(Collectors.toList());
        List<Card> leadSuitList = hand.getCardList().stream().filter(card -> card.getSuit().equals(Information.leadSuit)).collect(Collectors.toList());
        leadSuitList.addAll(trumpSuitList);
        if (leadSuitList.size() == 0)
            return hand.getCardList();
        return (ArrayList<Card>) leadSuitList;
    }
}
