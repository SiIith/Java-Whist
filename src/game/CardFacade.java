package game;

import Filter.FilterFactory;
import Strategy.*;
import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;

import java.util.ArrayList;
import java.util.Random;

// facade for card selection process
public class CardFacade {
    private static final Random RANDOM = new Random(30006);
    private final FilterFactory filterFactory = FilterFactory.getInstance();
    private final StrategyFactory strategyFactory = StrategyFactory.getInstance();
    private static final String[] filterList = {"trump", "naive", "none"};
    private static final String[] selcectList = {"random", "max", "smart"};


    /**
     * think for a given player based on the behaviours specified in the property file
     *
     * @param player identifier of the player
     * @param hand   the player's hand
     * @return a selected card based on the behaviours
     */
    public Card think(int player, int leader, Hand hand) {
        hand.sort(Hand.SortType.RANKPRIORITY, false);
        String filter = PropertyManager.getWhistProperty("Player" + player + "Filter");
        String select = PropertyManager.getWhistProperty("Player" + player + "Select");
        ArrayList<Card> filtered=new ArrayList<>();

        // if a filter is specified, apply filter to hand, else return full hand
        if (player != leader) {
            if (filter.equals("allRandom"))
                filtered = this.filterFactory.getFilter(filterList[RANDOM.nextInt(filterList.length)]).filterList(hand);
            else if ((!(filter.equals("none")))) {
                filtered = this.filterFactory.getFilter(filter).filterList(hand);
            }
        } else
            filtered = hand.getCardList();

        // choose a selection strategy, default to be random
        if (!select.equals("allRandom"))
            return strategyFactory.getStrategy(select).makeSelection(filtered);
        else
            return strategyFactory.getStrategy(selcectList[RANDOM.nextInt(selcectList.length)]).makeSelection(filtered);

    }
}
