package com.musevisions.android.SudokuSolver;

import android.os.AsyncTask;

public class SudokuRetrieverTask extends AsyncTask<Void, Void, Void> {
    SudokuRetriever mRetriever;
    SudokuRetrieverPreparedListener mListener;

    public SudokuRetrieverTask(SudokuRetriever retriever,
    		SudokuRetrieverPreparedListener listener) {
        mRetriever = retriever;
        mListener = listener;
    }

    @Override
    protected Void doInBackground(Void... arg0) {
        mRetriever.prepare();
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        mListener.onSudokuRetrieverPrepared();
    }

    public interface SudokuRetrieverPreparedListener {
        public void onSudokuRetrieverPrepared();
    }
}
