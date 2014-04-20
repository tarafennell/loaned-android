package com.mattallen.loaned.main;

import java.util.ArrayList;

import com.mattallen.loaned.Person;
import com.mattallen.loaned.R;
import com.mattallen.loaned.addloan.AddLoanActivity;
import com.mattallen.loaned.history.LoanHistoryActivity;
import com.mattallen.loaned.storage.DatabaseManager;

import android.app.Fragment;
import android.content.Intent;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class LoansByPersonFragment extends Fragment implements OnItemClickListener {

	private static final String					TAG = LoansByPersonFragment.class.getSimpleName();

	private ProgressBar							mProgress;
	private TextView							mEmptyState;
	private GridView							mGridView;
	private ArrayList<Person>					mPeople;
	private DatabaseManager						mDB;
	private LoansByPersonAdapter				mAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true); // Tell the activity that we have ActionBar items
	}

	@Override
	public void onCreateOptionsMenu(Menu menu,MenuInflater inf){
		super.onCreateOptionsMenu(menu, inf);
		inf.inflate(R.menu.loanslist, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		case R.id.action_addloan:
			getActivity().startActivity(new Intent(getActivity(), AddLoanActivity.class));
			return true;
		default:
			break;
		}
		return false;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View v = inflater.inflate(R.layout.fragment_loanslist, container, false);
		mProgress = (ProgressBar)v.findViewById(R.id.loanslist_progress);
		mEmptyState = (TextView)v.findViewById(R.id.loanslist_empty);
		mGridView = (GridView)v.findViewById(R.id.loanslist_grid);
		mGridView.setOnItemClickListener(this);
		mDB = new DatabaseManager(getActivity());
		return v;
	}

	@Override
	public void onStart(){
		super.onStart();
		new GetPeople().execute();
	}

	private class GetPeople extends AsyncTask<Void, Void, Exception>{
		@Override
		protected void onPreExecute(){
			mProgress.setVisibility(ProgressBar.VISIBLE);
			mGridView.setVisibility(GridView.INVISIBLE);
			mEmptyState.setVisibility(TextView.INVISIBLE);
			mPeople = null;
		}
		@Override
		protected Exception doInBackground(Void... params) {
			try{
				Log.d(TAG,"Getting people from the DB");
				mPeople = mDB.getAllPeople();
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
				if(mPeople==null || mPeople.size()<1){
					mEmptyState.setVisibility(TextView.VISIBLE);
				} else {
					try{
						mAdapter = new LoansByPersonAdapter(getActivity(), mPeople);
						mGridView.setAdapter(mAdapter);
						mGridView.setVisibility(GridView.VISIBLE);
					} catch (Exception e1){
						e1.printStackTrace();
					}
				}
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Bundle b = new Bundle();
		b.putBoolean(LoanHistoryActivity.IS_FOR_ITEM, false);
		b.putInt(LoanHistoryActivity.PERSON_ID, ((Person)arg1.getTag()).getPersonID());
		Intent i = new Intent(getActivity(), LoanHistoryActivity.class);
		i.putExtras(b);
		getActivity().startActivity(i);
	}
}