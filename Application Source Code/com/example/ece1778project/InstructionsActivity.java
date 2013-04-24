package com.example.ece1778project;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class InstructionsActivity extends Activity {
	
	RelativeLayout rl1, rl2, rl3, rl4, rl5, rl6;
	ImageView iv1, iv2, iv3, iv4;
	Button b1, b2, b3, b4, b5;
	TextView tv, hs, info;
	EditText st;
	int pageNum = 1;
	String username, experiment;
	SQLiteDatabase DB;
	AlarmManager am;
	long timer;
	boolean scheduleExists = false;
	String[] tableText = new String [6];
	Calendar cal;
	String statement, day, month, year, hour, minute;
	Connection conn;
	ProgressBar pb;
	DBConnection dburl = new DBConnection();
	//need to have a table loaded and saved which stores survey schedule and survey name
	//will need to pass 3 terms to alarm notification: username, experimentname, surveyname
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	    
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_instructions);
		
		//locate widgets
		rl1 = (RelativeLayout)findViewById(R.id.relativeLayout1);
		rl2 = (RelativeLayout)findViewById(R.id.relativeLayout2);
		rl3 = (RelativeLayout)findViewById(R.id.relativeLayout3);
		rl4 = (RelativeLayout)findViewById(R.id.relativeLayout4);
		rl5 = (RelativeLayout)findViewById(R.id.relativeLayout5);
		rl6 = (RelativeLayout)findViewById(R.id.relativeLayout6);
		iv1 = (ImageView)findViewById(R.id.imageView1);
		iv2 = (ImageView)findViewById(R.id.imageView2);
		iv3 = (ImageView)findViewById(R.id.imageView3);
		iv4 = (ImageView)findViewById(R.id.imageView4);
		b1 = (Button)findViewById(R.id.button1);
		b2 = (Button)findViewById(R.id.button2);
		b3 = (Button)findViewById(R.id.button3);
		b4 = (Button)findViewById(R.id.button4);
		b5 = (Button)findViewById(R.id.button5);
		tv = (TextView)findViewById(R.id.textView2);
		hs = (TextView)findViewById(R.id.homescreen);
		info = (TextView)findViewById(R.id.info);
		st = (EditText)findViewById(R.id.editText1);
		am = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
		pb = (ProgressBar)findViewById(R.id.progressBar1);
		
		//insert username into textview
		Bundle extras = getIntent().getExtras();
		username = extras.getString("name");
		experiment = extras.getString("experiment");
		tv.setText(username + ",");
		
		statement = "select * from " + experiment + "_schedule";
		ConnectAsyncTask newTask = new ConnectAsyncTask();
		newTask.execute();
		
		iv1.setImageResource(R.drawable.alert);
		iv2.setImageResource(R.drawable.done);
		iv3.setImageResource(R.drawable.question);
		iv4.setImageResource(R.drawable.mydata);
		
		//disable navigation buttons
		b2.setEnabled(false);
		b3.setEnabled(false);
		
		//make first page visible
		rl1.setVisibility(View.VISIBLE);
		
		//If Exit button is pressed at any time
		b4.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				//make instructions disappear
				rl1.setVisibility(View.INVISIBLE);
				rl2.setVisibility(View.INVISIBLE);
				rl3.setVisibility(View.INVISIBLE);
				rl4.setVisibility(View.INVISIBLE);
				rl5.setVisibility(View.INVISIBLE);
				rl6.setVisibility(View.INVISIBLE);
				//show homescreen text
				hs.setVisibility(View.VISIBLE);
				b5.setVisibility(View.VISIBLE);
				info.setVisibility(View.VISIBLE);
				//disable navigation buttons and make them disappear
				b2.setVisibility(View.INVISIBLE);
				b3.setVisibility(View.INVISIBLE);
				b4.setVisibility(View.INVISIBLE);
				b2.setEnabled(false);
				b3.setEnabled(false);
				b4.setEnabled(false);
			}		
		});
		
		//If back button is pressed at any time
		b2.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				//Go to last page
				if (pageNum == 3) {
					rl3.setVisibility(View.INVISIBLE);
					rl2.setVisibility(View.VISIBLE);
					b2.setEnabled(false);
					pageNum = 2;
				}
				else if (pageNum == 4) {
					rl4.setVisibility(View.INVISIBLE);
					rl3.setVisibility(View.VISIBLE);
					pageNum = 3;
				}
				else if (pageNum == 5) {
					rl5.setVisibility(View.INVISIBLE);
					rl4.setVisibility(View.VISIBLE);
					pageNum = 4;
				}
				else if (pageNum == 6) {
					rl6.setVisibility(View.INVISIBLE);
					rl5.setVisibility(View.VISIBLE);
					b3.setEnabled(true);
					pageNum = 5;
				}
			}		
		});
		
		//If next button is pressed at any time
		b3.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				//Go to next page
				if (pageNum == 2) {
					rl2.setVisibility(View.INVISIBLE);
					rl3.setVisibility(View.VISIBLE);
					b2.setEnabled(true);
					pageNum = 3;
				}
				else if (pageNum == 3) {
					rl3.setVisibility(View.INVISIBLE);
					rl4.setVisibility(View.VISIBLE);
					pageNum = 4;
				}
				else if (pageNum == 4) {
					rl4.setVisibility(View.INVISIBLE);
					rl5.setVisibility(View.VISIBLE);
					pageNum = 5;
				}
				else if (pageNum == 5) {
					rl5.setVisibility(View.INVISIBLE);
					rl6.setVisibility(View.VISIBLE);
					b3.setEnabled(false);
					pageNum = 6;
				}
			}		
		});
		
		//If Continue button is pressed 
		b1.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				//switch pages
				rl1.setVisibility(View.INVISIBLE);
				rl2.setVisibility(View.VISIBLE);
				//enable navigation button
				b3.setEnabled(true);
				pageNum = 2;
			}		
		});

		//If Update Survey button is pressed 
		b5.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {	
				/*FOR SAKE OF DEMO*/
				Intent intent = new Intent(InstructionsActivity.this, TimeAlarm.class);
				intent.putExtra("name", username);
				intent.putExtra("experiment", experiment);
				intent.putExtra("surveyName", "Event1");
				PendingIntent pendingIntent = PendingIntent.getBroadcast(InstructionsActivity.this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
				am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pendingIntent);
				
				/*
				//in final working code, the update button would have the following code.
				statement = "select * from " + experiment + "_schedule";
				ConnectAsyncTask newTask = new ConnectAsyncTask();
				newTask.execute();
				 */
			}		
		});	
	}			
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home, menu);
		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;
		switch(item.getItemId()) {
		case R.id.account_settings:
			//Goto account settings activity
			intent = new Intent(InstructionsActivity.this, AccountInfoActivity.class);
			intent.putExtra("name", username);
	    	startActivity(intent);
			return true;
		case R.id.signout:
			//verify that the user wants to log out
			AlertDialog.Builder alertDialog = new AlertDialog.Builder(InstructionsActivity.this);
			alertDialog.setTitle("Confirm Logout");
			alertDialog.setMessage("Are you certain that you want to logout?");		
			alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
					finish();
				}
			});
			alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
			});
			alertDialog.show();
			return true;
		case R.id.moodmap:
			//Goto moodmap activity
			intent = new Intent(InstructionsActivity.this, MoodMap.class);
			intent.putExtra("username", username);
			intent.putExtra("experiment", experiment);
	    	startActivity(intent);
			return true;
		case R.id.trends:
			//Goto mydata activity
			intent = new Intent(InstructionsActivity.this, MyDataActivity.class);
			intent.putExtra("name", username);
			intent.putExtra("experiment", experiment);
	    	startActivity(intent);
			return true;
		default :
			return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	public void onBackPressed() {
		//verify that the user wants to log out
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(InstructionsActivity.this);
		alertDialog.setTitle("Confirm Logout");
		alertDialog.setMessage("Are you certain that you want to logout?");		
		alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
				finish();
			}
		});
		alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		alertDialog.show();
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
	                	 scheduleExists = true;
	                	 statement = "Next Event: " + rs.getString(1) + "/" + rs.getString(2) + "/" + rs.getString(3) + " at " + rs.getString(4) + ":" + rs.getString(5);
	                	 
	                	 day = rs.getString(1);
	                	 month = rs.getString(2);
	                	 year = rs.getString(3);
	                	 hour = rs.getString(4);
	                	 minute = rs.getString(5);
	                	 
	                	 Calendar event = Calendar.getInstance();
	                	 event.set(Calendar.YEAR, Integer.valueOf(rs.getString(3)));
	                	 event.set(Calendar.MONTH, Integer.valueOf(rs.getString(2)));
	                	 event.set(Calendar.DAY_OF_MONTH, Integer.valueOf(rs.getString(1)));
	                	 event.set(Calendar.HOUR_OF_DAY, Integer.valueOf(rs.getString(4)));
	                	 event.set(Calendar.MINUTE, Integer.valueOf(rs.getString(5)));
	                	 event.set(Calendar.SECOND, 0);
	                	 
	                	 cal = Calendar.getInstance();
	                	 
	                	 if (cal.getTimeInMillis() < event.getTimeInMillis()) break;
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
	    	 if(scheduleExists) {
	    		 
	    		 info.setText("Next Event: " + day + "/" + month + "/" + year + " at " + hour + ":" + minute);
	    		 cal = Calendar.getInstance();
            	 cal.set(Calendar.YEAR, Integer.valueOf(year));
            	 cal.set(Calendar.MONTH, Integer.valueOf(month));
            	 cal.set(Calendar.DAY_OF_MONTH, Integer.valueOf(day));
            	 cal.set(Calendar.HOUR_OF_DAY, Integer.valueOf(hour));
            	 cal.set(Calendar.MINUTE, Integer.valueOf(minute));
            	 cal.set(Calendar.SECOND, 0);
	    		 //set alarm for notification for closest future event
	    		 Intent intent = new Intent(InstructionsActivity.this, TimeAlarm.class);
				 intent.putExtra("name", username);
				 intent.putExtra("experiment", experiment);
				 intent.putExtra("surveyName", "Event1");
				 PendingIntent pendingIntent = PendingIntent.getBroadcast(InstructionsActivity.this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
				 am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
	    	 }
	  	}
	}
}
