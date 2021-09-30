package Strategy;
 import ch.aplu.jcardgame.*;

 import java.util.ArrayList;
 import java.util.List;

public interface ISelectionStrategy {
    Card makeSelection(ArrayList<Card> cardList);
}
