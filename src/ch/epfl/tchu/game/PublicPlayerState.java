/*
 *	Author:      Ahmed Kallala (315594)
 *	Date:        14 mars 2021
 */

package ch.epfl.tchu.game;

import java.util.List;

import ch.epfl.tchu.Preconditions;

public class PublicPlayerState {
    private final int cardCount;
    private final int ticketCount;
    private final List<Route> routes;
    private final int claimPoints;
    private final int carCount;

    /**
     * Constructeur de PublicPlayerState
     * 
     * @param ticketCount(int)
     *            nombre de billets que possède le joueur
     * @param cardCount(int)
     *            nombre de cartes que possède le joueur
     * @param routes(List<Route>)
     *            les routes dont s'est emparé le joueur
     * @throws IllegalArgumentException
     *             si le nombre de billets(ticketCount) ou le nombre de
     *             cartes(cardCount) est strictement négatif(< 0).
     */
    public PublicPlayerState(int ticketCount, int cardCount,
            List<Route> routes) {
        Preconditions.checkArgument(cardCount >= 0 && ticketCount >= 0);
        int carCount = Constants.INITIAL_CAR_COUNT;
        int claimPoints = 0;
        for (Route route : routes) {
            carCount -= route.length();
            claimPoints += route.claimPoints();
        }
        this.ticketCount = ticketCount;
        this.cardCount = cardCount;
        this.routes = List.copyOf(routes);
        this.carCount = carCount;
        this.claimPoints = claimPoints;
    }

    /**
     * @return ticketCount(int) le nombre de billets que possède le joueur
     */
    public int ticketCount() {
        return ticketCount;
    }

    /**
     * @return cardCount(int) retourne le nombre de cartes que possède le joueur
     */
    public int cardCount() {
        return cardCount;
    }

    /**
     * @return routes(List<Route>) les routes dont le joueur s'est emparé
     */
    public List<Route> routes() {
        return routes;
    }

    /**
     * @return carCount(int) le nombre de wagons que possède le joueur
     */
    public int carCount() {
        return carCount;
    }

    /**
     * @return claimPoints(int) le nombre de points de construction obtenus par
     *         le joueur
     */
    public int claimPoints() {
        return claimPoints;
    }
}
