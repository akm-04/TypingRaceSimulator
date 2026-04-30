# TypingRaceSimulator

Object Oriented Programming Project — ECS414U

## Project Structure

```
TypingRaceSimulator/
├── Part1/    # Textual simulation (Java, command-line)
└── Part2/    # GUI simulation (to be completed)
```

## Part 1 — Textual Simulation

### How to compile

Go inside the 'Part1' directory then open terminal in that directory. For windows you may type cmd in addressbar to open command prompt there. Then run the following to compile all files:

```bash
    javac *.java
```

### How to run

While inside the same Part1 directory after compiling,

To run the main racing simulation:
```bash
java TypingRace
```

To run the test framework for the Typist class:
```bash
java testTypist
```

## Part 2 — GUI Simulation

The graphical version of the Typing Race Simulator features interactive race configuration, customizable typists, detailed statistics, and a persistent global leaderboard with earnable titles and badges.
The graphical version is started by calling `startRaceGUI()` or by simply calling the main method of GUITypingRace.java

 ### How to compile
 
Go inside the 'Part2' directory then open terminal in that directory. For windows you may type cmd in addressbar to open command prompt there. Then run the following to compile all files:
 
```text
   javac *.java
```

 ### How to run
 
The main entry point for the GUI game is the `GUITypingRace` class. Run the GUI version of typing simulator by running the following command in the terminal:
 
```text
    java GUITypingRace
```

Or if you're using any specific test framework, the main gui can be called by invoking the specific method `startRaceGUI()`. Inspect main method of GUITypingRace.java for more information.

### Features Included (Option A Satisfied)
- **Interactive Setup:** Configure passage length, active typist count, and difficulty modifiers (Autocorrect, Caffeine Mode, Night Shift).
- **Custom Typists:** Select from dynamic typing styles, keyboards, colors, symbols, and physical accessories.
- **Analytics & History:** View personal best WPMs, historical race logs, and side-by-side typist comparisons. 
- **Persistent Leaderboard:** Race history and algorithm-based points are saved locally to `history.csv`.
- **Titles & Badges:** Typists automatically earn milestone titles like "🏆 Speed Demon" (3 consecutive wins) and "🛡️ Iron Fingers" (5 consecutive races without burnouts).

## Dependencies

- Java Development Kit (JDK) 11 or higher
- No external libraries required for Part 1
- Part 2 uses Java Swing (included in standard JDK).

## Notes

- All code should compile and run using standard command-line tools without any IDE-specific configuration.
- The starter code in Part1 was originally written by Ty Posaurus. It contains known issues — finding and fixing them is part of the coursework.
