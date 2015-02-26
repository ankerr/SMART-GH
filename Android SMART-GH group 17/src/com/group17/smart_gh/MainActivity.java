package com.group17.smart_gh;

import com.graphhopper.GHRequest;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import org.json.JSONArray;
import org.json.JSONObject;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

public class MainActivity extends ActionBarActivity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Spinner spinner = (Spinner) findViewById(R.id.spinner1);
		// Create an ArrayAdapter using the string array and a default spinner layout
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
		        R.array.typesOfRoutes, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		spinner.setAdapter(adapter);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	/** Called when the user clicks the Search button */
	public void searchRoute(View view) throws Exception {
	    // Do something in response to button
		EditText editText = (EditText) findViewById(R.id.editText1);
		String to = editText.getText().toString();
		Log.i("SearchRoute", to);
		editText = (EditText) findViewById(R.id.editText2);
		String from = editText.getText().toString();
		Log.i("SearchRoute", from);
		
		Spinner spinner = (Spinner) findViewById(R.id.spinner1);
		String routeSelected=spinner.getSelectedItem().toString();

		Log.i("SearchRoute",routeSelected);
		
		String places = "point=" + 53.341841 + "," + -6.250191 + "&";
		places += "point=" + 53.291797 + "," + -6.136723 + "&";
		
		String url = "http://172.16.160.129:8989/route"
                + "?"
                + places
                + "&type=json"
                + "&points_encoded=" + true
                // + "&min_path_precision=" + request.getHint("douglas.minprecision", 1)
                + "&algo=" + routeSelected
                + "&elevation=" + false;
		
		URL temp = new URL(url);
		Log.i("","Creating Connection");
		HttpURLConnection connection = (HttpURLConnection) temp.openConnection();

		Log.i("","Connection Created");
		
		connection.setDoOutput(true);
		connection.setRequestMethod("GET");
		connection.setRequestProperty("User-Agent", "Mozilla/5.0");
		
		connection.connect();
		
		Log.i("","Creating BufferedReader");
		BufferedReader in = new BufferedReader(
		        new InputStreamReader(connection.getInputStream()));
		Log.i("","BufferedReader Created");
		
		String inputLine;
		StringBuffer response = new StringBuffer();
 
		Log.i("","Waiting for input");
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		Log.i("","Got input");
 
		//print result
		Log.i("", response.toString());
		
		
	
	}
}
