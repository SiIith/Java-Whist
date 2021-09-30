package Filter;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;


import java.util.ArrayList;

public interface IFilter {
    ArrayList<Card> filterList(Hand hand);
}
