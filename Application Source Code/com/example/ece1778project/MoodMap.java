package com.example.ece1778project;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MoodMap extends Activity {
	
	LatLng Location, currentLocation;
	private GoogleMap map;
	GPSTracker gps;
	Double currentLatitude, currentLongitude;
	SQLiteDatabase DB;
	String username, experiment, moodRating;
	String dateTime;
	double latitude, longitude;
	Cursor c;
	int ampm;
	ProgressBar pb;
    String statement;
	Connection conn;
	DBConnection dburl = new DBConnection();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mood_map);
		
		//get tablename and database name
		Bundle extras = getIntent().getExtras();
		username = extras.getString("username");
		experiment = extras.getString("experiment");
		pb = (ProgressBar)findViewById(R.id.progressBar1);
		
		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
		        .getMap();	
		
		//open or create database and table
		DB = MoodMap.this.openOrCreateDatabase(experiment, MODE_PRIVATE, null);
		DB.execSQL("DROP TABLE IF EXISTS " + username + "_results;");
		DB.execSQL("CREATE TABLE IF NOT EXISTS " + username + "_results (DateTime VARCHAR, Question VARCHAR, Answer VARCHAR, Latitude VARCHAR, Longitude VARCHAR);");
		
		//copy existing database into
		statement = "SELECT * FROM " + username + "_results";
		ConnectAsyncTask newTask = new ConnectAsyncTask();
		newTask.execute();
		
		c = null;
		c = DB.rawQuery("SELECT * FROM " + username + "_results WHERE Question = 'Please select the word that best describes your current mood.'", null);
		if(c.moveToFirst()) {
			do {
				dateTime = c.getString(c.getColumnIndex("DateTime"));
				moodRating = "Mood Rating: ";
				moodRating += c.getString(c.getColumnIndex("Answer"));
				latitude = Double.parseDouble(c.getString(c.getColumnIndex("Latitude")));
				longitude = Double.parseDouble(c.getString(c.getColumnIndex("Longitude")));
				Location = new LatLng(latitude, longitude);
				//add map markers depending on rating
				
				if(moodRating.equals("Mood Rating: 1")) {
					map.addMarker(new MarkerOptions()
					.position(Location)
					.title(dateTime)
					.snippet("Mood at time: Hyper")
					.icon(BitmapDescriptorFactory
							.fromResource(R.drawable.m02)));
				}
				else if (moodRating.equals("Mood Rating: 2")) {
					map.addMarker(new MarkerOptions()
					.position(Location)
					.title(dateTime)
					.snippet("Mood at time: Sad")
					.icon(BitmapDescriptorFactory
							.fromResource(R.drawable.m01)));
				}
				else if (moodRating.equals("Mood Rating: 3")) {
					map.addMarker(new MarkerOptions()
					.position(Location)
					.title(dateTime)
					.snippet("Mood at time: Calm")
					.icon(BitmapDescriptorFactory
							.fromResource(R.drawable.m05)));
				}
				else if (moodRating.equals("Mood Rating: 4")) {
					map.addMarker(new MarkerOptions()
					.position(Location)
					.title(dateTime)
					.snippet("Mood at time: Happy")
					.icon(BitmapDescriptorFactory
							.fromResource(R.drawable.m04)));
				}
				else if (moodRating.equals("Mood Rating: 5")) {
					map.addMarker(new MarkerOptions()
					.position(Location)
					.title(dateTime)
					.snippet("Mood at time: Mad")
					.icon(BitmapDescriptorFactory
							.fromResource(R.drawable.m03)));
				}
		            
			} while (c.moveToNext());
			//get current GPS Location to localize map
		    gps = new GPSTracker(MoodMap.this);
			if(gps.canGetLocation()) {
				latitude = gps.getLatitude();
				longitude = gps.getLongitude();
				Location = new LatLng(latitude, longitude);
				currentLocation = Location;
			}
			map.moveCamera(CameraUpdateFactory.newLatLngZoom(Location, 15));
			map.addMarker(new MarkerOptions()
			.position(Location)
			.title("You are here.")
			.icon(BitmapDescriptorFactory
					.fromResource(R.drawable.youarehere)));
		}
		DB.close();
		//tell user how moodmap works
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(MoodMap.this);
		alertDialog.setTitle("Mood Map");
		alertDialog.setMessage("The Mood Map displays the results of your recorded events in the experiment, based on the GPS location at the time. " +
				"Note that the colours represent the mood you gave at the time of the event. See the options in the menu to filter results.");		
		alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		alertDialog.show();
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.moodmap, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.allmoods:
			map.clear();
			//open or create database and table
			DB = MoodMap.this.openOrCreateDatabase(experiment, MODE_PRIVATE, null);
			c = null;
			c = DB.rawQuery("SELECT * FROM " + username + "_results WHERE Question = 'Please select the word that best describes your current mood.'", null);
			if(c.moveToFirst()) {
				do {
					dateTime = c.getString(c.getColumnIndex("DateTime"));
					moodRating = "Mood Rating: ";
					moodRating += c.getString(c.getColumnIndex("Answer"));
					latitude = Double.parseDouble(c.getString(c.getColumnIndex("Latitude")));
					longitude = Double.parseDouble(c.getString(c.getColumnIndex("Longitude")));
					Location = new LatLng(latitude, longitude);
					//add map markers depending on rating
					
					if(moodRating.equals("Mood Rating: 1")) {
						map.addMarker(new MarkerOptions()
						.position(Location)
						.title(dateTime)
						.snippet("Mood at time: Hyper")
						.icon(BitmapDescriptorFactory
								.fromResource(R.drawable.m02)));
					}
					else if (moodRating.equals("Mood Rating: 2")) {
						map.addMarker(new MarkerOptions()
						.position(Location)
						.title(dateTime)
						.snippet("Mood at time: Sad")
						.icon(BitmapDescriptorFactory
								.fromResource(R.drawable.m01)));
					}
					else if (moodRating.equals("Mood Rating: 3")) {
						map.addMarker(new MarkerOptions()
						.position(Location)
						.title(dateTime)
						.snippet("Mood at time: Calm")
						.icon(BitmapDescriptorFactory
								.fromResource(R.drawable.m05)));
					}
					else if (moodRating.equals("Mood Rating: 4")) {
						map.addMarker(new MarkerOptions()
						.position(Location)
						.title(dateTime)
						.snippet("Mood at time: Happy")
						.icon(BitmapDescriptorFactory
								.fromResource(R.drawable.m04)));
					}
					else if (moodRating.equals("Mood Rating: 5")) {
						map.addMarker(new MarkerOptions()
						.position(Location)
						.title(dateTime)
						.snippet("Mood at time: Mad")
						.icon(BitmapDescriptorFactory
								.fromResource(R.drawable.m03)));
					}	            
				} while (c.moveToNext());

				map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
				map.addMarker(new MarkerOptions()
				.position(currentLocation)
				.title("You are here.")
				.icon(BitmapDescriptorFactory
						.fromResource(R.drawable.youarehere)));
			}
			DB.close();
			return true;
		case R.id.hyper:
			map.clear();
			//open or create database and table
			DB = MoodMap.this.openOrCreateDatabase(experiment, MODE_PRIVATE, null);		
			c = null;
			c = DB.rawQuery("SELECT * FROM " + username + "_results WHERE Question = 'Please select the word that best describes your current mood.'", null);
			if(c.moveToFirst()) {
				do {
					dateTime = c.getString(c.getColumnIndex("DateTime"));
					moodRating = "Mood Rating: ";
					moodRating += c.getString(c.getColumnIndex("Answer"));
					latitude = Double.parseDouble(c.getString(c.getColumnIndex("Latitude")));
					longitude = Double.parseDouble(c.getString(c.getColumnIndex("Longitude")));
					Location = new LatLng(latitude, longitude);
					//add map markers depending on rating
					
					if(moodRating.equals("Mood Rating: 1")) {
						map.addMarker(new MarkerOptions()
						.position(Location)
						.title(dateTime)
						.snippet("Mood at time: Hyper")
						.icon(BitmapDescriptorFactory
								.fromResource(R.drawable.m02)));
					}		            
				} while (c.moveToNext());

				map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
				map.addMarker(new MarkerOptions()
				.position(currentLocation)
				.title("You are here.")
				.icon(BitmapDescriptorFactory
						.fromResource(R.drawable.youarehere)));
			}
			DB.close();
			return true;
		case R.id.sad:
			map.clear();
			//open or create database and table
			DB = MoodMap.this.openOrCreateDatabase(experiment, MODE_PRIVATE, null);	
			c = null;
			c = DB.rawQuery("SELECT * FROM " + username + "_results WHERE Question = 'Please select the word that best describes your current mood.'", null);
			if(c.moveToFirst()) {
				do {
					dateTime = c.getString(c.getColumnIndex("DateTime"));
					moodRating = "Mood Rating: ";
					moodRating += c.getString(c.getColumnIndex("Answer"));
					latitude = Double.parseDouble(c.getString(c.getColumnIndex("Latitude")));
					longitude = Double.parseDouble(c.getString(c.getColumnIndex("Longitude")));
					Location = new LatLng(latitude, longitude);
					//add map markers depending on rating
					
					if(moodRating.equals("Mood Rating: 2")) {
						map.addMarker(new MarkerOptions()
						.position(Location)
						.title(dateTime)
						.snippet("Mood at time: Sad")
						.icon(BitmapDescriptorFactory
								.fromResource(R.drawable.m01)));
					}		            
				} while (c.moveToNext());

				map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
				map.addMarker(new MarkerOptions()
				.position(currentLocation)
				.title("You are here.")
				.icon(BitmapDescriptorFactory
						.fromResource(R.drawable.youarehere)));
			}
			DB.close();
			return true;
		case R.id.calm:
			map.clear();
			DB = MoodMap.this.openOrCreateDatabase(experiment, MODE_PRIVATE, null);	
			c = null;
			c = DB.rawQuery("SELECT * FROM " + username + "_results WHERE Question = 'Please select the word that best describes your current mood.'", null);
			if(c.moveToFirst()) {
				do {
					dateTime = c.getString(c.getColumnIndex("DateTime"));
					moodRating = "Mood Rating: ";
					moodRating += c.getString(c.getColumnIndex("Answer"));
					latitude = Double.parseDouble(c.getString(c.getColumnIndex("Latitude")));
					longitude = Double.parseDouble(c.getString(c.getColumnIndex("Longitude")));
					Location = new LatLng(latitude, longitude);
					//add map markers depending on rating
					
					if(moodRating.equals("Mood Rating: 3")) {
						map.addMarker(new MarkerOptions()
						.position(Location)
						.title(dateTime)
						.snippet("Mood at time: Calm")
						.icon(BitmapDescriptorFactory
								.fromResource(R.drawable.m05)));
					}		            
				} while (c.moveToNext());

				map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
				map.addMarker(new MarkerOptions()
				.position(currentLocation)
				.title("You are here.")
				.icon(BitmapDescriptorFactory
						.fromResource(R.drawable.youarehere)));
			}
			DB.close();
			return true;
		case R.id.happy:
			map.clear();
			DB = MoodMap.this.openOrCreateDatabase(experiment, MODE_PRIVATE, null);		
			c = null;
			c = DB.rawQuery("SELECT * FROM " + username + "_results WHERE Question = 'Please select the word that best describes your current mood.'", null);
			if(c.moveToFirst()) {
				do {
					dateTime = c.getString(c.getColumnIndex("DateTime"));
					moodRating = "Mood Rating: ";
					moodRating += c.getString(c.getColumnIndex("Answer"));
					latitude = Double.parseDouble(c.getString(c.getColumnIndex("Latitude")));
					longitude = Double.parseDouble(c.getString(c.getColumnIndex("Longitude")));
					Location = new LatLng(latitude, longitude);
					//add map markers depending on rating
					
					if(moodRating.equals("Mood Rating: 4")) {
						map.addMarker(new MarkerOptions()
						.position(Location)
						.title(dateTime)
						.snippet("Mood at time: Happy")
						.icon(BitmapDescriptorFactory
								.fromResource(R.drawable.m04)));
					}		            
				} while (c.moveToNext());

				map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
				map.addMarker(new MarkerOptions()
				.position(currentLocation)
				.title("You are here.")
				.icon(BitmapDescriptorFactory
						.fromResource(R.drawable.youarehere)));
			}
			DB.close();
			return true;
		case R.id.mad:
			map.clear();
			DB = MoodMap.this.openOrCreateDatabase(experiment, MODE_PRIVATE, null);		
			c = null;
			c = DB.rawQuery("SELECT * FROM " + username + "_results WHERE Question = 'Please select the word that best describes your current mood.'", null);
			if(c.moveToFirst()) {
				do {
					dateTime = c.getString(c.getColumnIndex("DateTime"));
					moodRating = "Mood Rating: ";
					moodRating += c.getString(c.getColumnIndex("Answer"));
					latitude = Double.parseDouble(c.getString(c.getColumnIndex("Latitude")));
					longitude = Double.parseDouble(c.getString(c.getColumnIndex("Longitude")));
					Location = new LatLng(latitude, longitude);
					//add map markers depending on rating
					
					if(moodRating.equals("Mood Rating: 5")) {
						map.addMarker(new MarkerOptions()
						.position(Location)
						.title(dateTime)
						.snippet("Mood at time: Mad")
						.icon(BitmapDescriptorFactory
								.fromResource(R.drawable.m03)));
					}		            
				} while (c.moveToNext());

				map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
				map.addMarker(new MarkerOptions()
				.position(currentLocation)
				.title("You are here.")
				.icon(BitmapDescriptorFactory
						.fromResource(R.drawable.youarehere)));
			}
			DB.close();
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
	                 
	                 c = null;
	                 while (rs.next()) {	 
	                	 DB.execSQL("INSERT INTO " + username + "_results (DateTime, Question, Answer, Latitude, Longitude) " +
    							 "Values ('"+rs.getString(1)+"', '"+rs.getString(2)+"', '"+rs.getString(3)+"', '"+rs.getString(4)+"', '"+rs.getString(5)+"');");
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

