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
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;

public class RegisterActivity extends Activity implements AdapterView.OnItemSelectedListener {

	EditText et1, et2, et3, et4, et5, et6, et7, et8;
	String fname, lname, uname, pwd, cpwd, email, cemail, bdate;
	Spinner s1, s2;
	Button submit;
	boolean formFilled = false;
	boolean unameExists = false;
	boolean emailExists = false;
	private static final String[] items = {"Select Gender", "Male", "Female"};
	//note that in final design, experiment names must be loaded from some table
	private static final String[] experiments = {"Select Experiment", "Experiment 1", "Experiment 2", "Experiment 3", "Experiment 4", "Experiment 5"};
	String gender, experiment;
	
	SQLiteDatabase sampleDB, experimentDB;
	String DBNAME = "acctDB";
	String DBNAME2 = "expNum";
	String TABLENAME = "acctTable";
	
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
		setContentView(R.layout.activity_register);
		
		//locate widgets
		et1 = (EditText)findViewById(R.id.editText1);
		et2 = (EditText)findViewById(R.id.editText2);
		et3 = (EditText)findViewById(R.id.editText3);
		et4 = (EditText)findViewById(R.id.editText4);
		et5 = (EditText)findViewById(R.id.editText5);
		et6 = (EditText)findViewById(R.id.editText6);
		et7 = (EditText)findViewById(R.id.editText7);
		et8 = (EditText)findViewById(R.id.editText8);
		s1 = (Spinner)findViewById(R.id.spinner1);
		s2 = (Spinner)findViewById(R.id.spinner2);
		submit = (Button)findViewById(R.id.button1);
		pb = (ProgressBar)findViewById(R.id.progressBar1);
		
		//populate dropdown/spinner menu
		s1.setOnItemSelectedListener(this);
		ArrayAdapter<String> aa = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, items);
		aa.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);
		s1.setAdapter(aa);
		s2.setOnItemSelectedListener(this);
		ArrayAdapter<String> aa2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, experiments);
		aa2.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);
		s2.setAdapter(aa2);
		//Record spinner selections
		s1.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
				//store dropdown/spinner selection
				gender = items[position];
			}
			public void onNothingSelected(AdapterView<?> parent) {
				//do nothing
			}
		});
		s2.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
				//store dropdown/spinner selection
				experiment = experiments[position];
			}
			public void onNothingSelected(AdapterView<?> parent) {
				//do nothing
			}
		});
		
		submit.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				//make sure that all fields were filled out correctly
				fname = et1.getText().toString();
				lname = et2.getText().toString();
				uname = et3.getText().toString();
				pwd = et4.getText().toString();
				cpwd = et5.getText().toString();
				email = et6.getText().toString();
				cemail = et7.getText().toString();
				bdate = et8.getText().toString();
				
				
				//Check to see that all entries were filled out
				if (fname.equals("") || lname.equals("") || uname.equals("") || pwd.equals("")
						 || cpwd.equals("") || email.equals("") || cemail.equals("") || bdate.equals("")
						 || gender.equals("Select Gender") || experiment.equals("Select Experiment")) {
					//if any entry has not been filled out, notify user of failure
					AlertDialog.Builder alertDialog = new AlertDialog.Builder(RegisterActivity.this);
					alertDialog.setTitle("Submit Failure");
					alertDialog.setMessage("The form was not correctly filled out. Please go back and fill out every entry.");
					
					alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
						}
					});
					alertDialog.show();
				}
				else {
					//if all entries are non-empty, check to see passwords match
					if (pwd.equals(cpwd)) {
						//and check if emails match
						if (email.equals(cemail)) {
							//if passwords and emails match, then form is filled out correctly
							formFilled = true;
						}
						else {
							//if email addresses do not match, notify user
							AlertDialog.Builder alertDialog = new AlertDialog.Builder(RegisterActivity.this);
							alertDialog.setTitle("E-mail Mismatch");
							alertDialog.setMessage("The e-mail addresses that you specified do not match.");
							
							alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									dialog.cancel();
								}
							});
							alertDialog.show();
						}
					}
					else {
						//if passwords do not match, notify user
						AlertDialog.Builder alertDialog = new AlertDialog.Builder(RegisterActivity.this);
						alertDialog.setTitle("Password Mismatch");
						alertDialog.setMessage("The passwords that you specified do not match.");
						
						alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								dialog.cancel();
							}
						});
						alertDialog.show();
					}
				}

				if (formFilled) {
					unameExists = false;
					emailExists = false;
					statement = "select * from person where username='" + uname + "'";
					ConnectAsyncTask newTask = new ConnectAsyncTask();
					newTask.execute();
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
	                	 //if a record was found send account information to email address
	                	 unameExists = true;
	                 }
	                 
	                 if (!unameExists) {
	                	 statement = "select * from person where email='" + email + "'";
	                	 rs = st.executeQuery(statement);
	                	 
	                	 while (rs.next()) {
	                		 emailExists = true;
	                	 }
	                 }
	                 if (!unameExists && !emailExists) {
	                	 PreparedStatement stInsert = conn.prepareStatement("insert into person (fname, lname, username, password, email, birthdate, gender, experiment) "
	                			 	+ "values (?, ?, ?, ?, ?, ?, ?, ?)");
	                	 stInsert.setString(1, fname);
	                	 stInsert.setString(2, lname);
	                	 stInsert.setString(3, uname);
	                	 stInsert.setString(4, pwd);
	                	 stInsert.setString(5, email);
	                	 stInsert.setString(6, bdate);
	                	 stInsert.setString(7, gender);
	                	 stInsert.setString(8, experiment);
	                	 stInsert.executeUpdate();
	                	 
	                	 try {   
	                         GMailSender sender = new GMailSender("eXperienceaccts@gmail.com", "ece1778h1s");
	                         sender.sendMail("Welcome to eXperience",   
	                                 "Thank you for registering an account with eXperience " + uname + 
	                                 ". This email is to confirm that the email address that you specified is correctly associated with your account.",   
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
	    	 if (unameExists) {
	    		 //if account and associated password do not exist, notify user
	    		 AlertDialog.Builder alertDialog = new AlertDialog.Builder(RegisterActivity.this);
	    		 alertDialog.setTitle("Failed");
	    		 alertDialog.setMessage("There is already an account associated with the username that was given. Please try again.");
					
	    		 alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
	    			 public void onClick(DialogInterface dialog, int which) {
	    				 dialog.cancel();
	    			 }
	    		 });
	    		 	alertDialog.show();
	    	 }
	    	 else if (emailExists) {
	    		 //if account and associated password do not exist, notify user
	    		 AlertDialog.Builder alertDialog = new AlertDialog.Builder(RegisterActivity.this);
	    		 alertDialog.setTitle("Failed");
	    		 alertDialog.setMessage("There is already an account associated with the email address that was given. Please try again.");
					
	    		 alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
	    			 public void onClick(DialogInterface dialog, int which) {
	    				 dialog.cancel();
	    			 }
	    		 });
	    		 	alertDialog.show();
	    	 }
	    	 else {
	    		 //notify user of incoming email with account details
	    		 AlertDialog.Builder alertDialog = new AlertDialog.Builder(RegisterActivity.this);
	    		 alertDialog.setTitle("Success");
	    		 alertDialog.setMessage("An confirmation email has been sent to the email address specified. \n" + fname + "','" + lname + "','" + uname + "','" + pwd + "','" 
            			 + email + "','" + bdate + "', '" + gender + "', '" + experiment);
					
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
	
	@Override
	public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
		//store dropdown/spinner selection
		//gender = items[position];
		//experiment = experiments[position];
		//submit.setText(experiment);
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		//do nothing
	}
	
}
