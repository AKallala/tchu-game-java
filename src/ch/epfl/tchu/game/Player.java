/*
 *	Author:      Ahmed Kallala (315594)
 *	Date:        22 mars 2021
 */

package ch.epfl.tchu.game;

import java.util.List;
import java.util.Map;

import ch.epfl.tchu.SortedBag;

public interface Player {

    /**
     * Enumération représentant les différents types de tours possibles
     */
    public enum TurnKind {
        DRAW_TICKETS, // représente un tour durant lequel le joueur tire des
                      // billets
        DRAW_CARDS, // représente un tour durant lequel le joueur tire des
                    // cartes wagon/locomotive
        CLAIM_ROUTE; // représente un tour durant lequel le joueur s'empare
                     // d'une route(ou tente en tout cas de le faire)

        public final static List<TurnKind> ALL = List.of(TurnKind.values()); // liste
                                                                             // de
                                                                             // tous
                                                                             // les
                                                                             // éléments
                                                                             // de
                                                                             // TurnKind

    }

    /**
     * Méthode qui est communique au joueur sa propre identité "ownId" donnée en
     * paramètre, ainsi que les noms des différents joueurs, le sien inclus, qui
     * se trouvent dans "playerNames" donnés en paramètre
     * 
     * @param ownId(PlayerId)
     *            le nom du joueur(this)
     * @param playerNames(Map<PlayerId,
     *            String>) les noms des différents joueurs
     */
    public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames);

    /**
     * Méthode qui est appelée chaque fois qu'une information doit être
     * communiquée au joueur au cours de la partie 
     * 
     * @param info(String)
     *            l'information à communiquer au joueur
     */
    public void receiveInfo(String info);

    /**
     * Méthode qui est appelée chaque fois que l'état du jeu a changé, pour
     * informer le joueur de la composante publique de ce nouvel état,
     * "newState", ainsi que de son propre état, "ownState"
     * 
     * @param newState(PublicGameState)
     *            nouvel état du jeu
     * @param ownState(PlayerState)
     *            l'état du joueur
     */
    public void updateState(PublicGameState newState, PlayerState ownState);

    /**
     * Méthode qui est appelée au début de la partie pour communiquer au joueur
     * les cinq billets qui lui ont été distribués
     * 
     * @param tickets(Ticket)
     *            les billets distribués au joueur en début de partie
     */
    public void setInitialTicketChoice(SortedBag<Ticket> tickets);

    /**
     * Méthode qui est appelée au début de la partie pour demander au joueur
     * lesquels des billets qu'on lui a distribué initialement il garde
     * 
     * @return les billets que le joueur va garder
     */
    public SortedBag<Ticket> chooseInitialTickets();

    /**
     * Méthode qui est appelée au début du tour d'un joueur, pour savoir quel
     * type d'action il désire effectuer durant ce tour
     * 
     * @return l'action que le joueur va effectuer
     */
    public TurnKind nextTurn();

    /**
     * Méthode qui est appelée lorsque le joueur a décidé de tirer des billets
     * supplémentaires en cours de partie, afin de lui communiquer les billets
     * tirés et de savoir lesquels il garde
     * 
     * @param options(SortedBag<Ticket>)
     *            les billets tirés par le joueur
     * @return les billets que le joueur souhaite garder parmi les billets qu'il
     *         a tiré
     */
    public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options);

    /**
     * Méthode qui est appelée lorsque le joueur a décidé de tirer des cartes
     * wagon/locomotive, afin de savoir d'où il désire les tirer : d'un des
     * emplacements contenant une carte face visible, ou de la pioche
     * 
     * @return une valeur comprise entre 0 et 4(inclus) si le joueur décide de
     *         retourner une carte face visible et -1(Constants.DECK_SLOT) si il
     *         décide de tirer une carte de la pioche
     */
    public int drawSlot();

    /**
     * Méthode qui est appelée lorsque le joueur a décidé de (tenter de)
     * s'emparer d'une route, afin de savoir de quelle route il s'agit
     * 
     * @return la route dont le joueur veut (tenter de) s'emparer
     */
    public Route claimedRoute();

    /**
     * Méthode qui est appelée lorsque le joueur a décidé de (tenter de)
     * s'emparer d'une route, afin de savoir quelle(s) carte(s) il désire
     * initialement utiliser pour cela
     * 
     * @return les cartes que le joueur veut utiliser pour (tenter de) s'emparer
     *         d'une route
     */
    public SortedBag<Card> initialClaimCards();

    /**
     * Méthode qui est appelée lorsque le joueur a décidé de tenter de s'emparer
     * d'un tunnel et que des cartes additionnelles sont nécessaires, afin de
     * savoir quelle(s) carte(s) il désire utiliser pour cela, les possibilités
     * lui étant passées en argument ; si le multiensemble retourné est vide,
     * cela signifie que le joueur ne désire pas (ou ne peut pas) choisir l'une
     * de ces possibilités
     * 
     * @param options(List<SortedBag<Card>>)
     *            tous les enembles de cartes additionnelles possibles que le
     *            joueur peut utiliser
     * @return l'ensemble des cartes additionnelles que le joueur a choisi
     *         d'utiliser parmi "options"
     */
    public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options);
}
