/*
 *  Author:      Ahmed Kallala (315594)
 *  Date:        5 mars 2021
 */

package ch.epfl.tchu.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ch.epfl.tchu.Preconditions;

public final class Trip {
    private final Station from;
    private final Station to;
    private final int points;

    /**
     * Constructeur de Trip
     * 
     * @param from(Station)
     *            gare de départ du trajet
     * @param to(Station)
     *            gare d'arrivée du trajet
     * @param points(int)
     *            le nombre de points que vaut le trajet
     */
    public Trip(Station from, Station to, int points) {
        Preconditions.checkArgument(points > 0);
        this.from = Objects.requireNonNull(from);
        this.to = Objects.requireNonNull(to);
        this.points = points;
    }

    /**
     * Méthode qui retourne la liste de tous les trajets possibles allant d'une
     * des gares de la première liste (from) à l'une des gares de la seconde
     * liste (to) chacun valant le nombre de points donné
     * 
     * @param from(List<Station>)
     *            liste des gares de départ
     * @param to(List<Station>)
     *            liste des gares d'arrivée
     * @param points(int)
     *            nombre de points que valent les trajet
     * @return all(List<Trip>) la liste de tous les trajets possibles à partir
     *         des listes to et from
     */
    public static List<Trip> all(List<Station> from, List<Station> to,
            int points) {
        Preconditions
                .checkArgument(!from.isEmpty() && !to.isEmpty() && points >= 0);
        List<Trip> all = new ArrayList<Trip>();
        for (Station s1 : from) {
            for (Station s2 : to) {
                all.add(new Trip(s1, s2, points));
            }
        }
        return all;
    }

    /**
     * @return from(Station) la gare de départ du trajet
     */
    public Station from() {
        return from;
    }

    /**
     * @return to(Station) la gare d'arrivée du trajet
     */
    public Station to() {
        return to;
    }

    /**
     * @return points(int) le nombre de point que vaut le trajet
     */
    public int points() {
        return points;
    }

    /**
     * Méthode qui retourne le nombre de points que vaut le billet en fonction
     * de la connectivité donnée
     * 
     * @param connectivity(StationConnectivity)
     *            la connectivité du joueur
     * @return le nombre de points en fonction de la valeur de connectivity
     */
    public int points(StationConnectivity connectivity) {
        if (!connectivity.connected(from, to)) {
            return -points;
        } else
            return points;
    }

}
