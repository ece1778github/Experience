package com.example.ece1778project;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class QuestionActivity extends Activity {

	String username, experiment, currentLatitude, currentLongitude, surveyName;
	String mood = "3";
	private static final String[] rating = {"1", "2", "3", "4", "5"};
	SQLiteDatabase DB;
	SeekBar seek;
	Button b1;
	GPSTracker gps;
	TextView tv2;
	
	RelativeLayout layout[] = new RelativeLayout[4];
	CheckBox cb[] = new CheckBox[5];
	TextView tv000, tv00, tv_0, tv_1, tv_2;
	SeekBar sb;
	EditText et;
	int counter = 0, type, numAns, questionCount = 0, i;
	Button button;
	Cursor c = null;
	String results[];
	String questions[];
	String ans, pAns;
	String dateTime;
	String statement, day, month, year, hour, minute;
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
		setContentView(R.layout.activity_question);
		
		//locate widgets
		seek = (SeekBar)findViewById(R.id.seekBar1);
		b1 = (Button)findViewById(R.id.submit);
		tv2 = (TextView)findViewById(R.id.textView2);
		layout[0] = (RelativeLayout)findViewById(R.id.edittextLayout);
		layout[1] = (RelativeLayout)findViewById(R.id.checkboxLayout);
		layout[2] = (RelativeLayout)findViewById(R.id.sliderLayout);
		layout[3] = (RelativeLayout)findViewById(R.id.moodLayout);
		tv000 = (TextView)findViewById(R.id.text000);
		tv00 = (TextView)findViewById(R.id.text00);
		tv_0 = (TextView)findViewById(R.id.text0);
		tv_1 = (TextView)findViewById(R.id.text1);
		tv_2 = (TextView)findViewById(R.id.text2);
		cb[0] = (CheckBox)findViewById(R.id.checkBox1);
		cb[1] = (CheckBox)findViewById(R.id.checkBox2);
		cb[2] = (CheckBox)findViewById(R.id.checkBox3);
		cb[3] = (CheckBox)findViewById(R.id.checkBox4);
		cb[4] = (CheckBox)findViewById(R.id.checkBox5);
		et = (EditText)findViewById(R.id.editText1);
		sb = (SeekBar)findViewById(R.id.seekBar);
		
		//get tablename and database name
		Bundle extras = getIntent().getExtras();
		username = extras.getString("name");
		experiment = extras.getString("experiment");
		surveyName = extras.getString("surveyName");
		
		DB = QuestionActivity.this.openOrCreateDatabase(experiment, MODE_PRIVATE, null);
		DB.execSQL("DROP TABLE IF EXISTS " + surveyName + ";");
		DB.execSQL("CREATE TABLE IF NOT EXISTS " + surveyName + " (Question VARCHAR, QType INT, ANum INT, " +
				"A1 VARCHAR, A2 VARCHAR, A3 VARCHAR, A4 VARCHAR, A5 VARCHAR);");

		statement = "select * from " + surveyName;
		ConnectAsyncTask newTask = new ConnectAsyncTask();
		newTask.execute();
		
		b1.setText("Next");
	
		
		//for mood map information
		tv2.setText("Calm");
		seek.setProgress(2);
		seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			 @Override
			 public void onStopTrackingTouch(SeekBar seekBar) {
				 // TODO Auto-generated method stub
			 }
			 @Override
			 public void onStartTrackingTouch(SeekBar seekBar) {
				 // TODO Auto-generated method stub
			 }
			 @Override
			 public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
				 // TODO Auto-generated method stub
				 if(progress == 4) tv2.setText("Mad");
				 else if(progress == 3) tv2.setText("Happy");
				 else if(progress == 2) tv2.setText("Calm");
				 else if(progress == 1) tv2.setText("Sad");
				 else if(progress == 0) tv2.setText("Hyper");
				 mood = rating[progress];
			 }
		});
		//end of for mood map information
		
		c = DB.rawQuery("SELECT * FROM " + surveyName, null);
		//get number of questions in survey
		if (c != null) {
			if(c.moveToFirst()) {
				do {
					questionCount++;
				} while (c.moveToNext());
			}
		}
		results = new String[questionCount+2];
		questions = new String[questionCount+2];
		
		c = DB.rawQuery("SELECT * FROM " + surveyName, null);
		
		//make one checkbox true at all times and store that answer to some variable whenever a new one is checked
				cb[0].setChecked(true);
				cb[0].setOnCheckedChangeListener(new OnCheckedChangeListener()
				{
				    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
				    {
				        if ( isChecked )
				        {
				        	cb[1].setChecked(false);
				        	cb[2].setChecked(false);
				        	cb[3].setChecked(false);
				        	cb[4].setChecked(false);
				        	ans = c.getString(c.getColumnIndex("A1"));
				        }
				    }
				});
				cb[1].setOnCheckedChangeListener(new OnCheckedChangeListener()
				{
				    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
				    {
				        if ( isChecked )
				        {
				        	cb[0].setChecked(false);
				        	cb[2].setChecked(false);
				        	cb[3].setChecked(false);
				        	cb[4].setChecked(false);
				        	ans = c.getString(c.getColumnIndex("A2"));
				        }
				    }
				});
				cb[2].setOnCheckedChangeListener(new OnCheckedChangeListener()
				{
				    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
				    {
				        if ( isChecked )
				        {
				        	cb[0].setChecked(false);
				        	cb[1].setChecked(false);
				        	cb[3].setChecked(false);
				        	cb[4].setChecked(false);
				        	ans = c.getString(c.getColumnIndex("A3"));
				        }
				    }
				});
				cb[3].setOnCheckedChangeListener(new OnCheckedChangeListener()
				{
				    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
				    {
				        if ( isChecked )
				        {
				        	cb[1].setChecked(false);
				        	cb[2].setChecked(false);
				        	cb[0].setChecked(false);
				        	cb[4].setChecked(false);
				        	ans = c.getString(c.getColumnIndex("A4"));
				        }
				    }
				});
				cb[4].setOnCheckedChangeListener(new OnCheckedChangeListener()
				{
				    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
				    {
				        if ( isChecked )
				        {
				        	cb[1].setChecked(false);
				        	cb[2].setChecked(false);
				        	cb[3].setChecked(false);
				        	cb[0].setChecked(false);
				        	ans = c.getString(c.getColumnIndex("A5"));
				        }
				    }
				});
		        
				//store changing seekbar values
				sb.setProgress(5);
				sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
					 @Override
					 public void onStopTrackingTouch(SeekBar seekBar) {
						 // TODO Auto-generated method stub
					 }
					 @Override
					 public void onStartTrackingTouch(SeekBar seekBar) {
						 // TODO Auto-generated method stub
					 }
					 @Override
					 public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
						 // TODO Auto-generated method stub
						 pAns = String.valueOf(progress+1);
					 }
				});		
		
		//if submit button is clicked
		b1.setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View v) {
		    	if (counter == 0) {
		    		layout[3].setVisibility(View.INVISIBLE);
		    		if(c!=null) {
		            	if (c.moveToFirst()) {
		            		layout[2].setVisibility(View.VISIBLE);
		            		//Get answer info
		            		tv_0.setText("Please rate how are you feeling right now in terms of mood?");
		            		tv_1.setText("Negative");
		            		tv_2.setText("Positive");
		            		sb.setProgress(5);
		            		pAns = "5";
		            	}
		            }   		
		    		questions[counter] = "Please select the word that best describes your current mood.";
		    		results[counter] = mood;
		    		counter++;
		    	}
		    	else if (counter == 1) {
		    		layout[2].setVisibility(View.INVISIBLE);
		    		questions[counter] = "Please rate how are you feeling right now (mood)?";
		    		results[counter] = pAns;
		    		counter++;
					//get next question info
					if(c.moveToFirst()) {
						type = c.getInt(c.getColumnIndex("QType"));
			    		layout[type].setVisibility(View.VISIBLE);
			        	if(type == 0) {
			        		//Get answer info
			        		tv000.setText(c.getString(c.getColumnIndex("Question")));
			        	}
			        	else if(type == 1) {
			        		//Get answer info
			        		tv00.setText(c.getString(c.getColumnIndex("Question")));
			        		numAns = c.getInt(c.getColumnIndex("ANum"));
			       			//make the correct number of checkboxes visible and set their texts
			       			for(int i = 0; i < numAns; i++) {
			       				cb[i].setText(c.getString(c.getColumnIndex("A" + (i+1))));
			       				cb[i].setVisibility(View.VISIBLE);
			        		}
			       			cb[0].setChecked(true);
			       			ans = c.getString(c.getColumnIndex("A1"));
			        	}
			        	else if(type == 2) {
			        		//Get answer info
			        		tv_0.setText(c.getString(c.getColumnIndex("Question")));
			       			tv_1.setText(c.getString(c.getColumnIndex("A1")));
			       			tv_2.setText(c.getString(c.getColumnIndex("A2")));
			       			sb.setProgress(5);
			       			pAns = "5";
			       		}
					}
		    	}
		    	else {
		    		//make previous layout invisible
					layout[type].setVisibility(View.INVISIBLE);
				
					//store result depending on type
					if (type == 0) results[counter] = String.valueOf(et.getText());
					else if (type == 1) results[counter] = ans;
					else if (type == 2) results[counter] = pAns;
					
					questions[counter] = c.getString(c.getColumnIndex("Question"));
					counter++;
					//get next question info
					if(c.moveToNext()) {
						type = c.getInt(c.getColumnIndex("QType"));
		    			layout[type].setVisibility(View.VISIBLE);
		        		if(type == 0) {
		        			//Get answer info
		        			tv000.setText(c.getString(c.getColumnIndex("Question")));
		        		}
		        		else if(type == 1) {
		        			//Get answer info
		        			tv00.setText(c.getString(c.getColumnIndex("Question")));
		        			numAns = c.getInt(c.getColumnIndex("ANum"));
		        			//make the correct number of checkboxes visible and set their texts
		        			for(int i = 0; i < numAns; i++) {
		        				cb[i].setText(c.getString(c.getColumnIndex("A" + (i+1))));
		        				cb[i].setVisibility(View.VISIBLE);
		        			}
		        			cb[0].setChecked(true);
		        			ans = c.getString(c.getColumnIndex("A1"));
		        		}
		        		else if(type == 2) {
		        			//Get answer info
		        			tv_0.setText(c.getString(c.getColumnIndex("Question")));
		        			tv_1.setText(c.getString(c.getColumnIndex("A1")));
		        			tv_2.setText(c.getString(c.getColumnIndex("A2")));
		        			sb.setProgress(5);
		        			pAns = "5";
		        		}
		        		
		        		if (counter == questionCount+1) b1.setText("Submit");
					}
					else {
			    		//get time and date
			    		Calendar c = Calendar.getInstance();
			    		int Hr24 = c.get(Calendar.HOUR_OF_DAY);
			    		int Min = c.get(Calendar.MINUTE);
			    		int mYear = c.get(Calendar.YEAR);
			    		int mMonth = c.get(Calendar.MONTH);
			    		//note that January == 0
			    		mMonth++;
			    		int mDay = c.get(Calendar.DAY_OF_MONTH);
				   
			    		//get GPS Location
			    		gps = new GPSTracker(QuestionActivity.this);
					
			    		if(gps.canGetLocation()) {
			    			double latitude = gps.getLatitude();
			    			double longitude = gps.getLongitude();
			    			currentLatitude = Double.toString(latitude);				
			    			currentLongitude = Double.toString(longitude);
			    		}
			    		
			    		int ampm;
			    		dateTime = "";	
			    		if (mDay < 10) dateTime = "0";
			    		dateTime += mDay + "/";
			    		if (mMonth < 10) dateTime += "0";
			    		dateTime += mMonth + "/" + mYear + " - ";
			    		if (Hr24 < 10) dateTime += "0";
			    		if (Hr24 > 12){
			    			dateTime += Hr24 - 12;
			    			ampm = 1;		
			    		}
			    		else {
			    			dateTime += Hr24;
			    			ampm = 0;
			    		}
			    		dateTime += ":";
			    		if (Min < 10) dateTime += "0";
			    		dateTime += Min;
			    		if (ampm == 0) dateTime += " am";
			    		else dateTime += " pm";
			    		
						//save results to database
						
			    		for (i = 0; i < questionCount + 2; i++){
			    			statement = "insert into " + username + "_results Values ('" + dateTime + "', '" + questions[i] + "', '" + results[i] + "', '" + currentLatitude +  "', '" + currentLongitude + "')";
			    			ConnectAsyncTask newTask = new ConnectAsyncTask();
			    			newTask.execute();
			    		}
			    		
						//notify user that the mood has been saved
						AlertDialog.Builder alertDialog = new AlertDialog.Builder(QuestionActivity.this);
						alertDialog.setTitle("Survey Complete");
						alertDialog.setMessage("Thank you. You will be notified when the next event occurs.");		
						alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								dialog.cancel();
								//close DB
								DB.close();
								onBackPressed();
							}
						});
						alertDialog.show();
					}
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
	                 
	                 
	                 if (statement == ("select * from " + surveyName)) {
	                	 while (rs.next()) {
	                		 //populate internal database for future queries
	                		 c = DB.rawQuery("SELECT COUNT(*) FROM " + surveyName, null);
	                		 if(c != null) {
	                			 c.moveToFirst();
	                			 if (c.getInt(0) == 0) {
	                				 if (rs.getString(2).equals("1")) {
	                					 DB.execSQL("INSERT INTO " + surveyName + " (Question, QType, ANum, A1, A2, A3, A4, A5) " +
	                							 "Values ('"+rs.getString(1)+"', '"+rs.getString(2)+"', '"+rs.getString(3)+"', '"+rs.getString(4)+"', '"+rs.getString(5)+
	                							 "', '"+rs.getString(6)+"', '"+rs.getString(7)+"', '"+rs.getString(8)+"');");
	                				 }
	                				 else if (rs.getString(2).equals("2")) {
	                					 DB.execSQL("INSERT INTO " + surveyName + " (Question, QType, ANum, A1, A2) " +
	                							 "Values ('"+rs.getString(1)+"', '"+rs.getString(2)+"', '"+rs.getString(3)+"', '"+rs.getString(4)+"', '"+rs.getString(5)+"');");
	                				 }
	                				 else if (rs.getString(2).equals("0")) {
	                					 DB.execSQL("INSERT INTO " + surveyName + " (Question, QType) " +
	             							"Values ('"+rs.getString(1)+"', '"+rs.getString(2)+"');");
	                				 }
	                			 }
	                		 }
	                	 }
	             	}
	                 else {
	                	 PreparedStatement stInsert = conn.prepareStatement("insert into "+ username + "_results (DateTime VARCHAR, Question VARCHAR, Answer VARCHAR, Latitude VARCHAR, Longitude VARCHAR)"
	                			 	+ "values (?, ?, ?, ?, ?)");
	                	 stInsert.setString(1, dateTime);
	                	 stInsert.setString(2, questions[i]);
	                	 stInsert.setString(3, results[i]);
	                	 stInsert.setString(4, currentLatitude);
	                	 stInsert.setString(5, currentLongitude);
	                	 stInsert.executeUpdate();
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
	  	}
	}
	
}
