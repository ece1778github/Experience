package com.example.ece1778project;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class TimeAlarm extends BroadcastReceiver {
	
	NotificationManager nm;
	String username, experiment, surveyName;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle extras = intent.getExtras();
		username = extras.getString("name");
		experiment = extras.getString("experiment");
		surveyName = extras.getString("surveyName");
		
		nm = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
		Intent pIntent = new Intent(context, QuestionActivity.class);
		pIntent.putExtra("name", username);
		pIntent.putExtra("experiment", experiment);
		pIntent.putExtra("surveyName", surveyName);
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0, pIntent, 0);
		
		Notification noti = new Notification.Builder(context)
		.setTicker("eXperience Alert!")
		.setSmallIcon(R.drawable.ticker)
		.setContentTitle("New Survey Event")
		.setContentText("Click here to complete it now.")
		.setContentIntent(contentIntent)
		.setAutoCancel(true).build();
		noti.flags |= Notification.FLAG_NO_CLEAR;
		nm.notify(1, noti);
	}

}
