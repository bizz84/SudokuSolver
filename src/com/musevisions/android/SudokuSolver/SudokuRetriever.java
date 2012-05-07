
package com.musevisions.android.SudokuSolver;

import android.util.Log;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Retrieves and organizes media to play. Before being used, you must call {@link #prepare()},
 * which will retrieve all of the pictures on the user's device (by performing a query on a content
 * resolver). After that, it's ready to retrieve a random song, with its title and URI, upon
 * request.
 */
public class SudokuRetriever {
    static private final String TAG = "SudokuRetriever";
    
    static private final String SERVER_URL = "http://www.musevisions.com/sudoku/generate.php"; 
    
    private JSONArray mContents;
    
    /**
     * Loads pictures data. This method may take long, so be sure to call it asynchronously without
     * blocking the main thread.
     */
    public synchronized void prepare() {
    	// Clear in case images need to be updated
    	long startTime = System.nanoTime();

    	mContents = loadFromURL();

      	long deltaTime = System.nanoTime() - startTime;
    	double timeMs = (double)deltaTime / 1E6;

        Log.i(TAG, "Done querying URL in, " + timeMs + " ms. " + mContents.length() + " elements found.");
    }
   
    public JSONArray getContents() {
    	return mContents;
    }

    
    private JSONArray loadFromURL() {
    	
        try {
        	return JSONHelper.readJsonFromUrl(SERVER_URL);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
        return null;
    }
    
}
