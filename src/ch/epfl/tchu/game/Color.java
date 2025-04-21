/*
 *  Author:      Ahmed Kallala (315594)
 *  Date:        5 mars 2021
 */

package ch.epfl.tchu.game;

import java.util.List;

/**
 * Enumération représentant les différentes couleurs utilisées dans le jeu
 *
 */
public enum Color {
    BLACK, VIOLET, BLUE, GREEN, YELLOW, ORANGE, RED, WHITE;

    public final static List<Color> ALL = List.of(Color.values()); //liste de toutes les couleurs
    public final static int COUNT = ALL.size(); //taille de la liste ALL
}
