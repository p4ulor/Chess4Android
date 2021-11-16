# PDM-2122i-LI5X-G19
ChessRoyale like app for Mobile Device Programming discipline

### Tag **chess_royale_1**
This app gets today's chess puzzle and allows the user to solve it.


## Shortcuts for main source code
- [src](Chess4Android/app/src/main/java/pt/isel/pdm/chess4android)
- [layout (activities)](Chess4Android/app/src/main/res/layout)

## Consult today's game here
- https://lichess.org/training/daily

## Places to learn coordinates and moves
- https://lichess.org/analysis
- http://www.chessfornovices.com/chessnotation2.html
- [How to "castle"](https://youtu.be/4jXQyGaeUV8)
- https://en.wikipedia.org/wiki/Rules_of_chess
- https://levelup.gitconnected.com/finding-all-legal-chess-moves-2cb872d05bc6
- http://www.jimmyvermeer.com/rules.html
- https://www.chess.com/forum/view/game-analysis/in-a-computer-analysis---means-what
- https://www.chess.com/forum/view/general/all-the-terminology-and-symbol-meanings

- No letter -> Pawn
- B -> Bishop
- N -> Knight
- R -> Rook
- K -> King
- Q -> Queen
- ?x?? -> Movement that envolves a piece from the column ? killing the piece in position ??. b**x**c5 per example

## Special movements for the king (only when the king is selected, this is possible, when selecting the rook, it can't switch with king)
- O-O -> Kingside castle
- O-O-O -> Queenside castle (meaning the move is done towards the side of where the queen is initially positioned at, in respective to the piece's color)

[Read more here](https://en.wikipedia.org/wiki/Castling

### Extra (these symbols are placed at the end of a move):
- \+ -> means, that it's a check (the color that played can capture the opposite color king)
- \# -> means check mate (game over)

### Extra rules
- On a check, you can only move pieces that kill the piece that made the check, block it's path, or move the king out of the reach of that piece

#### When a pawn reaches the opponent's side limit, it can turn into: a queen, horse, tower or bishop. Format:
- d1=q   //turns into queen
- d1=q#  // turns into queen and checks king
- gxh1=g // eats piece and turns into queen
- gxh8=q# // pawn at g7 ate rook at h8, turns into queen and checks king

