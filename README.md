# 🐉 Dungeons & Dragons – Turn-Based Fantasy Combat Game

Welcome to **Dungeons & Dragons**, a modular and expandable Java console game inspired by the world of classic tabletop RPGs. Choose your character, fight mythical creatures, collect treasures, and survive on a dynamic grid-based map!

---

## 🧠 About the Project
Dungeons & Dragons is designed as a clean, extensible Java project that separates logic from visuals. You can play it in the terminal, or easily build a GUI on top of it in future iterations.

### 🔍 What Makes It Special?
- 🎯 Turn-based tactical combat
- 🧝 Three unique character classes (Warrior, Mage, Archer)
- 🗺️ Randomly generated map with enemies, potions, and treasures
- 📦 Clean modular architecture for easy maintenance & upgrades
- 📊 Battle summary table after each session

---

## 🕹️ Gameplay Overview
1. **Select a Character Class**: Warrior (melee), Mage (ranged), or Archer (ranged).
2. **Navigate the Grid**: Move in 4 directions, avoid obstacles and walls.
3. **Fight Automatically**: Battles trigger when an enemy is nearby.
4. **Use Potions**: Restore health or power using items you find.
5. **Win or Lose**: The game ends when you die or choose to exit – with a full combat summary.

---

## 🧱 Project Structure
```
DungeonsAndDragons/
├─ src/game/
│  ├─ core/         # Base abstractions
│  ├─ map/          # Map logic & positioning
│  ├─ items/        # Potions, walls, treasures
│  ├─ characters/   # Player classes & enemies
│  ├─ combat/       # Combat engine and result tracking
│  └─ Main.java     # Game launcher
└─ out/             # Compiled classes
```

---

## ⚙️ How to Install & Run

### 📥 Prerequisites
- Java 17+  
- Git (if cloning)

### 🚀 Run Locally
```bash
# Clone the repo
https://github.com/<your-username>/Dungeons-And-Dragons.git

# Compile
javac -d out src/game/**/*.java

# Run the game
java -cp out game.Main
```

---

## ✨ Features at a Glance
| Feature        | Description                                  |
|----------------|----------------------------------------------|
| 🧙 Classes      | Warrior, Mage, Archer                        |
| 🧟 Enemies      | Goblins, Orcs, Dragons                       |
| 🧪 Potions      | Healing and Power Potions                   |
| 📊 Summary      | End-of-game battle statistics               |
| 🧱 Obstacles     | Walls block movement                        |
| 🎯 Ranged Logic | Mage and Archer have distance attacks       |

---

## 🧩 Developer Notes & Architecture

This project is built with modularity in mind, making it easy to maintain, test, and expand. The game is currently a console-based application, but it is designed to support GUI integration in future versions.

### 🏗️ Package Breakdown

- **`game.core`**: Contains essential interfaces and abstract components like `GameEntity` and `Inventory`. These serve as the foundation for all characters and items.
- **`game.map`**: Implements the grid-based map system, including movement, visibility, and spatial logic using `GameMap` and `Position`.
- **`game.items`**: Defines all interactive and non-interactive items such as `Potion`, `PowerPotion`, `Wall`, and `Treasure`, which inherit from `GameItem`.
- **`game.characters`**: Includes all characters in the game:
  - `PlayerCharacter`: Base class for user-controlled heroes.
  - `Warrior`, `Mage`, `Archer`: Extend `PlayerCharacter`, implementing specialized behavior.
  - `Enemy`, `Goblin`, `Orc`, `Dragon`: Enemy types with varied combat logic.
- **`game.combat`**: Manages all battle logic, including interfaces (`Combatant`, `MeleeFighter`, etc.), the main loop (`CombatSystem`), and battle summaries (`BattleResult`).

### 🖼️ Coming Soon: Graphical Interface (GUI)
The code is written with separation of concerns and GUI support in future versions. The combat and game engine logic will remain reusable across both CLI and GUI modes.

---

## 📜 License
MIT License. Free for use, learning, and contributions.

---

> 🧙‍♂️ "The dungeon is yours to explore. Choose wisely, fight bravely, and collect all the treasure you can!"
