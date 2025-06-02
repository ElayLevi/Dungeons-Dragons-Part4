# 🐉 Dungeons & Dragons – Turn-Based Fantasy Combat Game

Welcome to Dungeons & Dragons, a modular and expandable Java game with both console-style logic and a full Swing GUI. Choose your hero, explore a randomly generated grid, battle mythical creatures, collect treasures, and survive global “magic waves”!
---

## 🧠 About the Project
Dungeons & Dragons is designed as a clean, extensible Java project that separates game logic from visualization. It currently ships with a Swing-based GUI but retains a console-friendly core engine that can be reused or extended.
### 🔍 What Makes It Special?
🎯 Turn-based tactical combat with auto-battle when enemies are in range

🧝 Three unique character classes (Warrior, Mage, Archer)

🧟 Multiple enemy types (Goblin, Orc, Dragon), each with distinct behaviors

🗺️ Randomly generated grid map filled with walls, potions, treasures, and roaming enemies

🌐 Global “magic wave” events every fixed interval that either damage or empower all participants

📦 Clean, modular architecture (engine vs. GUI) for easy maintenance & future upgrades
---

## 🕹️ Gameplay Overview
1. **Select a Character Class**: Warrior (melee), Mage (ranged), or Archer (ranged).
2. **Navigate the Grid**: Move in 4 directions, avoid obstacles and walls.
3. **Fight Automatically**: Battles trigger when an enemy is nearby.
4. **Use Potions**: Restore health or power using items you find.
5. **Win or Lose**: The game ends when you die or choose to exit – with a full combat summary.
6. **Global Magic Waves:** Every 40 seconds (configurable), a “magic wave” event occurs: either damages everyone (5–15 HP) or grants everyone extra power (5–15).
A “🔮 Magic Wave!” banner appears briefly at the top of the GUI whenever this happens.

---

## 🧱 Project Structure
```
Dungeons_Dragons_Project/
src/
└─ game/
   ├─ Controller/
   │   ├─ GameController.java
   │   └─ GameObserver.java
   │
   ├─ Model/
   │   ├─ characters/
   │   │   ├─ AbstractCharacter.java
   │   │   ├─ PlayerCharacter.java
   │   │   ├─ Warrior.java
   │   │   ├─ Mage.java
   │   │   ├─ Archer.java
   │   │   ├─ Enemy.java
   │   │   ├─ Goblin.java
   │   │   ├─ Orc.java
   │   │   └─ Dragon.java
   │   │
   │   ├─ combat/
   │   │   ├─ CombatSystem.java
   │   │   ├─ BattleResult.java
   │   │   ├─ Combatant.java
   │   │   ├─ MeleeFighter.java
   │   │   ├─ RangedFighter.java
   │   │   ├─ PhysicalAttacker.java
   │   │   └─ MagicAttacker.java
   │   │
   │   ├─ core/
   │   │   └─ GameEntity.java
   │   │
   │   ├─ engine/
   │   │   └─ GameWorld.java
   │   │
   │   ├─ items/
   │   │   ├─ GameItem.java
   │   │   ├─ Potion.java
   │   │   ├─ PowerPotion.java
   │   │   └─ Treasure.java
   │   │
   │   └─ map/
   │       ├─ GameMap.java
   │       ├─ Position.java
   │       └─ Wall.java
   │
   ├─ Resources/
   │   ├─ images/
   │   │   ├─ Warrior.png
   │   │   ├─ Mage.png
   │   │   ├─ Archer.png
   │   │   ├─ Goblin.png
   │   │   ├─ Orc.png
   │   │   ├─ Dragon.png
   │   │   ├─ HealthPotion.png
   │   │   ├─ PowerPotion.png
   │   │   ├─ Treasure.png
   │   │   └─ Wall.png
   │   │
   │   └─ sounds/
   │       ├─ background_game_sound.wav
   │       ├─ warrior_attack.wav
   │       ├─ mage_attack.wav
   │       ├─ archer_attack.wav
   │       ├─ goblin_attack.wav
   │       ├─ orc_attack.wav
   │       ├─ dragon_attack.wav
   │       ├─ enemy_die.wav
   │       ├─ footsteps.wav
   │       ├─ drink_potion.wav
   │       └─ treasure-sound.wav
   │
   ├─ Util/
   │   ├─ GameLogger.java
   │   └─ SoundPlayer.java
   │
   └─ View.gui/
       ├─ MainWindow.java
       ├─ MainPanel.java
       ├─ MapPanel.java
       ├─ StatusPanel.java
       ├─ InventoryPanel.java
       ├─ BattleLogPanel.java
       └─ MouseControl.java


```

---

## ⚙️ How to Install & Run

### 📥 Prerequisites
- Java 17+  
- Git (if cloning)

### 🚀 Run Locally
```bash
# Clone the repo
git clone https://github.com/ElayLevi/Dungeons_Dragons_Project.git
cd Dungeons_Dragons_Project


# Compile
IntelliJ IDEA / Eclipse / VS Code:

Import as an existing Java project.

Set Project SDK to Java 11+ (in IntelliJ: File → Project Structure → Project SDK).

Ensure src/game/Resources/images and src/game/Resources/sounds are marked as resource folders.

Run game.View.gui.MainWindow (right-click → Run).
# Run the game
# Compile all source files
javac -d out src/game/**/*.java

# Run the application, ensuring resources are on classpath
java -cp out;src/game/Resources; game.View.gui.MainWindow   # Windows
java -cp out:src/game/Resources game.View.gui.MainWindow   # macOS/Linux

```

---

## ✨ Features at a Glance
| Feature        | Description                                                              |
| -------------- | ------------------------------------------------------------------------ |
| 🧙 Classes     | **Warrior**, **Mage**, **Archer** each with distinct combat logic        |
| 🧟 Enemies     | **Goblins**, **Orcs**, **Dragons** roam the map, chase & attack          |
| 🧪 Potions     | **Health Potion** (restore 25 HP), **Power Potion** (boost 5–10 power)   |
| 💰 Treasure    | Drops from fallen enemies; collect for points                            |
| 🧱 Walls       | Block movement; map tiles are hidden until revealed                      |
| 🔮 Magic Waves | Every 40 seconds, a wave either damages (5–15 HP) or boosts (5–15 power) |
| 📊 Battle Log  | Shows each combat round: enemy name, total rounds, player dmg, enemy dmg |

---

## 🧩 Developer Notes & Architecture

This project is built with modularity in mind, making it easy to maintain, test, and expand. The game is currently a console-based application, but it is designed to support GUI integration in future versions.

### 🏗️ Package Breakdown

- **`game.core`**: 
- **GameEntity.java** – Base interface for anything on the map
- **Inventory.java** – Manages a collection of items
##
- **`game.map`**:
- **GameMap.java** – Manages a 2D grid, placement of entities, movement, and visibility
- **Position.java** – Simple row/col coordinate with helper methods (e.g., distanceTo())
- **Wall.java** – Impassable map tile
##
- **`game.items`**:
- **GameItem.java** – Abstract base for all items on the map
- **Potion.java** – Health potion logic (restores HP)
- **PowerPotion.java** – Power potion logic (boosts attack)
- **Treasure.java** – Loot dropped by defeated enemies (visible after drop)
##
- **`game.characters`**:
- **AbstractCharacter.java** – Shared properties (health, power, position, visibility).
- **PlayerCharacter.java** – Base class for user-controlled heroes
- Warrior.java – Melee fighter, high HP, physical attacks only
- Mage.java – Magic user, can cast spells with elemental advantages
- Archer.java – Ranged physical attacker, moderate HP
- **Enemy.java** – Abstract enemy logic (implements Runnable)
- Goblin.java – Melee attacker, agility-based evasion
- Orc.java – Melee, resists magic damage with chance-based block, critical hits
- Dragon.java – Hybrid melee/magic attacker, has elemental strengths/weaknesses
##
- **`game.combat`**:
- **Interfaces**: Combatant.java, MeleeFighter.java, RangedFighter.java, PhysicalAttacker.java, MagicAttacker.java
- **CombatSystem.java** – Resolves a full turn-based battle between a player and an enemy
- **BattleResult.java** – Records outcome: number of rounds, player damage dealt, enemy damage dealt
##
- **`game.engine`**:
- **GameWorld.java** – Singleton that holds all players, enemies, items, and the map
- Schedules each Enemy.run() with a randomized delay (500–1500 ms)
- Schedules periodic “magic waves” every 40 seconds on a single-thread scheduler
- Manages thread-safe actions (using ReentrantLock) for picking up items, moving the player, and attacking
- Notifies registered GameObserver instances whenever the model changes
##
- **`game.View.gui`**:
- **MainWindow.java** – Entry point that prompts for player name, class, and map size, then opens the Swing frame
- **MainPanel.java** – Assembles all subpanels (Map, Status, Inventory, Battle Log) and registers as a GameObserver
- Displays a “🔮 Magic Wave!” banner at the top when a global event occurs (via magicWaveLabel)
- **MapPanel.java** – Renders the 2D grid as a GridLayout of JButton cells
- Left-click to move the player; Right-click to show a popup with tile info
- Highlights the last action tile (move/combat/pickup) with a brief background flash
- **StatusPanel.java** – Shows player’s name, current HP, power, and treasure points
- **InventoryPanel.java** – Lists potions in the player’s inventory; “Use” button to consume potions
- **BattleLogPanel.java** – Displays a JTable with columns: Enemy | Rounds | Player Dmg | Enemy Dmg
- **Resources** (src/game/Resources/images/… and …/sounds/…)
- PNG icons for each entity (Warrior.png, Mage.png, Archer.png, Goblin.png, etc.)
- WAV files for sound effects (walk, attack, magic wave, game over, treasure pickup, etc.)



---

## 📜 License
MIT License. Free for use, learning, and contributions.

---

> 🧙‍♂️ "The dungeon is yours to explore. Choose wisely, fight bravely, and collect all the treasure you can!"
