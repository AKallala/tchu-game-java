/*
 *	Author:      Ahmed Kallala (315594)
 *	Date:        26 mai 2021
 */

package ch.epfl.tchu.gui;

import ch.epfl.tchu.net.RemotePlayerClient;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Classe exécutant le client du joueur distant
 * @author ahmedkallala
 *
 */
public class ClientMain extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        String hostName = "localhost";
        int portNumber = 5108;
        if (getParameters().getRaw().size() > 1) {
            hostName = getParameters().getRaw().get(0);
            portNumber = Integer.parseInt(getParameters().getRaw().get(1));
        } else if (getParameters().getRaw().size() == 1) {
            hostName = getParameters().getRaw().get(0);
        }
        RemotePlayerClient playerClient = new RemotePlayerClient(
                new GraphicalPlayerAdapter(), hostName, portNumber);
        new Thread(() -> playerClient.run()).start();
    }
}
