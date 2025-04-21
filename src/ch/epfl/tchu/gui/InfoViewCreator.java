/*
 *	Author:      Ahmed Kallala (315594)
 *	Date:        22 mai 2021
 */

package ch.epfl.tchu.gui;

import java.util.Map;

import ch.epfl.tchu.game.PlayerId;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

/**
 * Classe créant la vue des infos dans l'interface graphique
 * 
 * @author ahmedkallala
 *
 */
final class InfoViewCreator {
    private static final int CIRCLE_RAY = 5;

    /**
     * Méthode qui crée la vue des informations relatives au jeu
     * 
     * @param player(PlayerId)
     *            l'identité du joueur auquel l'interface correspond
     * @param playerNames(Map<PlayerId,
     *            String>) la table associative des noms des joueurs
     * @param observableGame(ObservableGameState)
     *            l'état de jeu observable utilisé pour mettre à jour
     *            l'interface graphique
     * @param gameInfo(ObservableList<Text>)
     *            une liste (observable) contenant les informations sur le
     *            déroulement de la partie
     * @return la vue des informations relatives au jeu
     */
    public static Node createInfoView(PlayerId player,
            Map<PlayerId, String> playerNames,
            ObservableGameState observableGame,
            ObservableList<Text> gameInfos) {
        VBox infoView = new VBox();
        infoView.getStylesheets().addAll("info.css", "colors.css");
        VBox playerStatsView = new VBox();
        playerStatsView.setId("player-stats");
        playerStatsView.getChildren().addAll(
                createStatsView(player, playerNames, observableGame),
                createStatsView(player.next(), playerNames, observableGame));
        infoView.getChildren().add(playerStatsView);
        Separator separator = new Separator();
        TextFlow messages = new TextFlow();
        messages.setId("game-info");
        Bindings.bindContent(messages.getChildren(), gameInfos);
        infoView.getChildren().addAll(messages, separator);
        return infoView;
    }

    private static Node createStatsView(PlayerId player,
            Map<PlayerId, String> playerNames,
            ObservableGameState observableGame) {
        TextFlow playerStats = new TextFlow();
        playerStats.getStyleClass().add(player.name());
        Circle playerCircle = new Circle(CIRCLE_RAY);
        playerCircle.getStyleClass().add("filled");
        Text statsText = new Text();
        statsText.textProperty()
                .bind(Bindings.format(StringsFr.PLAYER_STATS,
                        playerNames.get(player),
                        observableGame.ticketsCount(player.ordinal()),
                        observableGame.cardsCount(player.ordinal()),
                        observableGame.carsCount(player.ordinal()),
                        observableGame.claimPoints(player.ordinal())));
        playerStats.getChildren().addAll(playerCircle, statsText);
        return playerStats;
    }
}
