# ConwayLife
App to visualize Conway's game of life. Coroutines was used to delay the changes so it can be the each state can be viewed with ease

Current logic app folows:
If the number of live cells within a cell is > 0 and <= 2, cell lives else it dies (a bit different from the conventional conway game of life)
Board is a wrap around array i.e cell 0,0 is the neighbor of cells 0,x, y,0, 0,y and x,y where x and y are the 
row and column length of board.
