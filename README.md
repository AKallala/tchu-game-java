# TCHu board game implementation in java

tCHu is a game inspired by the Swiss version of Ticket to Ride, a famous board game. Our variant is designed for two players and takes place on a map representing a railway network connecting various cities in Switzerland and neighboring countries.

# Objective
Players aim to complete routes between cities to maximize their points. These routes are defined by tickets, which grant points if completed and deduct points if not. For example:

- Bern - Chur (10 points) gives 10 points if the cities are connected before the game ends.


# Gameplay Mechanics
 - **Routes** : Players use colored cards and wagons to claim routes between two neighboring stations. For instance, to connect Bern to Chur, Ada claimed the following routes: 

   - Bern - Lucerne
   - Lucerne - Schwyz
   - Schwyz - Wassen
   - Wassen - Chur

- **Cards and Wagons**: The cards in hand and the remaining number of wagons influence the possible actions. Players can:

   - Claim new routes.
   - Draw cards from visible options or the deck.
   - Obtain new tickets and keep at least one.

- **Game End** : The game ends when a player has two or fewer wagons left. Each player then plays one final turn before the points are tallied.


## Interface
The picture below illustrates the game interface:

<img width="1002" alt="Capture d’écran 2025-04-21 à 22 46 36" src="https://github.com/user-attachments/assets/353fb112-ad3f-4c66-82b6-6c780cb07ae7" />


# Main Elements
- **Statistics**: Information about wagons and scores.- Game Progress: Actions and events taking place.
- **Map**: View of the railway network.
- **Deck and Visible Cards**: Sources for drawing cards.
- **Tickets**: Objectives to complete.
- **Cards in Hand**: Cards available for actions.

The player with the most points at the end is the winner. Good luck 🍀
