/*
 *	Author:      Ahmed Kallala (315594)
 *	Date:        25 mai 2021
 */

package ch.epfl.tchu.gui;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Player;
import ch.epfl.tchu.game.PlayerId;
import ch.epfl.tchu.game.PlayerState;
import ch.epfl.tchu.game.PublicGameState;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.game.Ticket;
import javafx.application.Platform;

/**
 * Classe permetant d'adapter une instance de GraphicalPlayer en une valeur de
 * type Player
 * 
 * @author ahmedkallala
 *
 */
public class GraphicalPlayerAdapter implements Player {
    private GraphicalPlayer graphicalPlayer = null;
    private final BlockingQueue<SortedBag<Ticket>> ticketsChoiceQueue = new ArrayBlockingQueue<>(
            1);
    private final BlockingQueue<SortedBag<Card>> cardsChoiceQueue = new ArrayBlockingQueue<>(
            1);
    private final BlockingQueue<TurnKind> nextTurnChoiceQueue = new ArrayBlockingQueue<>(
            1);
    private final BlockingQueue<Route> routeChoiceQueue = new ArrayBlockingQueue<>(
            1);
    private final BlockingQueue<Integer> cardSlotChoiceQueue = new ArrayBlockingQueue<>(
            1);

    @Override
    /**
     * Méthode qui crée l'interphace graphique du joueur
     */
    public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        Platform.runLater(() -> graphicalPlayer = new GraphicalPlayer(ownId,
                playerNames));
    }

    @Override
    /**
     * Méthode qui transmet les infos sur le déroulement de la partie
     */
    public void receiveInfo(String info) {
        Platform.runLater(() -> graphicalPlayer.receiveInfo(info));
    }

    @Override
    /**
     * Méthode qui actualise l'état du jeu observable
     */
    public void updateState(PublicGameState newState, PlayerState ownState) {
        Platform.runLater(() -> {
            graphicalPlayer.setState(newState, ownState);
        });
    }

    @Override
    /**
     * Méthode qui met en place le choix du joueur parmi les billets qui lui
     * sont proposés au début du jeu
     */
    public void setInitialTicketChoice(SortedBag<Ticket> tickets) {
        Platform.runLater(() -> graphicalPlayer.chooseTickets(tickets, t -> {
            try {
                ticketsChoiceQueue.put(t);
            } catch (InterruptedException e) {
                throw new Error();
            }
        }));
    }

    @Override
    /**
     * Méthode qui permet au joueur de choisir les billets initiaux qu'il désire
     * garder
     * 
     * @return l'ensemble de billets que le joueur a choisi de garder parmi les
     *         billets initiaux
     */
    public SortedBag<Ticket> chooseInitialTickets() {
        try {
            return ticketsChoiceQueue.take();
        } catch (InterruptedException e) {
            throw new Error();
        }
    }

    @Override
    /**
     * Méthode qui permet au joueur de choisir quel type de tour il souhaite
     * effectuer
     * 
     * @return le tour que souhaite effectuer le joueur
     */
    public TurnKind nextTurn() {
        Platform.runLater(() -> graphicalPlayer.startTurn(() -> {
            try {
                nextTurnChoiceQueue.put(TurnKind.DRAW_TICKETS);
            } catch (InterruptedException e1) {
                throw new Error();
            }
        }, i -> {
            try {
                nextTurnChoiceQueue.put(TurnKind.DRAW_CARDS);
                cardSlotChoiceQueue.put(i);
            } catch (InterruptedException e) {
                throw new Error();
            }
        }, (r, c) -> {
            try {
                nextTurnChoiceQueue.put(TurnKind.CLAIM_ROUTE);
                routeChoiceQueue.put(r);
                cardsChoiceQueue.put(c);
            } catch (InterruptedException e) {
                throw new Error();
            }
        }));
        try {
            return nextTurnChoiceQueue.take();
        } catch (InterruptedException e) {
            throw new Error();
        }
    }

    @Override
    /**
     * Méthode qui permet au joueur de choisir parmi les billets piochés
     * lesquels il souhaite garder
     * 
     * @return l'ensemble des billets gardés par le joueur
     */
    public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
        Platform.runLater(() -> graphicalPlayer.chooseTickets(options, t -> {
            try {
                ticketsChoiceQueue.put(t);
            } catch (InterruptedException e) {
                throw new Error();
            }
        }));
        try {
            return ticketsChoiceQueue.take();
        } catch (InterruptedException e) {
            throw new Error();
        }
    }

    @Override
    /**
     * Méthode qui permet au joueur de choisir l'emplacement dont il souhaite
     * tirer la carte, cartes face visible ou pioche
     * 
     * @return l'emplacement choisi par le joueur
     */
    public int drawSlot() {
        if (cardSlotChoiceQueue.isEmpty()) {
            Platform.runLater(() -> graphicalPlayer.drawCard(i -> {
                try {
                    cardSlotChoiceQueue.put(i);
                } catch (InterruptedException e) {
                    throw new Error();
                }
            }));
            try {
                return cardSlotChoiceQueue.take();
            } catch (InterruptedException e) {
                throw new Error();
            }
        } else {
            try {
                return cardSlotChoiceQueue.take();
            } catch (InterruptedException e) {
                throw new Error();
            }
        }
    }

    @Override
    /**
     * Méthode qui permet au joueur de s'emparer d'une route
     * 
     * @return la route prise par le joueur
     */
    public Route claimedRoute() {
        try {
            return routeChoiceQueue.take();
        } catch (InterruptedException e) {
            throw new Error();
        }
    }

    @Override
    /**
     * Méthode qui permet au joueur de s'emparer d'une route
     * 
     * @return les cartes choisies par le joueur pour s'emparer de la route
     */
    public SortedBag<Card> initialClaimCards() {
        try {
            return cardsChoiceQueue.take();
        } catch (InterruptedException e) {
            throw new Error();
        }
    }

    @Override
    /**
     * Méthode qui permet au joueur les cartes additionnelles qu'il va utiliser
     * pour s'emparer d'un tunnel
     * 
     * @return l'ensemble des cartes choisi parmi la liste des ensembles de
     *         cartes proposés
     */
    public SortedBag<Card> chooseAdditionalCards(
            List<SortedBag<Card>> options) {
        Platform.runLater(
                () -> graphicalPlayer.chooseAdditionalCards(options, c -> {
                    try {
                        cardsChoiceQueue.put(c);
                    } catch (InterruptedException e) {
                        throw new Error();
                    }
                }));
        try {
            return cardsChoiceQueue.take();
        } catch (InterruptedException e) {
            throw new Error();
        }
    }

}
