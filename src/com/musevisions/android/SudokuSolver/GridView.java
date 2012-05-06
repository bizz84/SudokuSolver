package com.musevisions.android.SudokuSolver;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.util.AttributeSet;
import android.view.View;

public class GridView extends View {

	static final int GRID_DIM = 9;
	static final int GRID_SUB_DIM = 3;
	private static final int GRID_MARGIN = 5;

	
	private class GridDerivedParams {
		final float step;
		final float xStart;
		final float yStart;
		public GridDerivedParams(int width, int height) {
			int adaptWidth = width - 2 * GRID_MARGIN;
			int adaptHeight = height - 2 * GRID_MARGIN;
			float colRatio = (float)adaptWidth / (float)GRID_DIM;
			float rowRatio = (float)adaptHeight / (float)GRID_DIM;
			step = Math.min(colRatio, rowRatio);
			
			float gridWidth = step * GRID_DIM;
			float gridHeight = step * GRID_DIM;
			xStart = (width - gridWidth) / 2;
			yStart = (height - gridHeight) / 2;
		}		
	}
	
	private GridDerivedParams mParams;
	private int [] mInput;
	private int [] mOutput;
	
	
	public GridView(Context context, AttributeSet attributes) {
		super(context, attributes);
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
     
        drawGrid(canvas);
        
        drawCells(canvas);
    }
	
    public void setGameInput(int [] array) {
    	
    	mInput = array;
    	invalidate();

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

        for (int x = 0; x <= GRID_DIM; x++) {
        	Paint paint = (x % GRID_SUB_DIM) == 0 ? paintThick : paintThin;
        	float pos = xStart + x * step;
        	canvas.drawLine(pos, yStart, pos, yStart + step * GRID_DIM, paint);   
        }
        for (int y = 0; y <= GRID_DIM; y++) {
        	Paint paint = (y % GRID_SUB_DIM) == 0 ? paintThick : paintThin;
           	float pos = yStart + y * step;
        	canvas.drawLine(xStart, pos, xStart + step * GRID_DIM, pos, paint);       	
        }        
    }
    
    private void drawCells(Canvas canvas) {
    	if (mInput != null) {
	    	for (int i = 0; i < mInput.length; i++) {
	    		int row = i / GRID_DIM;
	    		int col = i % GRID_DIM;
	    		if (mInput[i] != 0)
	    			setCellText(canvas, col, row, mInput[i]);
	    	}
    	}
    }
	private void setCellText(Canvas canvas, int col, int row, int value) {
		
		String text = String.format("%d", value);
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		//paint.setSubpixelText(true);
		paint.setTextAlign(Align.CENTER);
		
		paint.setTextSize(mParams.step/2);
		//float width = paint.measureText(text);
		float startX = getX(col) + mParams.step / 2.0f;// - width / 2.0f;
		float startY = getY(row) + mParams.step * 0.7f;
		
		//paint.setColor(Color.GRAY);
		//canvas.drawText(text, startX+1.4f, startY+1.0f, paint);
		paint.setColor(Color.BLACK);
		//paint.setTypeface(Typeface.DEFAULT_BOLD);
		canvas.drawText(text, startX, startY, paint);
		
		//Log.v(TAG, "width: " + width + ", size: " + (mParams.step) + "sx: " + startX + ",sy: " + startY);
	}
	
    private float getX(int col) {
    	return mParams.xStart + mParams.step * col;
    }
    private float getY(int row) {
    	return mParams.yStart + mParams.step * row;
    }

}
