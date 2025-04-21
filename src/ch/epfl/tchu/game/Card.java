/*
 *  Author:      Ahmed Kallala (315594)
 *  Date:        5 mars 2021
 */

package ch.epfl.tchu.game;

import java.util.List;

/**
 * Enumération représentant les différents types de cartes utilisées dans le jeu
 *
 */
public enum Card {
    BLACK(Color.BLACK), // wagon noir
    VIOLET(Color.VIOLET), // wagon violet
    BLUE(Color.BLUE), // wagon bleu
    GREEN(Color.GREEN), // wagon vert
    YELLOW(Color.YELLOW), // wagon jaune
    ORANGE(Color.ORANGE), // wagon orange
    RED(Color.RED), // wagon rouge
    WHITE(Color.WHITE), // wagon blanc
    LOCOMOTIVE(null); // locomotive

    public final static List<Card> ALL = List.of(Card.values()); // liste de tous les types de cartes
    public final static int COUNT = ALL.size(); // taille de la liste ALL
    public final static List<Card> CARS = List.of(Card.BLACK, Card.VIOLET,
            Card.BLUE, Card.GREEN, Card.YELLOW, Card.ORANGE, Card.RED,
            Card.WHITE); // liste de toutes les cartes de type wagon
    private final Color color;

    private Card(Color color) {
        this.color = color;
    }

    /**
     * Méthode retournant la couleur de la carte à laquelle elle est appliquée
     * 
     * @return color(Color), la couleur de la carte
     */
    public Color color() {
        return color;
    }

    /**
     * Méthode qui retourne le type de carte correspondant à la couleur donnée en paramètre
     * 
     * @param color(Color) la couleur de la carte à retourner 
     * @return la carte associée à color
     */
    public static Card of(Color color) {
        return CARS.get(color.ordinal());
    }
}
