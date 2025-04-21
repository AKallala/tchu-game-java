/*
 *	Author:      Ahmed Kallala (315594)
 *	Date:        8 mars 2021
 */

package ch.epfl.tchu.game;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

public final class Deck<C extends Comparable<C>> {
    private final int size;
    private final List<C> cards;

    private Deck(int size, List<C> cards) {
        this.size = size;
        this.cards = cards;
    }

    /**
     * Méthode qui retourne un tas de cartes ayant les mêmes cartes que le
     * multiensemble cards, mélangées au moyen du générateur de nombres
     * aléatoires rng
     * 
     * @param <C>
     *            le type des cartes contenues dans le tas
     * @param cards(SortedBag<C>)
     *            l'ensemble des cartes qui composent le tas
     * @param rng(Random)
     *            le générateur de nombres aléatoires
     * @return un tas de cartes ayant les mêmes cartes que le multiensemble
     *         cards
     */
    public static <C extends Comparable<C>> Deck<C> of(SortedBag<C> cards,
            Random rng) {
        List<C> c = cards.toList();
        Collections.shuffle(c, rng);
        return new Deck<C>(cards.size(), c);
    }

    /**
     * @return size(int) la taille du tas
     */
    public int size() {
        return size;
    }

    /**
     * Méthode qui retourne vrai ssi le tas est vide
     * 
     * @return vrai si le tas est vide et faux sinon
     */
    public boolean isEmpty() {
        return cards.isEmpty();
    }

    /**
     * Méthode qui retourne la carte au sommet du tas
     * 
     * @return la carte au sommet du tas
     * @throws IllegalArgumentException si le tas est vide
     */
    public C topCard() {
        Preconditions.checkArgument(!isEmpty());
        return cards.get(0);
    }

    /**
     * Méthode qui retourne un tas identique au récepteur(this) mais sans la
     * carte au sommet
     * 
     * @return un tas identique au récepteur(this) mais sans la carte au sommet
     * @throws IllegalArgumentException si le tas(this) est vide
     */
    public Deck<C> withoutTopCard() {
        Preconditions.checkArgument(!isEmpty());
        return new Deck<C>(size - 1, cards.subList(1, size));
    }

    /**
     * Méthode qui retourne un multiensemble contenant les "count"(donné en
     * paramètre) cartes se trouvant au sommet du tas
     * 
     * count(int) le nombre de carte du sommet du tas qu'on veut avoir
     * 
     * @return un multiensemble composé des count cartes au sommet du tas
     * @throws IllegalArgumentException si count n'est pas compris entre 0
     *        (inclus) et la taille du tas (incluse)
     */
    public SortedBag<C> topCards(int count) {
        Preconditions.checkArgument(count >= 0 && count <= size);
        SortedBag.Builder<C> topCards = new SortedBag.Builder<>();
        for (int i = 0; i < count; ++i) {
            topCards.add(cards.get(i));
        }
        return topCards.build();
    }

    /**
     * Méthode qui retourne un tas identique au récepteur(this) mais sans les
     * les "count"(donné en paramètre) cartes du sommet
     * 
     * @param count(int)
     *            le nombre de carte du sommet du tas qu'on veut retirer du tas
     * @return un tas identique au récepteur(this) mais sans les count cartes du
     *         sommet
     * @throws IllegalArgumentException si count n'est pas compris entre
     *        0(inclus) et la taille du tas(incluse)
     */
    public Deck<C> withoutTopCards(int count) {
        Preconditions.checkArgument(!(count < 0 || count > size));
        return new Deck<C>(size - count, cards.subList(count, size));
    }

}
