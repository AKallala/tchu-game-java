/*
 *	Author:      Ahmed Kallala (315594)
 *	Date:        12 mai 2021
 */

package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Constants;
import ch.epfl.tchu.game.Ticket;
import ch.epfl.tchu.gui.ActionHandlers.DrawCardHandler;
import ch.epfl.tchu.gui.ActionHandlers.DrawTicketsHandler;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

/**
 * Classe créant la vue des cartes du jeu et de la main du joueur dans
 * l'interface graphique
 * 
 * @author ahmedkallala
 *
 */
final class DecksViewCreator {
    private static final int GAUGE_RECTANGLE_WIDTH = 50;
    private static final int GAUGE_RECTANGLE_HEIGHT = 5;
    private static final int INSIDE_CARD_RECTANGLE_WIDTH = 40;
    private static final int INSIDE_CARD_RECTANGLE_HEIGHT = 70;
    private static final int OUTSIDE_CARD_RECTANGLE_WIDTH = 60;
    private static final int OUTSIDE_CARD_RECTANGLE_HEIGHT = 90;

    private DecksViewCreator() {

    }

    /**
     * Méthode qui crée la vue de la main du joueur
     * 
     * @param observableGame(ObservableGameState)
     *            l'état du jeu observable utilisé pour mettre à jour
     *            l'interface graphique
     * @return la vue de la main du joueur
     */
    public static Node createHandView(ObservableGameState observableGame) {
        HBox handView = new HBox();
        handView.getStylesheets().addAll("decks.css", "colors.css");
        ListView<Ticket> ticketsView = new ListView<>(observableGame.tickets());
        ticketsView.setId("tickets");
        handView.getChildren().addAll(ticketsView,
                createHandCardsView(observableGame));
        return handView;
    }

    /**
     * Méthode qui crée la vue des différentes pioches de cartes et billets du
     * jeu
     * 
     * @param observableGame(ObservableGameState)
     *            l'état du jeu observable utilisé pour mettre à jour
     *            l'interface graphique
     * @param drawTicketsHP(ObjectProperty<DrawTicketsHandler>)
     *            propriété contenant un gestionnaire de tirage de billet
     *            utilisé pour gérer les tirages de billets par le joueur au
     *            niveau de l'interface
     * @param drawCardHP(ObjectProperty<DrawCardsHandler>)
     *            propriété contenant un gestionnaire de tirage de billet
     *            utilisé pour gérer les tirages de cartes par le joueur au
     *            niveau de l'interface
     * @return la vue des différentes pioches de cartes et billets du jeu
     */
    public static Node createCardsView(ObservableGameState observableGame,
            ObjectProperty<DrawTicketsHandler> drawTicketsHP,
            ObjectProperty<DrawCardHandler> drawCardHP) {
        VBox cardsView = new VBox();
        cardsView.getStylesheets().addAll("decks.css", "colors.css");
        cardsView.setId("card-pane");
        Button ticketsDeck = new Button(StringsFr.TICKETS);
        ticketsDeck.getStyleClass().add("gauged");
        ticketsDeck
                .setGraphic(gaugeGraphic(observableGame.ticketsPercentage()));
        ticketsDeck.disableProperty().bind(drawTicketsHP.isNull());
        ticketsDeck.setOnMouseClicked(e -> drawTicketsHP.get().onDrawTickets());
        cardsView.getChildren().add(ticketsDeck);
        for (int slot : Constants.FACE_UP_CARD_SLOTS) {
            cardsView.getChildren().add(
                    createFaceUpCardView(slot, observableGame, drawCardHP));
        }
        Button cardsDeck = new Button(StringsFr.CARDS);
        cardsDeck.getStyleClass().add("gauged");
        cardsDeck.setGraphic(gaugeGraphic(observableGame.cardsPercentage()));
        cardsDeck.disableProperty().bind(drawCardHP.isNull());
        cardsDeck.setOnMouseClicked(
                e -> drawCardHP.get().onDrawCard(Constants.DECK_SLOT));
        cardsView.getChildren().add(cardsDeck);
        return cardsView;
    }

    private static Group gaugeGraphic(
            ReadOnlyIntegerProperty percentageProperty) {
        Group gaugeView = new Group();
        Rectangle backGround = new Rectangle(GAUGE_RECTANGLE_WIDTH,
                GAUGE_RECTANGLE_HEIGHT);
        backGround.getStyleClass().add("background");
        Rectangle foreGround = new Rectangle(GAUGE_RECTANGLE_WIDTH,
                GAUGE_RECTANGLE_HEIGHT);
        foreGround.getStyleClass().add("foreground");
        foreGround.widthProperty()
                .bind(percentageProperty.multiply(50).divide(100));
        gaugeView.getChildren().addAll(backGround, foreGround);
        return gaugeView;
    }

    private static StackPane cardView() {
        Rectangle rectangle1 = new Rectangle(OUTSIDE_CARD_RECTANGLE_WIDTH,
                OUTSIDE_CARD_RECTANGLE_HEIGHT);
        Rectangle rectangle2 = new Rectangle(INSIDE_CARD_RECTANGLE_WIDTH,
                INSIDE_CARD_RECTANGLE_HEIGHT);
        Rectangle rectangle3 = new Rectangle(INSIDE_CARD_RECTANGLE_WIDTH,
                INSIDE_CARD_RECTANGLE_HEIGHT);
        rectangle1.getStyleClass().add("outside");
        rectangle2.getStyleClass().addAll("filled", "inside");
        rectangle3.getStyleClass().add("train-image");
        StackPane cardView = new StackPane(rectangle1, rectangle2, rectangle3);
        cardView.getStyleClass().addAll("card", "");
        return cardView;
    }

    private static Node createHandCardsView(
            ObservableGameState observableGame) {
        HBox cardsView = new HBox();
        cardsView.setId("hand-pane");
        for (Card c : Card.ALL) {
            StackPane cardView = new StackPane();
            cardView = cardView();
            cardView.getStyleClass()
                    .add(c.color() == null ? "NEUTRAL" : c.color().name());
            ReadOnlyIntegerProperty count = observableGame
                    .countOfEachKindOfCard(c.ordinal());
            Text countText = new Text();
            countText.getStyleClass().add("count");
            countText.textProperty().bind(Bindings.convert(count));
            countText.visibleProperty().bind(Bindings.greaterThan(count, 1));
            cardView.getChildren().add(countText);
            cardView.visibleProperty().bind(Bindings.greaterThan(count, 0));
            cardsView.getChildren().add(cardView);
        }
        return cardsView;
    }

    private static Node createFaceUpCardView(int slot,
            ObservableGameState observableGame,
            ObjectProperty<DrawCardHandler> drawCardHP) {
        StackPane faceUpCardView = cardView();
        observableGame.faceUpCard(slot).addListener(
                (o, oV, nV) -> faceUpCardView.getStyleClass().set(1,
                        nV.color() == null ? "NEUTRAL" : nV.color().name()));
        faceUpCardView.disableProperty().bind(drawCardHP.isNull());
        faceUpCardView
                .setOnMouseClicked(e -> drawCardHP.get().onDrawCard(slot));
        return faceUpCardView;
    }

}
