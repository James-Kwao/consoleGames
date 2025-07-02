# BrickGame

**BrickGame** is a classic brick breaker arcade game implemented in Java for the command-line interface (CLI). It features colorful ASCII graphics, responsive real-time controls, score and life tracking, and adapts automatically to your terminal size. The game is ideal for running in terminal environments like Termux on Android, Linux, or macOS.

---

## Features

- **Classic Arcade Gameplay:** Break all the bricks by bouncing the ball with your paddle.
- **Real-Time Controls:** Fast and responsive paddle movement with keyboard input.
- **Dynamic Terminal Sizing:** Game display adjusts automatically to your terminal window size.
- **Score and Lives Display:** Track your current score and remaining lives at all times.
- **Colorful ASCII Art:** Enhanced visuals for a more engaging experience.
- **Game Over Banner:** Stylish "Game Over" screen using `figlet` (if installed).
- **Easy to Run:** All dependencies, including `jline`, are included in this repository.

---

## Requirements

- **Java 8 or higher**
- **Terminal emulator** (Termux, Linux, or macOS terminal recommended)
- **figlet** (optional, for enhanced "Game Over" display)
  - Install via `pkg install figlet` (Termux) or `sudo apt install figlet` (Linux)

---

## Getting Started

### 1. Clone the Repository

```sh
git clone https://github.com/yourusername/BrickGame.git
cd BrickGame
```

### 2. Compile the Game

```sh
javac -cp ".:jline-3.25.0.jar" BrickGame.java
```

### 3. Run the Game

```sh
java -cp ".:jline-3.25.0.jar" BrickGame
```
