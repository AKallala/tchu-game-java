/*
 *  Author:      Ahmed Kallala (315594)
 *  Date:        5 mars 2021
 */

package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

public final class Station {
    private final int id;
    private final String name;

    /**
     * Constructeur de Station
     * 
     * @param id(int)
     *            identificateur de la gare(Station)
     * @param name(String)
     *            nom de la gare(Station)
     */
    public Station(int id, String name) {
        Preconditions.checkArgument(id >= 0);
        this.name = name;
        this.id = id;
    }

    /**
     * @return id(int) l'identificateur de la gare
     */
    public int id() {
        return id;
    }

    /**
     * @return name(String) le nom de la gare
     */
    public String name() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
