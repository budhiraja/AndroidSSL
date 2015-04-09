/*
 * MainActivity.java
 * 
 * Activity to get Host and Port to connect.
 * 
 * Author : Amar Budhiraja
 * 
 * Copyrights : None
 * 
 */

package com.budhiraja.androidssl;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
    
    private boolean isNetworkAvailable() {
    	
    	//Checks for connnectivity in the application : Wifi, Internet etc
    	
    	ConnectivityManager connectivityManager
    	= (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    	NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
    	return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    	
    	}
    
    public void connectToHost(View view){		
    	if (isNetworkAvailable()== false){
    		Toast.makeText(MainActivity.this,"No Connectivity. Please check your connection settings.",Toast.LENGTH_LONG).show();
    	}
    	
    	else{
			EditText hostBox = (EditText)findViewById(R.id.host);
			EditText portBox = (EditText)findViewById(R.id.port);
			String host = hostBox.getText().toString();
			int port = Integer.parseInt(portBox.getText().toString());
			Intent intent = new Intent(MainActivity.this,CommunicateActivity.class);
			intent.putExtra("host", host);
			intent.putExtra("port", port);
			startActivity(intent);
    	}
	}
}
