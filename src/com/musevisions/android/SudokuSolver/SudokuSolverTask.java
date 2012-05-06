package com.musevisions.android.SudokuSolver;

import com.musevisions.android.SudokuSolver.SudokuCore.SolverListener;

import android.os.AsyncTask;


public class SudokuSolverTask extends AsyncTask<Void, int[], Void> implements SolverListener {
    int mPuzzle[];
    int mSolution[];
    SolverListener mListener;
    GridView mGridView;

    public SudokuSolverTask(int puzzle[],
    		SolverListener listener, GridView updateView) {
        mPuzzle = puzzle;
        mListener = listener;
        mGridView = updateView;
    }

    @Override
    protected Void doInBackground(Void... arg0) {
        mSolution = SudokuCore.solve(mPuzzle, this);
        return null;
    }
    
    @SuppressWarnings({"UnusedDeclaration"})
    protected void onProgressUpdate(int[]... values) {
    	mGridView.setGameInput(values[0]);
    }

    /** Listener */
    @Override
    protected void onPostExecute(Void result) {
        mListener.onSolverEvent(mSolution);
    }

    /** Updater */
	@Override
	public void onSolverEvent(int[] result) {
		publishProgress(result);
	}


}
