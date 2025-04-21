/*
 *	Author:      Ahmed Kallala (315594)
 *	Date:        8 mars 2021
 */

package ch.epfl.tchu.gui;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.game.Trail;

public final class Info {

    private final String playerName;

    /**
     * Constructeur de Info qui construit un générateur de messages liés au
     * joueur dont le nom est donné en paramètre
     * 
     * @param playerName(String)
     *            le nom du jour auquel sont liés les messages
     */
    public Info(String playerName) {
        this.playerName = playerName;
    }

    /**
     * Méthode qui retourne le nom (français) de la carte donnée, au singulier
     * ssi la valeur absolue du second argument vaut 1
     * 
     * @param card(Card)
     *            la carte dont il faut retourner le nom
     * @param count(int)
     *            la multiplicité de la carte
     * @return
     */
    public static String cardName(Card card, int count) {
        switch (card) {
        case BLACK:
            return StringsFr.BLACK_CARD + StringsFr.plural(count);
        case WHITE:
            return StringsFr.WHITE_CARD + StringsFr.plural(count);
        case YELLOW:
            return StringsFr.YELLOW_CARD + StringsFr.plural(count);
        case VIOLET:
            return StringsFr.VIOLET_CARD + StringsFr.plural(count);
        case RED:
            return StringsFr.RED_CARD + StringsFr.plural(count);
        case GREEN:
            return StringsFr.GREEN_CARD + StringsFr.plural(count);
        case BLUE:
            return StringsFr.BLUE_CARD + StringsFr.plural(count);
        case ORANGE:
            return StringsFr.ORANGE_CARD + StringsFr.plural(count);
        case LOCOMOTIVE:
            return StringsFr.LOCOMOTIVE_CARD + StringsFr.plural(count);
        default:
            return "ERROR";
        }
    }

    private static String routeName(Route route) {
        String gare1 = route.station1().name();
        String gare2 = route.station2().name();
        return gare1 + StringsFr.EN_DASH_SEPARATOR + gare2;
    }

    private static String cardsNames(SortedBag<Card> cards) {
        List<String> cardsNames = new ArrayList<>();
        for (Card card : cards.toSet()) {
            cardsNames.add(String.format("%s %s", cards.countOf(card),
                    cardName(card, cards.countOf(card))));
        }
        String finalText = cardsNames.get(0);
        for (int i = 1; i < cardsNames.size() - 1; ++i) {
            finalText += (", " + cardsNames.get(i));
        }
        if (cardsNames.size() > 1) {
            finalText += (StringsFr.AND_SEPARATOR
                    + cardsNames.get(cardsNames.size() - 1));
        }
        return finalText;
    }

    /**
     * Méthode qui retourne le message déclarant que les joueurs, dont les noms
     * sont ceux donnés en paramètre, ont terminé la partie ex-æqo en ayant
     * chacun remporté les points donnés en paramètre
     * 
     * @param playerNames(List<Sring>)
     *            liste des noms des joueurs qui ont terminé ex-æqo
     * @param points(int)
     *            les points remportés par ces joueurs
     * @return le message déclarant que les joueurs ont terminé la partie ex-æqo
     */
    public static String draw(List<String> playerNames, int points) {
        String names = playerNames.get(0);
        for (int i = 1; i < playerNames.size(); ++i) {
            names += (" et " + playerNames.get(i));
        }
        return String.format(StringsFr.DRAW, names, points);
    }

    /**
     * Méthode qui retourne le message déclarant que le joueur jouera en premier
     * 
     * @return le message déclarant que le joueur jouera en premier
     */
    public String willPlayFirst() {
        return String.format(StringsFr.WILL_PLAY_FIRST, playerName);
    }

    /**
     * Méthode qui retourne le message déclarant que le joueur a gardé le nombre
     * de billets donné en paramètre
     * 
     * @param count(int)
     *            le nombre de billets gardés par le joueur
     * @return le message déclarant que le joueur a gardé le nombre de billets
     *         "count"
     */
    public String keptTickets(int count) {
        return String.format(StringsFr.KEPT_N_TICKETS, playerName, count,
                StringsFr.plural(count));
    }

    /**
     * Méthode qui retourne le message déclarant que le joueur peut jouer
     * 
     * @return le message déclarant que le joueur peut jouer
     */
    public String canPlay() {
        return String.format(StringsFr.CAN_PLAY, playerName);
    }

    /**
     * Méthode qui retourne le message déclarant que le joueur a tiré le nombre
     * de billets donné en paramètre(count)
     * 
     * @param count(int)
     *            le nombre de billets tirés par le joueur
     * @return le message déclarant que le joueur a tiré le nombre de billets
     *         "count"
     */
    public String drewTickets(int count) {
        return String.format(StringsFr.DREW_TICKETS, playerName, count,
                StringsFr.plural(count));

    }

    /**
     * Méthode qui retourne le message déclarant que le joueur a tiré une carte
     * « à l'aveugle », c-à-d du sommet de la pioche
     * 
     * @return le message déclarant que le joueur a tiré une carte « à
     *         l'aveugle »
     */
    public String drewBlindCard() {
        return String.format(StringsFr.DREW_BLIND_CARD, playerName);
    }

    /**
     * Méthode qui retourne le message déclarant que le joueur a tiré la carte
     * disposée face visible donnée en paramètre
     * 
     * @param card(Card)
     *            la carte disposée face visible tirée par le joueur
     * @return le message déclarant que le joueur a tiré la carte disposée face
     *         visible
     */
    public String drewVisibleCard(Card card) {
        return String.format(StringsFr.DREW_VISIBLE_CARD, playerName,
                cardName(card, 1));
    }

    /**
     * Méthode qui retourne le message déclarant que le joueur s'est emparé de
     * la route donnée en paramètre au moyen des cartes données en paramètre
     * 
     * @param route(Route)
     *            la route dont s'est emparé le joueur
     * @param cards(SortedBag<Card>)
     *            l'ensemble des cartes utilisées pour s'emparer de la route
     * @return le message déclarant que le joueur s'est emparé de la route
     */
    public String claimedRoute(Route route, SortedBag<Card> cards) {
        return String.format(StringsFr.CLAIMED_ROUTE, playerName,
                routeName(route), cardsNames(cards));
    }

    /**
     * Méthode qui retourne le message indiquant que le joueur désire s'emparer
     * de la route en tunnel donnée en paramètres en utilisant initialement les
     * cartes données en paramètre
     * 
     * @param route(Route)
     *            la route en tunnel dont désire s'emparer le joueur
     * @param initialCards(SortedBag<Card>)
     *            les cartes initialement utilisées par le joueur
     * @return le message indiquant que le joueur désire s'emparer de la route
     *         en tunnel
     */
    public String attemptsTunnelClaim(Route route,
            SortedBag<Card> initialCards) {
        return String.format(StringsFr.ATTEMPTS_TUNNEL_CLAIM, playerName,
                routeName(route), cardsNames(initialCards));
    }

    /**
     * Méthode qui retourne le message déclarant que le joueur a tiré les trois
     * cartes additionnelles données en paramètre, et qu'elles impliquent un
     * coût additionel du nombre de cartes donné en paramètre
     * 
     * @param drawnCards(SortedBag<Card>)
     *            les trois cartes additionnelles
     * @param additionalCost(int)
     *            le coût additionel du nombre de cartes
     * @return le message déclarant que le joueur a tiré les trois cartes
     *         additionnelles et qu'elles impliquent un coût additionel de
     *         nombre cartes
     */
    public String drewAdditionalCards(SortedBag<Card> drawnCards,
            int additionalCost) {
        if (additionalCost == 0) {
            return String.format(StringsFr.ADDITIONAL_CARDS_ARE,
                    cardsNames(drawnCards))
                    + String.format(StringsFr.NO_ADDITIONAL_COST);
        } else {
            return String.format(StringsFr.ADDITIONAL_CARDS_ARE,
                    cardsNames(drawnCards))
                    + String.format(StringsFr.SOME_ADDITIONAL_COST,
                            additionalCost, StringsFr.plural(additionalCost));
        }
    }

    /**
     * Méthode qui retourne le message déclarant que le joueur n'a pas pu (ou
     * voulu) s'emparer du tunnel donné en paramètre
     * 
     * @param route(Route)
     *            le tunnel dont le joueur n'a pas pu (ou voulu) s'emparer
     * @return le message déclarant que le joueur n'a pas pu (ou voulu)
     *         s'emparer du tunnel
     */
    public String didNotClaimRoute(Route route) {
        return String.format(StringsFr.DID_NOT_CLAIM_ROUTE, playerName,
                routeName(route));
    }

    /**
     * Méthode qui retourne le message déclarant que le joueur n'a plus que le
     * nombre donné en paramètre(inférieur ou égale à 2) de wagons, et que le
     * dernier tour commence donc
     * 
     * @param carCount(int)
     *            le nombre de wagons restants au joueur
     * @return le message déclarant que le joueur n'a plus que le nombre
     *         "carCount" de wagons, et que le dernier tour commence donc
     */
    public String lastTurnBegins(int carCount) {
        return String.format(StringsFr.LAST_TURN_BEGINS, playerName, carCount,
                StringsFr.plural(carCount));
    }

    /**
     * Méthode qui retourne le message déclarant que le joueur obtient le bonus
     * de fin de partie grâce au chemin donné en paramètre, qui est le plus
     * long, ou l'un des plus longs
     * 
     * @param longestTrail(Trail)
     *            le chemin grâce auquel le joueur obtient le bonus
     * @return le message déclarant que le joueur obtient le bonus de fin de
     *         partie grâce au chemin "longestTrail", qui est le plus long, ou
     *         l'un des plus longs
     */
    public String getsLongestTrailBonus(Trail longestTrail) {
        return String.format(StringsFr.GETS_BONUS, playerName,
                longestTrail.station1().name() + StringsFr.EN_DASH_SEPARATOR
                        + longestTrail.station2().name());
    }

    /**
     * Méthode qui retourne le message déclarant que le joueur remporte la
     * partie avec le nombre de points donnés en paramètre, son adversaire n'en
     * ayant obtenu que "loserPoints"(donnés en paramètre)
     * 
     * @param points(int)
     *            le nombre de points du joueur(gagnant)
     * @param loserPoints(int)
     *            le nombre de points de l'adversaire(perdant)
     * @return le message déclarant que le joueur remporte la partie avec le
     *         nombre de points "points" et que son adversaire n'en a obtenu que
     *         "loserPoints"
     */
    public String won(int points, int loserPoints) {
        return String.format(StringsFr.WINS, playerName, points,
                StringsFr.plural(points), loserPoints,
                StringsFr.plural(loserPoints));
    }

}
