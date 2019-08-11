// Name: Alay Dilipbhai Shah
// USC NetID: 4038-9488-19
// CS 455 PA3
// Spring 2019

import java.util.Arrays;
import java.util.Random;

/** 
   MineField
      class with locations of mines for a game.
      This class is mutable, because we sometimes need to change it once it's created.
      mutators: populateMineField, resetEmpty
      includes convenience method to tell the number of mines adjacent to a location.
 */

public class MineField {
   
   // <put instance variables here>
	private static final boolean ISMINE = true;
	private static final boolean NOMINE = false;
	private boolean[][] mineArr;
	private int num_mines;
	private Random generator = new Random();
	private boolean mines_placed = false;

	
   /**
      Create a minefield with same dimensions as the given array, and populate it with the mines in the array
      such that if mineData[row][col] is true, then hasMine(row,col) will be true and vice versa.  numMines() for
      this minefield will corresponds to the number of 'true' values in mineData.
    * @param mineData  the data for the mines; must have at least one row and one col.
    */
	
   public MineField(boolean[][] mineData) {
	   mineArr = new boolean[mineData.length][mineData[0].length];
	   for (int i=0; i<mineData.length; i++) {
		  for (int j=0; j<mineData[0].length; j++) {
			 mineArr[i][j] = mineData[i][j];
		  }
	   }
	   mines_placed = true;
	   num_mines = this.numMines();
   }
   
   
   /**
      Create an empty minefield (i.e. no mines anywhere), that may later have numMines mines (once 
      populateMineField is called on this object).  Until populateMineField is called on such a MineField, 
      numMines() will not correspond to the number of mines currently in the MineField.
      @param numRows  number of rows this minefield will have, must be positive
      @param numCols  number of columns this minefield will have, must be positive
      @param numMines   number of mines this minefield will have,  once we populate it.
      PRE: numRows > 0 and numCols > 0 and 0 <= numMines < (1/3 of total number of field locations). 
    */
   
   public MineField(int numRows, int numCols, int numMines) {
	   num_mines = numMines;
	   mineArr = new boolean[numRows][numCols];
	   resetEmpty();
   }

   
   /**
      Removes any current mines on the minefield, and puts numMines() mines in random locations on the minefield,
      ensuring that no mine is placed at (row, col).
      @param row the row of the location to avoid placing a mine
      @param col the column of the location to avoid placing a mine
      PRE: inRange(row, col)
    */
   
   public void populateMineField(int row, int col) {
	   	   
	 resetEmpty();
	   
	 int num_mines_placed = 0;
	 while (num_mines_placed < num_mines) {
		   
		int mine_row = generator.nextInt(mineArr.length);
		int mine_col = generator.nextInt(mineArr[0].length);
		   
		if (mine_row != row && mine_col != col && mineArr[mine_row][mine_col] == NOMINE) {
			mineArr[mine_row][mine_col] = ISMINE;
			num_mines_placed++;   
		}
	 }
	 mines_placed = true;
	  
   }

   
   /**
      Reset the minefield to all empty squares.  This does not affect numMines(), numRows() or numCols()
      Thus, after this call, the actual number of mines in the minefield does not match numMines().  
      Note: This is the state the minefield is in at the beginning of a game.
    */
   
   public void resetEmpty() {
			   
	 for (int i=0; i<mineArr.length; i++) {
		for (int j=0; j<mineArr[i].length; j++) {
			mineArr[i][j] = NOMINE;
		}
	 }
	 mines_placed = false;
		 
   }

   
  /**
     Returns the number of mines adjacent to the specified mine location (not counting a possible 
     mine at (row, col) itself).
     Diagonals are also considered adjacent, so the return value will be in the range [0,8]
     @param row  row of the location to check
     @param col  column of the location to check
     @return  the number of mines adjacent to the square at (row, col)
     PRE: inRange(row, col)
   */
   
   public int numAdjacentMines(int row, int col) {
	  int countAdjMines = 0; 
	  for (int i = row-1; i < row+2; i++) {
		  for (int j = col-1; j < col+2; j++) {
			  if (inRange(i, j)) {
				  if (i!=row || j!=col) {
					  if (hasMine(i, j)) {
						  countAdjMines++;
					  }					  
				  }
			  }
		  }
	  }
      return countAdjMines;       
   }   

   
   /**
      Returns true iff (row,col) is a valid field location.  Row numbers and column numbers
      start from 0.
      @param row  row of the location to consider
      @param col  column of the location to consider
      @return whether (row, col) is a valid field location
   */
   
   public boolean inRange(int row, int col) {
	  
	   boolean flag = false;
	  
	  if (row >= 0 && row < mineArr.length) {
		  if (col >=0 && col < mineArr[0].length) {
			  flag = true;
		  }
	  }	  
      return flag;       
   }

   
   /**
      Returns the number of rows in the field.
      @return number of rows in the field
   */
   
   public int numRows() {
      return mineArr.length;     
   }

   
   /**
      Returns the number of columns in the field.
      @return number of columns in the field
   */    

   public int numCols() {
      return mineArr[0].length;       
   }

   
   /**
      Returns whether there is a mine in this square
      @param row  row of the location to check
      @param col  column of the location to check
      @return whether there is a mine in this square
      PRE: inRange(row, col)   
   */    
   
   public boolean hasMine(int row, int col) {
      return mineArr[row][col];       
   }

   
   /**
      Returns the number of mines you can have in this minefield.  For mines created with the 3-arg constructor,
      some of the time this value does not match the actual number of mines currently on the field.  See doc for that
      constructor, resetEmpty, and populateMineField for more details.
    * @return
    */
   
   public int numMines() {
	   
	  int count_mines; 
	   
	  if (mines_placed) {  
		 count_mines = 0;
		 
		  for (int i=0; i<mineArr.length; i++) {
			  for (int j=0; j<mineArr[i].length; j++) {
				  if(hasMine(i, j)) {
					  count_mines++;
				  }
			  }
		  }
	  }
	  else {
		  count_mines = num_mines;
	  }
	  
      return count_mines;       
   }

   
   /**
    * Returns the mineArr Array in String format
    */

   public String toString() {
	   String arrstr = Arrays.deepToString(mineArr);
	   return arrstr;
   }

   // <put private methods here>   
}

