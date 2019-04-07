# ConwayLife
App to visualize Conway's game of life. Coroutines was used to delay the changes so it can be the each state can be viewed with ease

Current logic app folows:
If the number of live cells within a cell is > 0 and <= 2, cell lives else it dies (this logic is not really conway's; 
 I had to use it due to a custom algorithm task I had to solve. should change this to the conventional conway's game soon)
Board is a wrap around array i.e cell 0,0 is the neighbor of cells 0,x, y,0, 0,y and x,y where x and y are the 
row and column length of board.
