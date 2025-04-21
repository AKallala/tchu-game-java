/*
 *  Author:      Ahmed Kallala (315594)
 *  Date:        5 mars 2021
 */

package ch.epfl.tchu.game;

import java.util.List;
import java.util.TreeSet;

import ch.epfl.tchu.Preconditions;

public final class Ticket implements Comparable<Ticket> {
    private final List<Trip> trips;
    private final String ticketText;
    private final Station from;

    /**
     * Constructeur de Ticket qui construit un billet constitué de plusieurs
     * trajets
     * 
     * @param trips(List<Trip>)
     *            une liste de trajets
     */
    public Ticket(List<Trip> trips) {
        Preconditions.checkArgument(!trips.isEmpty());
        this.from = trips.get(0).from();
        for (Trip trip : trips) {
            Preconditions.checkArgument(trip.from().name().equals(from.name()));
        }
        this.trips = trips;
        ticketText = computeText(trips);
    }

    /**
     * Constructeur de Ticket qui construit un billet constitué d'un seul trajet
     * 
     * @param from(Station)
     *            la gare de départ
     * @param to(Station)
     *            la gare d'arrivée
     * @param points(int)
     *            le nombre de point que représente le trajet
     */
    public Ticket(Station from, Station to, int points) {
        this(List.of(new Trip(from, to, points)));

    }

    /**
     * Méthode qui retourne la représentation textuelle du billet
     * 
     * @return tiketText(String) la représentation textuelle du billet
     */
    public String text() {
        return ticketText;
    }

    /**
     * Méthode qui retourne le nombre de points que vaut le billet
     * 
     * @param connectivity(StationConnectivity)
     *            la connectivité du joueur possédant le billet
     * @return le nombre de points que vaut le billet en fonction de la
     *         connectivité
     */
    public int points(StationConnectivity connectivity) {
        int points = Integer.MIN_VALUE;
        for (Trip trip : trips) {
            if (trip.points(connectivity) > points) {
                points = trip.points(connectivity);
            }
        }
        return points;
    }

    private static String computeText(List<Trip> trips) {
        TreeSet<String> to = new TreeSet<>();
        for (Trip trip : trips) {
            to.add(String.format("%s (%s)", trip.to().name(), trip.points()));
        }
        if (trips.size() > 1) {
            return String.format("%s - {%s}", trips.get(0).from().name(),
                    String.join(", ", to));
        } else {
            return String.format("%s - %s", trips.get(0).from().name(),
                    String.join("", to));
        }

    }

    @Override
    public int compareTo(Ticket o) {
        return text().compareTo(o.text());
    }

    @Override
    public String toString() {
        return text();
    }
}
