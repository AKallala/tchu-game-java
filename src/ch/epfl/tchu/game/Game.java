/*
 *	Author:      Ahmed Kallala (315594)
 *	Date:        27 mars 2021
 */

package ch.epfl.tchu.game;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Player.TurnKind;
import ch.epfl.tchu.game.Route.Level;
import ch.epfl.tchu.gui.Info;

public final class Game {
    private final static int CARD_DRAWS_PER_TURN = 2;

    private Game() {
    }

    /**
     * Méthode qui fait jouer une partie de tCHu aux joueurs donnés en
     * paramètre, dont les noms figurent dans la table playerNames; les billets
     * disponibles pour cette partie sont ceux de tickets, et le générateur
     * aléatoire rng est utilisé pour créer l'état initial du jeu et pour
     * mélanger les cartes de la défausse pour en faire une nouvelle pioche
     * quand cela est nécessaire
     * 
     * @param players(Map<PlayerId,
     *            Player> players) les joueurs de la partie
     * @param playerNames(Map<PlayerId,
     *            String> playerNames) les noms des joueurs de la partie
     * @param tickets(SortedBag<Ticket>)
     *            les billets disponibles pour la partie à jouer
     * @param rng(Random)
     *            le générateur aléatoire utilisé
     * @throws IllegalArgumentException
     *             si l'une des deux tables associatives a une taille différente
     *             de 2
     */
    public static void play(Map<PlayerId, Player> players,
            Map<PlayerId, String> playerNames, SortedBag<Ticket> tickets,
            Random rng) {
        Preconditions.checkArgument(players.size() == PlayerId.COUNT
                && playerNames.size() == PlayerId.COUNT);
        Map<PlayerId, Info> infoPlayers = new EnumMap<>(PlayerId.class);
        for (PlayerId id : PlayerId.ALL) {
            infoPlayers.put(id, new Info(playerNames.get(id)));
            players.get(id).initPlayers(id, playerNames);
        }
        GameState gameState = GameState.initial(tickets, rng);
        receiveInfo(players,
                new Info(playerNames.get(gameState.currentPlayerId()))
                        .willPlayFirst());
        players.get(gameState.currentPlayerId()).setInitialTicketChoice(
                gameState.topTickets(Constants.INITIAL_TICKETS_COUNT));
        gameState = gameState
                .withoutTopTickets(Constants.INITIAL_TICKETS_COUNT);
        players.get(gameState.currentPlayerId().next()).setInitialTicketChoice(
                gameState.topTickets(Constants.INITIAL_TICKETS_COUNT));
        gameState = gameState
                .withoutTopTickets(Constants.INITIAL_TICKETS_COUNT);
        updateState(players, gameState);
        SortedBag<Ticket> chosenTickets1 = players
                .get(gameState.currentPlayerId()).chooseInitialTickets();
        gameState = gameState.withInitiallyChosenTickets(
                gameState.currentPlayerId(), chosenTickets1);
        updateState(players, gameState);
        SortedBag<Ticket> chosenTickets2 = players
                .get(gameState.currentPlayerId().next()).chooseInitialTickets();
        gameState = gameState.withInitiallyChosenTickets(
                gameState.currentPlayerId().next(), chosenTickets2);
        receiveInfo(players, infoPlayers.get(gameState.currentPlayerId())
                .keptTickets(chosenTickets1.size()));
        receiveInfo(players, infoPlayers.get(gameState.currentPlayerId().next())
                .keptTickets(chosenTickets2.size()));
        while (true) {

            PlayerId lastPlayer = gameState.lastPlayer();
            if (lastPlayer == gameState.currentPlayerId()) {
                receiveInfo(players,
                        new Info(playerNames.get(lastPlayer)).lastTurnBegins(
                                gameState.playerState(lastPlayer).carCount()));
                gameState = nextTurn(players, gameState, playerNames, rng);
                gameState = gameState.forNextTurn();
                gameState = nextTurn(players, gameState, playerNames, rng);
                endOfGame(gameState, players, infoPlayers, playerNames);
                break;
            } else {
                gameState = nextTurn(players, gameState, playerNames, rng);
                gameState = gameState.forNextTurn();
            }
        }

    }

    private static void receiveInfo(Map<PlayerId, Player> players,
            String info) {
        for (PlayerId id : PlayerId.ALL) {
            players.get(id).receiveInfo(info);
        }
    }

    private static void updateState(Map<PlayerId, Player> players,
            GameState gameState) {
        for (PlayerId id : PlayerId.ALL) {
            players.get(id).updateState(gameState, gameState.playerState(id));
        }
    }

    private static GameState nextTurn(Map<PlayerId, Player> players,
            GameState gameState, Map<PlayerId, String> playerNames,
            Random rng) {
        Player currentPlayer = players.get(gameState.currentPlayerId());
        PlayerState currentPlayerState = gameState.currentPlayerState();
        Info currentPlayerInfo = new Info(
                playerNames.get(gameState.currentPlayerId()));
        receiveInfo(players, currentPlayerInfo.canPlay());
        updateState(players, gameState);
        TurnKind turnKind = currentPlayer.nextTurn();
        switch (turnKind) {
        case DRAW_TICKETS:
            receiveInfo(players, currentPlayerInfo
                    .drewTickets(Constants.IN_GAME_TICKETS_COUNT));
            SortedBag<Ticket> chosenTickets = currentPlayer.chooseTickets(
                    gameState.topTickets(Constants.IN_GAME_TICKETS_COUNT));
            gameState = gameState.withChosenAdditionalTickets(
                    gameState.topTickets(Constants.IN_GAME_TICKETS_COUNT),
                    chosenTickets);
            receiveInfo(players,
                    currentPlayerInfo.keptTickets(chosenTickets.size()));
            break;

        case DRAW_CARDS:
            for (int i = 0; i < CARD_DRAWS_PER_TURN; ++i) {
                gameState = gameState.withCardsDeckRecreatedIfNeeded(rng);
                updateState(players, gameState);
                int slot = currentPlayer.drawSlot();
                if (slot == Constants.DECK_SLOT) {
                    receiveInfo(players, currentPlayerInfo.drewBlindCard());
                    gameState = gameState.withBlindlyDrawnCard();
                } else {
                    receiveInfo(players, currentPlayerInfo.drewVisibleCard(
                            gameState.cardState().faceUpCard(slot)));
                    gameState = gameState.withDrawnFaceUpCard(slot);
                }
            }
            break;

        case CLAIM_ROUTE:
            Route claimedRoute = currentPlayer.claimedRoute();
            SortedBag<Card> claimCards = currentPlayer.initialClaimCards();
            if (claimedRoute.level() == Level.UNDERGROUND) {
                receiveInfo(players, currentPlayerInfo
                        .attemptsTunnelClaim(claimedRoute, claimCards));
                SortedBag.Builder<Card> drawnCardsBuilder = new SortedBag.Builder<>();
                for (int i = 0; i < Constants.ADDITIONAL_TUNNEL_CARDS; ++i) {
                    gameState = gameState.withCardsDeckRecreatedIfNeeded(rng);
                    drawnCardsBuilder.add(gameState.topCard());
                    gameState = gameState.withoutTopCard();
                }
                SortedBag<Card> drawnCards = drawnCardsBuilder.build();
                gameState = gameState.withMoreDiscardedCards(drawnCards);
                int additionalClaimCardsCount = claimedRoute
                        .additionalClaimCardsCount(claimCards, drawnCards);
                receiveInfo(players, currentPlayerInfo.drewAdditionalCards(
                        drawnCards, additionalClaimCardsCount));
                if (additionalClaimCardsCount > 0) {
                    List<SortedBag<Card>> possibleAdditionalCards = currentPlayerState
                            .possibleAdditionalCards(additionalClaimCardsCount,
                                    claimCards);
                    if (!possibleAdditionalCards.isEmpty()) {

                        SortedBag<Card> chosenAdditionalCards = currentPlayer
                                .chooseAdditionalCards(possibleAdditionalCards);
                        if (chosenAdditionalCards.isEmpty()) {
                            receiveInfo(players, currentPlayerInfo
                                    .didNotClaimRoute(claimedRoute));
                        } else {
                            receiveInfo(players, currentPlayerInfo.claimedRoute(
                                    claimedRoute,
                                    claimCards.union(chosenAdditionalCards)));
                            gameState = gameState.withClaimedRoute(claimedRoute,
                                    claimCards.union(chosenAdditionalCards));
                        }
                    } else {
                        receiveInfo(players, currentPlayerInfo
                                .didNotClaimRoute(claimedRoute));
                    }
                } else {
                    receiveInfo(players, currentPlayerInfo
                            .claimedRoute(claimedRoute, claimCards));
                    gameState = gameState.withClaimedRoute(claimedRoute,
                            claimCards);
                }
            } else {
                receiveInfo(players, currentPlayerInfo
                        .claimedRoute(claimedRoute, claimCards));
                gameState = gameState.withClaimedRoute(claimedRoute,
                        claimCards);
            }
            break;
        }
        return gameState;

    }

    private static void endOfGame(GameState gameState,
            Map<PlayerId, Player> players, Map<PlayerId, Info> infoPlayers,
            Map<PlayerId, String> playerNames) {
        List<Trail> longestTrails = new ArrayList<>();
        for (PlayerId id : PlayerId.ALL) {
            longestTrails
                    .add(Trail.longest(gameState.playerState(id).routes()));
        }
        int longestTrailLength1 = longestTrails.get(0).length();
        int longestTrailLength2 = longestTrails.get(1).length();
        int finalPoints1 = gameState.playerState(PlayerId.PLAYER_1)
                .finalPoints();
        int finalPoints2 = gameState.playerState(PlayerId.PLAYER_2)
                .finalPoints();
        if (longestTrailLength1 > longestTrailLength2) {
            finalPoints1 += Constants.LONGEST_TRAIL_BONUS_POINTS;
            receiveInfo(players, infoPlayers.get(PlayerId.PLAYER_1)
                    .getsLongestTrailBonus(longestTrails.get(0)));
        } else if (longestTrailLength2 > longestTrailLength1) {
            finalPoints2 += Constants.LONGEST_TRAIL_BONUS_POINTS;
            receiveInfo(players, infoPlayers.get(PlayerId.PLAYER_2)
                    .getsLongestTrailBonus(longestTrails.get(1)));
        } else {
            finalPoints1 += Constants.LONGEST_TRAIL_BONUS_POINTS;
            finalPoints2 += Constants.LONGEST_TRAIL_BONUS_POINTS;
            receiveInfo(players, infoPlayers.get(PlayerId.PLAYER_1)
                    .getsLongestTrailBonus(longestTrails.get(0)));
            receiveInfo(players, infoPlayers.get(PlayerId.PLAYER_2)
                    .getsLongestTrailBonus(longestTrails.get(1)));
        }
        if (finalPoints1 > finalPoints2) {
            updateState(players, gameState);
            receiveInfo(players, infoPlayers.get(PlayerId.PLAYER_1)
                    .won(finalPoints1, finalPoints2));
        } else if (finalPoints2 > finalPoints1) {
            updateState(players, gameState);
            receiveInfo(players, infoPlayers.get(PlayerId.PLAYER_2)
                    .won(finalPoints2, finalPoints1));
        } else {
            updateState(players, gameState);
            receiveInfo(players, Info.draw((List<String>) playerNames.values(),
                    finalPoints1));
        }
    }

}
