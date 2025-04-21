/*
 *	Author:      Ahmed Kallala (315594)
 *	Date:        21 avr. 2021
 */

package ch.epfl.tchu.net;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.ChMap;
import ch.epfl.tchu.game.Player.TurnKind;
import ch.epfl.tchu.game.PlayerId;
import ch.epfl.tchu.game.PlayerState;
import ch.epfl.tchu.game.PublicCardState;
import ch.epfl.tchu.game.PublicGameState;
import ch.epfl.tchu.game.PublicPlayerState;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.game.Ticket;

/**
 * Classe contenant tous les serdes utilisés dans le jeu
 * 
 * @author ahmedkallala
 *
 */
public final class Serdes {
    public static final Serde<Integer> INTEGER = Serde
            .of(i -> Integer.toString(i), Integer::parseInt);

    public static final Serde<String> STRING = Serde.of(
            i -> Base64.getEncoder()
                    .encodeToString(i.getBytes(StandardCharsets.UTF_8)),
            s -> new String(Base64.getDecoder().decode(s),
                    StandardCharsets.UTF_8));

    public static final Serde<PlayerId> PLAYER_ID = Serde.oneOf(PlayerId.ALL);

    public static final Serde<TurnKind> TURN_KIND = Serde.oneOf(TurnKind.ALL);

    public static final Serde<Card> CARD = Serde.oneOf(Card.ALL);

    public static final Serde<Route> ROUTE = Serde.oneOf(ChMap.routes());

    public static final Serde<Ticket> TICKET = Serde.oneOf(ChMap.tickets());

    public static final Serde<List<String>> LIST_OF_STRING = Serde
            .listOf(STRING, ',');

    public static final Serde<List<Card>> LIST_OF_CARD = Serde.listOf(CARD,
            ',');

    public static final Serde<List<Route>> LIST_OF_ROUTE = Serde.listOf(ROUTE,
            ',');

    public static final Serde<SortedBag<Card>> BAG_OF_CARD = Serde.bagOf(CARD,
            ',');

    public static final Serde<SortedBag<Ticket>> BAG_OF_TICKET = Serde
            .bagOf(TICKET, ',');

    public static final Serde<List<SortedBag<Card>>> LIST_OF_BAG_OF_CARD = Serde
            .listOf(BAG_OF_CARD, ';');

    public static final Serde<PublicCardState> PUBLIC_CARD_STATE = Serde
            .of(i -> String.join(";",
                    List.of(LIST_OF_CARD.serialize(i.faceUpCards()),
                            INTEGER.serialize(i.deckSize()),
                            INTEGER.serialize(i.discardsSize()))),
                    s -> {
                        String[] serialized = s.split(Pattern.quote(";"), -1);
                        return new PublicCardState(
                                LIST_OF_CARD.deserialize(serialized[0]),
                                INTEGER.deserialize(serialized[1]),
                                INTEGER.deserialize(serialized[2]));
                    });

    public static final Serde<PublicPlayerState> PUBLIC_PLAYER_STATE = Serde
            .of(i -> String.join(";",
                    List.of(INTEGER.serialize(i.ticketCount()),
                            INTEGER.serialize(i.cardCount()),
                            LIST_OF_ROUTE.serialize(i.routes()))),
                    s -> {
                        String[] serialized = s.split(Pattern.quote(";"), -1);
                        return new PublicPlayerState(
                                INTEGER.deserialize(serialized[0]),
                                INTEGER.deserialize(serialized[1]),
                                LIST_OF_ROUTE.deserialize(serialized[2]));
                    });

    public static final Serde<PlayerState> PLAYER_STATE = Serde
            .of(i -> String.join(";",
                    List.of(BAG_OF_TICKET.serialize(i.tickets()),
                            BAG_OF_CARD.serialize(i.cards()),
                            LIST_OF_ROUTE.serialize(i.routes()))),
                    s -> {
                        String[] serialized = s.split(Pattern.quote(";"), -1);
                        return new PlayerState(
                                BAG_OF_TICKET.deserialize(serialized[0]),
                                BAG_OF_CARD.deserialize(serialized[1]),
                                LIST_OF_ROUTE.deserialize(serialized[2]));
                    });

    public static final Serde<PublicGameState> PUBLIC_GAME_STATE = Serde.of(
            i -> publicGameStateSerializer(i),
            s -> publicGameStateDeserializer(s));

    private Serdes() {

    }

    /**
     * Méthode qui sérialise un objet de type PublicGameState
     * 
     * @param i(PublicGameState)
     *            l'objet à sérialiser
     * @return la sérialisation de "i"
     */
    private static String publicGameStateSerializer(PublicGameState i) {
        String toComplete = String.join(":",
                List.of(INTEGER.serialize(i.ticketsCount()),
                        PUBLIC_CARD_STATE.serialize(i.cardState()),
                        PLAYER_ID.serialize(i.currentPlayerId()),
                        PUBLIC_PLAYER_STATE
                                .serialize(i.playerState(PlayerId.PLAYER_1)),
                        PUBLIC_PLAYER_STATE
                                .serialize(i.playerState(PlayerId.PLAYER_2))));
        return i.lastPlayer() == null ? (toComplete + ":")
                : (toComplete + ":" + PLAYER_ID.serialize(i.lastPlayer()));
    }

    /**
     * Méthode qui désérialise un objet de type PublicGameState
     * 
     * @param s(String)
     *            la sérialisation sous forme de chaîne de caractères
     * @return l'objet désérialisé
     */
    private static PublicGameState publicGameStateDeserializer(String s) {
        String[] serialized = s.split(Pattern.quote(":"), -1);
        PlayerId lastPlayer = null;
        if (!serialized[5].isEmpty()) {
            lastPlayer = PLAYER_ID.deserialize(serialized[5]);
        }
        return new PublicGameState(INTEGER.deserialize(serialized[0]),
                PUBLIC_CARD_STATE.deserialize(serialized[1]),
                PLAYER_ID.deserialize(serialized[2]),
                Map.of(PlayerId.PLAYER_1,
                        PUBLIC_PLAYER_STATE.deserialize(serialized[3]),
                        PlayerId.PLAYER_2,
                        PUBLIC_PLAYER_STATE.deserialize(serialized[4])),
                lastPlayer);
    }
}
