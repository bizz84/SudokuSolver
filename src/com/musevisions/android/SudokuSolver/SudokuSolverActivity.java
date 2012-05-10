package com.musevisions.android.SudokuSolver;

import java.io.InputStream;

import org.json.JSONArray;

import com.musevisions.android.SudokuSolver.HttpPostUtils.HttpCallbackListener;
import com.musevisions.android.SudokuSolver.SudokuCore.SolverListener;
import com.musevisions.android.SudokuSolver.SudokuCore.SolverMethod;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

/** Main class handling the UI for the Sudoku Solver */ 
public class SudokuSolverActivity extends Activity implements SolverListener {
	
	private static final String TAG = "SudokuSolverActivity";
	private GridView mGridView;
	private CheckBox mChkBruteForce; 
	private SudokuSolverTask mSolver;
	private int [] mCurrentInput;
	private String mPuzzleName;
    private AlertDialog mDialog;


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
        
        mChkBruteForce = (CheckBox)findViewById(R.id.checkBruteForce);
        
        Button verify = (Button)findViewById(R.id.btnVerify);
        verify.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				/* Verify input, or if puzzle has been solved, output. */
				int [] output = mGridView.getSolution();
				int [] toVerify = output != null ? output : mCurrentInput;
				String error = SudokuCore.verify(toVerify);
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
				SolverMethod method = mChkBruteForce.isChecked() ? SolverMethod.SOLVER_BRUTE_FORCE : SolverMethod.SOLVER_OPTIMISED; 
				mGridView.setSolution(null);
				SudokuSolverActivity.this.mSolver = new SudokuSolverTask(mCurrentInput,
						SudokuSolverActivity.this, mGridView, method);
				
				
		    	mDialog = ProgressDialog.show(SudokuSolverActivity.this, "Solving", "Please wait...",
		    				false, true, new OnCancelListener() {								
								@Override
								public void onCancel(DialogInterface dialog) {
									if (mSolver != null)
										mSolver.cancel(true);
								}
							});

				SudokuSolverActivity.this.mSolver.execute();
			}
		});
    }

    /** Callback to be called when solver thread completes */
	@Override
	public boolean onSolverEvent(int[] result) {
    	mDialog.dismiss();
		mSolver = null;
		if (result != null) {
			mGridView.setSolution(result);
			writeOutput(result);
		}
		return true;
	}

    public void updateView(int newInput[], String name) {
    	mPuzzleName = name;
    	mCurrentInput = newInput;
    	mGridView.setGameInput(newInput);
    	mGridView.setSolution(null);
    }


    private void writeOutput(int[] result) {
    	if (HttpPostUtils.isConnected(this) && mPuzzleName != null) {
        	JSONArray json = JSONHelper.toJSONArray(result);
    		HttpPostUtils.postResult(mPuzzleName, json, this);
    	}
    }
    
    public void ShortToast(String msg) {
    	
		Toast.makeText(SudokuSolverActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

   
}