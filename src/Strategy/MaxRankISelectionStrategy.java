package Strategy;

import ch.aplu.jcardgame.Card;

import java.util.ArrayList;


// select the largest card based on its rank
public class MaxRankISelectionStrategy implements ISelectionStrategy {
    @Override
    public Card makeSelection(ArrayList<Card> cardList) {
            return cardList.get(0);
    }
}
