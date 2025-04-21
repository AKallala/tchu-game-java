/*
 *	Author:      Ahmed Kallala (315594)
 *	Date:        12 mai 2021
 */

package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.game.Ticket;

/**
 * Interface contenat différentes interfaces fonctionnelles qui sont des
 * "gestionnaires d'actions"
 * 
 * @author ahmedkallala
 *
 */
public interface ActionHandlers {

    @FunctionalInterface
    interface DrawTicketsHandler {
        /**
         * Méthode qui gère le tirage de billets par le joueur
         * 
         */
        void onDrawTickets();
    }

    @FunctionalInterface
    interface DrawCardHandler {
        /**
         * Méthode qui gère le tirage d'une carte par le joueur
         * 
         * @param drawSlot(int)
         *            l'emplacement duquel le joueur souhaite tirer la carte
         */
        void onDrawCard(int drawSlot);
    }

    @FunctionalInterface
    interface ClaimRouteHandler {
        /**
         * Méthode qui gère la tentative de prise de possession d'une route par
         * un joueur
         * 
         * @param route(Route)
         *            la route dont le joueur souhaite prendre possession
         * @param claimCards(SortedBag<Card>)
         *            l'ensemble des cartes que le joueur souhaite utiliser pour
         *            tenter de s'emparer de la route
         */
        void onClaimRoute(Route route, SortedBag<Card> claimCards);
    }

    @FunctionalInterface
    interface ChooseTicketsHandler {
        /**
         * Méthode qui gère le choix des billets par le joueur parmi les options
         * de billets qui lui sont proposées
         * 
         * @param tickets(SortedBag<Ticket>)
         *            l'ensemble de tickets que le joueur a choisi
         */
        void onChooseTickets(SortedBag<Ticket> tickets);
    }

    @FunctionalInterface
    interface ChooseCardsHandler {
        /**
         * Méthode qui gère le choix des cates par le joueur parmi les options
         * de cartes qui lui sont proposées
         * 
         * @param cards(SortedBag<Card>)
         *            l'ensemble de cartes que le joueur a choisi
         */
        void onChooseCards(SortedBag<Card> cards);
    }
}
