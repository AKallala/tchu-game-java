/*
 *	Author:      Ahmed Kallala (315594)
 *	Date:        12 mai 2021
 */

package ch.epfl.tchu.gui;

import java.util.List;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.ChMap;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.gui.ActionHandlers.ChooseCardsHandler;
import ch.epfl.tchu.gui.ActionHandlers.ClaimRouteHandler;
import javafx.beans.property.ObjectProperty;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

/**
 * Classe créant la vue de la carte du jeu dans l'interface graphique
 * 
 * @author ahmedkallala
 *
 */
final class MapViewCreator {
    private static final int CASE_WIDTH = 36;
    private static final int CASE_HEIGHT = 12;
    private static final int CIRCLE_RAY = 3;

    private MapViewCreator() {

    }

    /**
     * Méthode qui crée la vue de la carte du jeu
     * 
     * @param observableGame(ObsaervableGameState)
     *            l'état du jeu observable utilisé pour mettre à jour
     *            l'interface graphique
     * @param claimRouteHP(ObjectProperty<ClaimRouteHandler>)
     *            propriété contenant un gestionnaire de prise de route utilisé
     *            pour gérer les prises de route au niveau de l'interface
     * @return la vue de la carte du jeu
     */
    public static Node createMapView(ObservableGameState observableGame,
            ObjectProperty<ClaimRouteHandler> claimRouteHP,
            CardChooser cardChooser) {
        Pane mapView = new Pane();
        mapView.getStylesheets().addAll("map.css", "colors.css");
        ImageView backGround = new ImageView();
        mapView.getChildren().add(backGround);
        createRoutesGroups(observableGame, claimRouteHP, cardChooser, mapView);
        return mapView;
    }

    private static void createRoutesGroups(ObservableGameState observableGame,
            ObjectProperty<ClaimRouteHandler> claimRouteHP,
            CardChooser cardChooser, Pane mapView) {
        for (Route r : ChMap.routes()) {
            Group routeGroup = new Group();
            routeGroup.setId(r.id());
            routeGroup.getStyleClass().addAll("route", r.level().name(),
                    r.color() == null ? "NEUTRAL" : r.color().name());
            createCasesGroups(r, routeGroup);
            observableGame.route(ChMap.routes().indexOf(r)).addListener(
                    (o, oV, nV) -> routeGroup.getStyleClass().add(nV.name()));
            routeGroup.disableProperty()
                    .bind(claimRouteHP.isNull().or(observableGame
                            .canClaimRoute(ChMap.routes().indexOf(r)).not()));
            routeGroup.setOnMouseClicked(e -> {
                if (observableGame.possibleClaimCards(r).size() > 1) {
                    ChooseCardsHandler chooseCardsH = chosenCards -> claimRouteHP
                            .get().onClaimRoute(r, chosenCards);
                    cardChooser.chooseCards(
                            observableGame.possibleClaimCards(r), chooseCardsH);
                } else {
                    claimRouteHP.get().onClaimRoute(r,
                            observableGame.possibleClaimCards(r).get(0));
                }
            });
            mapView.getChildren().add(routeGroup);
        }
    }

    private static void createCasesGroups(Route route, Group routeGroup) {
        for (int i = 1; i <= route.length(); ++i) {
            Group caseGroup = new Group();
            caseGroup.setId(route.id() + "_" + i);
            Rectangle way = new Rectangle(CASE_WIDTH, CASE_HEIGHT);
            way.getStyleClass().addAll("track", "filled");
            caseGroup.getChildren().add(way);
            caseGroup.getChildren().add(createCarGroup());
            routeGroup.getChildren().add(caseGroup);
        }
    }

    private static Node createCarGroup() {
        Group carGroup = new Group();
        Rectangle carRectangle = new Rectangle(CASE_WIDTH, CASE_HEIGHT);
        carRectangle.getStyleClass().add("filled");
        Circle circle1 = new Circle(CASE_HEIGHT, CASE_HEIGHT / 2, CIRCLE_RAY);
        Circle circle2 = new Circle(CASE_HEIGHT * 2, CASE_HEIGHT / 2,
                CIRCLE_RAY);
        carGroup.getChildren().addAll(carRectangle, circle1, circle2);
        carGroup.getStyleClass().add("car");
        return carGroup;
    }

    @FunctionalInterface
    public interface CardChooser {
        /**
         * Méthode qui permet au joueur de choisir un ensemble de cartes parmis
         * les différents ensembles qui lui sont proposés
         * 
         * @param options(List<SortedBag<Card>>)
         *            liste des ensembles de cartes dont le joueur peut choisir
         * @param handler(ChooseCardsHandler)
         *            gestionnaire utilisé pour gérer le choix du joueur
         */
        void chooseCards(List<SortedBag<Card>> options,
                ChooseCardsHandler handler);
    }
}
