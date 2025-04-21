/*
 *	Author:      Ahmed Kallala (315594)
 *	Date:        22 mars 2021
 */

package ch.epfl.tchu.game;

import java.util.EnumMap;
import java.util.Map;
import java.util.Random;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

public final class GameState extends PublicGameState {
    private final Deck<Ticket> tickets;
    private final CardState cardState;
    private final Map<PlayerId, PlayerState> playerState;

    private GameState(Deck<Ticket> tickets, int ticketsCount,
            CardState cardState, PlayerId currentPlayerId,
            Map<PlayerId, PlayerState> playerState, PlayerId lastPlayer) {
        super(ticketsCount, cardState, currentPlayerId, Map.copyOf(playerState),
                lastPlayer);
        this.tickets = tickets;
        this.cardState = cardState;
        this.playerState = Map.copyOf(playerState);
    }

    /**
     * Méthode qui retourne l'état initial d'une partie de tCHu dans laquelle la
     * pioche des billets contient les billets donnés en paramètre et la pioche
     * des cartes contient les cartes de Constants.ALL_CARDS, sans les 8(2×4) du
     * dessus, distribuées aux joueurs; ces pioches sont mélangées au moyen du
     * générateur aléatoire donné en paramètre, qui est aussi utilisé pour
     * choisir au hasard l'identité du premier joueur
     * 
     * @param tickets(SortedBag<Ticket>)
     *            les billets contenus dans la pioche de billets
     * @param rng(Random)
     *            générateur aléatoire utilisé pour mélanger les cartes et
     *            choisir le premier joueur au hasard
     * @return l'état initial d'une partie de tCHu
     */
    public static GameState initial(SortedBag<Ticket> tickets, Random rng) {
        Deck<Ticket> initialTickets = Deck.of(tickets, rng);
        Deck<Card> initialCards = Deck.of(Constants.ALL_CARDS, rng);
        Map<PlayerId, PlayerState> initialPlayerState = new EnumMap<>(
                PlayerId.class);
        for (PlayerId id : PlayerId.ALL) {
            initialPlayerState.put(id, PlayerState.initial(
                    initialCards.topCards(Constants.INITIAL_CARDS_COUNT)));
            initialCards = initialCards
                    .withoutTopCards(Constants.INITIAL_CARDS_COUNT);
        }
        return new GameState(initialTickets, initialTickets.size(),
                CardState.of(initialCards),
                PlayerId.ALL.get(rng.nextInt(PlayerId.ALL.size())),
                initialPlayerState, null);
    }

    @Override
    public PlayerState playerState(PlayerId playerId) {
        return playerState.get(playerId);
    }

    @Override
    public PlayerState currentPlayerState() {
        return playerState.get(currentPlayerId());
    }

    /**
     * Méthode qui retourne les "count" billets du sommet de la pioche
     * 
     * @param count(int)
     *            le nombre de billets que l'on veut tirer du sommet de la
     *            pioche
     * @return les "count" billets du sommet de la pioche
     * @throws IllegalArgumentException
     *             si count n'est pas compris entre 0 et la taille de la
     *             pioche(inclus)
     */
    public SortedBag<Ticket> topTickets(int count) {
        Preconditions.checkArgument(count >= 0 && count <= tickets.size());
        return tickets.topCards(count);
    }

    /**
     * Méthode qui retourne un état identique au récepteur, mais sans les
     * "count" billets du sommet de la pioche
     * 
     * @param count(int)
     *            le nombre de billets à retirer du sommet de la pioche
     * @return un état identique au récepteur, mais sans les "count" billets du
     *         sommet de la pioche
     * @throws IllegalArgumentException
     *             si count n'est pas compris entre 0 et la taille de la
     *             pioche(inclus)
     */
    public GameState withoutTopTickets(int count) {
        Preconditions.checkArgument(count >= 0 && count <= tickets.size());
        return new GameState(tickets.withoutTopCards(count),
                tickets.size() - count, cardState, currentPlayerId(),
                playerState, lastPlayer());
    }

    /**
     * Méthode qui retourne la carte au sommet de la pioche
     * 
     * @return la carte au sommet de la pioche
     * @throws IllegalArgumentException
     *             si la pioche est vide
     */
    public Card topCard() {
        Preconditions.checkArgument(cardState.deckSize() != 0);
        return cardState.topDeckCard();
    }

    /**
     * Méthode qui retourne un état identique au récepteur mais sans la carte au
     * sommet de la pioche
     * 
     * @return un état identique au récepteur mais sans la carte au sommet de la
     *         pioche
     * @throws IllegalArgumentException
     *             si la pioche est vide
     */
    public GameState withoutTopCard() {
        Preconditions.checkArgument(cardState.deckSize() != 0);
        return new GameState(tickets, tickets.size(),
                cardState.withoutTopDeckCard(), currentPlayerId(), playerState,
                lastPlayer());
    }

    /**
     * Méthode qui retourne un état identique au récepteur mais avec les cartes
     * données en paramètre ajoutées à la défausse
     * 
     * @param discardedCards(SortedBag<Card>
     *            discardedCards) les cartes à ajouter à la défausse
     * @return un état identique au récepteur mais avec les cartes
     *         "discardedCards" ajoutées à la défausse
     */
    public GameState withMoreDiscardedCards(SortedBag<Card> discardedCards) {
        return new GameState(tickets, tickets.size(),
                cardState.withMoreDiscardedCards(discardedCards),
                currentPlayerId(), playerState, lastPlayer());
    }

    /**
     * Méthode qui retourne un état identique au récepteur sauf si la pioche de
     * cartes est vide, auquel cas elle est recréée à partir de la défausse,
     * mélangée au moyen du générateur aléatoire donné en paramètre
     * 
     * @param rng(Random)
     *            le générateur aléatoire utilisé pour recréer la pioche
     * @return un état identique au récepteur ou un état où la pioche est
     *         recréée à partir de la défausse
     */
    public GameState withCardsDeckRecreatedIfNeeded(Random rng) {
        if (cardState.deckSize() == 0) {
            return new GameState(tickets, tickets.size(),
                    cardState.withDeckRecreatedFromDiscards(rng),
                    currentPlayerId(), playerState, lastPlayer());
        } else {
            return this;
        }
    }

    /**
     * Méthode qui retourne un état identique au récepteur mais dans lequel les
     * billets donnés en paramètre ont été ajoutés à la main du joueur donné en
     * paramètre 
     * 
     * @param playerId(PlayerId)
     *            le joueur qui va recevoir des billets supplémentaires
     * @param chosenTickets(SortedBag<Ticket>)
     *            les billets qui sont ajoutés à la main du joueur
     * @return un état identique au récepteur mais dans lequel les billets
     *         "chosenTickets" ont été ajoutés à la main du joueur "playerId"
     * @throws IllegalArgumentException
     *             si le joueur en question possède déjà au moins un billet
     */
    public GameState withInitiallyChosenTickets(PlayerId playerId,
            SortedBag<Ticket> chosenTickets) {
        Preconditions
                .checkArgument(playerState.get(playerId).tickets().isEmpty());
        return new GameState(tickets, tickets.size(), cardState,
                currentPlayerId(),
                Map.of(playerId,
                        playerState.get(playerId)
                                .withAddedTickets(chosenTickets),
                        playerId.next(), playerState.get(playerId.next())),
                lastPlayer());
    }

    /**
     * Méthode qui retourne un état identique au récepteur, mais dans lequel le
     * joueur courant a tiré les billets "drawnTickets" du sommet de la pioche,
     * et choisi de garder ceux contenus dans "chosenTicket"
     * 
     * @param drawnTickets(SortedBag<Ticket>)
     *            les billets tirés de la pioche par le joueur
     * @param chosenTickets(SortedBag<Ticket>)
     *            les billets gardés par le joueur
     * @return un état identique au récepteur avec "chosenTicket" ajoutées au
     *         billets du joueurs
     * @throws IllegalArgumentException
     *             si l'ensemble des billets gardés n'est pas inclus dans celui
     *             des billets tirés
     */
    public GameState withChosenAdditionalTickets(SortedBag<Ticket> drawnTickets,
            SortedBag<Ticket> chosenTickets) {
        Preconditions.checkArgument(drawnTickets.contains(chosenTickets));
        return new GameState(tickets.withoutTopCards(drawnTickets.size()),
                tickets.size() - drawnTickets.size(), cardState,
                currentPlayerId(),
                Map.of(currentPlayerId(),
                        playerState.get(currentPlayerId()).withAddedTickets(
                                chosenTickets),
                        currentPlayerId().next(),
                        playerState.get(currentPlayerId().next())),
                lastPlayer());
    }

    /**
     * Méthode qui retourne un état identique au récepteur si ce n'est que la
     * carte face retournée à l'emplacement donné en paramètre a été placée dans
     * la main du joueur courant, et remplacée par celle au sommet de la pioche
     * 
     * @param slot(int)
     *            l'emplacement de la carte face retournée ajoutée à la main du
     *            joueur courant
     * @return un état identique au récepteur si ce n'est que la carte face
     *         retournée à l'emplacement "slot" a été placée dans la main du
     *         joueur courant, et remplacée par celle au sommet de la pioche
     * @throws IllegalArgumentException
     *             s'il n'est pas possible de tirer des cartes
     */
    public GameState withDrawnFaceUpCard(int slot) {
        return new GameState(tickets, tickets.size(),
                cardState.withDrawnFaceUpCard(slot), currentPlayerId(),
                Map.of(currentPlayerId(),
                        playerState.get(currentPlayerId())
                                .withAddedCard(cardState.faceUpCard(slot)),
                        currentPlayerId().next(),
                        playerState.get(currentPlayerId().next())),
                lastPlayer());
    }

    /**
     * Méthode qui retourne un état identique au récepteur si ce n'est que la
     * carte du sommet de la pioche a été placée dans la main du joueur courant
     * 
     * @return un état identique au récepteur avec la carte au sommet de la
     *         pioche ajoutée aux cartes du joueur courant
     * @throws IllegalArgumentException
     *             s'il n'est pas possible de tirer des cartes
     */
    public GameState withBlindlyDrawnCard() {
        return new GameState(tickets, tickets.size(),
                cardState.withoutTopDeckCard(), currentPlayerId(),
                Map.of(currentPlayerId(),
                        playerState.get(currentPlayerId())
                                .withAddedCard(cardState.topDeckCard()),
                        currentPlayerId().next(),
                        playerState.get(currentPlayerId().next())),
                lastPlayer());
    }

    /**
     * Méthode qui retourne un état identique au récepteur mais dans lequel le
     * joueur courant s'est emparé de la route donnée en paramètre au moyen des
     * cartes données en paramètre 
     * 
     * @param route(Route)
     *            la route dont s'est emparé le joueur courant
     * @param cards(SortedBag<Card>)
     *            les cartes utilisées pour s'emparer de "route"
     * @return un état identique au récepteur dans lequel le joueur courant
     *         s'est emparé de route au moyen des cartes cards
     */
    public GameState withClaimedRoute(Route route, SortedBag<Card> cards) {
        return new GameState(tickets, tickets.size(),
                cardState.withMoreDiscardedCards(cards), currentPlayerId(),
                Map.of(currentPlayerId(),
                        playerState.get(currentPlayerId()).withClaimedRoute(
                                route, cards),
                        currentPlayerId().next(),
                        playerState.get(currentPlayerId().next())),
                lastPlayer());
    }

    /**
     * Méthode qui retourne vrai ssi le dernier tour commence, c-à-d si
     * l'identité du dernier joueur est actuellement inconnue mais que le joueur
     * courant n'a plus que deux wagons ou moins ; cette méthode doit être
     * appelée uniquement à la fin du tour d'un joueur
     * 
     * @return vrai si le dernier tour commence et faux sinon
     */
    public boolean lastTurnBegins() {
        return (lastPlayer() == null
                && playerState.get(currentPlayerId()).carCount() <= 2);
    }

    /**
     * Méthode qui termine le tour du joueur courant, c-à-d retourne un état
     * identique au récepteur si ce n'est que le joueur courant est celui qui
     * suit le joueur courant actuel ; de plus, si lastTurnBegins retourne vrai,
     * le joueur courant actuel devient le dernier joueur
     * 
     * @return un état identique au récepteur si ce n'est que le joueur courant
     *         est celui qui suit le joueur courant actuel et, si lastTurnBegins
     *         retourne vrai, le joueur courant actuel devient le dernier joueur
     */
    public GameState forNextTurn() {
        if (lastTurnBegins()) {
            return new GameState(tickets, ticketsCount(), cardState,
                    currentPlayerId().next(), playerState, currentPlayerId());
        } else {
            return new GameState(tickets, ticketsCount(), cardState,
                    currentPlayerId().next(), playerState, lastPlayer());
        }
    }
}
