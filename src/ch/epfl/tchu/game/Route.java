/*
 *  Author:      Ahmed Kallala (315594)
 *  Date:        3 mars 2021
 */

package ch.epfl.tchu.game;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

public final class Route {
    /**
     * Enumeration representant les deux niveaux selon lesquels peut etre
     * representee une route
     *
     */
    public enum Level {
        OVERGROUND, // (route en surface)
        UNDERGROUND; // (route en tunnel)
    }

    private final String id;
    private final Station station1;
    private final Station station2;
    private final int length;
    private final Level level;
    private final Color color;

    /**
     * Constructeur d'une Route
     * 
     * @param id(String)
     *            l'identité de la Route
     * @param station1(Station)
     *            la première gare de la Route
     * @param station2(Station)
     *            la dernière gare de la Route
     * @param length(int)
     *            la taille de la Route
     * @param level(Level)
     *            le niveau auquel se trouve la Route(en surface ou en tunnel)
     * @param color(Color)
     *            la couleur de la Route
     * @throws IllegalArgumentException
     *             si les deux stations passées en paramètre sont les mêmes ou
     *             si la taille passée en paramètre est inferieure(ou
     *             suppérieure) à la taille min(max) que peut prendre une route
     * @throws NullPointerException
     *             les l'un des paramètres id ou station1 ou station2 ou level
     *             est nul
     */
    public Route(String id, Station station1, Station station2, int length,
            Level level, Color color) {
        Preconditions.checkArgument(station1.id() != station2.id()
                && length >= Constants.MIN_ROUTE_LENGTH
                && length <= Constants.MAX_ROUTE_LENGTH);
        if (id == null || station1 == null || station2 == null
                || level == null) {
            throw new NullPointerException();
        }
        this.id = id;
        this.station1 = station1;
        this.station2 = station2;
        this.length = length;
        this.level = level;
        this.color = color;

    }

    /**
     * @return id(String) l'identité de la route
     */
    public String id() {
        return id;
    }

    /**
     * @return station1(Station) la première gare de la route
     */
    public Station station1() {
        return station1;
    }

    /**
     * @return station2(Station) la dernière gare de la route
     */
    public Station station2() {
        return station2;
    }

    /**
     * @return length(int) la taille de la route
     */
    public int length() {
        return length;
    }

    /**
     * @return level(Level) le niveau auquel se trouve la route
     */
    public Level level() {
        return level;
    }

    /**
     * @return color(Color) la couleur de la route ou null si la route est de
     *         couleur neutre
     */
    public Color color() {
        return color;
    }

    /**
     * @return une liste des deux gares de la route
     */
    public List<Station> stations() {
        return List.of(station1, station2);
    }

    /**
     * Méthode permettant d'avoir la gare se trouvant à l'opposé de l'une des
     * gares d'une route
     * 
     * @param station(Station)
     *            une gare
     * @return la gare opposée à station(la gare donnée en paramètre) dans la
     *         route
     * @throws IllegalArgumentException si la gare donnée en paramètre
     *        n'appartient pas à la route
     */
    public Station stationOpposite(Station station) {
        if (station == station1) {
            return station2;
        } else if (station == station2) {
            return station1;
        } else
            throw new IllegalArgumentException();
    }

    /**
     * Méthode qui permet d'avoir la liste triée(par ordre croissant de nombre
     * de cartes locomotive, puis par couleur) de tous les ensembles de cartes
     * qui pourraient être joués pour (tenter de) s'emparer de la route
     * 
     * @return possibleClaimCards(List<SortedBag<Card>>) la liste de tous les
     *         ensembles de cartes qui pourraient être joués pour (tenter de)
     *         s'emparer de la route
     */
    public List<SortedBag<Card>> possibleClaimCards() {
        List<SortedBag<Card>> possibleClaimCardsUnderground = new ArrayList<>();
        List<SortedBag<Card>> possibleClaimCardsOverground = new ArrayList<>();
        for (int i = 0; i < length; ++i) {
            if (color == null) {
                for (int j = 0; j < Color.COUNT; ++j) {
                    possibleClaimCardsUnderground.add(SortedBag.of(length - i,
                            Card.CARS.get(j), i, Card.LOCOMOTIVE));
                }
            } else {
                possibleClaimCardsUnderground.add(SortedBag.of(length - i,
                        Card.of(color), i, Card.LOCOMOTIVE));
            }
        }

        for (int j = 0; j < Color.COUNT; ++j) {
            possibleClaimCardsOverground
                    .add(SortedBag.of(length, Card.CARS.get(j)));
        }

        possibleClaimCardsUnderground
                .add(SortedBag.of(length, Card.LOCOMOTIVE));
        if (level == Level.UNDERGROUND) {
            return possibleClaimCardsUnderground;
        } else if (color == null) {
            return possibleClaimCardsOverground;
        } else {
            return List
                    .of(SortedBag.of(length, Card.CARS.get(color.ordinal())));
        }
    }

    /**
     * Méthode qui détermine le nombre de cartes additionnelles à jouer pour
     * s'emparer de la route (en tunnel), en fonction des cartes que le joueur a
     * initialement posé(claimCards) et des trois cartes tirées du sommet de la
     * pioche(drawnCards)
     * 
     * @param claimCards(SortedBag<Card>)
     *            représente l'ensemble des cartes initialement posées par le
     *            joueur
     * @param drawnCards(SortedBag<Card>)
     *            représente les trois cartes tirées du sommet de la pioche par
     *            le joueur
     * @return n le nombre de cartes additionnelles à jouer
     * @throws IllegalArgumentException si la route n'est pas en tunnel ou si la
     *        taille de drawnCards n'est pas 3
     */
    public int additionalClaimCardsCount(SortedBag<Card> claimCards,
            SortedBag<Card> drawnCards) {
        Preconditions.checkArgument(
                level == Level.UNDERGROUND && drawnCards.size() == 3);
        int n = 0;
        for (int i = 0; i < drawnCards.size(); ++i) {
            if (claimCards.contains(drawnCards.get(i))
                    || drawnCards.get(i) == Card.LOCOMOTIVE) {
                ++n;
            }
        }
        return n;
    }

    /**
     * @return le nombre de points de construction qu'un joueur obtient
     *         lorsqu'il s'empare de la route
     */
    public int claimPoints() {
        return Constants.ROUTE_CLAIM_POINTS.get(length);
    }
}
