## Places to learn coordinates and moves
- https://lichess.org/analysis
- http://www.chessfornovices.com/chessnotation2.html
- https://en.wikipedia.org/wiki/Rules_of_chess
- https://levelup.gitconnected.com/finding-all-legal-chess-moves-2cb872d05bc6
- http://www.jimmyvermeer.com/rules.html
- https://www.chess.com/forum/view/game-analysis/in-a-computer-analysis---means-what
- https://www.chess.com/forum/view/general/all-the-terminology-and-symbol-meanings

## About the encoding of the json
- The movements to build up the puzzle are in the property: **game.pgn**
- The solution to the puzzle is in the property: **puzzle.solution**

- No letter -> Pawn
- B -> Bishop
- N -> Knight
- R -> Rook
- K -> King
- Q -> Queen
- ?x?? -> Movement that envolves a piece from the column ? killing the piece in position ??. b**x**c5 per example

### Extra rules
- [En Passant, pawn move](https://www.youtube.com/watch?v=c_KRIH0wnhE). If your opponement's pawn moves 2 places (in a pawn's first move) and lands on side by side with one of your pawns, in your turn, with your pawn, you can move diagonaly and land on the position behind your oponements pawns and eat that piece.
- Pawn promotion. Occurs when a pawn reaches the farthest rank from its original square. In other words, the tip of the other opponent board. When this happens, the player can replace the pawn for a queen, a rook, a bishop, or a knigh
### Special movements for the king (only when the king is selected, this is possible, when selecting the rook, it can't switch with king)
- O-O -> Kingside castle
- O-O-O -> Queenside castle (meaning the move is done towards the side of where the queen is initially positioned at, in respective to the piece's color)
- [Read more here](https://en.wikipedia.org/wiki/Castling
- [How to "castle"](https://youtu.be/4jXQyGaeUV8)

### Extra (these symbols are placed at the end of a move):
- \+ -> means, that it's a check (the color that played can capture the opposite color king)
- \# -> means check mate (game over)


#### When a pawn reaches the opponent's side limit, it can turn into: a queen, horse, tower or bishop. Format examples:
- d1=q   //turns into queen
- d1=q#  // turns into queen and checks king
- gxh1=g // eats piece and turns into queen
- gxh8=q# // pawn at g7 ate rook at h8, turns into queen and checks king

