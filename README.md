# 🔤 String Genetic Algorithm (JavaFX)

A JavaFX-based visualization of a genetic algorithm that evolves random strings toward a target string. Inspired by the "Weasel Program" concept by Richard Dawkins/Infinite Monkey Theorem.

## 🚀 Features

- Real-time UI showing generation, best fitness, and current best match
- Configurable target string input
- Mutation, crossover, and roulette wheel selection implemented
- CSV export of all generations (triggered manually via Export button)

## 🎥 Demo

![GitHubProj](https://github.com/user-attachments/assets/94639a08-7f16-4eaf-8535-39d8b82a65cb)


## 🧠 How It Works

The program starts with a population of random strings and uses:
- **Fitness-based selection** (closer to target = higher chance)
- **Single-point crossover** for combining parents
- **Random mutation** to introduce diversity

It continues until one string perfectly matches the target input.

## 🛠️ Technologies Used

- Java 11+
- JavaFX
- MVC architecture
- CSV export with `FileWriter`

## ⚙️ Future features:

- N/a

## 💎 New Features:

- Added a button for settings where user can modify population size and mutation rate
- Improved UI
- FileChooser to choose where the CSV file is saved