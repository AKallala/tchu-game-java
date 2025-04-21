/*
 *	Author:      Ahmed Kallala (315594)
 *	Date:        21 avr. 2021
 */

package ch.epfl.tchu.net;

/**
 * Enumération représentant les types de messages que le serveur peut envoyer
 * aux clients
 */
public enum MessageId {
    INIT_PLAYERS, RECEIVE_INFO, UPDATE_STATE, SET_INITIAL_TICKETS, CHOOSE_INITIAL_TICKETS, NEXT_TURN, CHOOSE_TICKETS, DRAW_SLOT, ROUTE, CARDS, CHOOSE_ADDITIONAL_CARDS;
}
