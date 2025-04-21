/*
 *	Author:      Ahmed Kallala (315594)
 *	Date:        14 mars 2021
 */

package ch.epfl.tchu.game;

import java.util.List;

/**
 * Enumération représentant les identifiants des deux joueurs
 */
public enum PlayerId {
    PLAYER_1, // représente l'identité du joueur 1
    PLAYER_2; // représente l'identité du joueur 2

    public final static List<PlayerId> ALL = List.of(PlayerId.values()); // liste
                                                                         // des
                                                                         // deux
                                                                         // identifiants
    public final static int COUNT = ALL.size(); // taille de la liste ALL

    /**
     * Méthode qui retourne l'identité du joueur qui suit celui auquel on
     * l'applique(PLAYER_2 pour PLAYER_1, et PLAYER_1 pour PLAYER_2).
     * 
     * @return PLAYER_2 si on l'applique à PLAYER_1, et PLAYER_1 si on
     *         l'applique à PLAYER_2
     */
    public PlayerId next() {
        if (this.equals(PLAYER_1)) {
            return PLAYER_2;
        } else {
            return PLAYER_1;
        }
    }

}
