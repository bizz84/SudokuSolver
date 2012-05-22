/**
 * JSON Sudoku solver is covered under the Creative Commons Attribution 3.0 Unported License
 * http://creativecommons.org/licenses/by/3.0/
 * 
 * @author: Andrea Bizzotto {@link www.musevisions.com}, {@link www.bizzotto.biz}
 * @email: bizz84dev@gmail.com
 */
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
