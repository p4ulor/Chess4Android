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

## Special movements for the king (only when the king is selected, this is possible, when selecting the rook, it can't switch with king)
- O-O -> Kingside castle
- O-O-O -> Queenside castle (meaning the move is done towards the side of where the queen is initially positioned at, in respective to the piece's color)
- [Read more here](https://en.wikipedia.org/wiki/Castling
- [How to "castle"](https://youtu.be/4jXQyGaeUV8)

### Extra (these symbols are placed at the end of a move):
- \+ -> means, that it's a check (the color that played can capture the opposite color king)
- \# -> means check mate (game over)

### Extra rules
- On a check, you can only move pieces that kill the piece that made the check, block it's path, or move the king out of the reach of that piece
- [En Passant, pawn move](https://www.youtube.com/watch?v=c_KRIH0wnhE)

#### When a pawn reaches the opponent's side limit, it can turn into: a queen, horse, tower or bishop. Format examples:
- d1=q   //turns into queen
- d1=q#  // turns into queen and checks king
- gxh1=g // eats piece and turns into queen
- gxh8=q# // pawn at g7 ate rook at h8, turns into queen and checks king

