# 🎲 tCHu – Java Board Game Implementation

*tCHu* is a game inspired by the Swiss version of [**Aventuriers du Rail**](https://cs108.epfl.ch/archive/21/p/00_introduction.html#:~:text=version%20suisse%20des-,Aventuriers%20du%20Rail,-%2C%20un%20c%C3%A9l%C3%A8bre%20jeu), a famous board game by Alan R. Moon. This variant is designed for **two players** and takes place on a map representing a railway network connecting various cities in **Switzerland** and neighboring countries.

---

## 🎯 Objective

Players aim to complete routes between cities to maximize their points. These routes are defined by **tickets**, which grant points if completed and deduct points if not.

For example:  
🟩 *Bern - Chur (10 points)* → Gives **10 points** if the cities are connected before the game ends.

---

## 🕹 Gameplay Mechanics

### 🚂 Routes  
Players use **colored cards** and **wagons** to claim routes between two neighboring stations.

Example: To connect Bern to Chur, a player could claim:
- Bern - Lucerne  
- Lucerne - Schwyz  
- Schwyz - Wassen  
- Wassen - Chur

### 🃏 Cards and Wagons  
Players can:
- Claim new routes.
- Draw cards (from visible options or the deck).
- Obtain new tickets (must keep at least one).

### 🔚 Game End  
The game ends when a player has **two or fewer wagons** remaining. Each player takes one final turn before scores are tallied.

---

## 🖥 Interface Overview

<img width="1002" alt="Capture d’écran 2025-04-21 à 22 46 36" src="https://github.com/user-attachments/assets/353fb112-ad3f-4c66-82b6-6c780cb07ae7" />

### Main Elements:
- **Statistics**: Wagons remaining, player scores.
- **Game Progress**: Log of actions and events.
- **Map**: View of the railway network.
- **Deck and Visible Cards**: Draw pile and visible options.
- **Tickets**: Objectives to complete.
- **Cards in Hand**: Cards available for actions.

🎉 The player with the most points at the end is the **winner**. Good luck 🍀
