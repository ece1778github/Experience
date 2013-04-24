package com.example.ece1778project;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

public class AccountInfoActivity extends Activity {

	SQLiteDatabase DB;
	String DBNAME = "acctDB";
	String TABLENAME = "acctTable";
	String fname, lname, pwd, cpwd, email, cemail, bdate, username;
	EditText et1, et2, et3, et4, et5, et6, et7;
	Button b1;
	ProgressBar pb;
    String statement;
	Connection conn;
	DBConnection dburl = new DBConnection();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//Remove titlebar within app
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
	        WindowManager.LayoutParams.FLAG_FULLSCREEN);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_account_info);
		
		//get username
		Bundle extras = getIntent().getExtras();
		username = extras.getString("name");
		
		//locate widgets
		et1 = (EditText)findViewById(R.id.editText1);
		et2 = (EditText)findViewById(R.id.editText2);
		et3 = (EditText)findViewById(R.id.editText3);
		et4 = (EditText)findViewById(R.id.editText4);
		et5 = (EditText)findViewById(R.id.editText5);
		et6 = (EditText)findViewById(R.id.editText6);
		et7 = (EditText)findViewById(R.id.editText7);
		b1 = (Button)findViewById(R.id.button1);
		pb = (ProgressBar)findViewById(R.id.progressBar1);
		
		statement = "SELECT * FROM " + TABLENAME + " WHERE Username = '" + username + "'";
		ConnectAsyncTask newTask = new ConnectAsyncTask();
		newTask.execute();
		
		//if submit button is clicked
		b1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//get updated info
				fname = String.valueOf(et1.getText());
				lname = String.valueOf(et2.getText());
				pwd = String.valueOf(et3.getText());
				cpwd = String.valueOf(et4.getText());
				email = String.valueOf(et5.getText());
				cemail = String.valueOf(et6.getText());
				bdate = String.valueOf(et7.getText());
				if (fname.equals("") || lname.equals("") || pwd.equals("")
						 || cpwd.equals("") || email.equals("") || cemail.equals("") || bdate.equals("")) {
					//if any entry has not been filled out, notify user of failure
					AlertDialog.Builder alertDialog = new AlertDialog.Builder(AccountInfoActivity.this);
					alertDialog.setTitle("Failure");
					alertDialog.setMessage("All entries need to be filled in order to update account information.");
					
					alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
						}
					});
					alertDialog.show();
				}
				else if(!pwd.equals(cpwd)) {
					//notify user to have matching passwords
					AlertDialog.Builder alertDialog = new AlertDialog.Builder(AccountInfoActivity.this);
					alertDialog.setTitle("Password Mismatch");
					alertDialog.setMessage("The passwords that you specified do not match.");		
					alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
						}
					});
					alertDialog.show();	
				}
				else if (!email.equals(cemail)) {
					//notify user to have matching email addresses
					AlertDialog.Builder alertDialog = new AlertDialog.Builder(AccountInfoActivity.this);
					alertDialog.setTitle("E-mail Mismatch");
					alertDialog.setMessage("The e-mail addresses that you specified do not match.");		
					alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
						}
					});
					alertDialog.show();	
				}
				else {
					//open or create database and table
					DB = AccountInfoActivity.this.openOrCreateDatabase(DBNAME, MODE_PRIVATE, null);
							
					//store to table
					DB.execSQL("UPDATE " + TABLENAME + " SET First='" + fname + "', Last='" + lname + "', Password='" +
							pwd + "', Email='" + email + "', Birthday='" + bdate + "' WHERE Username='" + username + "';");	
					//close DB
					DB.close();
							
					statement = "UPDATE " + TABLENAME + " SET fname='" + fname + "', lname='" + lname + "', password='" +
							pwd + "', email='" + email + "', birthdate='" + bdate + "' WHERE Username='" + username + "'";
					ConnectAsyncTask newTask2 = new ConnectAsyncTask();
					newTask2.execute();
					
					//notify user that the mood has been saved
					AlertDialog.Builder alertDialog = new AlertDialog.Builder(AccountInfoActivity.this);
					alertDialog.setTitle("Updated");
					alertDialog.setMessage("Your account information has been successfully updated.");		
					alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
							onBackPressed();
						}
					});
					alertDialog.show();	
				}	
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
	                	 if (statement == "SELECT * FROM " + TABLENAME + " WHERE Username = '" + username + "'") {
	                		 fname = rs.getString(1);
	                		 lname = rs.getString(2);
	                		 pwd = rs.getString(4);
	                		 email = rs.getString(5);
	                		 bdate = rs.getString(6);
	                	 }
	                	 else {
	                		 PreparedStatement stInsert = conn.prepareStatement("insert into person (fname, lname, password, email, birthdate) "
		                			 	+ "values (?, ?, ?, ?, ?)");
		                	 stInsert.setString(1, fname);
		                	 stInsert.setString(2, lname);;
		                	 stInsert.setString(3, pwd);
		                	 stInsert.setString(4, email);
		                	 stInsert.setString(5, bdate);

		                	 stInsert.executeUpdate();
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
	    	 et1.setText(fname);
			 et2.setText(lname);
			 et3.setText(pwd);
			 et4.setText(pwd);
			 et5.setText(email);
			 et6.setText(email);
			 et7.setText(bdate);
	  	}
	}
}
