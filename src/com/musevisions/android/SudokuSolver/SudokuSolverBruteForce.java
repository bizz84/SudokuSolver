package com.musevisions.android.SudokuSolver;

import java.util.HashSet;
import java.util.Set;

import com.musevisions.android.SudokuSolver.SudokuCore.SolverListener;
import com.musevisions.android.SudokuSolver.SudokuCore;

public class SudokuSolverBruteForce {

	/** Method to solve a valid puzzle */
	static public int[] solve(int puzzle[], SolverListener listener) {

		/* Handle user break */
		if (listener != null && !listener.onSolverEvent(puzzle))
			return null;
		
		if (isComplete(puzzle))
			return puzzle;
		int i;
		for (i = 0; i < SudokuCore.GRID_DIM*SudokuCore.GRID_DIM; i++) {
			/* Once we find an unused position, try to find a value for it and solve recursively */
			if (puzzle[i] == 0) {
				int row = i / SudokuCore.GRID_DIM;
				int col = i % SudokuCore.GRID_DIM;
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
					if (solution != null || !listener.onSolverEvent(puzzle))
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
		for (int i = 0; i < SudokuCore.GRID_DIM*SudokuCore.GRID_DIM; i++) {
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
			int j, end = (row + 1) * SudokuCore.GRID_DIM;
			for (j = row * SudokuCore.GRID_DIM; j < end; j++) {
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
		for (int value = 1; value <= SudokuCore.GRID_DIM; value++) {
			/* For given value, check if it's contained in row */
			int j, end = col + SudokuCore.GRID_DIM*SudokuCore.GRID_DIM;
			for (j = col; j < end; j += SudokuCore.GRID_DIM) {
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
		
		int subcol = col / SudokuCore.GRID_SUB_DIM;
		int subrow = row / SudokuCore.GRID_SUB_DIM;
		int indices[] = SudokuCore.SubGridIndices[subcol + subrow * SudokuCore.GRID_SUB_DIM];
		
		Set<Integer> set = new HashSet<Integer>();
		for (int value = 1; value <= SudokuCore.GRID_DIM; value++) {
			/* For given value, check if it's contained in row */
			int j;
			for (j = 0; j < SudokuCore.GRID_DIM; j++) {
				if (puzzle[indices[j]] == value)
					break;
			}
			/* Not found: insert */
			if (j == SudokuCore.GRID_DIM) {
				set.add(value);
			}
		}
		return set;
	}
	
	
}