package com.mattallen.loaned.notifier;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.mattallen.loaned.Loan;
import com.mattallen.loaned.settings.SettingsFragment;

public class SetAlarm {

	public static void setAlarm(Context c, Loan loan){
		if (loan.isNotifying() && SettingsFragment.getNotificationPreference(c)!=SettingsFragment.NOTIFICATIONS_OFF)
		{
			AlarmManager alMan = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
			Intent intent = new Intent(c, WakeupReceiver.class);
			Bundle b = new Bundle();
			b.putInt(WakeupReceiver.LOAN_ID, loan.getLoanID());
			intent.putExtras(b);
			// Then give it a unique ID
			final int id = loan.getLoanID();
			PendingIntent appIntent = PendingIntent.getBroadcast(c, id, intent, PendingIntent.FLAG_ONE_SHOT);
			long timeToWait = 1210000000; // Two Weeks
			if(SettingsFragment.getNotificationPreference(c)==SettingsFragment.NOTIFICATIONS_3WEEKS){
				timeToWait = (long) (timeToWait*1.5);
			} else if (SettingsFragment.getNotificationPreference(c)==SettingsFragment.NOTIFICATIONS_4WEEKS){
				timeToWait = (long) (timeToWait*2);
			}
			long triggerTime = loan.getStartDate().getTime()+timeToWait;
			alMan.set(AlarmManager.RTC_WAKEUP, triggerTime, appIntent);
			// Do everything in the constructor, then everything is done
			// on instatiation and the garbage collector will kill it after.
		}
	}
}