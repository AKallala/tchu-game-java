/*
 *	Author:      Ahmed Kallala (315594)
 *	Date:        12 mai 2021
 */

package ch.epfl.tchu.gui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.ChMap;
import ch.epfl.tchu.game.Constants;
import ch.epfl.tchu.game.PlayerId;
import ch.epfl.tchu.game.PlayerState;
import ch.epfl.tchu.game.PublicGameState;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.game.Station;
import ch.epfl.tchu.game.Ticket;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Classe qui représente l'état observable du jeu et du joueur qui lui est
 * attachée
 * 
 * @author ahmedkallala
 *
 */
public final class ObservableGameState {
    private final PlayerId player;
    private final List<ObjectProperty<Card>> faceUpCards = createFaceUpCards();
    private final IntegerProperty cardsPercentage = new SimpleIntegerProperty();
    private final IntegerProperty ticketsPercentage = new SimpleIntegerProperty();
    private final List<ObjectProperty<PlayerId>> routes = createRoutes();
    private final List<IntegerProperty> cardsCount = createPublicPlayerStateComponents();
    private final List<IntegerProperty> ticketsCount = createPublicPlayerStateComponents();
    private final List<IntegerProperty> carsCount = createPublicPlayerStateComponents();
    private final List<IntegerProperty> claimPoints = createPublicPlayerStateComponents();
    private final ObservableList<Ticket> tickets = FXCollections
            .observableArrayList();
    private final List<IntegerProperty> countOfEachKindOfCard = createCountOfEachKindOfCard();
    private final List<BooleanProperty> canClaimRoute = createCanClaimRoute();
    private PublicGameState gameState = null;
    private PlayerState playerState = null;

    /**
     * Constructeur de ObservableGameState
     * 
     * @param player(PlayerId)
     *            l'identité du joueur auquel l'objet courant correspond
     */
    public ObservableGameState(PlayerId player) {
        this.player = player;
    }

    /**
     * Méthode qui met à jour la totalité des propriétés contenues dans l'objet
     * courant
     * 
     * @param newGameState(PublicGameState)
     *            l'état publique du jeu en cours
     * @param newPlayerState(PlayerState)
     *            l'état complet auquel correspond l'objet courant
     */
    public void setState(PublicGameState newGameState,
            PlayerState newPlayerState) {
        gameState = newGameState;
        playerState = newPlayerState;
        setFaceUpCards(newGameState);
        cardsPercentage.set((100 * newGameState.cardState().deckSize())
                / Constants.TOTAL_CARDS_COUNT);
        ticketsPercentage.set(
                (100 * newGameState.ticketsCount()) / ChMap.tickets().size());
        setRoutes(newGameState);
        setPublicPlayerStateComponents(newGameState);
        tickets.setAll(newPlayerState.tickets().toList());
        setCountOfEachKindOfCard(newPlayerState);
        setCanClaimRoute(newPlayerState, newGameState);
    }

    private static List<ObjectProperty<Card>> createFaceUpCards() {
        List<ObjectProperty<Card>> faceUpCards = new ArrayList<>();
        for (int i = 0; i < Constants.FACE_UP_CARDS_COUNT; ++i) {
            faceUpCards.add(new SimpleObjectProperty<>());
        }
        return faceUpCards;
    }

    private static List<ObjectProperty<PlayerId>> createRoutes() {
        List<ObjectProperty<PlayerId>> routes = new ArrayList<>();
        for (int i = 0; i < ChMap.routes().size(); ++i) {
            routes.add(new SimpleObjectProperty<>());
        }
        return routes;
    }

    private static List<IntegerProperty> createPublicPlayerStateComponents() {
        List<IntegerProperty> component = new ArrayList<>();
        for (int i = 0; i < PlayerId.COUNT; ++i) {
            component.add(new SimpleIntegerProperty());
        }
        return component;
    }

    private static List<IntegerProperty> createCountOfEachKindOfCard() {
        List<IntegerProperty> countOfEachKindOfCard = new ArrayList<>();
        for (int i = 0; i < Card.COUNT; ++i) {
            countOfEachKindOfCard.add(new SimpleIntegerProperty());
        }
        return countOfEachKindOfCard;
    }

    private static List<BooleanProperty> createCanClaimRoute() {
        List<BooleanProperty> canClaimRoute = new ArrayList<>();
        for (int i = 0; i < ChMap.routes().size(); ++i) {
            canClaimRoute.add(new SimpleBooleanProperty());
        }
        return canClaimRoute;
    }

    private void setFaceUpCards(PublicGameState newGameState) {
        for (int slot : Constants.FACE_UP_CARD_SLOTS) {
            faceUpCards.get(slot)
                    .set(newGameState.cardState().faceUpCard(slot));
        }
    }

    private void setPublicPlayerStateComponents(PublicGameState newGameState) {
        for (PlayerId id : PlayerId.ALL) {
            cardsCount.get(id.ordinal())
                    .set(newGameState.playerState(id).cardCount());
            ticketsCount.get(id.ordinal())
                    .set(newGameState.playerState(id).ticketCount());
            carsCount.get(id.ordinal())
                    .set(newGameState.playerState(id).carCount());
            claimPoints.get(id.ordinal())
                    .set(newGameState.playerState(id).claimPoints());
        }
    }

    private void setRoutes(PublicGameState newGameState) {
        for (Route r : ChMap.routes()) {
            if (newGameState.playerState(PlayerId.PLAYER_1).routes()
                    .contains(r)) {
                routes.get(ChMap.routes().indexOf(r)).set(PlayerId.PLAYER_1);
            } else if (newGameState.playerState(PlayerId.PLAYER_2).routes()
                    .contains(r)) {
                routes.get(ChMap.routes().indexOf(r)).set(PlayerId.PLAYER_2);
            } else {
                routes.get(ChMap.routes().indexOf(r)).set(null);
            }
        }
    }

    private void setCanClaimRoute(PlayerState newPlayerState,
            PublicGameState newGameState) {
        boolean isClaimable = true;
        for (Route r : ChMap.routes()) {
            if (newGameState.claimedRoutes().contains(r)
                    || neighbours(newGameState).contains(r.stations())) {
                isClaimable = false;
            }
            canClaimRoute.get(ChMap.routes().indexOf(r))
                    .set(newPlayerState.canClaimRoute(r)
                            && newGameState.currentPlayerId() == player
                            && isClaimable);
            isClaimable = true;
        }
    }

    private Set<List<Station>> neighbours(PublicGameState newGameState) {
        Set<List<Station>> claimedRoutesStations = new HashSet<>();
        for (Route r : newGameState.claimedRoutes()) {
            claimedRoutesStations.add(r.stations());
        }
        return claimedRoutesStations;
    }

    private void setCountOfEachKindOfCard(PlayerState newPlayerState) {
        for (Card card : Card.ALL) {
            countOfEachKindOfCard.get(card.ordinal())
                    .set(newPlayerState.cards().countOf(card));
        }
    }

    /**
     * Méthode qui retourne l'élément à la position "slot" de la liste
     * faceUpCards
     * 
     * @param slot(int)
     *            l'indice de l'élément à retourner
     * @return l'élément à la position "slot" de la liste faceUpCards
     */
    public ReadOnlyObjectProperty<Card> faceUpCard(int slot) {
        return faceUpCards.get(slot);
    }

    /**
     * @return cardsPercentage
     */
    public ReadOnlyIntegerProperty cardsPercentage() {
        return cardsPercentage;
    }

    /**
     * @return ticketsPercentage
     */
    public ReadOnlyIntegerProperty ticketsPercentage() {
        return ticketsPercentage;
    }

    /**
     * Méthode qui retourne l'élément à la position "slot" de la liste routes
     * 
     * @param slot(int)
     *            l'indice de l'élément à retourner
     * @return l'élément à la position "slot" de la liste routes
     */
    public ReadOnlyObjectProperty<PlayerId> route(int slot) {
        return routes.get(slot);
    }

    /**
     * Méthode qui retourne l'élément à la position "slot" de la liste
     * cardsCount
     * 
     * @param slot(int)
     *            l'indice de l'élément à retourner
     * @return l'élément à la position "slot" de la liste cardsCount
     */
    public ReadOnlyIntegerProperty cardsCount(int slot) {
        return cardsCount.get(slot);
    }

    /**
     * Méthode qui retourne l'élément à la position "slot" de la liste carsCount
     * 
     * @param slot(int)
     *            l'indice de l'élément à retourner
     * @return l'élément à la position "slot" de la liste carsCount
     */
    public ReadOnlyIntegerProperty carsCount(int slot) {
        return carsCount.get(slot);
    }

    /**
     * Méthode qui retourne l'élément à la position "slot" de la liste
     * ticketsCount
     * 
     * @param slot(int)
     *            l'indice de l'élément à retourner
     * @return l'élément à la position "slot" de la liste ticketsCount
     */
    public ReadOnlyIntegerProperty ticketsCount(int slot) {
        return ticketsCount.get(slot);
    }

    /**
     * Méthode qui retourne l'élément à la position "slot" de la liste
     * claimPoints
     * 
     * @param slot(int)
     *            l'indice de l'élément à retourner
     * @return l'élément à la position "slot" de la liste claimPoints
     */
    public ReadOnlyIntegerProperty claimPoints(int slot) {
        return claimPoints.get(slot);
    }

    /**
     * @return tickets
     */
    public ObservableList<Ticket> tickets() {
        return FXCollections.unmodifiableObservableList(tickets);
    }

    /**
     * Méthode qui retourne l'élément à la position "slot" de la liste
     * countOfEachKindOfCard
     * 
     * @param slot(int)
     *            l'indice de l'élément à retourner
     * @return l'élément à la position "slot" de la liste countOfEachKindOfCard
     */
    public ReadOnlyIntegerProperty countOfEachKindOfCard(int slot) {
        return countOfEachKindOfCard.get(slot);
    }

    /**
     * Méthode qui retourne l'élément à la position "slot" de la liste
     * canClaimRoute
     * 
     * @param slot(int)
     *            l'indice de l'élément à retourner
     * @return l'élément à la position "slot" de la liste canClaimRoute
     */
    public ReadOnlyBooleanProperty canClaimRoute(int slot) {
        return canClaimRoute.get(slot);
    }

    /**
     * Méthode qui si l'on peut piocher des billets, elle appelle la méthode
     * canDrawTickets de PublicGameState sur l'état courant du jeu "gameState"
     * 
     * @return vrai s'il est possible de piocher des billets et faux sinon
     */
    public boolean canDrawTickets() {
        return gameState.canDrawTickets();
    }

    /**
     * Méthode qui si l'on peut piocher des cartes, elle appelle la méthode
     * canDrawTickets de PublicGameState sur l'état courant du jeu "gameState"
     * 
     * @return vrai s'il est possible de piocher des cartes et faux sinon
     */
    public boolean canDrawCards() {
        return gameState.canDrawCards();
    }

    /**
     * Méthode qui retourne la liste des ensembles de cartes que le joueur
     * correspondant à l'état courant "playerState" peut utiliser pour s'emparer
     * de la route donnée en paramètre, elle appelle la méthode
     * possibleClaimCards de PlayerState sur l'état courant "playerState"
     * 
     * @param route(Route)
     *            la route dont on détermine la liste des ensembles de cartes
     *            que le joueur peut utiliser pour s'en emparer
     * @return la liste des ensembles de cartes que le joueur peut utiliser pour
     *         s'emparer de "route"
     */
    public List<SortedBag<Card>> possibleClaimCards(Route route) {
        return playerState.possibleClaimCards(route);
    }
}
