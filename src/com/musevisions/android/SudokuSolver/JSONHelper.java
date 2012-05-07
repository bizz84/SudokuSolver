package com.musevisions.android.SudokuSolver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.res.Resources.NotFoundException;
import android.widget.Toast;

public class JSONHelper {

	private static final int SUDOKU_LEN = 81;
	
	private static String readAll(Reader rd) throws IOException {
		StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1) {
			sb.append((char) cp);
		}
		return sb.toString();
	}

	public static JSONArray readJSONArrayFromIS(InputStream is) throws IOException {
		try {
			BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
			String jsonText = readAll(rd).replace("\n", " ");
			return new JSONArray(jsonText);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		catch (JSONException e) {
			e.printStackTrace();
		}
		finally {
			is.close();
		}
		return null;
	}

	public static JSONArray readJsonFromUrl(String url) throws IOException, JSONException {
		InputStream is = new URL(url).openStream();
		return readJSONArrayFromIS(is);
	}
  
	public static int[] toIntArray(JSONArray array) {
       
		try {
			assert(array.length() == SUDOKU_LEN);
			int intArray[] = new int[SUDOKU_LEN];
			for (int i = 0; i < SUDOKU_LEN; i++) {
				intArray[i] = array.getInt(i);
			}
			return intArray;
		}
		catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static JSONArray toJSONArray(int array[]) {
		JSONArray json = new JSONArray();
		for (int i = 0; i < array.length; i++) {
			try {
				json.put(i, array[i]);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return json;
	}
	
	public static int[] getSudokuArray(InputStream is) {
		
        try {
			return toIntArray(readJSONArrayFromIS(is));
		} catch (IOException e) {
			e.printStackTrace();
		}
        return null;
	}

}
