package com.example.bookinfoviewer;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.zxing.client.android.CaptureActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;


public class MainActivity extends Activity {
	
	private EditText inputText;
	private Button searchButton;	
	private Button searchWithBarcodeReaderButton;
	private ListView resultList;
	private ArrayAdapter<String> resultAdapter;
	private JSONObject resultJsonObj;
	private final static int READ_ISBN_REQUEST = 1000; 
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setupUI();
	}	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {        
        case READ_ISBN_REQUEST: 
        	if(resultCode == RESULT_OK){
        		String result = data.getStringExtra("SCAN_RESULT");
        		startQuery("isbn", result);        		
        	}
        	break;
        }
	}
	
	private void setupUI() {
		inputText = (EditText) findViewById(R.id.input_text);
		searchButton = (Button) findViewById(R.id.search_button);
		searchWithBarcodeReaderButton = (Button) findViewById(R.id.search_with_barcode_reader_button);
		resultList = (ListView) findViewById(R.id.result_list);
		
		searchButton.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				if(getCurrentFocus()!=null) {
			        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
			        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
			    }
				startQuery("title", inputText.getText().toString().trim());
			}
		});
		
		searchWithBarcodeReaderButton.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), CaptureActivity.class);
				intent.setAction("com.google.zxing.client.android.SCAN");
				intent.putExtra("SCAN_MODE", "ONE_D_MODE");
				startActivityForResult(intent, READ_ISBN_REQUEST);				
			}
		});
		
		resultList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
            	if(resultJsonObj.has("success")){            		
					try {
						JSONArray items = resultJsonObj.getJSONArray("success");
						JSONObject item = items.getJSONObject(position);						
						Intent intent = new Intent(MainActivity.this, DetailActivity.class);
						intent.putExtra("Item", item.toString());
						startActivity(intent);
					} catch (JSONException e) {
						e.printStackTrace();
					}										
            	}            	
            }
        });
		
		resultAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, new ArrayList<String>());	
		resultList.setAdapter(resultAdapter);
	}
	
	private void startQuery(String key, String value) {
		resultAdapter.clear();
		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
        	new BookInfoRequestTask(this) {        		
        		@Override
                public void onPostExecute(JSONObject result){                	
                	resultAdapter.clear();
                	resultJsonObj = result;
                	List<String> resultListItems = new ArrayList<String>();
                	try {
                		if(resultJsonObj.has("success")) {
                			JSONArray items = resultJsonObj.getJSONArray("success");					
							for (int i=0; i<items.length(); i++) {
								JSONObject item = items.getJSONObject(i);
	                			resultListItems.add(item.getString("title"));
	                		}						               		
                		}else if(resultJsonObj.has("error")) {
                			resultListItems.add(resultJsonObj.getString("error"));
                		}
                	} catch (JSONException e) {
						e.printStackTrace();
					} 
                	resultAdapter.addAll(resultListItems);
                }        	
        	}.execute(key, value);        	
        } else {
        	new AlertDialog.Builder(this)
            	.setTitle(getString(R.string.no_network_alert_title))
            	.setNeutralButton(getString(R.string.no_network_alert_ok),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog,
                                int which) {}
                    }).show();
        }
	}	
}
