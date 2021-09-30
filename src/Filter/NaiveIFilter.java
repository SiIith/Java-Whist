package Filter;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;
import game.Information;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


// filter the hand to decide from only trump + lead
public class NaiveIFilter implements IFilter {
    @Override
    public ArrayList<Card> filterList(Hand hand) {
       List<Card> candidate= hand.getCardList().stream().filter(c -> c.getSuit().equals(Information.leadSuit) || c.getSuit().equals(Information.trumpSuit)).collect(Collectors.toList());
       if(candidate.size()==0)
           return hand.getCardList();
       return (ArrayList<Card>) candidate;
    }
}
