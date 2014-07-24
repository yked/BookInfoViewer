package com.example.bookinfoviewer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;


public class BookInfoRequestTask extends AsyncTask<String, Void, JSONObject>{
	
	private final static String TAG = "BookInfoRequestTask";
	private final static String API_URL = 
			"https://app.rakuten.co.jp/services/api/BooksBook/Search/20130522?applicationId=1018355779123490370&";
	
	private Context context;

	
	public BookInfoRequestTask(Context context) {
		this.context = context;
	}

	@Override
	protected JSONObject doInBackground(String... params) {
		JSONObject result = new JSONObject();
		HttpURLConnection conn = null;
	    String error = null;
	    
		try {
			String query = params[0] + "=" + URLEncoder.encode(params[1], "UTF-8");			
            URL url = new URL(API_URL + query);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();

            JSONObject jsonObj = new JSONObject(convertInputStreamToString(conn.getInputStream()));            
            if(jsonObj.has("Items")) {
            	JSONArray items = jsonObj.getJSONArray("Items");
            	JSONArray formattedItems = new JSONArray();
            	if(items.length() > 0) {
            		for(int i=0; i<items.length(); i++){
            			formattedItems.put(items.getJSONObject(i).getJSONObject("Item"));                    	
            		}
            		result.put("success", formattedItems);
            	}else{
            		result.put("error", context.getString(R.string.no_result_message));
            	}
            }else if(jsonObj.has("error")) {            	
            	result.put("error", jsonObj.getString("error_description"));            	
            }
          
		} catch (Exception e) {
			error = e.toString();
			Log.e(TAG, error);			
        } finally {
            if (conn != null)
                conn.disconnect();
        }
		
		if (error != null) {
            result = new JSONObject();
            try {
				result.put("error", error);
			} catch (JSONException e) {
				e.printStackTrace();
			}		
        }        
		
        return result;			
	}	
	
	private static String convertInputStreamToString(InputStream inputStream)
			throws IOException {
		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(inputStream));
		String line = "";
		String result = "";
		
		while ((line = bufferedReader.readLine()) != null) {
			result += line;
		}

		inputStream.close();
		return result;
	}

}
