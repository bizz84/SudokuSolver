package com.musevisions.android.SudokuSolver;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;


public class SudokuLoaderActivity extends Activity implements SudokuRetrieverTask.SudokuRetrieverPreparedListener {

	//private static final String TAG = "SudokuLoaderActivity";
	private SudokuRetriever mRetriever;
	private ListView mListView;
	private TextView mTextView;
	
	private MainTabActivity mMainActivity;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loader);

        mMainActivity = (MainTabActivity)getParent(); 
        mMainActivity.setLoaderActivity(this);
        
        mTextView = (TextView)findViewById(R.id.textInfo);
        if (HttpPostUtils.isConnected(this)) {
        	mTextView.setVisibility(View.GONE);
        	mRetriever = new SudokuRetriever();
            (new SudokuRetrieverTask(mRetriever,this)).execute();
        }
        else {
        	mTextView.setVisibility(View.VISIBLE);
        	mTextView.setText("Check Internet Connection");
        }
        
        mListView = (ListView)findViewById(R.id.listView);
        mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				PuzzleHolder holder = (PuzzleHolder)mListView.getAdapter().getItem(position);
				//Toast.makeText(SudokuLoaderActivity.this, holder.name, Toast.LENGTH_SHORT);
				
				mMainActivity.getSolverActivity().updateView(holder.puzzle, holder.name);
				TabHost tabHost =  (TabHost)getParent().findViewById(android.R.id.tabhost);
				tabHost.setCurrentTab(0);
			}		
        });
    }

    
    
    private void updateView(JSONArray results) {
    	ListView lv = (ListView)findViewById(R.id.listView);
    	
    	/* Fill list with all json files */
    	ArrayList<PuzzleHolder> list = new ArrayList<PuzzleHolder>();
    	for (int i = 0; i < results.length(); i++) {
    		try {
				JSONObject obj = results.getJSONObject(i);
				JSONArray names = obj.names();
    			String name = names.getString(0);
    			JSONArray array = obj.getJSONArray(name);
    			int puzzle[] = JSONHelper.toIntArray(array);
    			if (puzzle != null)    			
    				list.add(new PuzzleHolder(name, puzzle));
				//Log.v(TAG, "File: " + name);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}    		
    	}
    	
		lv.setAdapter(new ListArrayAdapter(this, list));
    }
    
    
	@Override
	public void onSudokuRetrieverPrepared() {
		// TODO Auto-generated method stub
		updateView(mRetriever.getContents());
	}
	
	private class PuzzleHolder {
		String name;
		int puzzle[];
		public PuzzleHolder(String name, int puzzle[]) {
			this.name = name;
			this.puzzle = puzzle;
		}
	}
	
	// http://www.vogella.com/articles/AndroidListView/article.html#ownadapter_viewHolder
    private class ListArrayAdapter extends ArrayAdapter<PuzzleHolder> {
    	
    	Activity activity;
    	List<PuzzleHolder> data;
    	
    	public ListArrayAdapter(Activity context, List<PuzzleHolder> data) {
    		super(context, R.layout.row_layout, data);
    		this.activity = context;
    		this.data = data;
    	}
 
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
        	View rowView = convertView;
    		if (rowView == null) {	
    			try {
	    			LayoutInflater inflater = activity.getLayoutInflater();
	    			rowView = inflater.inflate(R.layout.row_layout, null);
    			}
    			catch (InflateException ex) {
    				
    				ex.printStackTrace();
    				return null;
    			}
    		}

    		TextView label = (TextView)rowView.findViewById(R.id.textLabel);
    		PuzzleHolder holder = data.get(position); 
			
			label.setText(holder.name);		
			
			rowView.setTag(label);

			return rowView;
        }
    }

}
