package game;

import ch.aplu.jcardgame.*;
import ch.aplu.jgamegrid.*;

import java.awt.Color;
import java.awt.Font;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@SuppressWarnings("serial")
public class Whist extends CardGame {

    public enum Suit
    {
        SPADES, HEARTS, DIAMONDS, CLUBS
    }

    public enum Rank
    {
        // Reverse order of rank importance (see rankGreater() below)
        // Order of cards is tied to card images
        ACE, KING, QUEEN, JACK, TEN, NINE, EIGHT, SEVEN, SIX, FIVE, FOUR, THREE, TWO
    }

    // all available properties. New prop files should also be added here
    public enum Mode{
        whist, legal, smart
    }

    final String trumpImage[] = {"bigspade.gif","bigheart.gif","bigdiamond.gif","bigclub.gif"};

   private static final Random random = new Random(30006);

    // return random Enum value
    public static <T extends Enum<?>> T randomEnum(Class<T> clazz){
        int x = random.nextInt(clazz.getEnumConstants().length);
        return clazz.getEnumConstants()[x];
    }

    // return random Card from Hand
    public static Card randomCard(Hand hand){
        int x = random.nextInt(hand.getNumberOfCards());
        return hand.get(x);
    }

    // return random Card from ArrayList
    public static Card randomCard(ArrayList<Card> list){
        int x = random.nextInt(list.size());
        return list.get(x);
    }

    public static boolean rankGreater(Card card1, Card card2) {
        return card1.getRankId() < card2.getRankId(); // Warning: Reverse rank order of cards (see comment on enum)
    }

    private final String version = "1.0";
    public int nbPlayers = 4;
    public int nbStartCards = 13;
    public int winningScore = 24;
    private final int handWidth = 400;
    private final int trickWidth = 40;
    private final Deck deck = new Deck(Suit.values(), Rank.values(), "cover");
    private final Location[] handLocations = {
            new Location(350, 625),
            new Location(75, 350),
            new Location(350, 75),
            new Location(625, 350)
    };
    private final Location[] scoreLocations = {
            new Location(575, 675),
            new Location(25, 575),
            new Location(575, 25),
            new Location(650, 575)
    };
    private  CardFacade cardFacade = new CardFacade();


    private Actor[] scoreActors = {null, null, null, null };
    private final Location trickLocation = new Location(350, 350);
    private final Location textLocation = new Location(350, 450);
    private final int thinkingTime = 2000;
    private Hand[] hands;
    private Location hideLocation = new Location(-500, - 500);
    private Location trumpsActorLocation = new Location(50, 50);
    private boolean enforceRules=false;

    public void setStatus(String string) { setStatusText(string); }

    private int[] scores = new int[nbPlayers];

    Font bigFont = new Font("Serif", Font.BOLD, 36);

    private void initScore() {
        for (int i = 0; i < nbPlayers; i++) {
            scores[i] = 0;
            scoreActors[i] = new TextActor("0", Color.WHITE, bgColor, bigFont);
            addActor(scoreActors[i], scoreLocations[i]);
        }
    }

    private void updateScore(int player) {
        removeActor(scoreActors[player]);
        scoreActors[player] = new TextActor(String.valueOf(scores[player]), Color.WHITE, bgColor, bigFont);
        addActor(scoreActors[player], scoreLocations[player]);
    }

    private Card selected;

    private void initRound() {
        nbStartCards = Integer.parseInt(PropertyManager.getWhistProperty("StartingHand")); // set starting hand
        nbPlayers = Integer.parseInt((PropertyManager.getWhistProperty("NPlayers")));
        winningScore = Integer.parseInt(PropertyManager.getWhistProperty("WinningScore"));
        hands = deck.dealingOut(nbPlayers, nbStartCards); // Last element of hands is leftover cards; these are ignored
        for (int i = 0; i < nbPlayers; i++) {
            hands[i].sort(Hand.SortType.SUITPRIORITY, true);
        }
        // Set up human player for interaction
        CardListener cardListener = new CardAdapter()  // Human Player plays card
        {
            public void leftDoubleClicked(Card card) { selected = card; hands[0].setTouchEnabled(false); }
        };
        hands[0].addCardListener(cardListener);
        // graphics
        RowLayout[] layouts = new RowLayout[nbPlayers];
        for (int i = 0; i < nbPlayers; i++) {
            layouts[i] = new RowLayout(handLocations[i], handWidth);
            layouts[i].setRotationAngle(90 * i);
            // layouts[i].setStepDelay(10);
            hands[i].setView(this, layouts[i]);
            hands[i].setTargetArea(new TargetArea(trickLocation));
            hands[i].draw();
        }

        // hide all bot players' hands if cardDown is turned on
        if (Boolean.parseBoolean(PropertyManager.getWhistProperty("cardDown"))) {
            for (int i = 0; i < nbPlayers; i++) {
                if (!Boolean.parseBoolean(PropertyManager.getWhistProperty("Player"+i+"Human")))
                    hands[i].setVerso(true);
            }
        }
        // End graphics
    }

    private String printHand(ArrayList<Card> cards) {
        String out = "";
        for(int i = 0; i < cards.size(); i++) {
            out += cards.get(i).toString();
            if(i < cards.size()-1) out += ",";
        }
        return(out);
    }

    private Optional<Integer> playRound() {
        // Returns winner, if any
        // Select and display trump suit
        final Suit trumps = randomEnum(Suit.class);
        //todo: redundant?
        Information.trumpSuit=trumps;
        final Actor trumpsActor = new Actor("sprites/"+trumpImage[trumps.ordinal()]);
        addActor(trumpsActor, trumpsActorLocation);
        // End trump suit
        Hand trick;
        int winner;
        Card winningCard;
        Suit lead;
        int nextPlayer = random.nextInt(nbPlayers); // randomly select player to lead for this round
        int leader = nextPlayer;
        for (int i = 0; i < nbStartCards; i++) {
            trick = new Hand(deck);
            selected = null;
            if (Boolean.parseBoolean(PropertyManager.getWhistProperty("Player"+nextPlayer+"Human"))) {  // Select lead depending on player type
                hands[nextPlayer].setTouchEnabled(true);
                setStatus("Player " + nextPlayer + " double-click on card to lead.");
                while (null == selected) delay(100);
            } else {
                setStatusText("Player " + nextPlayer + " thinking...");
                delay(thinkingTime);
                selected = cardFacade.think(nextPlayer,leader, hands[nextPlayer]);
                System.out.println(selected.getSuit().toString());
            }
            // Lead with selected card
            trick.setView(this, new RowLayout(trickLocation, (trick.getNumberOfCards()+2)*trickWidth));
            trick.draw();
            selected.setVerso(false);
            // No restrictions on the card being lead
            lead = (Suit) selected.getSuit();
            Information.leadSuit=lead;
            selected.transfer(trick, true); // transfer to trick (includes graphic effect)
            winner = nextPlayer;
            winningCard = selected;
            Information.winningCard=winningCard;
            System.out.println("New trick: Lead Player = "+nextPlayer+", Lead suit = "+selected.getSuit()+", Trump suit = "+trumps);
            System.out.println("Player "+nextPlayer+" play: "+selected.toString()+" from ["+printHand(hands[nextPlayer].getCardList())+"]");
            // End Lead
            for (int j = 1; j < nbPlayers; j++) {
                if (++nextPlayer >= nbPlayers) nextPlayer = 0;  // From last back to first
                selected = null;
                if (Boolean.parseBoolean(PropertyManager.getWhistProperty("Player"+nextPlayer+"Human"))) {
                    hands[nextPlayer].setTouchEnabled(true);
                    setStatus("Player " +nextPlayer+ " double-click on card to follow.");
                    while (null == selected) delay(100);
                } else {
                    setStatusText("Player " + nextPlayer + " thinking...");
                    delay(thinkingTime);
                    selected = cardFacade.think(nextPlayer,leader, hands[nextPlayer]);
                }
                // Follow with selected card
                trick.setView(this, new RowLayout(trickLocation, (trick.getNumberOfCards()+2)*trickWidth));
                trick.draw();
                selected.setVerso(false);  // In case it is upside down
                // Check: Following card must follow suit if possible
                if (selected.getSuit() != lead && hands[nextPlayer].getNumberOfCardsWithSuit(lead) > 0) {
                    // Rule violation
                    String violation = "Follow rule broken by player " + nextPlayer + " attempting to play " + selected;
                    //System.out.println(violation);
                    if (enforceRules)
                        try {
                            throw(new BrokeRuleException(violation));
                        } catch (BrokeRuleException e) {
                            e.printStackTrace();
                            System.out.println("A cheating player spoiled the game!");
                            System.exit(0);
                        }
                }
                // End Check
                selected.transfer(trick, true); // transfer to trick (includes graphic effect)
                System.out.println("Winning card: "+winningCard.toString());
                System.out.println("Player "+nextPlayer+" play: "+selected.toString()+" from ["+printHand(hands[nextPlayer].getCardList())+"]");
                if ( // beat current winner with higher card
                        (selected.getSuit() == winningCard.getSuit() && rankGreater(selected, winningCard)) ||
                                // trumped when non-trump was winning
                                (selected.getSuit() == trumps && winningCard.getSuit() != trumps)) {
                    winner = nextPlayer;
                    winningCard = selected;
                    Information.winningCard=winningCard;
                }
                // End Follow
            }
            delay(600);
            trick.setView(this, new RowLayout(hideLocation, 0));
            trick.draw();
            nextPlayer = winner;
            System.out.println("Winner: "+winner);
            setStatusText("Player " + nextPlayer + " wins trick.");
            leader = winner;
            scores[nextPlayer]++;
            updateScore(nextPlayer);
            Information.winningCard=null;
            if (winningScore == scores[nextPlayer]) return Optional.of(nextPlayer);
        }
        removeActor(trumpsActor);
        return Optional.empty();
    }

    public Whist() throws IOException {
        super(700, 700, 30);
        setTitle("Whist (V" + version + ") Constructed for UofM SWEN30006 with JGameGrid (www.aplu.ch)");
        setStatusText("Initializing...");
        initScore();
        Optional<Integer> winner;

        // change the enum here to switch between different properties
        PropertyManager.initProperty(Mode.whist.toString()); // make the property manager
        do {
            initRound();
            winner = playRound();
        } while (!winner.isPresent());
        addActor(new Actor("sprites/gameover.gif"), textLocation);
        setStatusText("Game over. Winner is player: " + winner.get());
        refresh();
    }

    public static void main(String[] args) throws IOException {
        new Whist();
    }

}
