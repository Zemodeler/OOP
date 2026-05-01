# TypingRaceSimulator

Object Oriented Programming Project — ECS414U

## Overview

This project is a Java typing race simulator developed for the ECS414U Object Oriented Programming module.

The project is split into two parts:

- `Part1/` — textual command-line typing race simulator
- `Part2/` — graphical user interface version of the typing race simulator

The program simulates typists racing through a passage of text. Typists can move forward when they type correctly, slide backwards when they mistype, and temporarily burn out if they push too hard.

## Project Structure

```
TypingRaceSimulator/
├── Part1/
│   ├── Typist.java
│   └── TypingRace.java
│
├── Part2/
│   ├── Typist.java
│   ├── TypingRace.java
│   ├── TypingRaceGUI.java
│   ├── RaceHistoryRecord.java
│   ├── RewardProfile.java
│   └── SponsorProfile.java
│
├── README.md
└── TypingRaceSimulator_Spec.pdf
```

## Part 1 — Textual Simulation

### How to compile

```bash
cd Part1
javac Typist.java TypingRace.java
```

### How to run

The race is started by calling `main()` on a `TypingRace` object.

```bash
java TypingRace

```
Example Output:
```
TYPING RACE — passage length: 40 chars
===========================================
|        ①                                | TURBOFINGERS (Accuracy: 0.8)
|      ②                                  | QWERTY_QUEEN (Accuracy: 0.7)
|   ③~                                    | HUNT_N_PECK (Accuracy: 0.68) BURNT OUT (2 turns)
===========================================
[zz] = burnt out    [<] = just mistyped

And the winner is... TURBOFINGERS!
Final accuracy: 0.82 improved from 0.8
```

## Part 2 — GUI Simulation

Part 2 is the graphical version of the simulator.

It contains a Java Swing interface that allows the user to configure and run typing races visually.

The GUI version includes features such as:

passage selection
custom passage input
configurable number of typists
difficulty modifiers
typist customisation
performance statistics
race history
personal best tracking
reward or leaderboard-related features

### How to Compile

```bash
cd Part2
javac TypingRace.java Typist.java RaceHistoryRecord.java RewardProfile.java SponsorProfile.java TypingRaceGUI.java
```
### How to run
```
java TypingRaceGUI
```

## GitHub Rep
All available in the .git folder
2 Branches

main - Part 1
gui-development - part 2


## Dependencies

- Java Development Kit (JDK) 11 or higher
- No external libraries required for Part 1
- Part 2 may use Java Swing (included in standard JDK) or JavaFX
