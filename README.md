# Chess Engine (Java)

A custom chess game and rule engine and GUI built from scratch in Java 17 using Maven.

I started this project as a practical exercise to get better at object-oriented design and Java Swing. 
I followed a few basic rule-implementation tutorials to get the initial logic down, but the actual board state, piece behavior, and move validation were all built from scratch without using any third-party chess libraries.


## V1:
 <img width="637" height="724" alt="Captura de tela 2026-09-02 114329" src="https://github.com/user-attachments/assets/5397e8cd-9aee-4257-9e1b-61b7dd64dfd7" />
## V2:
<img width="848" height="690" alt="image" src="https://github.com/user-attachments/assets/d3a1830c-46ac-4df4-8e9c-24014e2889f8" />


## Features

* GUI: Built with Java Swing, featuring mouse controls for moving pieces.

* Full Rule Enforcement: Supports legal move calculation, check/checkmate detection, and special moves (en passant, castling, pawn promotion).

* Turn Indicator: A visual "traffic light" in the UI to show whose turn it is.


## Key Takeaways

* **Polymorphism and inheritance for Game Rules:** Used an abstract `Piece` class with custom move logic implemented for each piece type.
* **State Management:** Learned how to track board state history to accurately detect checks and handle en passant conditions.
* **Swing Event Handling:** Worked through repaint issues and mouse listener events to keep the board UI smooth.

## Art

Illustrated Chess Pieces and Board Pack by Joszs in itch.io was used for the pieces. The license for the assets is Creative Commons Attribution 4.0 International.
