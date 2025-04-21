/*
 *	Author:      Ahmed Kallala (315594)
 *	Date:        24 mai 2021
 */

package ch.epfl.tchu.gui;

import java.util.Map;
import java.util.StringJoiner;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import javafx.util.StringConverter;

public class CardBagStringConverter extends StringConverter<SortedBag<Card>> {

    @Override
    public String toString(SortedBag<Card> object) {
        Map<Card, Integer> cards = object.toMap();
        StringJoiner cardsToString = new StringJoiner(StringsFr.AND_SEPARATOR);
        for (Card card : cards.keySet()) {
            cardsToString
                    .add(cards.get(card) + " " + Info.cardName(card, cards.get(card)));
        }
        return cardsToString.toString();
    }

    @Override
    public SortedBag<Card> fromString(String string) {
        throw new UnsupportedOperationException();
    }
    
    

}
