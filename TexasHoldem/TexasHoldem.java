import java.util.*;

/**
 * Texas Hold'em Poker: you against computer opponents (no-limit betting).
 *
 * Run it (Java 11+):      java TexasHoldem.java [opponents]
 * Or compile first:       javac TexasHoldem.java && java TexasHoldem [opponents]
 *
 * [opponents] is 1-8 (default 3).  Add -Ddelay=0 to remove the bot "thinking" pause.
 */
public class TexasHoldem {

    // ======================================================================
    //  Settings
    // ======================================================================
    static final int STARTING_CHIPS = 1000;
    static final int START_SMALL_BLIND = 10;
    static final int HANDS_PER_LEVEL = 10;          // blinds double every N hands
    static final boolean USE_SUIT_SYMBOLS = false;  // true => prints suit symbols (needs a UTF-8 terminal)
    static final int BOT_DELAY_MS = Integer.getInteger("delay", 500);

    static final Random RNG = new Random();
    static final Scanner IN = new Scanner(System.in);

    // ======================================================================
    //  Cards
    // ======================================================================
    static final String RANK_CHARS = "23456789TJQKA";
    static final String SUIT_ASCII = "cdhs";
    static final String SUIT_SYMBOLS = "\u2663\u2666\u2665\u2660";
    static final String[] NAME = {"", "", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight",
            "Nine", "Ten", "Jack", "Queen", "King", "Ace"};
    static final String[] PLURAL = {"", "", "Twos", "Threes", "Fours", "Fives", "Sixes", "Sevens",
            "Eights", "Nines", "Tens", "Jacks", "Queens", "Kings", "Aces"};

    static final class Card {
        final int rank;   // 2..14 (14 = Ace)
        final int suit;   // 0..3

        Card(int rank, int suit) {
            this.rank = rank;
            this.suit = suit;
        }

        @Override
        public String toString() {
            return "" + RANK_CHARS.charAt(rank - 2) + (USE_SUIT_SYMBOLS ? SUIT_SYMBOLS : SUIT_ASCII).charAt(suit);
        }
    }

    static List<Card> newShuffledDeck() {
        List<Card> deck = new ArrayList<>(52);
        for (int s = 0; s < 4; s++)
            for (int r = 2; r <= 14; r++)
                deck.add(new Card(r, s));
        Collections.shuffle(deck, RNG);
        return deck;
    }

    // ======================================================================
    //  Hand evaluation
    //  A hand's strength is one int: category in the top bits, then the
    //  tie-breaking ranks (4 bits each), so higher int == better hand.
    //  Categories: 0 high card, 1 pair, 2 two pair, 3 trips, 4 straight,
    //              5 flush, 6 full house, 7 quads, 8 straight flush
    // ======================================================================
    static int score5(Card[] c) {
        int[] cnt = new int[15];
        boolean flush = true;
        for (Card x : c) {
            cnt[x.rank]++;
            if (x.suit != c[0].suit) flush = false;
        }
        // Distinct ranks ordered by (how many times they appear, then rank), both descending.
        int[] r = new int[5];
        int n = 0;
        for (int k = 4; k >= 1; k--)
            for (int rank = 14; rank >= 2; rank--)
                if (cnt[rank] == k) r[n++] = rank;

        int top = cnt[r[0]];
        int second = n > 1 ? cnt[r[1]] : 0;

        boolean straight = false;
        if (n == 5) {
            if (r[0] - r[4] == 4) {
                straight = true;
            } else if (r[0] == 14 && r[1] == 5) {          // A-2-3-4-5 "wheel": the ace plays low
                straight = true;
                r = new int[]{5, 4, 3, 2, 1};
            }
        }

        int cat;
        if (straight && flush) cat = 8;
        else if (top == 4) cat = 7;
        else if (top == 3 && second == 2) cat = 6;
        else if (flush) cat = 5;
        else if (straight) cat = 4;
        else if (top == 3) cat = 3;
        else if (top == 2 && second == 2) cat = 2;
        else if (top == 2) cat = 1;
        else cat = 0;

        int score = cat;
        for (int i = 0; i < 5; i++) score = (score << 4) | (i < n ? r[i] : 0);
        return score;
    }

    /** Best 5-card hand out of 5, 6 or 7 cards. */
    static int bestScore(List<Card> cards) {
        int n = cards.size();
        Card[] five = new Card[5];
        int best = -1;
        for (int mask = 0; mask < (1 << n); mask++) {
            if (Integer.bitCount(mask) != 5) continue;
            int k = 0;
            for (int i = 0; i < n; i++)
                if (((mask >> i) & 1) == 1) five[k++] = cards.get(i);
            best = Math.max(best, score5(five));
        }
        return best;
    }

    static String describe(int score) {
        int cat = score >> 20;
        int[] r = new int[5];
        for (int i = 0; i < 5; i++) r[i] = (score >> (16 - 4 * i)) & 15;
        switch (cat) {
            case 8:  return r[0] == 14 ? "Royal Flush" : "Straight Flush, " + NAME[r[0]] + "-high";
            case 7:  return "Four of a Kind, " + PLURAL[r[0]];
            case 6:  return "Full House, " + PLURAL[r[0]] + " over " + PLURAL[r[1]];
            case 5:  return "Flush, " + NAME[r[0]] + "-high";
            case 4:  return "Straight, " + NAME[r[0]] + "-high";
            case 3:  return "Three of a Kind, " + PLURAL[r[0]];
            case 2:  return "Two Pair, " + PLURAL[r[0]] + " and " + PLURAL[r[1]];
            case 1:  return "Pair of " + PLURAL[r[0]];
            default: return "High Card, " + NAME[r[0]];
        }
    }

    // ======================================================================
    //  Players and actions
    // ======================================================================
    static final class Player {
        final String name;
        final boolean human;
        int chips;
        Card[] hole = new Card[2];
        boolean folded, allIn, out, acted;
        int roundBet;      // chips put in during the current betting round
        int totalBet;      // chips put in during the whole hand
        double aggression = 1.0;   // bots: how often they bet/raise (0.5 - 1.5)
        double looseness = 0.0;    // bots: how willing they are to play marginal hands

        Player(String name, boolean human, int chips) {
            this.name = name;
            this.human = human;
            this.chips = chips;
        }

        boolean canAct() {
            return !folded && !allIn;
        }
    }

    enum Type {FOLD, CALL, RAISE}   // CALL doubles as "check" when there is nothing to call

    static final class Action {
        final Type type;
        final int amount;   // RAISE only: the player's total bet for this round after raising

        Action(Type type, int amount) {
            this.type = type;
            this.amount = amount;
        }
    }

    // ======================================================================
    //  Game state
    // ======================================================================
    final List<Player> players = new ArrayList<>();
    final List<Card> board = new ArrayList<>();
    List<Card> deck;
    int dealer;
    int smallBlind = START_SMALL_BLIND;
    int bigBlind = START_SMALL_BLIND * 2;
    int currentBet;        // highest bet this round that everyone must match
    int minRaise;          // size of the smallest legal raise
    int raisesThisRound;
    int handNumber;

    public static void main(String[] args) {
        int bots = 3;
        if (args.length > 0) {
            try {
                bots = Integer.parseInt(args[0]);
            } catch (NumberFormatException ignored) {
            }
        }
        bots = Math.max(1, Math.min(8, bots));
        new TexasHoldem().run(bots);
    }

    void run(int bots) {
        String[] botNames = {"Alice", "Bob", "Carol", "Dave", "Eve", "Frank", "Grace", "Heidi"};
        players.add(new Player("You", true, STARTING_CHIPS));
        for (int i = 0; i < bots; i++) {
            Player b = new Player(botNames[i], false, STARTING_CHIPS);
            b.aggression = 0.5 + RNG.nextDouble();
            b.looseness = -0.03 + RNG.nextDouble() * 0.08;
            players.add(b);
        }
        dealer = RNG.nextInt(players.size());

        println("==============================================");
        println("   TEXAS HOLD'EM  -  you vs " + bots + " opponent" + (bots > 1 ? "s" : ""));
        println("   Everyone starts with " + STARTING_CHIPS + " chips. Blinds " + smallBlind + "/" + bigBlind
                + ", doubling every " + HANDS_PER_LEVEL + " hands.");
        println("   Cards: rank + suit (c d h s), T = ten.  e.g. Ah = ace of hearts");
        println("==============================================");

        Player me = players.get(0);
        while (true) {
            playHand();
            for (Player p : players) {
                if (p.chips == 0 && !p.out) {
                    p.out = true;
                    println(p.name + (p.human ? " are out of chips and leave" : " is out of chips and leaves") + " the table.");
                }
            }
            if (me.out) {
                println("\nYou're out of chips. Game over!");
                break;
            }
            if (activeCount() == 1) {
                println("\nYou knocked out every opponent. You win the game!");
                break;
            }
            String s = readLine("\nPress Enter for the next hand (or q to quit): ");
            if (s.equalsIgnoreCase("q")) break;
        }
        println("\nFinal chip counts:");
        for (Player p : players) println("  " + p.name + ": " + p.chips);
    }

    // ======================================================================
    //  One hand of poker
    // ======================================================================
    void playHand() {
        handNumber++;
        if (handNumber > 1 && (handNumber - 1) % HANDS_PER_LEVEL == 0) {
            smallBlind *= 2;
            bigBlind *= 2;
            println("\n*** Blinds go up to " + smallBlind + "/" + bigBlind + " ***");
        }

        board.clear();
        deck = newShuffledDeck();
        for (Player p : players) {
            p.folded = p.out;
            p.allIn = false;
            p.roundBet = 0;
            p.totalBet = 0;
            p.hole = new Card[2];
        }

        dealer = nextSeat(dealer);
        int active = activeCount();
        int sb = (active == 2) ? dealer : nextSeat(dealer);   // heads-up: the dealer posts the small blind
        int bb = nextSeat(sb);

        println("\n----------------------------------------------");
        println(" HAND #" + handNumber + "   blinds " + smallBlind + "/" + bigBlind + "   dealer: "
                + players.get(dealer).name);
        println("----------------------------------------------");
        StringJoiner stacks = new StringJoiner(" | ");
        for (Player p : players) if (!p.out) stacks.add(p.name + " " + p.chips);
        println("Stacks: " + stacks);

        // Deal two cards to each player, starting with the small blind.
        for (int round = 0; round < 2; round++) {
            int i = sb;
            for (int k = 0; k < active; k++) {
                players.get(i).hole[round] = deck.remove(deck.size() - 1);
                i = nextSeat(i);
            }
        }

        // Blinds
        postBlind(players.get(sb), smallBlind, "the small blind");
        postBlind(players.get(bb), bigBlind, "the big blind");
        currentBet = bigBlind;
        minRaise = bigBlind;
        raisesThisRound = 0;

        Player me = players.get(0);
        if (!me.out) println("Your cards: " + me.hole[0] + " " + me.hole[1]);

        // Pre-flop betting, then flop / turn / river
        bettingRound(nextSeat(bb));
        String[] streets = {"FLOP", "TURN", "RIVER"};
        int[] dealCount = {3, 1, 1};
        for (int s = 0; s < 3 && countInHand() > 1; s++) {
            deck.remove(deck.size() - 1);   // burn card
            for (int k = 0; k < dealCount[s]; k++) board.add(deck.remove(deck.size() - 1));
            for (Player p : players) p.roundBet = 0;
            currentBet = 0;
            minRaise = bigBlind;
            raisesThisRound = 0;
            println("\n*** " + streets[s] + " ***   Board: " + boardString() + "   Pot: " + pot());
            bettingRound(nextSeat(dealer));
        }

        // If the hand reached the end with players all-in, deal out any missing board cards.
        while (countInHand() > 1 && board.size() < 5) {
            deck.remove(deck.size() - 1);
            board.add(deck.remove(deck.size() - 1));
        }

        awardPots();
        assert totalChips() == STARTING_CHIPS * players.size() : "Chip count changed!";
    }

    void postBlind(Player p, int amount, String label) {
        put(p, amount);
        println(p.name + " post" + (p.human ? "" : "s") + " " + label + " (" + p.roundBet + ")"
                + (p.allIn ? " and is all-in" : "") + ".");
    }

    // ======================================================================
    //  Betting
    // ======================================================================
    void bettingRound(int first) {
        for (Player p : players) p.acted = false;
        int idx = first;
        while (countInHand() > 1) {
            // If at most one player can still act, and they have already matched the bet, we're done.
            if (countCanAct() <= 1) {
                Player only = null;
                for (Player p : players) if (p.canAct()) only = p;
                if (only == null || only.roundBet >= currentBet) return;
            }
            Player actor = null;
            for (int k = 0; k < players.size(); k++) {
                int i = (idx + k) % players.size();
                Player p = players.get(i);
                if (p.canAct() && (!p.acted || p.roundBet < currentBet)) {
                    actor = p;
                    idx = i;
                    break;
                }
            }
            if (actor == null) return;   // everyone has acted and matched the bet
            act(actor);
            idx = (idx + 1) % players.size();
        }
    }

    void act(Player p) {
        Action a = p.human ? humanAction(p) : aiAction(p);
        if (!p.human) pause();

        if (a.type == Type.FOLD) {
            p.folded = true;
            println(does(p, "fold") + ".");
        } else if (a.type == Type.CALL) {
            int toCall = Math.min(currentBet - p.roundBet, p.chips);
            if (toCall <= 0) {
                println(does(p, "check") + ".");
            } else {
                put(p, toCall);
                println(does(p, "call") + " " + toCall + (p.allIn ? " (all-in)" : "") + ".");
            }
        } else {
            boolean isBet = currentBet == 0;
            int before = currentBet;
            put(p, a.amount - p.roundBet);
            println(does(p, isBet ? "bet" : "raise") + (isBet ? " " : " to ") + p.roundBet
                    + (p.allIn ? " (all-in)" : "") + ".");
            if (p.roundBet > before) {
                int size = p.roundBet - before;
                if (size >= minRaise) minRaise = size;
                currentBet = p.roundBet;
                raisesThisRound++;
                for (Player o : players) if (o != p) o.acted = false;   // everyone must respond to a raise
            }
        }
        p.acted = true;
    }

    void put(Player p, int amount) {
        amount = Math.max(0, Math.min(amount, p.chips));
        p.chips -= amount;
        p.roundBet += amount;
        p.totalBet += amount;
        if (p.chips == 0) p.allIn = true;
    }

    // ======================================================================
    //  Human player
    // ======================================================================
    Action humanAction(Player p) {
        int owed = currentBet - p.roundBet;
        int toCall = Math.min(owed, p.chips);
        int maxTo = p.roundBet + p.chips;
        int minTo = Math.min(currentBet + minRaise, maxTo);
        boolean canRaise = p.chips > owed && othersCanAct(p) > 0;

        printTable(p);
        StringBuilder menu = new StringBuilder("Your move:  (f)old   ");
        if (toCall == 0) menu.append("(c)heck");
        else menu.append("(c)all ").append(toCall).append(toCall == p.chips ? " [all-in]" : "");
        if (canRaise) {
            menu.append("   (r)").append(currentBet == 0 ? "bet" : "aise").append(" <total>  [")
                    .append(minTo).append("-").append(maxTo).append("]   (a)ll-in ").append(p.chips);
        }
        menu.append("   (h)int   (q)uit");
        println(menu.toString());

        while (true) {
            String[] t = readLine("> ").toLowerCase().split("\\s+");
            String cmd = t[0];
            if (cmd.equals("f") || cmd.equals("fold")) {
                return new Action(Type.FOLD, 0);
            } else if (cmd.equals("c") || cmd.equals("call") || cmd.equals("check")) {
                return new Action(Type.CALL, 0);
            } else if (cmd.equals("h") || cmd.equals("hint")) {
                int opps = Math.min(4, othersInHand(p));
                double e = equity(p, opps, 3000);
                println(String.format("Estimated chance to win against %d random hand%s: %.0f%%",
                        opps, opps == 1 ? "" : "s", e * 100));
            } else if (cmd.equals("q") || cmd.equals("quit")) {
                println("Thanks for playing! You leave with " + p.chips + " chips.");
                System.exit(0);
            } else if (cmd.equals("a") || cmd.equals("allin") || cmd.equals("all-in")) {
                return canRaise ? new Action(Type.RAISE, maxTo) : new Action(Type.CALL, 0);
            } else if (cmd.equals("r") || cmd.equals("raise") || cmd.equals("bet")) {
                if (!canRaise) {
                    println("You can't raise right now.");
                    continue;
                }
                String text = t.length > 1 ? t[1] : readLine("Total bet for this round: ");
                try {
                    int amt = Integer.parseInt(text.trim());
                    if (amt >= maxTo) return new Action(Type.RAISE, maxTo);
                    if (amt < minTo) {
                        println("The minimum is " + minTo + " (or go all-in with " + maxTo + ").");
                        continue;
                    }
                    return new Action(Type.RAISE, amt);
                } catch (NumberFormatException e) {
                    println("Please enter a whole number, e.g.  r " + minTo);
                }
            } else {
                println("Sorry, I didn't understand that.");
            }
        }
    }

    void printTable(Player me) {
        println("");
        println("Board: " + boardString() + "     Pot: " + pot());
        StringJoiner sj = new StringJoiner(" | ");
        for (int i = 0; i < players.size(); i++) {
            Player p = players.get(i);
            if (p.out) continue;
            String s = p.name + " " + p.chips;
            if (i == dealer) s += " [D]";
            if (p.folded) s += " (folded)";
            else if (p.allIn) s += " (all-in)";
            else if (p.roundBet > 0) s += " (bet " + p.roundBet + ")";
            sj.add(s);
        }
        println(sj.toString());
        println("Your cards: " + me.hole[0] + " " + me.hole[1]);
    }

    // ======================================================================
    //  Computer players
    //  Each bot estimates its winning chances by Monte Carlo simulation
    //  (random opponent hands + random remaining board cards), compares that
    //  to the pot odds, and adds a little randomness / personality.
    // ======================================================================
    Action aiAction(Player p) {
        int owed = currentBet - p.roundBet;
        int toCall = Math.min(owed, p.chips);
        int pot = pot();
        int opps = Math.min(4, othersInHand(p));
        double fair = 1.0 / (opps + 1);
        double strong = 0.40 + 0.60 * fair;           // equity needed to bet/raise for value
        double eq = equity(p, opps, 400) + p.looseness + (RNG.nextDouble() - 0.5) * 0.10;
        boolean canRaise = raisesThisRound < 3 && p.chips > owed && othersCanAct(p) > 0;
        boolean preflop = board.isEmpty();

        if (owed <= 0) {   // nothing to call: check or bet
            if (canRaise) {
                if (eq >= strong - 0.05 && RNG.nextDouble() < 0.45 + 0.40 * p.aggression)
                    return raiseTo(p, currentBet + betSize(pot));
                if (!preflop && RNG.nextDouble() < 0.06 * p.aggression)   // occasional bluff
                    return raiseTo(p, currentBet + betSize(pot));
            }
            return new Action(Type.CALL, 0);
        }

        // Facing a bet: bigger bets mean stronger opposing hands, so discount our equity.
        eq -= Math.min(0.20, 0.25 * toCall / (double) pot);
        double potOdds = (double) toCall / (pot + toCall);

        if (canRaise && eq >= strong && RNG.nextDouble() < 0.35 + 0.35 * p.aggression)
            return raiseTo(p, currentBet + Math.max(minRaise, betSize(pot)));
        if (eq >= potOdds + 0.03) return new Action(Type.CALL, 0);
        return new Action(Type.FOLD, 0);
    }

    int betSize(int pot) {
        int size = (int) (pot * (0.5 + 0.35 * RNG.nextDouble()));
        size = Math.max(size, board.isEmpty() ? 2 * bigBlind : bigBlind);
        return size / smallBlind * smallBlind;
    }

    /** Builds a legal raise to (about) the target total bet; goes all-in if the leftover stack would be tiny. */
    Action raiseTo(Player p, int target) {
        int maxTo = p.roundBet + p.chips;
        int minTo = currentBet + minRaise;
        int to = Math.min(Math.max(target, minTo), maxTo);
        if (maxTo - to < minRaise) to = maxTo;
        if (to <= currentBet) return new Action(Type.CALL, 0);
        return new Action(Type.RAISE, to);
    }

    /** Probability that p wins (ties count fractionally) against `opponents` random hands. */
    double equity(Player p, int opponents, int sims) {
        boolean[] used = new boolean[52];
        used[(p.hole[0].rank - 2) * 4 + p.hole[0].suit] = true;
        used[(p.hole[1].rank - 2) * 4 + p.hole[1].suit] = true;
        for (Card c : board) used[(c.rank - 2) * 4 + c.suit] = true;
        List<Card> rest = new ArrayList<>();
        for (int r = 2; r <= 14; r++)
            for (int s = 0; s < 4; s++)
                if (!used[(r - 2) * 4 + s]) rest.add(new Card(r, s));

        int need = (5 - board.size()) + 2 * opponents;
        double wins = 0;
        for (int sim = 0; sim < sims; sim++) {
            for (int i = 0; i < need; i++)
                Collections.swap(rest, i, i + RNG.nextInt(rest.size() - i));
            List<Card> full = new ArrayList<>(board);
            int pos = 0;
            while (full.size() < 5) full.add(rest.get(pos++));

            List<Card> mine = new ArrayList<>(full);
            mine.add(p.hole[0]);
            mine.add(p.hole[1]);
            int myScore = bestScore(mine);

            boolean lost = false;
            int ties = 0;
            for (int o = 0; o < opponents; o++) {
                List<Card> theirs = new ArrayList<>(full);
                theirs.add(rest.get(pos++));
                theirs.add(rest.get(pos++));
                int sc = bestScore(theirs);
                if (sc > myScore) {
                    lost = true;
                    break;
                }
                if (sc == myScore) ties++;
            }
            if (!lost) wins += 1.0 / (ties + 1);
        }
        return wins / sims;
    }

    // ======================================================================
    //  Showdown and pot distribution (including side pots)
    // ======================================================================
    void awardPots() {
        List<Player> live = new ArrayList<>();
        for (Player p : players) if (!p.folded) live.add(p);

        if (live.size() == 1) {
            Player w = live.get(0);
            int pot = pot();
            w.chips += pot;
            println("\n" + does(w, "win") + " " + pot + " (everyone else folded).");
            return;
        }

        println("\n=== SHOWDOWN ===      Board: " + boardString());
        Map<Player, Integer> score = new HashMap<>();
        for (Player p : live) {
            List<Card> seven = new ArrayList<>(board);
            seven.add(p.hole[0]);
            seven.add(p.hole[1]);
            int s = bestScore(seven);
            score.put(p, s);
            println(String.format("%-7s %s %s   %s", p.name + ":", p.hole[0], p.hole[1], describe(s)));
        }
        println("");

        // Every distinct all-in level among live players creates a pot layer.
        TreeSet<Integer> levels = new TreeSet<>();
        for (Player p : live) levels.add(p.totalBet);
        int prev = 0;
        int potCount = 0;
        for (int level : levels) {
            int amount = 0;
            for (Player p : players) amount += Math.min(p.totalBet, level) - Math.min(p.totalBet, prev);
            prev = level;
            if (amount == 0) continue;

            List<Player> eligible = new ArrayList<>();
            for (Player p : live) if (p.totalBet >= level) eligible.add(p);

            int best = -1;
            for (Player p : eligible) best = Math.max(best, score.get(p));

            // Winners listed starting left of the dealer, so odd chips go to the right people.
            List<Player> winners = new ArrayList<>();
            int i = dealer;
            for (int k = 0; k < players.size(); k++) {
                i = (i + 1) % players.size();
                Player p = players.get(i);
                if (eligible.contains(p) && score.get(p) == best) winners.add(p);
            }
            int share = amount / winners.size();
            int extra = amount % winners.size();
            StringJoiner names = new StringJoiner(" and ");
            for (Player w : winners) {
                w.chips += share + (extra-- > 0 ? 1 : 0);
                names.add(w.name);
            }
            String label = (potCount++ == 0) ? "Main pot" : "Side pot " + (potCount - 1);
            if (winners.size() == 1)
                println(label + " (" + amount + "): " + does(winners.get(0), "win") + " with " + describe(best) + ".");
            else
                println(label + " (" + amount + "): split between " + names + " (" + describe(best) + ").");
        }
    }

    // ======================================================================
    //  Helpers
    // ======================================================================
    int pot() {
        int total = 0;
        for (Player p : players) total += p.totalBet;
        return total;
    }

    int totalChips() {
        int total = 0;
        for (Player p : players) total += p.chips;
        return total;
    }

    int nextSeat(int from) {
        int n = players.size();
        for (int k = 1; k <= n; k++) {
            int i = (((from + k) % n) + n) % n;
            if (!players.get(i).out) return i;
        }
        return from;
    }

    int activeCount() {
        int c = 0;
        for (Player p : players) if (!p.out) c++;
        return c;
    }

    int countInHand() {
        int c = 0;
        for (Player p : players) if (!p.folded) c++;
        return c;
    }

    int countCanAct() {
        int c = 0;
        for (Player p : players) if (p.canAct()) c++;
        return c;
    }

    int othersInHand(Player me) {
        int c = 0;
        for (Player p : players) if (p != me && !p.folded) c++;
        return c;
    }

    int othersCanAct(Player me) {
        int c = 0;
        for (Player p : players) if (p != me && p.canAct()) c++;
        return c;
    }

    String boardString() {
        if (board.isEmpty()) return "--";
        StringJoiner sj = new StringJoiner(" ");
        for (Card c : board) sj.add(c.toString());
        return sj.toString();
    }

    /** "You fold" vs "Alice folds". */
    static String does(Player p, String verb) {
        return p.name + " " + (p.human ? verb : verb + "s");
    }

    static void println(String s) {
        System.out.println(s);
    }

    static void pause() {
        if (BOT_DELAY_MS <= 0) return;
        try {
            Thread.sleep(BOT_DELAY_MS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    static String readLine(String prompt) {
        System.out.print(prompt);
        System.out.flush();
        if (!IN.hasNextLine()) {
            System.out.println("\nInput closed. Goodbye!");
            System.exit(0);
        }
        return IN.nextLine().trim();
    }
}
