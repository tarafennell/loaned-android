package com.mattallen.loaned.notifier;

import java.util.Date;

import com.mattallen.loaned.Item;
import com.mattallen.loaned.Loan;
import com.mattallen.loaned.Person;
import com.mattallen.loaned.R;
import com.mattallen.loaned.history.LoanHistoryActivity;
import com.mattallen.loaned.main.MainActivity;
import com.mattallen.loaned.settings.SettingsFragment;
import com.mattallen.loaned.storage.DatabaseManager;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class WakeupReceiver extends BroadcastReceiver {

	private static final String					TAG = WakeupReceiver.class.getSimpleName();
	private static final int					NOTIFICATION_ID = 100;

	/**
	 * Use this store the loan ID in the extras of the intent used to
	 * launch this broadcast receiver 
	 */
	public static final String					LOAN_ID = "loanID";
	private DatabaseManager						mDB;

	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle b = intent.getExtras();
		mDB = new DatabaseManager(context);
		Loan loan;
		try{
			int loanID = b.getInt(LOAN_ID);
			loan = mDB.getLoanByID(loanID);
			if(loan.isNotifying() && loan.getReturnDate()==null){
				Person person = mDB.getPersonByID(loan.getPersonID());
				Item item = mDB.getItemByID(loan.getItemID());
				doNotification(context, person, item, loan);
			}
		} catch (Exception e){
			Log.d(TAG,"Error in gathering data for notification");
			e.printStackTrace();
		}
	}

	@SuppressLint("NewApi")
	private void doNotification(Context c, Person person, Item item, Loan loan){
		// Get picture for person. If none available, use app icon
		// Calculate days on loan
		// Set title to item name
		// Set message to "{person name} has had this for {number of days} days"
		// Set Intent to open app to main screen
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(c);
		mBuilder.setSmallIcon(R.drawable.ic_launcher_grey);
		mBuilder.setContentTitle(item.getName());
		long timeDifference = new Date().getTime()-loan.getStartDate().getTime();
		int days = (int) (timeDifference / (1000*60*60*24));
		mBuilder.setContentText(person.getName()+" has had this item for "+ Integer.toString(days) +" days");
		NotificationManager mNotificationManager = (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
		Intent resultIntent = new Intent(c, LoanHistoryActivity.class);
		Bundle b = new Bundle();
		// Set which fragment to show based on the user's preference of preferred view
		if(SettingsFragment.isShowingItemList(c)){
			b.putBoolean(LoanHistoryActivity.IS_FOR_ITEM, true);
			b.putInt(LoanHistoryActivity.ITEM_ID, item.getItemID());
		} else {
			b.putBoolean(LoanHistoryActivity.IS_FOR_ITEM, false);
			b.putInt(LoanHistoryActivity.PERSON_ID, person.getPersonID());
		}
		resultIntent.putExtras(b);
		// The stack builder object will contain an artificial back stack for the started Activity.
		// This ensures that navigating backward from the Activity leads out of
		// your application to the Home screen.
		if(Build.VERSION.SDK_INT>=16){ // If JellyBean or newer
			TaskStackBuilder stackBuilder = TaskStackBuilder.create(c);
			// Adds the back stack for the Intent (but not the Intent itself)
			stackBuilder.addParentStack(MainActivity.class);
			stackBuilder.addNextIntent(resultIntent);
			PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
					PendingIntent.FLAG_UPDATE_CURRENT);
			mBuilder.setContentIntent(resultPendingIntent);
		}
		// NOTIFICATION_ID allows you to update the notification later on.
		mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
	}
}