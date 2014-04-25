package com.mattallen.loaned.main;

import java.text.ParseException;
import java.util.ArrayList;

import com.mattallen.loaned.Item;
import com.mattallen.loaned.Loan;
import com.mattallen.loaned.Person;
import com.mattallen.loaned.R;
import com.mattallen.loaned.storage.DatabaseManager;
import com.mattallen.loaned.views.ItemLoanPieChartView;
import com.mattallen.loaned.views.PersonBarChartView;

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
import android.widget.ProgressBar;
import android.widget.TextView;

public class StatsFragment extends Fragment {

	private static final String					TAG = StatsFragment.class.getSimpleName();

	private ProgressBar							mProgress;
	private TextView							mEmptyState, mAverageReturn, mAverageReturnLbl, mPieChartLbl, mBarChartlbl;
	private int									mAverageReturnAmount = 0;
	private DatabaseManager						mDB;
	private ItemLoanPieChartView				mPieChart;
	private PersonBarChartView					mBarChart;

	private ArrayList<Loan>						mLoans;
	private ArrayList<Person>					mPeople;
	private ArrayList<Item>						mItems;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true); // Tell the activity that we have ActionBar items
	}

	@Override
	public void onCreateOptionsMenu(Menu menu,MenuInflater inf){
		super.onCreateOptionsMenu(menu, inf);
        // Check if the menu already has items in it.
        // Otherwise we could cause a duplicate menu item issue.
		if(menu.size()==0)inf.inflate(R.menu.stats, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		case R.id.action_refresh:
			updateData();
			return true;
		default:
			break;
		}
		return false;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View v = inflater.inflate(R.layout.fragment_stats, container, false);
		mProgress = (ProgressBar)v.findViewById(R.id.stats_progress);
		mEmptyState = (TextView)v.findViewById(R.id.stats_empty);
		mAverageReturn = (TextView)v.findViewById(R.id.stats_averagereturn);
		mAverageReturnLbl = (TextView)v.findViewById(R.id.stats_averagereturn_label);
		mPieChart = (ItemLoanPieChartView)v.findViewById(R.id.stats_piechart);
		mPieChartLbl = (TextView)v.findViewById(R.id.stats_piechart_label);
		mBarChart = (PersonBarChartView)v.findViewById(R.id.stats_barchart);
		mBarChartlbl = (TextView)v.findViewById(R.id.stats_barchart_label);
		mDB = new DatabaseManager(getActivity());
		updateData();
		return v;
	}

	public void updateData(){
		Log.d(TAG,"Updating UI");
		new UpdateData().execute();
	}

	private class UpdateData extends AsyncTask<Void, Void, Void>{
		@Override
		protected void onPreExecute(){
			mProgress.setVisibility(ProgressBar.VISIBLE);
			mEmptyState.setVisibility(TextView.INVISIBLE);
			mAverageReturn.setVisibility(TextView.GONE);
			mAverageReturnLbl.setVisibility(TextView.GONE);
			mPieChartLbl.setVisibility(TextView.GONE);
			mPieChart.setVisibility(View.GONE);
			mBarChartlbl.setVisibility(TextView.GONE);
			mBarChart.setVisibility(View.GONE);
			mAverageReturnAmount = 0;
		}
		@Override
		protected Void doInBackground(Void... params) {
			try {
				mLoans = mDB.getAllLoans();
				int count=0,total=0;
				for(int i=0;i<=mLoans.size()-1;i++){
					if(mLoans.get(i).getReturnDate()!=null){
						long timeDifference = mLoans.get(i).getReturnDate().getTime()-mLoans.get(i).getStartDate().getTime();
						total += (int) (timeDifference / (1000*60*60*24));
						count++;
					}
				}
				if(mLoans.size()>0 && total>0){
					mAverageReturnAmount=total/count;
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
			mItems = mDB.getAllItems();
			// Put into the pie chart
			mPeople = mDB.getAllPeople();
			// Put into the bar chart
			return null;
		}
		@Override
		protected void onPostExecute(Void no){
			mProgress.setVisibility(ProgressBar.INVISIBLE);
			if(mLoans==null && mItems==null && mPeople==null){
				mEmptyState.setVisibility(TextView.VISIBLE);
			}
			if(mLoans==null || mLoans.size()==0){
				mEmptyState.setVisibility(TextView.VISIBLE);
			} else {
				mAverageReturn.setVisibility(TextView.VISIBLE);
				mAverageReturnLbl.setVisibility(TextView.VISIBLE);
				// Average was probably calculated correctly
				mAverageReturn.setText(String.format(getActivity().getResources().getString(R.string.stats_averageloan_value),
						mAverageReturnAmount));
				if(mItems!=null){
					mPieChartLbl.setVisibility(TextView.VISIBLE);
					mPieChart.setVisibility(View.VISIBLE);
					mPieChart.setData(mItems);
				}
				if(mPeople!=null){
					mBarChartlbl.setVisibility(TextView.VISIBLE);
					mBarChart.setVisibility(View.VISIBLE);
					mBarChart.setData(mPeople);
				}
			}
		}
	}
}