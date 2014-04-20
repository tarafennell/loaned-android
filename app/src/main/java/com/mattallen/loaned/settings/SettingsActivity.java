package com.mattallen.loaned.settings;

import com.mattallen.loaned.R;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.PreferenceFragment;

public class SettingsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// If the available screen size is that of an average tablet (as defined
		// in the Android documentation) then allow the screen to rotate
		if(getResources().getBoolean(R.bool.lock_orientation)){
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		getActionBar().setDisplayHomeAsUpEnabled(true);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);

		PreferenceFragment mPrefsFrag = new SettingsFragment();

		FragmentTransaction mFragMan = getFragmentManager().beginTransaction();
		mFragMan.add(R.id.settings_frame, mPrefsFrag);
		mFragMan.commit();
	}
}