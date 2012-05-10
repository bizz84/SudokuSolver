package com.musevisions.android.SudokuSolver;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.util.AttributeSet;
import android.view.View;

/** Class used to display the Sudoku View */
public class GridView extends View {

	private static final int GRID_MARGIN = 5;

	private class GridDerivedParams {
		final float step;
		final float xStart;
		final float yStart;
		public GridDerivedParams(int width, int height) {
			int adaptWidth = width - 2 * GRID_MARGIN;
			int adaptHeight = height - 2 * GRID_MARGIN;
			float colRatio = (float)adaptWidth / (float)SudokuCore.GRID_DIM;
			float rowRatio = (float)adaptHeight / (float)SudokuCore.GRID_DIM;
			step = Math.min(colRatio, rowRatio);
			
			float gridWidth = step * SudokuCore.GRID_DIM;
			float gridHeight = step * SudokuCore.GRID_DIM;
			xStart = (width - gridWidth) / 2;
			yStart = (height - gridHeight) / 2;
		}		
	}
	
	private GridDerivedParams mParams;
	private int [] mInput;
	private int [] mOutput;
	
	
	public GridView(Context context, AttributeSet attributes) {
		super(context, attributes);
		mInput = null;
		mOutput = null;
	}
	
	
	@Override
	protected void onMeasure (int widthMeasureSpec, int heightMeasureSpec)
	{
		heightMeasureSpec = widthMeasureSpec;
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        int width = getWidth();
        int height = getHeight();
        mParams = new GridDerivedParams(width, height);
     
        /* Background and grid */
        drawGrid(canvas);
        
        /* Draw all Sudoku cells, solution ones in red */
        drawCells(canvas);
    }
	
    public void setGameInput(int [] array) {
    	
    	mInput = array;
    	invalidate();
    }
    
    public int [] getInput() {
    	return mInput;
    }
    
    public void setSolution(int [] array) {
    	mOutput = array;
    	invalidate();
    }
    
    public int [] getSolution() {
    	return mOutput;
    }
    
    private void drawGrid(Canvas canvas) {
    	
    	float step = mParams.step;
        float xStart = mParams.xStart;
        float yStart = mParams.yStart;
        
        Paint paintBG = new Paint();
        paintBG.setStyle(Paint.Style.FILL);
        paintBG.setColor(Color.WHITE);

        canvas.drawPaint(paintBG);
      
        /* Grid */
        // paint black lines
        Paint paintThin = new Paint();
        paintThin.setColor(Color.BLACK);

        Paint paintThick = new Paint();
        paintThick.setColor(Color.BLACK);
        paintThick.setStrokeWidth(3.0f);

        for (int x = 0; x <= SudokuCore.GRID_DIM; x++) {
        	Paint paint = (x % SudokuCore.GRID_SUB_DIM) == 0 ? paintThick : paintThin;
        	float pos = xStart + x * step;
        	canvas.drawLine(pos, yStart, pos, yStart + step * SudokuCore.GRID_DIM, paint);   
        }
        for (int y = 0; y <= SudokuCore.GRID_DIM; y++) {
        	Paint paint = (y % SudokuCore.GRID_SUB_DIM) == 0 ? paintThick : paintThin;
           	float pos = yStart + y * step;
        	canvas.drawLine(xStart, pos, xStart + step * SudokuCore.GRID_DIM, pos, paint);       	
        }        
    }
    
    private void drawCells(Canvas canvas) {
    	if (mInput != null) {
	    	for (int i = 0; i < mInput.length; i++) {
	    		int row = i / SudokuCore.GRID_DIM;
	    		int col = i % SudokuCore.GRID_DIM;
	    		if (mInput[i] != 0)
	    			setCellText(canvas, col, row, mInput[i], Color.BLACK);
	    		else if (mOutput != null && mOutput[i] != 0) /* Draw output cell if set */
	    			setCellText(canvas, col, row, mOutput[i], Color.RED);
	    	}
    	}
    }
	private void setCellText(Canvas canvas, int col, int row, int value, int color) {
		
		String text = String.format("%d", value);
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setTextAlign(Align.CENTER);
		
		paint.setTextSize(mParams.step/2);
		float startX = getX(col) + mParams.step / 2.0f;
		float startY = getY(row) + mParams.step * 0.7f;
		
		paint.setColor(color);
		canvas.drawText(text, startX, startY, paint);
	}
	
    private float getX(int col) {
    	return mParams.xStart + mParams.step * col;
    }
    private float getY(int row) {
    	return mParams.yStart + mParams.step * row;
    }

}
