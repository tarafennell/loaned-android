package com.mattallen.loaned.addloan;

import java.util.Date;

import com.mattallen.loaned.Item;
import com.mattallen.loaned.Loan;
import com.mattallen.loaned.Person;
import com.mattallen.loaned.R;
import com.mattallen.loaned.notifier.SetAlarm;
import com.mattallen.loaned.storage.DatabaseManager;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

public class AddLoanActivity extends Activity implements AddLoanCallback {

	private static final String				TAG = AddLoanActivity.class.getSimpleName();

	private ActionBar						mActionBar;
	private int								mFrameLayout = R.id.addloan_frame;
	private Fragment						mChoosePerson, mChooseItem;
	private Person							mSelectedPerson;
	private Item							mSelectedItem;
	private DatabaseManager					mDB;
	private ProgressBar						mProgress;
	private FrameLayout						mFrame;
	private Context							mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_addloan);
		// If the available screen size is that of an average tablet (as defined
		// in the Android documentation) then allow the screen to rotate
		if(getResources().getBoolean(R.bool.lock_orientation)){
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		mActionBar = getActionBar();
		mActionBar.setDisplayHomeAsUpEnabled(true);
		mActionBar.setHomeButtonEnabled(true);

		// Instatiate Fragments
		mChooseItem = new ChooseItemFragment();
		mChoosePerson = new ChoosePersonFragment();

		// Show first Fragment
		FragmentTransaction mFragMan = getFragmentManager().beginTransaction();
		mFragMan.add(mFrameLayout, mChoosePerson);
		mFragMan.commit();
		mDB = new DatabaseManager(this);
		mProgress = (ProgressBar)findViewById(R.id.addloan_progress);
		mFrame = (FrameLayout)findViewById(R.id.addloan_frame);
		mContext = this;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data){
		super.onActivityResult(requestCode, resultCode, data);
		if(data!=null && requestCode==ChoosePersonFragment.PICK_CONTACT_REQUEST
				&&resultCode==RESULT_OK){
			((ChoosePersonFragment)mChoosePerson).addContactCallback(data);
		}
	}

	@Override
	public void onNextButtonClicked(Person person) {
		Log.d(TAG, "Next button clicked");
		mSelectedPerson = person;
		FragmentTransaction mFragMan = getFragmentManager().beginTransaction();
		mFragMan.replace(mFrameLayout, mChooseItem);
		mFragMan.addToBackStack(null);
		mFragMan.commit();
	}

	@Override
	public void onSaveLoan(Item item) {
		Log.d(TAG, "Save loan button clicked");
		mSelectedItem = item;
		if(mSelectedItem.isCurrentlyOnLoan()){
			AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
			dialog.setTitle(R.string.loanhistory_itemdetails_onloan).setMessage(R.string.dialog_addloan_alreadyonloan);
			dialog.setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			}).show();
		} else {
			new SaveLoan().execute();
		}
	}

	private class SaveLoan extends AsyncTask<Void, Void, Exception>{
		private Loan loan;
		@Override
		protected void onPreExecute(){
			mProgress.setVisibility(ProgressBar.VISIBLE);
			mFrame.setVisibility(FrameLayout.INVISIBLE);
		}
		@Override
		protected Exception doInBackground(Void... params) {
			try{
				mDB.addLoan(mSelectedPerson.getPersonID(), mSelectedItem.getItemID(), new Date(), true);
				mSelectedPerson.setItemsLoaned(mSelectedPerson.getItemsLoaned()+1);
				mSelectedPerson.setItemsOnLoan(mSelectedPerson.getItemsOnLoan()+1);
				mDB.updatePerson(mSelectedPerson);
				mSelectedItem.setCurrentlyOnLoan(true);
				mSelectedItem.setTimesLoaned(mSelectedItem.getTimesLoaned()+1);
				mDB.updateItem(mSelectedItem);
				loan = mDB.getMostRecentLoanByBothIDs(mSelectedPerson.getPersonID(), mSelectedItem.getItemID());
				return null;
			} catch (Exception e){
				return e;
			}
		}
		@Override
		protected void onPostExecute(Exception e){
			if(e!=null){
				mProgress.setVisibility(ProgressBar.INVISIBLE);
				mFrame.setVisibility(FrameLayout.VISIBLE);
				Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
			} else {
				SetAlarm.setAlarm(mContext, loan);
				finish();
			}
		}
	}
}