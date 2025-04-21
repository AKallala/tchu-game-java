/*
 *	Author:      Ahmed Kallala (315594)
 *	Date:        8 mars 2021
 */

package ch.epfl.tchu.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

public final class CardState extends PublicCardState {
    private final Deck<Card> deck;
    private final SortedBag<Card> discards;

    private CardState(List<Card> faceUpCards, Deck<Card> deck,
            SortedBag<Card> discards) {
        super(faceUpCards, deck.size(), discards.size());
        this.deck = deck;
        this.discards = discards;
    }

    /**
     * Méthode qui retourne un état dans lequel les 5 cartes disposées faces
     * visibles sont les 5 premières du tas donné en paramètre, la pioche est
     * constituée des cartes du tas restantes, et la défausse est vide
     * 
     * @param deck(Deck<Card>)
     *            un tas de cartes
     * @return un état dans lequel les 5 cartes disposées faces visibles sont
     *         les 5 premières de deck, la pioche est constituée des cartes du
     *         tas restantes, et la défausse est vide
     * @throws IllegalArgumentException si le tas donné contient moins de 5
     *        cartes
     */
    public static CardState of(Deck<Card> deck) {
        Preconditions
                .checkArgument(!(deck.size() < Constants.FACE_UP_CARDS_COUNT));
        return new CardState(
                deck.topCards(Constants.FACE_UP_CARDS_COUNT).toList(),
                deck.withoutTopCards(Constants.FACE_UP_CARDS_COUNT),
                SortedBag.of());
    }

    /**
     * Méthode qui retourne un ensemble de cartes identique au récepteur(this),
     * si ce n'est que la carte face visible d'index slot a été remplacée par
     * celle se trouvant au sommet de la pioche, qui en est du même coup retirée
     * 
     * @param slot(int)
     *            l'index de la carte face visible qui va être remplacée
     * @return un ensemble de cartes identique au récepteur(this), si ce n'est
     *         que la carte face visible d'index "slot" a été remplacée par
     *         celle se trouvant au sommet de la pioche
     * @throws IndexOutOfBoundsException si l'index donné n'est pas compris entre
     *        0(inclus) et 5(exclus), ou IllegalArgumentException si la pioche
     *        est vide
     */
    public CardState withDrawnFaceUpCard(int slot) {
        Objects.checkIndex(slot, Constants.FACE_UP_CARDS_COUNT);
        Preconditions.checkArgument(!(deck.isEmpty()));
        List<Card> newFaceUpCard = new ArrayList<>();
        for (int i : Constants.FACE_UP_CARD_SLOTS) {
            if (slot == i) {
                newFaceUpCard.add(deck.topCard());
            } else {
                newFaceUpCard.add(faceUpCards().get(i));
            }
        }
        return new CardState(newFaceUpCard, deck.withoutTopCard(), discards);
    }

    /**
     * Méthode qui retourne la carte se trouvant au sommet de la pioche
     * 
     * @return la carte se trouvant au sommet de la pioche
     * @throws IllegalArgumentException si la pioche est vide
     */
    public Card topDeckCard() {
        Preconditions.checkArgument(!(deck.isEmpty()));
        return deck.topCard();
    }

    /**
     * Méthode qui retourne un ensemble de cartes identique au récepteur(this),
     * mais sans la carte se trouvant au sommet de la pioche 
     * 
     * @return un ensemble de cartes identique au récepteur(this), mais sans la
     *         carte se trouvant au sommet de la pioche
     * @throws IllegalArgumentException si la pioche est vide
     */
    public CardState withoutTopDeckCard() {
        Preconditions.checkArgument(!(deck.isEmpty()));
        return new CardState(faceUpCards(), deck.withoutTopCard(), discards);
    }

    /**
     * Méthode qui retourne un ensemble de cartes identique au récepteur(this),
     * si ce n'est que les cartes de la défausse ont été mélangées au moyen du
     * générateur aléatoire donné en paramètre afin de constituer la nouvelle
     * pioche
     * 
     * @param rng(Random)
     *            le générateur aléatoire
     * @return un ensemble de cartes identique au récepteur(this), si ce n'est
     *         que les cartes de la défausse ont été mélangées au moyen du
     *         générateur aléatoire donné en paramètre afin de constituer la
     *         nouvelle pioche
     * @throws IllegalArgumentException si la pioche du récepteur n'est pas vide
     */
    public CardState withDeckRecreatedFromDiscards(Random rng) {
        Preconditions.checkArgument(deck.isEmpty());
        return new CardState(faceUpCards(), Deck.of(discards, rng),
                SortedBag.of());
    }

    /**
     * Méthode qui retourne un ensemble de cartes identique au récepteur(this),
     * mais avec les cartes données en paramètre ajoutées à la défausse
     * 
     * @param additionalDiscards(SortedBag<Card>)
     *            les cartes ajoutées à la défausse
     * @return un ensemble de cartes identique au récepteur(this), mais avec les
     *         cartes "additionalDiscards" ajoutées à la défausse
     */
    public CardState withMoreDiscardedCards(
            SortedBag<Card> additionalDiscards) {
        return new CardState(faceUpCards(), deck,
                discards.union(additionalDiscards));
    }
}
