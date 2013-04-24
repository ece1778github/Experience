package com.example.ece1778project;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class StartActivity extends Activity {
	
	long currentTime, openTime; 
	ImageView iv;
	EditText et1, et2;
	Button login, register, forgot;
	boolean authenticated = false;
	String username = "USER";
	String password = "PASSWORD";
	String experiment;
	
	SQLiteDatabase sampleDB;
	String DBNAME = "acctDB";
	String TABLENAME = "acctTable";
	
	String statement;
	ProgressBar pb;
	DBConnection dburl = new DBConnection();
	Connection conn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//Remove titlebar within app
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
	    		WindowManager.LayoutParams.FLAG_FULLSCREEN);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);
		
		//locate widgets
		et1 = (EditText)findViewById(R.id.editText1);
		et2 = (EditText)findViewById(R.id.editText2);
		iv = (ImageView)findViewById(R.id.imageView2);
		login = (Button)findViewById(R.id.button1);
		register = (Button)findViewById(R.id.button2);
		forgot = (Button)findViewById(R.id.button3);
		pb = (ProgressBar)findViewById(R.id.progressBar1);
		
		login.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				authenticated = false;
				username = et1.getText().toString();
				password = et2.getText().toString();
				statement = "select * from person where username='" + username + "' and password='" + password + "'";
				ConnectAsyncTask newTask = new ConnectAsyncTask();
	            newTask.execute();				
				if (authenticated) {
					//if credentials have been authenticated, goto instructions screen
					Intent intent = new Intent(StartActivity.this, InstructionsActivity.class);
			    	intent.putExtra("name", username);
			    	intent.putExtra("experiment", experiment);
					startActivity(intent);
				}
			}		
		});
		
		register.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				//Send to register form
				Intent intent = new Intent(StartActivity.this, RegisterActivity.class);
				intent.putExtra("name", username);
		    	startActivity(intent);
			}		
		});
		
		forgot.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				//Send to register form
				Intent intent = new Intent(StartActivity.this, ForgotPasswordActivity.class);
				intent.putExtra("name", username);
		    	startActivity(intent);
			}		
		});		
		
	}
	
	private class ConnectAsyncTask extends AsyncTask<Void, Integer, Void> {
		  
	     @Override
	     protected void onPreExecute() {
	    	 super.onPreExecute();
	    	 pb.setVisibility(View.VISIBLE);
	     }

	     @Override
	     protected Void doInBackground(Void... params) {
	    	 try {
	             Class.forName("com.mysql.jdbc.Driver").newInstance();
	             try {  
	                 conn = DriverManager.getConnection(dburl.getUrl());
	                 //conn = DriverManager.getConnection("jdbc:mysql://192.168.0.196:3306/experience?user=root&password=root");
	                 
	                 Statement st = conn.createStatement();
	                 ResultSet rs = st.executeQuery(statement);
	                 
	                 ResultSetMetaData rsmd = rs.getMetaData();
	                 
	                 while (rs.next()) {
	                	 authenticated = true;
	                	 experiment = rs.getString(8);
	                 }
	                 
	             } catch (java.sql.SQLException e1) {
	                 e1.printStackTrace();
	             }
	             
	 		} catch (ClassNotFoundException e) {
	             // TODO Auto-generated catch block
	             e.printStackTrace();
	 		} catch (IllegalAccessException e) {
	             // TODO Auto-generated catch block
	             e.printStackTrace();
	 		} catch (InstantiationException e) {
	             // TODO Auto-generated catch block
	             e.printStackTrace();
	 		}
	    	return null;
	     }
	   
	     @Override
	     protected void onPostExecute(Void result) {
	    	 super.onPostExecute(result);
	    	 pb.setVisibility(View.INVISIBLE);
	    	 if(authenticated) {
	    		 //login user
	    		 Intent intent = new Intent(StartActivity.this, InstructionsActivity.class);
			     intent.putExtra("name", username);
			     intent.putExtra("experiment", experiment);
			     startActivity(intent);
	    	 }
	    	 else {
	    		 AlertDialog.Builder alertDialog = new AlertDialog.Builder(StartActivity.this);
				 alertDialog.setTitle("Login Failed");
				 alertDialog.setMessage("The username and password were not found. Please Try Again.");
					
				 alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					 public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
				alertDialog.show();
	    	 }
	  	}
	}
	
}
