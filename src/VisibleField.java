import java.util.Arrays;

/**
  VisibleField class
  This is the data that's being displayed at any one point in the game (i.e., visible field, because it's what the
  user can see about the minefield), Client can call getStatus(row, col) for any square.
  It actually has data about the whole current state of the game, including  
  the underlying mine field (getMineField()).  Other accessors related to game status: numMinesLeft(), isGameOver().
  It also has mutators related to actions the player could do (resetGameDisplay(), cycleGuess(), uncover()),
  and changes the game state accordingly.
  
  It, along with the MineField (accessible in mineField instance variable), forms
  the Model for the game application, whereas GameBoardPanel is the View and Controller, in the MVC design pattern.
  It contains the MineField that it's partially displaying.  That MineField can be accessed (or modified) from 
  outside this class via the getMineField accessor.  
 */

public class VisibleField {
   // ----------------------------------------------------------   
   // The following public constants (plus numbers mentioned in comments below) are the possible states of one
   // location (a "square") in the visible field (all are values that can be returned by public method 
   // getStatus(row, col)).
   
   // Covered states (all negative values):
   public static final int COVERED = -1;   // initial value of all squares
   public static final int MINE_GUESS = -2;
   public static final int QUESTION = -3;

   // Uncovered states (all non-negative values):
   
   // values in the range [0,8] corresponds to number of mines adjacent to this square
   
   public static final int MINE = 9;      // this loc is a mine that hasn't been guessed already (end of losing game)
   public static final int INCORRECT_GUESS = 10;  // is displayed a specific way at the end of losing game
   public static final int EXPLODED_MINE = 11;   // the one you uncovered by mistake (that caused you to lose)
   // ----------------------------------------------------------   
  
   // <put instance variables here>
   private int[][] visMineField;
   private MineField storedMineField;
   private boolean game_over;
   

   /**
      Create a visible field that has the given underlying mineField.
      The initial state will have all the mines covered up, no mines guessed, and the game
      not over.
      @param mineField  the minefield to use for for this VisibleField
    */
   
   public VisibleField(MineField mineField) {
	  storedMineField = mineField; 
      visMineField = new int[mineField.numRows()][mineField.numCols()];
      resetGameDisplay();
   }
 
   
   /**
      Reset the object to its initial state (see constructor comments), using the same underlying
      MineField. 
   */
   
   public void resetGameDisplay() {
	   for (int i = 0; i < visMineField.length; i++) {
		   for (int j = 0; j < visMineField[i].length; j++) {
			   visMineField[i][j] = COVERED;
			   game_over = false;
		   }
	   }
   }
  
   
   /**
      Returns a reference to the mineField that this VisibleField "covers"
      @return the minefield
    */
   
   public MineField getMineField() {
      return storedMineField;       
   }
   
   
   /**
      Returns the visible status of the square indicated.
      @param row  row of the square
      @param col  col of the square
      @return the status of the square at location (row, col).  See the public constants at the beginning of the class
      for the possible values that may be returned, and their meanings.
      PRE: getMineField().inRange(row, col)
    */
   
   public int getStatus(int row, int col) {
      return visMineField[row][col];       
   }

   
   /**
      Returns the the number of mines left to guess.  This has nothing to do with whether the mines guessed are correct
      or not.  Just gives the user an indication of how many more mines the user might want to guess.  So the value can
      be negative, if they have guessed more than the number of mines in the minefield.     
      @return the number of mines left to guess.
    */
   
   public int numMinesLeft() {
      int mines_left = storedMineField.numMines();       
      for (int i=0; i<visMineField.length; i++) {
    	  for (int j=0; j<visMineField[i].length; j++) {
    		  if (visMineField[i][j] == MINE_GUESS) {
    			  if (mines_left > 0) {
    				  mines_left--;
    			  }  
    		  }
    	  }
      }
      return mines_left;
   }
 
   
   /**
      Cycles through covered states for a square, updating number of guesses as necessary.  Call on a COVERED square
      changes its status to MINE_GUESS; call on a MINE_GUESS square changes it to QUESTION;  call on a QUESTION square
      changes it to COVERED again; call on an uncovered square has no effect.  
      @param row  row of the square
      @param col  col of the square
      PRE: getMineField().inRange(row, col)
    */
   
   public void cycleGuess(int row, int col) {
	  if (visMineField[row][col] == COVERED) {
		  visMineField[row][col] = MINE_GUESS;
	  }
	  else if (visMineField[row][col] == MINE_GUESS) {
		  visMineField[row][col] = QUESTION;
	  }
	  else if (visMineField[row][col] == QUESTION) {
		  visMineField[row][col] = COVERED;
	  }
   }

   
   /**
      Uncovers this square and returns false iff you uncover a mine here.
      If the square wasn't a mine or adjacent to a mine it also uncovers all the squares in 
      the neighboring area that are also not next to any mines, possibly uncovering a large region.
      Any mine-adjacent squares you reach will also be uncovered, and form 
      (possibly along with parts of the edge of the whole field) the boundary of this region.
      Does not uncover, or keep searching through, squares that have the status MINE_GUESS. 
      Note: this action may cause the game to end: either in a win (opened all the non-mine squares)
      or a loss (opened a mine).
      @param row  of the square
      @param col  of the square
      @return false   iff you uncover a mine at (row, col)
      PRE: getMineField().inRange(row, col)
    */
   
   public boolean uncover(int row, int col) {
	
	  if (storedMineField.hasMine(row, col)){
		  visMineField[row][col] = EXPLODED_MINE;
		  game_over = true;

	  }
	  else if (storedMineField.numAdjacentMines(row, col) > 0) {
		  
		  visMineField[row][col] = visMineField[row][col] = storedMineField.numAdjacentMines(row, col);
	  }
	  
	  else {
		  recursive_uncover(row, col);
	  }
	  
      return !game_over;      
   }
 
   
   /**
      Returns whether the game is over.
      (Note: This is not a mutator.)
      @return whether game over
    */
   
   public boolean isGameOver() {
	   
	  if (game_over || hasWon()) {
		 wrap_up();
	  }
 
      return game_over || hasWon();      
   }
 
   
   /**
      Returns whether this square has been uncovered.  (i.e., is in any one of the uncovered states, 
      vs. any one of the covered states).
      @param row of the square
      @param col of the square
      @return whether the square is uncovered
      PRE: getMineField().inRange(row, col)
    */
   
   public boolean isUncovered(int row, int col) {
	  
	  if (visMineField[row][col] > -1) {
		 return true;  
	  }
      return false;       
   }
   
   /**
    * Returns the visMineField Array in String format
    */
   
   public String toString() {
	   String arrstr = Arrays.deepToString(visMineField);
	   return arrstr;
   }

   
   // <put private methods here>

   /**
    * Helper method for uncovering the empty square recursively.
    * While recursively opening the square, if a mine is found. then it does nothing and returns.
    * While recursively opening the square, if the square is already uncovered/openend, then it does nothing and returns.
    * While recursively opening the square, if the square has been marked as a MINE_GUESS, then it does nothing and just returns.
    * Else, if there are no adjacent mines, then it keeps opening the square recursively until one of the above conditions are met at all the border squares.
    * @param row
    * @param col
    */
   
   private void recursive_uncover(int row, int col) {
	  
      if (storedMineField.hasMine(row, col)) {
    	 return;
      }
      else if (this.isUncovered(row, col)) {
    	 return;
      }
      else if (visMineField[row][col] == MINE_GUESS) {
    	 return;
      }
      else {
    	 visMineField[row][col] = storedMineField.numAdjacentMines(row, col);
    	 for (int i = row-1; i < row+2; i++) {
    		for (int j = col-1; j < col+2; j++) {
    			if (storedMineField.inRange(i, j)) {
    				if (i!=row || j!=col) {
    					if (storedMineField.numAdjacentMines(i, j) > 0 && !storedMineField.hasMine(i, j)) {		// If square has numAdjacentMines > 0, and the square to be opened does not have a mine 		
    						if (visMineField[i][j] != MINE_GUESS) {												// or is not marked as mine_guess, then open that square.
    							visMineField[i][j] = storedMineField.numAdjacentMines(i, j);
    						}
    					}
    					else {
    					recursive_uncover(i, j);                                                                // Else, recursively open that square by passing that row and col in the function again.
    					}
    				}
    			}
    		} 
    	 }
      }
   }
   
   
   /**
    * Returns whether the player has won the game by successfully opening all the non-mine squares.
    * @return
    */
   
   private boolean hasWon() {
	  int guessed_mines = 0;
	  for (int i=0; i<visMineField.length; i++) {
		  for (int j=0; j<visMineField[i].length; j++) {
			  if (visMineField[i][j] < 0) {
				  guessed_mines++;
			  }
		  }
	  }
	  if (guessed_mines == storedMineField.numMines()) {
		  return true;
	  }
	  return false;
   }
   
   /**
    * Wraps up the game after the player has won or lost it.
    * If the player lost the game, then sets the status of the mine player was not able to guess as MINE and also sets the status of all the wrong Mine guesses by player as INCORRECT guess.
    * If the player won the game, then sets all the squares which player did not mark as MINE_GUESS to MINE_GUESS.
    */
   private void wrap_up() {
   
	  if (game_over) {
		  for (int i=0; i<visMineField.length; i++) {
			  for(int j=0; j<visMineField[i].length; j++) {
				  if (storedMineField.hasMine(i,j) && visMineField[i][j] != MINE_GUESS) {
					 if (visMineField[i][j] != EXPLODED_MINE) {
						 visMineField[i][j] = MINE;
					 }
				  }
				  if (!storedMineField.hasMine(i,j) && visMineField[i][j] == MINE_GUESS) {
					 visMineField[i][j] = INCORRECT_GUESS;
				  }
			  }
		  }
	  }
	  else {
		 for (int i=0; i < visMineField.length; i++) {
			for (int j=0; j < visMineField[i].length; j++) {
				if (storedMineField.hasMine(i, j)) {
					visMineField[i][j] = MINE_GUESS;
				}
			}
		 }
	  }
   }  
}

