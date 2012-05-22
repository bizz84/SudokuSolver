/**
 * JSON Sudoku solver is covered under the Creative Commons Attribution 3.0 Unported License
 * http://creativecommons.org/licenses/by/3.0/
 * 
 * @author: Andrea Bizzotto {@link www.musevisions.com}, {@link www.bizzotto.biz}
 * @email: bizz84dev@gmail.com
 */
package com.musevisions.android.SudokuSolver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.widget.Toast;


public class HttpPostUtils {

	//static private final String TAG = "HttpPostUtils";
	/** Class representing the server settings to use for post requests */
	static public class HttpPostSettings {

		private static final String DefaultHttpServer = "www.musevisions.com";
		private static final String DefaultHttpPostTarget = "http://musevisions.com/sudoku/post.php";
			
		final String mHttpServer;
		final String mHttpPostTarget;

		public HttpPostSettings(String httpServer, String httpPostTarget) {
			mHttpServer = httpServer;
			mHttpPostTarget = httpPostTarget;
		}
		public HttpPostSettings() {
			this(DefaultHttpServer, DefaultHttpPostTarget);
		}
		
		final String getServer() { return mHttpServer; }
		final String getPostTarget() { return mHttpPostTarget; }
	}
	
	public interface HttpCallbackListener {
		public void onCallback(String message);
	}
	
	static public class ServerResponseAsync extends AsyncTask<ArrayList<NameValuePair>, String, Void> implements HttpCallbackListener {

		private String mResponse;
		private Activity mParent;
		public ServerResponseAsync(Activity parent) {
			mParent = parent;
			//ServerResponseAsync
		}
		@Override
		protected Void doInBackground(ArrayList<NameValuePair>... params) {
			mResponse = send(new HttpPostSettings(), params[0]);
			
			return null;
		}
		protected void onPostExecute(Void result) {
			onCallback(mResponse);
		}
		@Override
		public void onCallback(String message) {
			Toast.makeText(mParent, message, Toast.LENGTH_SHORT).show();
		}		
	}


	/* Simple name-value pair implementation */
	static public class AppNameValuePair implements NameValuePair {
		private String name;
		private String value;
		public AppNameValuePair(String name, String value) {
			this.name = name;
			this.value = value;
		}
		@Override
		public String getName() {
			return name;
		}
		@Override
		public String getValue() {
			return value;
		}
	}
	@SuppressWarnings("unchecked")
	static public void postResult(final String name, final JSONArray solution, final Activity parent) {

		ArrayList<NameValuePair> result = new ArrayList<NameValuePair>();
		result.add(new AppNameValuePair("name", name));
		result.add(new AppNameValuePair("solution", solution.toString()));

		(new ServerResponseAsync(parent)).execute(result);
		/*new Thread(new Runnable() {
			@Override
		    public void run() {
				String response = send(new HttpPostSettings(), result);
				listener.onCallback(response);
			}
		}).start();*/
	}
	
	
	static public String send(HttpPostSettings settings, List<NameValuePair> values) {
		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(settings.getPostTarget());
			httppost.setEntity(new UrlEncodedFormEntity(values));			
			HttpResponse response = httpclient.execute(httppost);
			return EntityUtils.toString(response.getEntity());
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
    static public boolean isConnected(Activity parent) { 
    	ConnectivityManager cm = (ConnectivityManager)parent.getSystemService(Context.CONNECTIVITY_SERVICE);
    	final NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
    	return activeNetwork != null && activeNetwork.getState() == NetworkInfo.State.CONNECTED;
    }
}
