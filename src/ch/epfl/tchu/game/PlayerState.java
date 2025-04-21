/*
 *	Author:      Ahmed Kallala (315594)
 *	Date:        14 mars 2021
 */

package ch.epfl.tchu.game;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

public final class PlayerState extends PublicPlayerState {
    private final SortedBag<Ticket> tickets;
    private final SortedBag<Card> cards;

    /**
     * Constructeur de PlayerState
     * 
     * @param tickets(SortedBag<Ticket>)
     *            les billets que possède le joueur
     * @param cards(SortedBag<Card>)
     *            les cartes que possède le joueur
     * @param routes(List<Route>)
     *            les routes dont s'est emparé le joueur
     */
    public PlayerState(SortedBag<Ticket> tickets, SortedBag<Card> cards,
            List<Route> routes) {
        super(tickets.size(), cards.size(), routes);
        this.tickets = SortedBag.of(tickets);
        this.cards = SortedBag.of(cards);
    }

    /**
     * Méthode qui retourne l'état initial d'un joueur auquel les cartes
     * initiales données en paramètre ont été distribuées
     * 
     * @param initialCards(SortedBag<Card>)
     *            les cartes initiales distribuées au joueur
     * @return l'état initial d'un joueur
     * @throws IllegalArgumentException
     *             si le nombre de cartes initiales ne vaut pas 4
     */
    public static PlayerState initial(SortedBag<Card> initialCards) {
        Preconditions.checkArgument(
                initialCards.size() == Constants.INITIAL_CARDS_COUNT);
        return new PlayerState(SortedBag.of(), initialCards, List.of());
    }

    /**
     * @return tickets(SortedBag<Ticket>) les billets que possède le joueur
     */
    public SortedBag<Ticket> tickets() {
        return tickets;
    }

    /**
     * Méthode qui retourne un état identique au récepteur, si ce n'est que le
     * joueur possède en plus les billets donnés en paramètre
     * 
     * @param newTickets(SortedBag<Ticket>)
     *            les billets ajoutés à ceux du joueur
     * @return un état identique au récepteur avec les billets "newTickets"
     *         ajoutés aux billets du joueur
     */
    public PlayerState withAddedTickets(SortedBag<Ticket> newTickets) {
        return new PlayerState(tickets.union(newTickets), cards, routes());
    }

    /**
     * @return cards(SortedBag<Card>) les cartes wagon/locomotive du joueur
     */
    public SortedBag<Card> cards() {
        return cards;
    }

    /**
     * Méthode qui retourne un état identique au récepteur, si ce n'est que le
     * joueur possède en plus la carte donnée en paramètre
     * 
     * @param card(Card)
     *            la carte ajoutée aux cartes du joueur
     * @return un état identique au récepteur, si ce n'est que le joueur possède
     *         en plus la carte "card"
     */
    public PlayerState withAddedCard(Card card) {
        return new PlayerState(tickets, cards.union(SortedBag.of(card)),
                routes());
    }

    /**
     * Méthode qui retourne vrai ssi le joueur peut s'emparer de la route donnée
     * en paramètre, c-à-d s'il lui reste assez de wagons et s'il possède les
     * cartes nécessaires
     * 
     * @param route(Route)
     *            la route dont le joueur peut s'emparer ou pas
     * @return vrai ssi le joueur peut s'emparer de la route "route" et faux
     *         sinon
     */
    public boolean canClaimRoute(Route route) {
        if (route.length() > carCount()) {
            return false;
        } else if (possibleClaimCards(route).isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Méthode qui retourne la liste de tous les ensembles de cartes que le
     * joueur pourrait utiliser pour prendre possession de la route donnée en
     * paramètre
     * 
     * @param route(Route)
     *            la route dont dont il faut retourner la liste de tous les
     *            ensembles de cartes que le joueur pourrait utiliser pour s'en
     *            emparer
     * @return possibleClaimCards(List<SotedBag<Card>>) la liste de tous les
     *         ensembles de cartes que le joueur pourrait utiliser pour prendre
     *         possession de "route"
     * @throws IllegalArgumentException
     *             si le joueur n'a pas assez de wagons pour s'emparer de la
     *             route
     */
    public List<SortedBag<Card>> possibleClaimCards(Route route) {
        Preconditions.checkArgument(route.length() <= carCount());
        List<SortedBag<Card>> possibleClaimCards = new ArrayList<>();
        for (SortedBag<Card> c : route.possibleClaimCards()) {
            if (cards.contains(c)) {
                possibleClaimCards.add(c);
            }
        }
        return possibleClaimCards;
    }

    /**
     * Méthode qui retourne la liste de tous les ensembles de cartes que le
     * joueur pourrait utiliser pour s'emparer d'un tunnel, trié par ordre
     * croissant du nombre de cartes locomotives, sachant qu'il a initialement
     * posé les cartes "initialCards", que les 3 cartes tirées du sommet de la
     * pioche sont "drawnCards", et que ces dernières forcent le joueur à poser
     * encore "additionalCardsCount" cartes 
     * 
     * @param additionalCardsCount(int)
     *            le nombre additionnel de cartes à jouer pour s'emparer d'un
     *            tunnel
     * @param initialCards(SortedBag<Card>)
     *            les cartes initialement posées pour s'emparer d'un tunnel
     * @param drawnCards(SortedBag<Card>)
     *            les 3 cartes piochées
     * @return possibleAdditionalCards(List<SortedBag<Card>>) la liste de tous
     *         les ensembles de cartes additionnelles que le joueur pourrait
     *         utiliser pour s'emparer d'un tunnel
     * @throws IllegalArgumentException
     *             si le nombre de cartes additionnelles n'est pas compris entre
     *             1 et 3(inclus), si l'ensemble des cartes initiales est vide
     *             ou contient plus de 2 types de cartes différents, ou si
     *             l'ensemble des cartes tirées ne contient pas exactement 3
     *             cartes
     */
    public List<SortedBag<Card>> possibleAdditionalCards(
            int additionalCardsCount, SortedBag<Card> initialCards) {
        Preconditions.checkArgument(
                additionalCardsCount <= Constants.ADDITIONAL_TUNNEL_CARDS
                        && additionalCardsCount >= 1);
        Preconditions.checkArgument(
                !(initialCards.isEmpty()) && initialCards.toSet().size() <= 2);
        SortedBag.Builder<Card> builderOfAdditionalCards = new SortedBag.Builder<>();
        for (Card c : cards.difference(initialCards)) {
            if (initialCards.contains(c) || c == Card.LOCOMOTIVE) {
                builderOfAdditionalCards.add(c);
            }
        }
        List<SortedBag<Card>> possibleAdditionalCards = new ArrayList<>();
        if (builderOfAdditionalCards.build().size() >= additionalCardsCount) {
            possibleAdditionalCards.addAll(builderOfAdditionalCards.build()
                    .subsetsOfSize(additionalCardsCount));
            possibleAdditionalCards.sort(
                    Comparator.comparingInt(cs -> cs.countOf(Card.LOCOMOTIVE)));
        }
        return possibleAdditionalCards;
    }

    /**
     * Méthode qui retourne un état identique au récepteur, si ce n'est que le
     * joueur s'est de plus emparé de la route donnée en paramètre au moyen des
     * cartes données en paramètre
     * 
     * @param route(Route)
     *            dont le joueur s'est emparé
     * @param claimCards(SortedBag<Card>)
     *            les cartes utilisées pour s'emparer de "route"
     * @return un état identique au récepteur, si ce n'est que le joueur s'est
     *         de plus emparé de "route"
     */
    public PlayerState withClaimedRoute(Route route,
            SortedBag<Card> claimCards) {
        List<Route> newRoutes = new ArrayList<>();
        newRoutes.addAll(routes());
        newRoutes.add(route);
        return new PlayerState(tickets, cards.difference(claimCards),
                newRoutes);
    }

    /**
     * Méthode qui retourne le nombre de points obtenus par le joueur grâce à
     * ses billets
     * 
     * @return ticketPoints(int) le nombre de points obtenus par le joueur grâce
     *         à ses billets
     */
    public int ticketPoints() {
        int n = 0;
        int ticketPoints = 0;
        for (Route route : routes()) {
            if (Math.max(route.station1().id(), route.station2().id()) > n) {
                n = Math.max(route.station1().id(), route.station2().id());
            }
        }
        StationPartition.Builder partitionBuilder = new StationPartition.Builder(
                n + 1);
        for (Route route : routes()) {
            partitionBuilder.connect(route.station1(), route.station2());
        }
        StationPartition partition = partitionBuilder.build();
        for (Ticket ticket : tickets) {
            ticketPoints += ticket.points(partition);
        }
        return ticketPoints;
    }

    /**
     * Méthode qui retourne la totalité des points obtenus par le joueur à la
     * fin de la partie
     * 
     * @return la totalité des points obtenus par le joueur à la fin de la
     *         partie
     */
    public int finalPoints() {
        return claimPoints() + ticketPoints();
    }
}
