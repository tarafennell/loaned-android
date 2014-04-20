package com.mattallen.loaned.settings;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.mattallen.loaned.R;
import com.mattallen.loaned.SendMail;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceClickListener;

public class SettingsFragment extends PreferenceFragment {

	private static final String					PREF_KEY_DATEFORMAT = "dateformat";
	private static final String					PREF_KEY_NOTIFICATIONS = "notifications";
	private static final String					PREF_KEY_DEFAULTLIST = "defaultList";
	
	public static final int						NOTIFICATIONS_OFF = 4;
	public static final int						NOTIFICATIONS_2WEEKS = 1;
	public static final int						NOTIFICATIONS_3WEEKS = 2;
	public static final int						NOTIFICATIONS_4WEEKS = 3;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Load the preferences from an XML resource
		addPreferencesFromResource(R.xml.pref_general);
		// Response for when the feedback option is used
		findPreference("feedback").setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				SendMail.sendFeedback(getActivity());
				return true;
			}
		});
	}

	/**
	 * @param c The Context to operate in
	 * @param date The date as returned from the database
	 * @return The date, formatted as the user preference specifies
	 */
	public static String getFormattedDate(Context c, Date date){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		// Add 1 to the month because it returns one less than it should
		String dateString = cal.get(Calendar.DATE)+"/"+(cal.get(Calendar.MONTH)+1)+"/"+cal.get(Calendar.YEAR);
		String currencyPref = prefs.getString(PREF_KEY_DATEFORMAT, dateString);
		if(currencyPref.equals("2")){
			dateString = (cal.get(Calendar.MONTH)+1)+"/"+cal.get(Calendar.DATE)+"/"+cal.get(Calendar.YEAR);
		}
		else if(currencyPref.equals("3")){
			SimpleDateFormat format = new SimpleDateFormat("EEE d MMM yyyy",Locale.getDefault());
			dateString = format.format(date);
		}
		return dateString;
	}

	public static boolean isShowingItemList(Context c){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
		return prefs.getBoolean(PREF_KEY_DEFAULTLIST, false);
	}
	
	public static int getNotificationPreference(Context c){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
		return Integer.parseInt(prefs.getString(PREF_KEY_NOTIFICATIONS, "1"));
	}
}