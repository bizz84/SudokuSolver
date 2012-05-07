package com.musevisions.android.SudokuSolver;

import com.musevisions.android.SudokuSolver.SudokuCore.SolverListener;
import com.musevisions.android.SudokuSolver.SudokuCore.SolverMethod;

import android.os.AsyncTask;


public class SudokuSolverTask extends AsyncTask<Void, int[], Void> implements SolverListener {
    private int mPuzzle[];
    private int mSolution[];
    private SolverListener mListener;
    private GridView mGridView;
    private SolverMethod mMethod;
    private boolean inProgress;


    public SudokuSolverTask(int puzzle[], SolverListener listener,
    		GridView updateView, SolverMethod method) {
        mPuzzle = puzzle;
        mListener = listener;
        mGridView = updateView;
        mMethod = method;
        inProgress = false;
    }

    @Override
    protected Void doInBackground(Void... arg0) {
    	switch(mMethod) {
    	case SOLVER_BRUTE_FORCE:
            mSolution = SudokuCore.solveMethodBruteForce(mPuzzle, this);
            break;
    	case SOLVER_OPTIMISED:
            mSolution = SudokuCore.solveMethodOptimised(mPuzzle);
            break;            
    	}
        return null;
    }
    
    
    @Override
    protected void onPreExecute() {
    	inProgress = true;

    }

    protected void onProgressUpdate(int[]... values) {
    	mGridView.setSolution(values[0]);
    }

    /** Listener */
    @Override
    protected void onPostExecute(Void result) {
    	inProgress = false;
        mListener.onSolverEvent(mSolution);
    }

    /** Updater */
	@Override
	public boolean onSolverEvent(int[] result) {
		publishProgress(result);
		if (isCancelled() || !inProgress) {
			return false;
		}
		return true;
	}

	public boolean inProgress() {
		return inProgress;
	}


}
