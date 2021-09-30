package Strategy;

import ch.aplu.jcardgame.Card;
import game.Information;
import game.Whist;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

// select a card based on the "smart" logic
public class SmartSelectionStrategy implements ISelectionStrategy {
    private List<Card> spadeList = new ArrayList<>(),
            heartList = new ArrayList<>(),
            diamondList = new ArrayList<>(),
            clubList = new ArrayList<>();

    @Override
    public Card makeSelection(ArrayList<Card> cardList) {
        for (Card card : cardList) {
            getList(card.getSuit().toString()).add(card);
        }
        Card winningCard = Information.winningCard;
        if(winningCard==null){
            return cardList.get(0);
        }
        if (winningCard.getSuit().equals(Information.trumpSuit)) {
            if (getList(winningCard.getSuit().toString()).size() != 0) {
                if (Whist.rankGreater(getList(winningCard.getSuit().toString()).get(0), winningCard)) {
                    return getList(winningCard.getSuit().toString()).get(0);
                }
            }
        } else {
            if (!getList(winningCard.getSuit().toString()).isEmpty()) {
                if (Whist.rankGreater(getList(winningCard.getSuit().toString()).get(0), winningCard))
                    return getList(winningCard.getSuit().toString()).get(0);
            }
            if (!getList(Information.trumpSuit.toString()).isEmpty())
                return getList(Information.trumpSuit.toString()).get(0);

        }
        return selectTheMin();
    }

    private List<Card> getList(String type) {
        switch (type) {
            case "SPADES": {
                return this.spadeList;

            }
            case "HEARTS": {
                return this.heartList;
            }
            case "DIAMONDS": {
                return this.diamondList;
            }
            case "CLUBS": {
                return this.clubList;
            }


        }
        return null;
    }

    private Card selectTheMin() {
        Stack<Card> compare = new Stack<>();
        if (!spadeList.isEmpty())
            compare.push(spadeList.get(spadeList.size() - 1));
        if (!heartList.isEmpty())
            compare.push(heartList.get(heartList.size() - 1));
        if (!diamondList.isEmpty())
            compare.push(diamondList.get(diamondList.size() - 1));
        if (!clubList.isEmpty())
            compare.push(clubList.get(clubList.size() - 1));
        if (compare.isEmpty())
            throw new RuntimeException("there are something run in hand");
        while (compare.size() > 1) {
            Card card1 = compare.pop();
            Card card2 = compare.pop();
            if (Whist.rankGreater(card1, card2))
                compare.push(card2);
            else compare.push(card1);
        }
        return compare.peek();

    }

}


