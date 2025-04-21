/*
 *  Author:      Ahmed Kallala (315594)
 *  Date:        5 mars 2021
 */

package ch.epfl.tchu.game;

public interface StationConnectivity {

    /**
     * Méthode donnant l'état de la connectivité entre les deux gares entrées en paramètre(s1 et s2)
     * 
     * @param s1(Station) la première gare
     * @param s2(Station) la deuxième gare
     * @return l'état de la connectivité(boolean)
     */
    public boolean connected(Station s1, Station s2);

}
