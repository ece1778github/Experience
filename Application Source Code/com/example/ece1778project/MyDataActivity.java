package com.example.ece1778project;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.Arrays;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.androidplot.series.XYSeries;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYStepMode;

public class MyDataActivity extends Activity {

	TextView tv;
	SQLiteDatabase DB;
	String tableData = "", surveyName = "Event1", username, experiment;
	LinearLayout chartLayout, pieLayout;
	WebView pie;
	ScrollView tableLayout;
	XYPlot myPlot;
	int chartVis = 0;
	int counter = 0;
	Number []ratings;
    String []entrytime;
    
    double hyperCount = 0, sadCount = 0, calmCount = 0, happyCount = 0, madCount = 0;
    boolean noData = false;
    ProgressBar pb;
    String statement;
	Connection conn;
	DBConnection dburl = new DBConnection();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_data);
		
		Bundle extras = getIntent().getExtras();
		username = extras.getString("name");
		experiment = extras.getString("experiment");
		
		//locate widgets
		tv = (TextView)findViewById(R.id.textView1);
		chartLayout = (LinearLayout)findViewById(R.id.linearLayout1);
		myPlot = (XYPlot)findViewById(R.id.XYPlot);
		tableLayout = (ScrollView)findViewById(R.id.scrollview1);
		pieLayout = (LinearLayout)findViewById(R.id.linearLayout2);
		pie = (WebView)findViewById(R.id.webView);
		pie.getSettings().setJavaScriptEnabled(true);
		pb = (ProgressBar)findViewById(R.id.progressBar1);
		
		Cursor c = null;
		
		//get PieGraph Data
		statement = "SELECT * FROM " + username + "_results WHERE Question = 'Please select the word that best describes your current mood.'";
		ConnectAsyncTask newTask = new ConnectAsyncTask();
		newTask.execute();
		
        //get line graph data
        ratings = new Number[10];
        statement = "SELECT * FROM " + username + "_results WHERE Question = 'Please rate how are you feeling right now (mood)?'";
        ConnectAsyncTask newTask2 = new ConnectAsyncTask();
		newTask2.execute();
        
        //get info for table
        statement = "SELECT * FROM " + username + "_results";
        ConnectAsyncTask newTask3 = new ConnectAsyncTask();
		newTask3.execute();
        tv.setText(Html.fromHtml(tableData));
        
        if (tableData.equals("")) noData = true;
        
        //set up pie graph
        new PieAsyncTask().execute();
        
        //set up line graph
        XYSeries series1 = new SimpleXYSeries(Arrays.asList(ratings), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Rating of Mood");
        LineAndPointFormatter series1Format = new LineAndPointFormatter(
				Color.rgb(0,200,0), 	//line color
				Color.rgb(0,100,0), 	//point color
				null);					//fill color
		myPlot.addSeries(series1,series1Format);
		myPlot.setDomainValueFormat(new Format() {
			@Override
			public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
				Number num = (Number) obj;
				for(int z=0; z<counter;z++) {
					if(num.intValue() == z) {
						toAppendTo.append(z+1);
						break;
					}
				}
				return toAppendTo;
			}
			@Override
			public Object parseObject(String source, ParsePosition pos) {
				return null;
			}
		});
		myPlot.setRangeTopMin(11);
		myPlot.setDomainRightMin(counter);
		myPlot.setDomainStep(XYStepMode.INCREMENT_BY_VAL, 1);
		myPlot.setRangeStep(XYStepMode.INCREMENT_BY_VAL, 1);
		myPlot.setTicksPerDomainLabel(1);
		myPlot.setTitle("Mood Over Time");
		myPlot.setRangeLabel("Rating");
		myPlot.setDomainLabel("Entry #");
		
        
	}
	
	//Pie Graph Thread(must do in background since loading URL)
	private class PieAsyncTask extends AsyncTask<Void, Integer, Void> {
	      
		@Override
	    protected void onPreExecute() {
			super.onPreExecute();
	    	pb.setVisibility(View.VISIBLE);
	    }

		@Override
		protected Void doInBackground(Void... params) {
			//get search URL and put it in a bundle
			String URLstart = "http://chart.apis.google.com/chart?cht=p3&chs=280x200&chd=t:";
			String URLdata = happyCount/counter + "," + sadCount/counter + "," + madCount/counter + "," +
					calmCount/counter + "," + hyperCount/counter;
			String URLtitle = "&chtt=Distribution+of+Moods&chco=CC0000|0033FF|CC33FF|335423|FF9900&chdl=";
			String happy = String.format("%.2f", happyCount/counter*100);
			String sad = String.format("%.2f", sadCount/counter*100);
			String mad = String.format("%.2f", madCount/counter*100);
			String calm = String.format("%.2f", calmCount/counter*100);
			String hyper = String.format("%.2f", hyperCount/counter*100);		
			String URLlabels = "Happy (" + happy + 
					"%)|Sad (" + sad +
					"%)|Mad (" + mad +
					"%)|Calm (" + calm +
					"%)|Hyper (" + hyper + "%)";
			pie.loadUrl(URLstart + URLdata + URLtitle + URLlabels);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			pb.setVisibility(View.INVISIBLE);
		}
	}
	
	//populate screen based on menu option selected
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.mydata, menu);
		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.toggle1:
			if (!noData) {
				chartLayout.setVisibility(View.VISIBLE);
				tableLayout.setVisibility(View.INVISIBLE);
				pieLayout.setVisibility(View.INVISIBLE);
				chartVis = 1;
			}
			return true;
		case R.id.toggle2:
			if (!noData) {
				chartLayout.setVisibility(View.INVISIBLE);
				tableLayout.setVisibility(View.INVISIBLE);
				pieLayout.setVisibility(View.VISIBLE);
				chartVis = 2;
			}
			return true;
		case R.id.toggle3:
			if (!noData) {
				chartLayout.setVisibility(View.INVISIBLE);
				tableLayout.setVisibility(View.VISIBLE);
				pieLayout.setVisibility(View.INVISIBLE);
				chartVis = 0;
			}
			return true;
		
		default :
			return super.onOptionsItemSelected(item);
		}
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
	                 
	                 
	                 int i = 0;
	                 int z = 1;
	                 int y = 1;
	                 tableData = "";
	                 while (rs.next()) {
	                	 if(statement.equals("SELECT * FROM " + username + "_results WHERE Question = 'Please select the word that best describes your current mood.'")) {
	                		 String mood = rs.getString(3);
	        				 if(mood.equals("5")) madCount++;
	        				 else if(mood.equals("4")) happyCount++;
	        				 else if(mood.equals("3")) calmCount++;
	        				 else if(mood.equals("2"))  sadCount++;
	        				 else if(mood.equals("1")) hyperCount++;
	             			 counter++;
	                	 }
	                	 else if (statement == "SELECT * FROM " + username + "_results WHERE Question = 'Please rate how are you feeling right now (mood)?'") {
	                		 ratings[i] = Integer.valueOf(rs.getString(3));
	             			 i++;
	                	 }
	                	 else {
	                		 String newEntry = rs.getString(2);
	             			 if (newEntry.equals("Please select the word that best describes your current mood.")) {
	             				z++;
	             				y = 1;
	             				tableData += "<br><b><u>Entry " + z + "</b></u><br>Date/Time: <i>";
	             				tableData += rs.getString(1);
	             				tableData += "<br></i>Q" + y + ": ";
	                 			tableData += rs.getString(2);
	                 			tableData += "<br>Response: <i>";
	                 			String mood = rs.getString(3);
	                				 if(mood.equals("5")) {
	                				 	tableData += "Mad";
	                				 }
	                				 else if(mood.equals("4")) {
	                				 	tableData += "Happy";
	                				 }
	                				 else if(mood.equals("3")) {
	                				 	tableData += "Calm";
	                				 }
	                				 else if(mood.equals("2")) {
	                				 	tableData += "Sad";
	                				 }
	                				 else if(mood.equals("1")) {
	                				 	tableData += "Hyper";
	                				 }
	             			}
	             			else {
	             				tableData += "Q" + y + ": ";
	             				tableData += rs.getString(2);
	             				tableData += "<br>Response: <i>";
	            				 	tableData += rs.getString(3);
	             			}
	             			tableData += "<br></i>";
	             			y++;
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
	    	 
	  	}
	}
}
