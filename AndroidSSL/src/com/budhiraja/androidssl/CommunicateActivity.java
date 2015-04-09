/*
 * CommunicateActivity.java
 * 
 * First Connects to the host
 * 
 * Then run commands on it
 * 
 * Author : Amar Budhiraja
 * 
 * Copyrights : None
 * 
 */


package com.budhiraja.androidssl;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.UnknownHostException;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class CommunicateActivity extends Activity {
	
	private String host;
	private int port;
	private boolean connected;
	String result;
	String command;
	private SSLSocket sslSocket = null;
	private InputStreamReader inputStreamReader;
	private OutputStreamWriter outputStreamWriter;
	private InputStream inputStream;
	private BufferedReader bufferedreader;
	
	
	public CommunicateActivity() {
		this.connected = false;
		this.result = "";
		this.command = "";
	}

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_communicate);
		Intent intent = getIntent();
		this.host = intent.getStringExtra("host");
		this.port = intent.getIntExtra("port", -1);		
		ConnectToServer connectToServer = new ConnectToServer();
		connectToServer.execute();		
	}

	
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.communicate, menu);
		return true;
	}
	
	private boolean isNetworkAvailable() {
    	
    	//Checks for connnectivity in the application : Wifi, Internet etc
    	
    	ConnectivityManager connectivityManager
    	= (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    	NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
    	return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    	
    	}
	
	public void sendCommandToHost(View view){
		if (isNetworkAvailable()== false){
    		Toast.makeText(CommunicateActivity.this,"No Connectivity. Please check your connection settings.",Toast.LENGTH_LONG).show();
    	}
		else{
			EditText commandBox = (EditText) findViewById(R.id.command);
			this.command = commandBox.getText().toString();
			RunCommand runCommand = new RunCommand();
			runCommand.execute();
		}
	}
	
	public class ConnectToServer extends AsyncTask<Void, Void, Void>{
		 private ProgressDialog dialog;
		
		 
		 public ConnectToServer() {
			 this.dialog = new ProgressDialog(CommunicateActivity.this);
			 this.dialog.setCanceledOnTouchOutside(false);
		}
		
		protected Void doInBackground(Void... params) {			
			establishConnection();			
			return null;
		}		
		
		private void establishConnection() {			
			try {
				SSLSocketFactory sslSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();	        
				sslSocket = (SSLSocket) sslSocketFactory.createSocket(host,port);
				sslSocket.setEnabledCipherSuites(sslSocket.getSupportedCipherSuites());
				connected = sslSocket.isConnected();
				Log.d("Socket Status", String.valueOf(connected));
				sslSocket.startHandshake();
	
			} catch (UnknownHostException e) {
				Log.d("Exception Caught: ", "UnknowHostException");
				runOnUiThread(new Runnable() {
					public void run() {
						Toast.makeText(CommunicateActivity.this,"Unknown Host. Please check the host provided.",Toast.LENGTH_LONG).show();
					}
					});
				CommunicateActivity.this.finish();
				
			} catch (IOException e) {				
				Log.d("Exception Caught: ", "IOException");
				runOnUiThread(new Runnable() {
					public void run() {
						Toast.makeText(CommunicateActivity.this,"Input Output Failure - Probably caused by Handshake Failure",Toast.LENGTH_LONG).show();
					}
					});
				CommunicateActivity.this.finish();
			}
			
			setUpIOStreams();
			
	}

		private void setUpIOStreams() {
			
			OutputStream outputStream = null;
			try {
				outputStream = sslSocket.getOutputStream();
				outputStreamWriter = new OutputStreamWriter(outputStream,"UTF-8");
				inputStream = sslSocket.getInputStream();		        
		        inputStreamReader = new InputStreamReader(inputStream);
		        bufferedreader = new BufferedReader(inputStreamReader);
			} catch (IOException e) {
				Log.d("Error","IOStreamFailure");	
				runOnUiThread(new Runnable() {
					public void run() {
						Toast.makeText(CommunicateActivity.this,"Input Output Failure.",Toast.LENGTH_LONG).show();
					}
					});
				CommunicateActivity.this.finish();
			}
			catch (NullPointerException e) {
				Log.d("Error","NOT CONNECTED TO SERVER. CONNECTION FAILURE");
				runOnUiThread(new Runnable() {
					public void run() {
						Toast.makeText(CommunicateActivity.this,"Connection Failure.",Toast.LENGTH_LONG).show();
					}
					});
				CommunicateActivity.this.finish();
				
			}	
		}

		protected void onPreExecute(){			
			this.dialog.setMessage("Connecting to Server");
	        this.dialog.show();
		}		
		
		protected void onPostExecute(Void result) {
			if (dialog.isShowing())
				this.dialog.dismiss();	
			
		}		
	}
	
	public class RunCommand extends AsyncTask<Void, Void, Void>{
		 private ProgressDialog dialog;	
		 
		 public RunCommand() {
			 this.dialog = new ProgressDialog(CommunicateActivity.this);
			 this.dialog.setCanceledOnTouchOutside(false);
		}
		
		protected Void doInBackground(Void... params) {			
			runCommand();			
			return null;
		}		

		private void runCommand() {
			if (!sslSocket.isConnected())
				return;			
			try {
				result = "";
				command = command+"\n";
	            sslSocket.startHandshake();
	            
		        outputStreamWriter.write(command, 0, command.length());
		        outputStreamWriter.flush();	       
		      
	            result = bufferedreader.readLine();
	            while (true) {
	            	
	            	if (inputStream.available()==0)
	                    break;
	                result += bufferedreader.readLine();	                
	                result +='\n';
	                }
	            setResult();
	            
			} catch (IOException e) {
				Log.d("Error","IOStreamFailure");
			}
		}

		private void setResult() {
			Log.d("Result",result);
			runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					TextView resultView = (TextView) findViewById(R.id.resultView);
					resultView.setText(result);
					
				}
			});
		}

		protected void onPreExecute(){
			
			this.dialog.setMessage("Connecting to Server");
	        this.dialog.show();
		}
		
		protected void onPostExecute(Void result) {
			if (dialog.isShowing())
				this.dialog.dismiss();	
		}		
	}

	
	
}