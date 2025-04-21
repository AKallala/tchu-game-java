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
import java.util.regex.Pattern;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Player;
import ch.epfl.tchu.game.Player.TurnKind;
import ch.epfl.tchu.game.PlayerId;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.game.Ticket;

/**
 * Classe qui représente un client de joueur distant
 * 
 * @author ahmedkallala
 *
 */
public final class RemotePlayerClient {
    private final Player player;
    private final Socket socket;

    /**
     * Constructeur de RemotePlayerClient
     * 
     * @param player(Player)
     *            le joueur auquel le client courant fourni un accès distant
     * @param name(String)
     *            le nom utilisé pour se connecter au mandataire
     * @param port(int)
     *            le numéro du port pour se connecter au mandataire
     */
    public RemotePlayerClient(Player player, String name, int port) {
        this.player = player;
        try {
            socket = new Socket(name, port);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Méthode qui lance le client, elle reçoit un messages du mandataire
     * correspondant à une méthode du joueur et si la méthode en question
     * retourne un résultat, ce dernier est sérialisé et envoyé au mandataire
     */
    public void run() {

        try {
            BufferedReader r = new BufferedReader(new InputStreamReader(
                    socket.getInputStream(), StandardCharsets.US_ASCII));
            BufferedWriter w = new BufferedWriter(new OutputStreamWriter(
                    socket.getOutputStream(), StandardCharsets.US_ASCII));
            String s;
            while ((s = r.readLine()) != null) {
                String[] message = s.split(Pattern.quote(String.valueOf(" ")));
                switch (MessageId.valueOf(message[0])) {
                case INIT_PLAYERS:
                    List<String> names = Serdes.LIST_OF_STRING
                            .deserialize(message[2]);
                    player.initPlayers(Serdes.PLAYER_ID.deserialize(message[1]),
                            Map.of(PlayerId.PLAYER_1, names.get(0),
                                    PlayerId.PLAYER_2, names.get(1)));
                    break;
                case RECEIVE_INFO:
                    player.receiveInfo(Serdes.STRING.deserialize(message[1]));
                    break;
                case UPDATE_STATE:
                    player.updateState(
                            Serdes.PUBLIC_GAME_STATE.deserialize(message[1]),
                            Serdes.PLAYER_STATE.deserialize(message[2]));
                    break;
                case SET_INITIAL_TICKETS:
                    player.setInitialTicketChoice(
                            Serdes.BAG_OF_TICKET.deserialize(message[1]));
                    break;
                case CHOOSE_INITIAL_TICKETS:
                    SortedBag<Ticket> chosenInitialTickets = player
                            .chooseInitialTickets();
                    w.write(Serdes.BAG_OF_TICKET
                            .serialize(chosenInitialTickets));
                    w.write('\n');
                    w.flush();
                    break;
                case NEXT_TURN:
                    TurnKind nextTurn = player.nextTurn();
                    w.write(Serdes.TURN_KIND.serialize(nextTurn));
                    w.write('\n');
                    w.flush();
                    break;
                case CHOOSE_TICKETS:
                    SortedBag<Ticket> chosenTickets = player.chooseTickets(
                            Serdes.BAG_OF_TICKET.deserialize(message[1]));
                    w.write(Serdes.BAG_OF_TICKET.serialize(chosenTickets));
                    w.write('\n');
                    w.flush();
                    break;
                case DRAW_SLOT:
                    int drawSlot = player.drawSlot();
                    w.write(Serdes.INTEGER.serialize(drawSlot));
                    w.write('\n');
                    w.flush();
                    break;
                case ROUTE:
                    Route claimedRoute = player.claimedRoute();
                    w.write(Serdes.ROUTE.serialize(claimedRoute));
                    w.write('\n');
                    w.flush();
                    break;
                case CARDS:
                    SortedBag<Card> claimCards = player.initialClaimCards();
                    w.write(Serdes.BAG_OF_CARD.serialize(claimCards));
                    w.write('\n');
                    w.flush();
                    break;
                case CHOOSE_ADDITIONAL_CARDS:
                    SortedBag<Card> chosenAdditionalCards = player
                            .chooseAdditionalCards(Serdes.LIST_OF_BAG_OF_CARD
                                    .deserialize(message[1]));
                    w.write(Serdes.BAG_OF_CARD
                            .serialize(chosenAdditionalCards));
                    w.write('\n');
                    w.flush();
                    break;
                }

            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
