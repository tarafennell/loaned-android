package com.mattallen.loaned.addloan;

import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mattallen.loaned.Item;
import com.mattallen.loaned.R;
import com.mattallen.loaned.storage.DatabaseManager;

import java.util.ArrayList;

public class ChooseItemFragment extends Fragment implements OnItemClickListener {

	private static final String				TAG = ChooseItemFragment.class.getSimpleName();

	private TextView						mEmptyState;
	private ProgressBar						mProgress;
	private ListView						mList;
	private AddLoanCallback					mCallback;
	private DatabaseManager					mDB;
	private ArrayList<Item>					mItems;
	private ChooseItemAdapter				mAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true); // Tell the activity that we have ActionBar items
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View v = inflater.inflate(R.layout.fragment_chooseitem, container, false);
		mEmptyState = (TextView)v.findViewById(R.id.chooseitem_emptystate);
		mProgress = (ProgressBar)v.findViewById(R.id.chooseitem_progress);
		mList = (ListView)v.findViewById(R.id.chooseitem_list);
		mList.setOnItemClickListener(this);
		mDB = new DatabaseManager(getActivity());
		Log.d(TAG, "Fragment created");
		return v;
	}

	@Override
	public void onStart(){
		super.onStart();
		new GetItems().execute();
	}

	@Override
	public void onAttach(Activity activity){
		super.onAttach(activity);
		try{
			mCallback = (AddLoanCallback)activity;
		}catch(ClassCastException e){
			throw new ClassCastException(activity.toString() + "\n" +
					mCallback.getClass().getName() + " not implemented in host activity");
		}
	}

	/*
	 * Here we add the extra menu items needed into the ActionBar. Even with
	 * implementing this method, we still need to tell the Activity that we
	 * have menu items to add
	 */
	@Override
	public void onCreateOptionsMenu(Menu menu,MenuInflater inf){
		super.onCreateOptionsMenu(menu, inf);
        // Check if the menu already has items in it.
        // Otherwise we could cause a duplicate menu item issue.
        if(menu.size()==0)inf.inflate(R.menu.chooseitem, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		case R.id.action_additem:
			showAddItemDialog();
			return true;

		default:
			return true;
		}
	}
	
	private void showAddItemDialog(){
        mCallback.onCreateNewItem();
	}
	
	private class GetItems extends AsyncTask<Void, Void, Exception>{
		@Override
		protected void onPreExecute(){
			mProgress.setVisibility(ProgressBar.VISIBLE);
			mList.setVisibility(GridView.INVISIBLE);
			mEmptyState.setVisibility(TextView.INVISIBLE);
			mItems = null;
		}
		@Override
		protected Exception doInBackground(Void... params) {
			try{
				Log.d(TAG, "Getting all items from the database");
				mItems = mDB.getAllItems();
			} catch (Exception e){
				return e;
			}
			return null;
		}
		@Override
		protected void onPostExecute(Exception e){
			mProgress.setVisibility(ProgressBar.INVISIBLE);
			if(e!=null){
				Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
			} else {
				if(mItems!=null && mItems.size()>=1){
					mAdapter = new ChooseItemAdapter(getActivity(), R.layout.fragment_chooseitem_item, mItems);
					mList.setAdapter(mAdapter);
					mList.setVisibility(ListView.VISIBLE);
				} else {
					mEmptyState.setVisibility(TextView.VISIBLE);
				}
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		mCallback.onSaveLoan((Item)arg1.getTag());
	}
}