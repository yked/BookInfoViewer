package com.example.bookinfoviewer;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;


public class DetailActivity extends Activity {
	
	private TextView bookNameText;
	private TextView salesDateText;
	private TextView publisherText;
	private TextView authorText;
	private TextView isbnText;
	private TextView priceText;
	private TextView captionText;
	private Button buyButton;
	private JSONObject item = new JSONObject();

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);
		Intent intent = getIntent();
		try {
			item = new JSONObject(intent.getStringExtra("Item"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		setupUI();		
	}	
	
	private void setupUI() {
		bookNameText = (TextView) findViewById(R.id.book_name);
		salesDateText = (TextView) findViewById(R.id.sales_date);
		publisherText = (TextView) findViewById(R.id.publisher);
		authorText = (TextView) findViewById(R.id.author);
		isbnText = (TextView) findViewById(R.id.isbn);
		priceText = (TextView) findViewById(R.id.price);
		captionText = (TextView) findViewById(R.id.caption);
		
		setItemValueToTextView("title", bookNameText);
		setItemValueToTextView("salesDate", salesDateText);
		setItemValueToTextView("publisherName", publisherText);
		setItemValueToTextView("author", authorText);
		setItemValueToTextView("isbn", isbnText);
		setItemValueToTextView("itemPrice", priceText);
		setItemValueToTextView("itemCaption", captionText);
		
		buyButton = (Button) findViewById(R.id.buy_button);
		buyButton.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				if(item.has("itemUrl")) {
					try {
						Uri uri = Uri.parse(item.getString("itemUrl"));
						Intent intent = new Intent(Intent.ACTION_VIEW, uri);
						startActivity(intent);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
		});
	}
	
	private void setItemValueToTextView(String key, TextView textView) {
		if(item.has(key)) {
			try {
				textView.setText(item.getString(key));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
}
