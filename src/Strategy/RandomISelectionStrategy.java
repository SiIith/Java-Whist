package Strategy;

import ch.aplu.jcardgame.Card;

import java.util.ArrayList;
import java.util.Random;


// select a card randomly
public class RandomISelectionStrategy implements ISelectionStrategy {
    @Override
    public Card makeSelection(ArrayList<Card> cardList) {
        Random random=new Random(30006);
        int x = random.nextInt(cardList.size());
        return cardList.get(x);
    }
}
