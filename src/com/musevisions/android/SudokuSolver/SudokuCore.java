package com.musevisions.android.SudokuSolver;

import java.util.HashSet;
import java.util.Set;


/** Class defining the functionality for verifying and solving Sudoku Puzzles 
 * 
 * This is not Android-specific so it can be easily ported to other projects
 * @author andrea
 *
 */
public class SudokuCore {

	static final int GRID_DIM = 9;
	static final int GRID_SUB_DIM = 3;
	
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
	
    public interface SolverListener {
    	public void onSolverEvent(int result[]);
    }
	/** Method to solve a valid puzzle */
	static public int[] solve(int puzzle[], SolverListener listener) {
		
		if (listener != null)
			listener.onSolverEvent(puzzle);
		
		if (isComplete(puzzle))
			return puzzle;
		int i;
		for (i = 0; i < GRID_DIM*GRID_DIM; i++) {
			/* Once we find an unused position, try to find a value for it and solve recursively */
			if (puzzle[i] == 0) {
				int row = i / GRID_DIM;
				int col = i % GRID_DIM;
				/* Add some constraints: pick only values that are allowed for this position */
				Set<Integer> unusedInRow = unusedInRow(row, puzzle);
				Set<Integer> unusedInCol = unusedInCol(col, puzzle);
				Set<Integer> unusedInQuad = unusedInQuad(row, col, puzzle);
				Set<Integer> intersection = intersection(unusedInRow, unusedInCol, unusedInQuad);

				/* If no values are allowed for this position, current puzzle can't be solved. */
				if (intersection.size() == 0)
					return null;
				
				/* Try each of the allowed values */
				for (Integer value : intersection) {
					
					int tryThis[] = clone(puzzle, value, i);
					int solution[] = solve(tryThis, listener);
					if (solution != null)
						return solution;					
				}
			}
		}
		// Find first cell that is 0
		// Find valid value for cell [check row, col, quadrant]
		// If no valid value -> error!
		//
		
		return null;
	}
	
	/** Method to return whether all cells have been set */
	static private boolean isComplete(int puzzle[]) {
		for (int i = 0; i < GRID_DIM*GRID_DIM; i++) {
			if (puzzle[i] == 0)
				return false;
		}
		return true;
	}
	/** Clone input puzzle by updating given value at given position */ 
	static private int[] clone(int puzzle[], int newValue, int index) {
		int newPuzzle[] = puzzle.clone();
		newPuzzle[index] = newValue;
		return newPuzzle;	
	}
	
	static private Set<Integer> intersection(Set<Integer> set1, Set<Integer> set2) {
		
        boolean set1IsLarger = set1.size() > set2.size();
        Set<Integer> cloneSet = new HashSet<Integer>(set1IsLarger ? set2 : set1);
        cloneSet.retainAll(set1IsLarger ? set1 : set2);
        return cloneSet;
	}
	static private Set<Integer> intersection(Set<Integer> valuesRow, Set<Integer> valuesCol, Set<Integer> valuesQuad) {
				
		Set<Integer> rowsCols = intersection(valuesRow, valuesCol);
		
		Set<Integer> result = intersection(rowsCols, valuesQuad);
        
		return result;
	}
	
	/** Method to find unused values in row */
	static private Set<Integer> unusedInRow(int row, int puzzle[]) {
		
		Set<Integer> set = new HashSet<Integer>();
		/* Try all values and see if they are unused in row */
		for (int value = 1; value <= 9; value++) {
			/* For given value, check if it's contained in row */
			int j, end = (row + 1) * GRID_DIM;
			for (j = row * GRID_DIM; j < end; j++) {
				if (puzzle[j] == value)
					break;
			}
			/* Not found: insert */
			if (j == end) {
				set.add(value);
			}
		}
		return set;
	}

	/** Method to find unused values in col */
	static private Set<Integer> unusedInCol(int col, int puzzle[]) {
		
		Set<Integer> set = new HashSet<Integer>();
		/* Try all values and see if they are unused in row */
		for (int value = 1; value <= GRID_DIM; value++) {
			/* For given value, check if it's contained in row */
			int j, end = col + GRID_DIM*GRID_DIM;
			for (j = col; j < end; j += GRID_DIM) {
				if (puzzle[j] == value)
					break;
			}
			/* Not found: insert */
			if (j == end) {
				set.add(value);
			}
		}
		return set;
	}

	/** Method to find unused values in sub-quadrant */
	static private Set<Integer> unusedInQuad(int row, int col, int puzzle[]) {
		
		int subcol = col / GRID_SUB_DIM;
		int subrow = row / GRID_SUB_DIM;
		int indices[] = SubGridIndices[subcol + subrow * GRID_SUB_DIM];
		
		Set<Integer> set = new HashSet<Integer>();
		for (int value = 1; value <= GRID_DIM; value++) {
			/* For given value, check if it's contained in row */
			int j;
			for (j = 0; j < GRID_DIM; j++) {
				if (puzzle[indices[j]] == value)
					break;
			}
			/* Not found: insert */
			if (j == GRID_DIM) {
				set.add(value);
			}
		}
		return set;
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
	
	/*
    interface SolverCallback {
    	public void callback(int result[]);
    }
    static class SudokuSolver {
    	
    	SolverCallback mListener;
    	int mPuzzle[];
    	public SudokuSolver(SolverCallback listener, int puzzle[]) {
    		mListener = listener;
    		mPuzzle = puzzle;
    		new Thread() { 
    			public void run() {
    				int result[] = SudokuCore.solve(mPuzzle);
    				mListener.callback(result);
    			};
    		}.start();
    	}    	
    }
    */
}
