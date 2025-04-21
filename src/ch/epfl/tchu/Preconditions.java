/*
 *  Author:      Ahmed Kallala (315594)
 *  Date:        5 mars 2021
 */

package ch.epfl.tchu;

public final class Preconditions {
    private Preconditions() {
    }

    /**
     * Méthode qui lève une exception(IllegalArgumentException) si son
     * argument(shouldBeTrue) est faux
     * 
     * @param shouldBeTrue(boolean)
     *            l'argument à vérifier
     * @throws IllegalArgumentException
     *             si le paramètre shoulBeTrue n'est pas vrai
     */
    public static void checkArgument(boolean shouldBeTrue) {
        if (!shouldBeTrue) {
            throw new IllegalArgumentException();
        }
    }
}
