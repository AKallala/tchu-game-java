/*
 *  Author:      Ahmed Kallala (315594)
 *  Date:        5 mars 2021
 */

package ch.epfl.tchu.game;

import java.util.ArrayList;
import java.util.List;

public final class Trail {
    private final int length;
    private final Station station1;
    private final Station station2;
    private final List<Route> routes;

    private Trail(List<Route> routes, int length, Station station1,
            Station station2) {
        this.length = length;
        this.station1 = station1;
        this.station2 = station2;
        this.routes = routes;
    }

    /**
     * Méthode permettant de trouver le plus long chemin qu'il est possible de
     * former à partir des routes données en paramètre
     * 
     * @param routes(List<Route>)
     *            une liste de routes
     * @return longest(Trail) le plus long chemin
     */
    public static Trail longest(List<Route> routes) {
        if (routes.isEmpty()) {
            return new Trail(null, 0, null, null);
        }
        List<Trail> chemins = new ArrayList<>();
        List<Trail> chemins2 = new ArrayList<>();
        Trail longest = new Trail(null, 0, null, null);
        for (Route route : routes) {

            chemins.add(new Trail(List.of(route), route.length(),
                    route.station1(), route.station2()));
            chemins.add(new Trail(List.of(route), route.length(),
                    route.station2(), route.station1()));

        }

        while (!chemins.isEmpty()) {
            chemins2.clear();
            for (Trail trail : chemins) {
                if (trail.length() > longest.length()) {
                    longest = new Trail(trail.routes, trail.length(),
                            trail.station1(), trail.station2());
                }
                List<Route> routes2 = new ArrayList<>();
                for (Route r : routes) {
                    if (!(trail.routes.contains(r)) && ((trail.station2()
                            .name() == r.station1().name()
                            && trail.station2().id() == r.station1().id())
                            || (trail.station2().name() == r.station2().name()
                                    && trail.station2().id() == r.station2()
                                            .id()))) {

                        routes2.add(r);
                    }
                }
                for (Route r2 : routes2) {

                    List<Route> newRoutesOfTrail = new ArrayList<>();
                    newRoutesOfTrail.addAll(trail.routes);
                    newRoutesOfTrail.add(r2);
                    chemins2.add(new Trail(newRoutesOfTrail,
                            trail.length() + r2.length(), trail.station1(),
                            r2.stationOpposite(trail.station2())));

                }
            }
            chemins = List.copyOf(chemins2);

        }
        return longest;

    }

    /**
     * @return length(int) la taille du chemin
     */
    public int length() {
        return length;
    }

    /**
     * @return station1(Station) la première gare du chemin ou null si le chemin
     *         est de longueur 0
     */
    public Station station1() {
        if (length == 0) {
            return null;
        } else {
            return station1;
        }

    }

    /**
     * @return station2(Station) la dernière gare du chemin ou null si le chemin
     *         est de longueur 0
     */
    public Station station2() {
        if (length == 0) {
            return null;
        } else {
            return station2;
        }
    }

    @Override
    public String toString() {
        if (length == 0) {
            return "Vide";
        } else {
            return String.format("%s - %s (%s)", station1.name(),
                    station2.name(), length);
        }
    }
}
