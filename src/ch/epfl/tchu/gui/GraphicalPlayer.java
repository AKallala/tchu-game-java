/*
 *	Author:      Ahmed Kallala (315594)
 *	Date:        22 mai 2021
 */

package ch.epfl.tchu.gui;

import java.util.List;
import java.util.Map;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Constants;
import ch.epfl.tchu.game.PlayerId;
import ch.epfl.tchu.game.PlayerState;
import ch.epfl.tchu.game.PublicGameState;
import ch.epfl.tchu.game.Ticket;
import ch.epfl.tchu.gui.ActionHandlers.ChooseCardsHandler;
import ch.epfl.tchu.gui.ActionHandlers.ChooseTicketsHandler;
import ch.epfl.tchu.gui.ActionHandlers.ClaimRouteHandler;
import ch.epfl.tchu.gui.ActionHandlers.DrawCardHandler;
import ch.epfl.tchu.gui.ActionHandlers.DrawTicketsHandler;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Classe représentant l'interface graphique du joueur
 * 
 * @author ahmedkallala
 *
 */
public final class GraphicalPlayer {
    private final ObservableGameState observableGame;
    private final ObservableList<Text> gameInfos = FXCollections
            .observableArrayList();
    private final ObjectProperty<ClaimRouteHandler> claimRouteHP = new SimpleObjectProperty<>();
    private final ObjectProperty<DrawCardHandler> drawCardHP = new SimpleObjectProperty<>();
    private final ObjectProperty<DrawTicketsHandler> drawTicketsHP = new SimpleObjectProperty<>();
    private final Stage stage;

    /**
     * Constructeur de GraphicalPlayer
     * 
     * @param player(Player)
     *            le joueur auquel l'objet courant correspond
     * @param playerNames(Map<PlayerId,
     *            String>) les noms des joueurs de la partie
     */
    public GraphicalPlayer(PlayerId player, Map<PlayerId, String> playerNames) {
        assert Platform.isFxApplicationThread();
        observableGame = new ObservableGameState(player);
        BorderPane graphicalPlayerView = new BorderPane(
                MapViewCreator.createMapView(observableGame, claimRouteHP,
                        (options,
                                handler) -> chooseClaimCards(options, handler)),
                null,
                DecksViewCreator.createCardsView(observableGame, drawTicketsHP,
                        drawCardHP),
                DecksViewCreator.createHandView(observableGame),
                InfoViewCreator.createInfoView(player, playerNames,
                        observableGame, gameInfos));
        stage = new Stage();
        stage.setTitle("tCHu \u2014 " + playerNames.get(player));
        stage.setScene(new Scene(graphicalPlayerView));
        stage.show();
    }

    /**
     * Méthode qui actualise l'état observable avec l'état du jeu et l'état du
     * joueur actuels
     * 
     * @param newGameState(PublicGameState)
     *            l'état actuel du jeu
     * @param newPlayerState(PlayerState)
     *            l'état actuel du joueur
     */
    public void setState(PublicGameState newGameState,
            PlayerState newPlayerState) {
        assert Platform.isFxApplicationThread();
        observableGame.setState(newGameState, newPlayerState);
    }

    /**
     * Méthode qui ajoute les messages liés au déroulement du jeu à la fenêtre
     * d'infos
     * 
     * @param info(String)
     *            la nouvelle info à ajouter
     */
    public void receiveInfo(String info) {
        assert Platform.isFxApplicationThread();
        if (gameInfos.size() == 5) {
            gameInfos.remove(0);
            gameInfos.add(new Text(info));
        } else {
            gameInfos.add(new Text(info));
        }
    }

    /**
     * Méthode qui permet au joueur d'effectuer une des trois types d'actions
     * possibles
     * 
     * @param drawTicketsH(DrawTicketsHandler)
     *            gestionnaire de tirage de billets
     * @param drawCardH(DrawCardHandler)
     *            gestionnaire de tirage de carte
     * @param claimRouteH(ClaimRouteHandler)
     *            gestionnaire de prise de route
     */
    public void startTurn(DrawTicketsHandler drawTicketsH,
            DrawCardHandler drawCardH, ClaimRouteHandler claimRouteH) {
        assert Platform.isFxApplicationThread();
        claimRouteHP.set((r, c) -> {
            claimRouteH.onClaimRoute(r, c);
            setPropertiesToNull();
        });

        drawCardHP.set(i -> {
            if (observableGame.canDrawCards()) {
                drawCardH.onDrawCard(i);
                setPropertiesToNull();
            }
        });

        drawTicketsHP.setValue(() -> {
            if (observableGame.canDrawTickets()) {
                drawTicketsH.onDrawTickets();
                setPropertiesToNull();
            }
        });
    }

    /**
     * Méthode qui crée la fenêtre permettant au joueur de faire son choix de
     * billets à garder parmi ceux piochés
     * 
     * @param options(SortedBag<Ticket>)
     *            ensemble des billets piochés dont le joueur va choisir
     * @param chooseTicketsH(ChooseTicketsHandler)
     *            gestionnaire de tirage de billets
     */
    public void chooseTickets(SortedBag<Ticket> options,
            ChooseTicketsHandler chooseTicketsH) {
        assert Platform.isFxApplicationThread();
        int minTickets = options.size() - Constants.DISCARDABLE_TICKETS_COUNT;
        ListView<Ticket> ticketsList = new ListView<>(
                FXCollections.observableArrayList(options.toList()));
        ticketsList.getSelectionModel()
                .setSelectionMode(SelectionMode.MULTIPLE);
        Button button = new Button(StringsFr.CHOOSE);
        button.disableProperty()
                .bind(Bindings.size(
                        ticketsList.getSelectionModel().getSelectedItems())
                        .lessThan(minTickets));
        Stage selectionWindow = createSelectionWindow(StringsFr.TICKETS_CHOICE,
                String.format(StringsFr.CHOOSE_TICKETS,
                        Integer.toString(minTickets),
                        StringsFr.plural(minTickets)),
                ticketsList, button);
        selectionWindow.show();
        button.setOnAction(e -> {
            selectionWindow.hide();
            chooseTicketsH.onChooseTickets(SortedBag
                    .of(ticketsList.getSelectionModel().getSelectedItems()));
        });
    }

    /**
     * Méthode qui gère le tirage de la deuxième carte lorsque le joueur a
     * choisi lors de sontour de piocher des cartes
     * 
     * @param drawCardH(DrawCardHandler)
     *            gestionnaire de tirage de carte
     */
    public void drawCard(DrawCardHandler drawCardH) {
        assert Platform.isFxApplicationThread();
        drawCardHP.set(i -> {
            if (observableGame.canDrawCards()) {
                drawCardH.onDrawCard(i);
                setPropertiesToNull();
            }
        });
    }

    /**
     * Méthode qui crée la fenêtre permettant au joueur de faire son choix de
     * cartes à utiliser pour s'emparer d'une route
     * 
     * @param options(List<SortedBag<Card>>)
     *            la liste des ensembles de cartes dont le joueur doit choisir
     * @param chooseCardsH
     *            le gestionnaire de choix de cartes
     */
    public void chooseClaimCards(List<SortedBag<Card>> options,
            ChooseCardsHandler chooseCardsH) {
        assert Platform.isFxApplicationThread();
        ListView<SortedBag<Card>> claimCardsList = new ListView<>(
                FXCollections.observableArrayList(options));
        chooseCards(claimCardsList, chooseCardsH, StringsFr.CHOOSE_CARDS)
                .disableProperty().bind(Bindings.isEmpty(
                        claimCardsList.getSelectionModel().getSelectedItems()));
    }

    /**
     * Méthode qui crée la fenêtre permettant au joueur de faire son choix
     * d'ensemble de cartes additionnelles
     * 
     * @param options(List<SortedBag<Card>>)
     *            la liste des ensembles de cartes dont le joueur doit choisir
     * @param chooseCardsH(ChooseCardsHandler)
     *            le gestionnaire de choix de cartes
     */
    public void chooseAdditionalCards(List<SortedBag<Card>> options,
            ChooseCardsHandler chooseCardsH) {
        assert Platform.isFxApplicationThread();
        ListView<SortedBag<Card>> additionalCardsList = new ListView<>(
                FXCollections.observableArrayList(options));
        chooseCards(additionalCardsList, chooseCardsH,
                StringsFr.CHOOSE_ADDITIONAL_CARDS);
    }

    private void setPropertiesToNull() {
        claimRouteHP.setValue(null);
        drawCardHP.set(null);
        drawTicketsHP.set(null);
    }

    private <E> Stage createSelectionWindow(String windowName, String introText,
            ListView<E> optionsView, Button button) {
        Stage selectionWindow = new Stage(StageStyle.UTILITY);
        selectionWindow.setTitle(windowName);
        selectionWindow.initOwner(stage);
        selectionWindow.initModality(Modality.WINDOW_MODAL);
        selectionWindow.setOnCloseRequest(e -> e.consume());
        VBox selectionView = new VBox();
        TextFlow introTextView = new TextFlow(new Text(introText));
        selectionView.getChildren().addAll(introTextView, optionsView, button);
        Scene selectionScene = new Scene(selectionView);
        selectionScene.getStylesheets().add("chooser.css");
        selectionWindow.setScene(selectionScene);
        return selectionWindow;
    }

    private Button chooseCards(ListView<SortedBag<Card>> cardsList,
            ChooseCardsHandler chooseCardsH, String introText) {
        cardsList.setCellFactory(
                v -> new TextFieldListCell<>(new CardBagStringConverter()));
        Button button = new Button(StringsFr.CHOOSE);
        Stage selectionWindow = createSelectionWindow(StringsFr.CARDS_CHOICE,
                introText, cardsList, button);
        selectionWindow.show();
        button.setOnAction(e -> {
            selectionWindow.hide();
            chooseCardsH.onChooseCards(
                    cardsList.getSelectionModel().getSelectedItem());
        });
        return button;
    }

}
