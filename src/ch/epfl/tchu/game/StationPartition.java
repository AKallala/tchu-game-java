/*
 *	Author:      Ahmed Kallala (315594)
 *	Date:        14 mars 2021
 */

package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

public final class StationPartition implements StationConnectivity {
    private final int[] partition;

    private StationPartition(int[] partition) {
        this.partition = partition;
    }

    @Override
    public boolean connected(Station s1, Station s2) {
        if (s1.id() >= partition.length || s2.id() >= partition.length) {
            return (s1.id() == s2.id());
        } else {
            return (partition[s1.id()] == partition[s2.id()]);
        }
    }

    public static final class Builder {
        private int[] partition;

        /**
         * Constructeur de Builder qui construit un bâtisseur de partition d'un
         * ensemble de gares dont l'identité est comprise entre 0(inclus) et
         * stationCount(exclus)
         * 
         * @param stationCount
         *            l'identité max des gares dans la partition à construire(la
         *            borne suppérieure de la partition)
         * @throws IllegalArgumentException si stationCount est strictement
         *        négatif(< 0)
         */
        public Builder(int stationCount) {
            Preconditions.checkArgument(stationCount >= 0);
            partition = new int[stationCount];
            for (int i = 0; i < partition.length; ++i) {
                partition[i] = i;
            }
        }

        /**
         * Méthode qui joint les sous-ensembles contenant les deux gares passées
         * en argument, en « élisant » l'un des deux représentants comme
         * représentant du sous-ensemble joint 
         * 
         * @param s1(Station) la première des deux gares à joindre
         * @param s2(Station) la deuxième des deux gares à joindre
         * @return le bâtisseur(this)
         */
        public Builder connect(Station s1, Station s2) {
            partition[representative(s2.id())] = partition[s1.id()];
            return this;
        }

        /**
         * Méthode qui retourne la partition aplatie des gares correspondant à
         * la partition profonde en cours de construction par ce bâtisseur
         * 
         * @return la partition aplatie des gares correspondant à la partition
         *         profonde en cours de construction
         */
        public StationPartition build() {
            for (int i = 0; i < partition.length; ++i) {
                partition[i] = representative(i);
            }
            return new StationPartition(partition);
        }

        private int representative(int i) {
            int j = i;
            while (j != partition[j]) {
                j = partition[j];
            }
            return j;
        }
    }
}
