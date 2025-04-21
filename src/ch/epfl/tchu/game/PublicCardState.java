/*
 *	Author:      Ahmed Kallala (315594)
 *	Date:        8 mars 2021
 */

package ch.epfl.tchu.game;

import java.util.List;
import java.util.Objects;

import ch.epfl.tchu.Preconditions;

public class PublicCardState {
    private final List<Card> faceUpCards;
    private final int deckSize;
    private final int discardsSize;

    /**
     * Constructeur de PublicCardState
     * 
     * @param faceUpCards(List<Card>)
     *            la liste des cartes face visible
     * @param deckSize(int)
     *            la taille de la pioche
     * @param discardsSize(int)
     *            la taille de la défausse
     * @throws IllegalArgumentException si faceUpCards ne contient pas le bon
     *        nombre d'éléments(5), ou si la taille de la pioche ou de la
     *        défausse sont négatives(< 0)
     */
    public PublicCardState(List<Card> faceUpCards, int deckSize,
            int discardsSize) {
        Preconditions.checkArgument(
                faceUpCards.size() == Constants.FACE_UP_CARDS_COUNT
                        && deckSize >= 0 && discardsSize >= 0);
        this.faceUpCards = List.copyOf(faceUpCards);
        this.deckSize = deckSize;
        this.discardsSize = discardsSize;
    }

    /**
     * @return faceUpCards(List<Card>) la liste des 5 cartes visibles
     */
    public List<Card> faceUpCards() {
        return faceUpCards;
    }

    /**
     * Méthode qui retourne la carte face visible à l'index donné en paramètre
     * 
     * @param slot(int)
     *            l'index de la carte face visible voulue
     * @return la carte face visible à l'index "slot"
     * @throw IndexOutOfBoundsException si cet index n'est pas compris entre
     *        0(inclus) et 5(exclus)
     */
    public Card faceUpCard(int slot) {
        return faceUpCards.get(Objects.checkIndex(slot, faceUpCards.size()));
    }

    /**
     * @return deckSize(int) la taille de la pioche
     */
    public int deckSize() {
        return deckSize;
    }

    /**
     * Méthode qui retourne vrai ssi la pioche est vide
     * 
     * @return vrai si la pioche est vide et faux sinon
     */
    public boolean isDeckEmpty() {
        return (deckSize == 0);
    }

    /**
     * @return discardsSize(int) la taille de la défausse
     */
    public int discardsSize() {
        return discardsSize;
    }

}
