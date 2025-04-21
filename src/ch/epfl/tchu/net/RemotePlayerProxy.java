/*
 *	Author:      Ahmed Kallala (315594)
 *	Date:        7 mai 2021
 */

package ch.epfl.tchu.net;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UncheckedIOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Player;
import ch.epfl.tchu.game.PlayerId;
import ch.epfl.tchu.game.PlayerState;
import ch.epfl.tchu.game.PublicGameState;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.game.Ticket;

/**
 * Classe représentant un mandataire de joueur distant.
 * 
 * @author ahmedkallala
 *
 */
public final class RemotePlayerProxy implements Player {
    private final BufferedReader r;
    private final BufferedWriter w;

    /**
     * Constructeur de RemotePlayerProxy
     * 
     * @param socket(Socket)
     *            la prise utilisée pour communiquer à travers le réseau avec le
     *            client
     */
    public RemotePlayerProxy(Socket socket) {
        try {
            r = new BufferedReader(new InputStreamReader(
                    socket.getInputStream(), StandardCharsets.US_ASCII));
            w = new BufferedWriter(new OutputStreamWriter(
                    socket.getOutputStream(), StandardCharsets.US_ASCII));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        String serialized = Serdes.PLAYER_ID.serialize(ownId) + " "
                + Serdes.LIST_OF_STRING
                        .serialize(List.copyOf(playerNames.values()));
        sendMessage(MessageId.INIT_PLAYERS, serialized);
    }

    @Override
    public void receiveInfo(String info) {
        String serialized = Serdes.STRING.serialize(info);
        sendMessage(MessageId.RECEIVE_INFO, serialized);
    }

    @Override
    public void updateState(PublicGameState newState, PlayerState ownState) {
        String serialized = Serdes.PUBLIC_GAME_STATE.serialize(newState) + " "
                + Serdes.PLAYER_STATE.serialize(ownState);
        sendMessage(MessageId.UPDATE_STATE, serialized);
    }

    @Override
    public void setInitialTicketChoice(SortedBag<Ticket> tickets) {
        String serialized = Serdes.BAG_OF_TICKET.serialize(tickets);
        sendMessage(MessageId.SET_INITIAL_TICKETS, serialized);
    }

    @Override
    public SortedBag<Ticket> chooseInitialTickets() {
        sendMessage(MessageId.CHOOSE_INITIAL_TICKETS, "");
        try {
            return Serdes.BAG_OF_TICKET.deserialize(r.readLine());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public TurnKind nextTurn() {
        sendMessage(MessageId.NEXT_TURN, "");
        try {
            return Serdes.TURN_KIND.deserialize(r.readLine());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
        String serialized = Serdes.BAG_OF_TICKET.serialize(options);
        sendMessage(MessageId.CHOOSE_TICKETS, serialized);
        try {
            return Serdes.BAG_OF_TICKET.deserialize(r.readLine());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public int drawSlot() {
        sendMessage(MessageId.DRAW_SLOT, "");
        try {
            return Serdes.INTEGER.deserialize(r.readLine());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public Route claimedRoute() {
        sendMessage(MessageId.ROUTE, "");
        try {
            return Serdes.ROUTE.deserialize(r.readLine());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public SortedBag<Card> initialClaimCards() {
        sendMessage(MessageId.CARDS, "");
        try {
            return Serdes.BAG_OF_CARD.deserialize(r.readLine());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public SortedBag<Card> chooseAdditionalCards(
            List<SortedBag<Card>> options) {
        String serialized = Serdes.LIST_OF_BAG_OF_CARD.serialize(options);
        sendMessage(MessageId.CHOOSE_ADDITIONAL_CARDS, serialized);
        try {
            return Serdes.BAG_OF_CARD.deserialize(r.readLine());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void sendMessage(MessageId method, String arguments) {
        String message = method.name() + " " + arguments;
        try {
            w.write(message);
            w.write('\n');
            w.flush();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}
