package com.musevisions.android.SudokuSolver;


/** Class defining the functionality for verifying and solving Sudoku Puzzles 
 * 
 * This is not Android-specific so it can be easily ported to other projects
 * @author andrea
 *
 */
public class SudokuCore {

	static final int GRID_DIM = 9;
	static final int GRID_SUB_DIM = 3;
	
	static enum SolverMethod {
		SOLVER_BRUTE_FORCE,
		SOLVER_OPTIMISED
	};
	
	/** Array representing the indices of each sub-block */ 
	static final int SubGridIndices[][] = {
		{  0,  1,  2, 
		   9, 10, 11,
		  18, 19, 20 },
		{  3,  4,  5, 
		  12, 13, 14,
		  21, 22, 23 },
		{  6,  7,  8, 
		  15, 16, 17,
		  24, 25, 26 },

		{ 27, 28, 29, 
		  36, 37, 38,
		  45, 46, 47 },
		{ 30, 31, 32, 
		  39, 40, 41,
		  48, 49, 50 },
		{ 33, 34, 35, 
		  42, 43, 44,
		  51, 52, 53 },

	
		{ 54, 55, 56, 
		  63, 64, 65,
		  72, 73, 74 },
		{ 57, 58, 59, 
		  66, 67, 68,
		  75, 76, 77 },
		{ 60, 61, 62, 
		  69, 70, 71,
		  78, 79, 80 }
	};

	/** Method to verify if given puzzle is correct i.e. the numbers from 1 to 9 are unique in the following cases:
	 * - Unique in all rows
	 * - Unique in all cols
	 * - Unique in all 3x3 sub-quadrants
	 * 
	 * @param puzzle array of length 81 representing the input configurations. A value of 0 means not assigned
	 * @return
	 */
	static public String verify(int puzzle[]) {
		
		/* Rows verification */
		for (int r = 0; r < GRID_DIM; r++) {
			
			int bin[] = initializeBin();
			for (int c = 0; c < GRID_DIM; c++) {
				int value = puzzle[i(r,c)]; 
				if (!updateAndCheck(value, bin)) {
					return "Row " + (r+1) + " is incorrect. " + errorString(value, bin);
				}
			}
		}
		/* Cols verification */
		for (int c = 0; c < GRID_DIM; c++) {
			
			int bin[] = initializeBin();
			for (int r = 0; r < GRID_DIM; r++) {
				int value = puzzle[i(r,c)]; 
				if (!updateAndCheck(value, bin)) {
					return "Col " + (c+1) + " is incorrect. " + errorString(value, bin);
				}
			}
		}
		/* Sub-quadrants verification */
		for (int i = 0; i < GRID_DIM; i++) {
			int bin[] = initializeBin();
			for (int j = 0; j < GRID_DIM; j++) {
				int quadrantIndices[] = SubGridIndices[i]; 
				int value = puzzle[quadrantIndices[j]];
				if (!updateAndCheck(value, bin)) {
					return "Quadrant " + (i+1) + " is incorrect. " + errorString(value, bin);
				}
			}
		}
		
		return null;
	}
	

	
	static private String errorString(int value, int bin[]) {
		return "Element " + value + " found " + bin[value-1] + " times";
	}
	
	static private boolean updateAndCheck(int value, int bin[]) {
		if (value > 0) {
			bin[value-1]++;
			if (bin[value-1] > 1) {
				return false;
			}
		}
		return true;		
	}	

	static private int i(int r, int c) {
		return r * GRID_DIM + c;
	}
	
	/** Initialize a bin with zeros */ 
	static private int[] initializeBin() {
		
		int bins[] = new int [GRID_DIM];
		for (int i = 0; i < GRID_DIM; i++)
			bins[i] = 0;
		return bins;
	}
	
	/** Solving methods */
    public interface SolverListener {
    	/** Return false if execution needs to be terminated */
    	public boolean onSolverEvent(int result[]);
    }
    static public int[] solveMethodBruteForce(int puzzle[], SolverListener listener) {
    	return SudokuSolverBruteForce.solve(puzzle, listener);
    }
    static public int[] solveMethodOptimised(int puzzle[]) {
    
    	int board[][] = new int[9][9];
    	for (int i = 0; i < 9; i++) {
    		for (int j = 0; j < 9; j++)
    			board[i][j] = puzzle[i*9+j];
    	}
    	SudokuSolverOptimised.solveBoard(board);
    	int output[] = new int[81];
    	for (int i = 0; i < 9; i++) {
    		for (int j = 0; j < 9; j++)
    			output[i*9+j] = board[i][j];
    	}
    	return output;    	
    }
    

}
