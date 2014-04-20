package com.mattallen.loaned.history;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Locale;

import com.mattallen.loaned.Item;
import com.mattallen.loaned.Loan;
import com.mattallen.loaned.Person;
import com.mattallen.loaned.R;
import com.mattallen.loaned.storage.DatabaseManager;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class LoanHistoryActivity extends FragmentActivity implements ActionBar.TabListener {

	private static final String				TAG = LoanHistoryActivity.class.getSimpleName();

	public static final String				IS_FOR_ITEM = "forItem";
	public static final String				ITEM_ID = "itemID";
	public static final String				PERSON_ID = "personID";

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	private ViewPager						mViewPager;

	private Person							mPerson;
	private Item							mItem;
	private boolean							isForItem = false;

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	private SectionsPagerAdapter 			mSectionsPagerAdapter;
	private DatabaseManager					mDB;
	private Fragment[] 						mFragments;
	private Context							mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// If the available screen size is that of an average tablet (as defined
		// in the Android documentation) then allow the screen to rotate
		if(getResources().getBoolean(R.bool.lock_orientation)){
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_loanhistory);
		mFragments = new Fragment[2];
		Bundle extras = getIntent().getExtras();
		mDB = new DatabaseManager(this);
		if(extras.getBoolean(IS_FOR_ITEM)){
			isForItem = true;
			mFragments[0] = new ItemDetailsFragment();
			mFragments[1] = new ItemHistoryFragment();
			Log.d(TAG, "Looking up item id: "+extras.getInt(ITEM_ID));
			mItem = mDB.getItemByID(extras.getInt(ITEM_ID));
		} else {
			mFragments[0] = new PersonDetailsFragment();
			mFragments[1] = new PersonHistoryFragment();
			Log.d(TAG, "Looking up person id: "+extras.getInt(PERSON_ID));
			mPerson = mDB.getPersonByID(extras.getInt(PERSON_ID));
		}
		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.loanhistory_viewpager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		// Set the ViewPager animation
		mViewPager.setPageTransformer(true, new DepthPageTransformer());
		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager
		.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				actionBar.setSelectedNavigationItem(position);
			}
		});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}
		mContext = this;
	}

	public Item getItem(){
		return mItem;
	}

	public Person getPerson(){
		return mPerson;
	}

	private void updateFragments(){
		((LoanHistoryFragment)mFragments[0]).updateInformation();
		((LoanHistoryFragment)mFragments[1]).updateInformation();
	}

	public void updateItem(Item item){
		mItem = item;
		mDB.updateItem(mItem);
		updateFragments();
	}

	public void updatePerson(Person person){
		mPerson = person;
		mDB.updatePerson(mPerson);
		updateFragments();
	}
	
	public void deletePerson(Person person) throws ParseException{
		mDB.removePerson(person);
		finish();
	}
	
	public void deleteItem(Item item) throws ParseException{
		mDB.removeItem(item);
		finish();
	}
	
	public void deleteLoan(Loan loan){
		mDB.removeLoan(loan, true);
		updateFragments();
	}
	
	public boolean isItemOnLoan(int itemID) throws ParseException{
		return mDB.isAlreadyOnLoan(itemID);
	}

	public Person getPersonByID(int personID){
		return mDB.getPersonByID(personID);
	}
	
	public Item getItemByID(int itemID){
		return mDB.getItemByID(itemID);
	}
	
	public ArrayList<Loan> getLoans(Item item) throws ParseException{
		return mDB.getAllLoansByItemID(item.getItemID());
	}

	public ArrayList<Loan> getLoans(Person person) throws ParseException{
		return mDB.getAllLoansByPersonID(person.getPersonID());
	}

	public void updateLoan(Loan loan){
		mDB.updateLoan(loan);
		updateFragments();
	}

	public void itemReturned(Loan loan){
		new ItemReturned().execute(loan);
	}

	public ArrayList<Item> getAllItems(){
		return mDB.getAllItems();
	}

	public ArrayList<Person> getAllPeople(){
		return mDB.getAllPeople();
	}

	private class ItemReturned extends AsyncTask<Loan, Void, Exception>{
		@Override
		protected Exception doInBackground(Loan... params) {
			try{
				mDB.itemReturned(params[0]);
				if(isForItem){
					mItem = mDB.getItemByID(mItem.getItemID());
				} else {
					mPerson = mDB.getPersonByID(mPerson.getPersonID());
				}
			} catch (Exception e){
				return e;
			}
			return null;
		}
		protected void onPostExecute(Exception e){
			if(e!=null){
				Toast.makeText(mContext, R.string.error_cantcancelloan, Toast.LENGTH_SHORT).show();
				e.printStackTrace();
			}
			updateFragments();
		}
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	private class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			return mFragments[position];
		}

		@Override
		public int getCount() {
			return mFragments.length;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return (isForItem)? mItem.getName().toUpperCase(l) : mPerson.getName().toUpperCase(l);
			case 1:
				return getString(R.string.loanhistory).toUpperCase(l);
			}
			return null;
		}
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		// When the given tab is selected, switch to the corresponding page in the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
	}
	
	public class DepthPageTransformer implements ViewPager.PageTransformer {
		private static final float MIN_SCALE = 0.5f;

		@Override
		public void transformPage(View view, float position) {
			int pageWidth = view.getWidth();

			if (position < -1) { // [-Infinity,-1)
				// This page is way off-screen to the left.
				view.setAlpha(0);

			} else if (position <= 0) { // [-1,0]
				// Use the default slide transition when moving to the left page
				view.setAlpha(1);
				view.setTranslationX(0);
				view.setScaleX(1);
				view.setScaleY(1);

			} else if (position <= 1) { // (0,1]
				// Fade the page out.
				view.setAlpha(1 - position);

				// Counteract the default slide transition
				view.setTranslationX(pageWidth * -position);

				// Scale the page down (between MIN_SCALE and 1)
				float scaleFactor = MIN_SCALE
						+ (1 - MIN_SCALE) * (1 - Math.abs(position));
				view.setScaleX(scaleFactor);
				view.setScaleY(scaleFactor);

			} else { // (1,+Infinity]
				// This page is way off-screen to the right.
				view.setAlpha(0);
			}
		}
	}
}