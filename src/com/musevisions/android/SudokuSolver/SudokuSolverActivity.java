package com.musevisions.android.SudokuSolver;

import java.io.InputStream;



import android.app.Activity;
import android.os.Bundle;

public class SudokuSolverActivity extends Activity {
	
	private int [] mTestInput;
	private GridView mGridView;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.solver);
        
        InputStream is = getResources().openRawResource(R.raw.input_array);
        mTestInput = JSONHelper.getSudokuArray(is);
        
        mGridView = (GridView)findViewById(R.id.gridView);
        
        mGridView.setGameInput(mTestInput);
    }
}