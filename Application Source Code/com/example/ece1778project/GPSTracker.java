package com.example.ece1778project;


import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

public class GPSTracker extends Service implements LocationListener {
	private final Context mContext;
	boolean GPSEnabled = false;
	boolean NetworkEnabled = false;
	boolean canGetLocation = false;
	Location location;
	double latitude, longitude;
	
	private static final long MIN_DISTANCE_FOR_UPDATES = 10;
	private static final long MIN_TIME_FOR_UPDATES = 1000*60;
			
	protected LocationManager locationManager;
	
	public GPSTracker(Context context) {
		this.mContext = context;
		getLocation();
	}
	
	public Location getLocation() {
		try {
			locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
			GPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
			NetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
			
			if (!GPSEnabled && !NetworkEnabled) {
				//no network
			}
			else {
				this.canGetLocation = true;
				if (NetworkEnabled) {
					locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
							MIN_TIME_FOR_UPDATES, MIN_DISTANCE_FOR_UPDATES, this);
					Log.d("Network", "Network");
					if(locationManager != null) {
						location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
						if(location != null) {
							latitude = location.getLatitude();
							longitude = location.getLongitude();
						}
					}
					
				}
				if (GPSEnabled) {
					if (location == null) {
						locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
								MIN_TIME_FOR_UPDATES, MIN_DISTANCE_FOR_UPDATES, this);
						Log.d("GPS Enabled", "GPS Enabled");
						if (locationManager != null) {
							location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
							if (location != null) {
								latitude = location.getLatitude();
								longitude = location.getLongitude();
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return location;
	}
	@Override
	public void onLocationChanged(Location location) {
		
	}
	@Override
	public void onProviderDisabled(String provider) {
		
	}
	@Override
	public void onProviderEnabled(String provider) {
		
	}
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		
	}
	@Override
	public IBinder onBind (Intent arg0) {
		return null;
	}
	
	//functions
	public double getLatitude() {
		if(location != null) {
			latitude = location.getLatitude();
		}
		return latitude;
	}
	
	public double getLongitude() {
		if(location != null) {
			longitude = location.getLongitude();
		}
		return longitude;
	}
	
	public void stopUsingGPS() {
		if (locationManager != null) {
			locationManager.removeUpdates(GPSTracker.this);
		}
	}
	
	public boolean canGetLocation() {
		return this.canGetLocation;
	}
	
	public void showSettingsAlert() {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
		alertDialog.setTitle("GPS Settings");
		alertDialog.setMessage("GPS is not enabled. Do you want to enable it in settings?");
		
		alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				mContext.startActivity(intent);
				dialog.cancel();
			}
		});
		alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		alertDialog.show();
	}
	
	
}