/*
 *	Author:      Ahmed Kallala (315594)
 *	Date:        22 mars 2021
 */

package ch.epfl.tchu.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ch.epfl.tchu.Preconditions;

public class PublicGameState {
    private final int ticketsCount;
    private final PublicCardState cardState;
    private final PlayerId currentPlayerId;
    private final Map<PlayerId, PublicPlayerState> playerState;
    private final PlayerId lastPlayer;

    /**
     * Constructeur de PublicGameState
     * 
     * @param ticketsCount(int)
     *            la taille de la pioche de billets
     * @param cardState(PublicCardState)
     *            l'état public des cartes wagon/locomotive
     * @param currentPlayerId(PlayerId)
     *            le joueur courant
     * @param playerState(Map<PlayerId,
     *            PublicPlayerState>) qui contient l'état public des joueurs
     * @param lastPlayer(PlayerId)
     *            l'identité du dernier joueur
     * @throws IllegalArgumentException
     *             si la taille de la pioche est strictement négative ou si
     *             playerState ne contient pas exactement deux paires
     *             clef/valeur
     * @throws NullPointerException
     *             si cardState ou currentPlayerId est null
     */
    public PublicGameState(int ticketsCount, PublicCardState cardState,
            PlayerId currentPlayerId,
            Map<PlayerId, PublicPlayerState> playerState, PlayerId lastPlayer) {
        Preconditions
                .checkArgument(ticketsCount >= 0 && playerState.size() == 2);
        if (cardState == null || currentPlayerId == null) {
            throw new NullPointerException();
        }
        this.ticketsCount = ticketsCount;
        this.cardState = cardState;
        this.currentPlayerId = currentPlayerId;
        this.playerState = Map.copyOf(playerState);
        this.lastPlayer = lastPlayer;

    }

    /**
     * @return ticketCount(int) la taille de la pioche de billets
     */
    public int ticketsCount() {
        return ticketsCount;
    }

    /**
     * Méthode qui retourne vrai ssi il est possible de tirer des billets
     * 
     * @return vrai s'il est possible de tirer des billets et faux sinon
     */
    public boolean canDrawTickets() {
        return (ticketsCount != 0);
    }

    /**
     * @return cardState(PublicCardState) la partie publique de l'état des
     *         cartes wagon/locomotive
     */
    public PublicCardState cardState() {
        return cardState;
    }

    /**
     * Méthode qui retourne vrai ssi il est possible de tirer des cartes
     * 
     * @return vrai s'il est possible de tirer des cartes et faux sinon
     */
    public boolean canDrawCards() {
        return (cardState.deckSize() + cardState.discardsSize()) >= 5;
    }

    /**
     * @return currentPlayerId(PlayerId) l'identité du joueur actuel
     */
    public PlayerId currentPlayerId() {
        return currentPlayerId;
    }

    /**
     * Méthode qui retourne la partie publique de l'état du joueur d'identité
     * donnée en paramètre
     * 
     * @param playerId(PlayerId)
     *            l'identité du joueur dont la partie publique de son état est
     *            retournée
     * @return la partie publique de l'état du joueur d'identité "playerId"
     */
    public PublicPlayerState playerState(PlayerId playerId) {
        return playerState.get(playerId);
    }

    /**
     * Méthode qui retourne la partie publique de l'état du joueur courant
     * 
     * @return la partie publique de l'état du joueur courant
     */
    public PublicPlayerState currentPlayerState() {
        return playerState.get(currentPlayerId);
    }

    /**
     * Méthode qui retourne la totalité des routes dont l'un ou l'autre des
     * joueurs s'est emparé
     * 
     * @return la totalité des routes dont l'un ou l'autre des joueurs s'est
     *         emparé
     */
    public List<Route> claimedRoutes() {
        List<Route> claimedRoutes = new ArrayList<>();
        claimedRoutes.addAll(playerState(PlayerId.PLAYER_1).routes());
        claimedRoutes.addAll(playerState(PlayerId.PLAYER_2).routes());
        return claimedRoutes;
    }

    /**
     * Méthode qui retourne l'identité du dernier joueur, ou null si elle n'est
     * pas encore connue car le dernier tour n'a pas commencé
     * 
     * @return l'identité du dernier joueur
     */
    public PlayerId lastPlayer() {
        return lastPlayer;
    }
}
