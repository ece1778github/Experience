package com.example.ece1778project;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

public class ForgotPasswordActivity extends Activity {

	EditText et;
	Button submit;
	boolean exists = false;
	String email;
	
	SQLiteDatabase sampleDB;
	String DBNAME = "acctDB";
	String TABLENAME = "acctTable";
	String uname, pwd;
	
	String statement;
	Connection conn;
	ProgressBar pb;
	DBConnection dburl = new DBConnection();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//Remove titlebar within app
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
	        WindowManager.LayoutParams.FLAG_FULLSCREEN);
	    
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_forgot_password);
		
		//locate widgets
		submit = (Button)findViewById(R.id.button1);
		et = (EditText)findViewById(R.id.editText1);
		pb = (ProgressBar)findViewById(R.id.progressBar1);
		
		submit.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				//Validate e-mail exists in database
				email = et.getText().toString();
				statement = "select * from person where email='" + email + "'";
				ConnectAsyncTask newTask = new ConnectAsyncTask();
				newTask.execute();
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
	                 Statement st = conn.createStatement();
	                 ResultSet rs = st.executeQuery(statement);
	                 
	                 ResultSetMetaData rsmd = rs.getMetaData();
	                 
	                 while (rs.next()) {
	                	 //if a record was found send account information to email address
	                	 exists = true;
	                	 uname = rs.getString(3);
	                	 pwd = rs.getString(4);
	                	 try {   
	                         GMailSender sender = new GMailSender("eXperienceaccts@gmail.com", "ece1778h1s");
	                         sender.sendMail("eXperience Account Information",   
	                                 "Your username is: " + uname + ", and your password is: " + pwd,   
	                                 "eXperienceaccts@gmail.com",   
	                                 email);   
	                     } catch (Exception e) {   
	                         Log.e("SendMail", e.getMessage(), e);   
	                     }      
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
	    	 if (!exists) {
	    		 //if account and associated password do not exist, notify user
	    		 AlertDialog.Builder alertDialog = new AlertDialog.Builder(ForgotPasswordActivity.this);
	    		 alertDialog.setTitle("Failed");
	    		 alertDialog.setMessage("Account associated with email address given was not found. Please try again.");
					
	    		 alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
	    			 public void onClick(DialogInterface dialog, int which) {
	    				 dialog.cancel();
	    			 }
	    		 });
	    		 	alertDialog.show();
	    	 }
	    	 else {
	    		 //notify user of incoming email with account details
	    		 AlertDialog.Builder alertDialog = new AlertDialog.Builder(ForgotPasswordActivity.this);
	    		 alertDialog.setTitle("Success");
	    		 alertDialog.setMessage("An email has been sent to the address specified with account details.");
					
	    		 alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
	    			 public void onClick(DialogInterface dialog, int which) {
	    				 	dialog.cancel();
							//go back to StartActivity
							onBackPressed();
	    			 }
	    		 });
	    		 	alertDialog.show();
	    	 }
	 	}
	}
}
