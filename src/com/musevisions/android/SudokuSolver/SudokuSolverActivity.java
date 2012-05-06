package com.musevisions.android.SudokuSolver;

import java.io.InputStream;

import com.musevisions.android.SudokuSolver.SudokuCore.SolverListener;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class SudokuSolverActivity extends Activity implements SolverListener {
	
	private GridView mGridView;
	private int [] mCurrentInput;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.solver);
        
        ((MainTabActivity)getParent()).setSolverActivity(this);

        InputStream is = getResources().openRawResource(R.raw.input_array);
        mCurrentInput = JSONHelper.getSudokuArray(is);
        
        mGridView = (GridView)findViewById(R.id.gridView);
        
        mGridView.setGameInput(mCurrentInput);
        
        Button verify = (Button)findViewById(R.id.btnVerify);
        verify.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String error = SudokuCore.verify(mCurrentInput);
				if (error != null)
					ShortToast(error);
				else
					ShortToast("Puzzle is valid");
			}
		});
        
        Button solve = (Button)findViewById(R.id.btnSolve);
        solve.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				(new SudokuSolverTask(mCurrentInput, SudokuSolverActivity.this, mGridView)).execute();
				//new SudokuCore.SudokuSolver(SudokuSolverActivity.this, mCurrentInput);
			}
		});
    }

	@Override
	public void onSolverEvent(int[] result) {
		if (result != null) {
			updateView(result);
		}
	}


	
    public void updateView(int newInput[]) {
    	mCurrentInput = newInput;
    	mGridView.setGameInput(newInput);
    }
    
    public void ShortToast(String msg) {
    	
		Toast.makeText(SudokuSolverActivity.this, msg, Toast.LENGTH_SHORT).show();
    }
   
}