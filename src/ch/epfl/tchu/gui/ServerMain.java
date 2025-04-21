/*
 *	Author:      Ahmed Kallala (315594)
 *	Date:        26 mai 2021
 */

package ch.epfl.tchu.gui;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.EnumMap;
import java.util.Map;
import java.util.Random;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.ChMap;
import ch.epfl.tchu.game.Game;
import ch.epfl.tchu.game.PlayerId;
import ch.epfl.tchu.net.RemotePlayerProxy;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Classe exécutant le serveur du joueur distant
 * 
 * @author ahmedkallala
 *
 */
public class ServerMain extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Map<PlayerId, String> playerNames = new EnumMap<>(PlayerId.class);
        playerNames.put(PlayerId.PLAYER_1, "Ada");
        playerNames.put(PlayerId.PLAYER_2, "Charles");
        if (getParameters().getRaw().size() > 1) {
            playerNames.put(PlayerId.PLAYER_1, getParameters().getRaw().get(0));
            playerNames.put(PlayerId.PLAYER_2, getParameters().getRaw().get(1));
        } else if (getParameters().getRaw().size() == 1) {
            playerNames.put(PlayerId.PLAYER_1, getParameters().getRaw().get(0));
        }
        try (ServerSocket serverSocket = new ServerSocket(5108)) {
            Socket socket = serverSocket.accept();
            GraphicalPlayerAdapter player1 = new GraphicalPlayerAdapter();
            RemotePlayerProxy player2 = new RemotePlayerProxy(socket);
            new Thread(() -> Game.play(
                    Map.of(PlayerId.PLAYER_1, player1, PlayerId.PLAYER_2,
                            player2),
                    playerNames, SortedBag.of(ChMap.tickets()), new Random()))
                            .start();
        }
    }
}
