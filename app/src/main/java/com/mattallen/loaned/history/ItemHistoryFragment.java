package com.mattallen.loaned.history;

import java.text.ParseException;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mattallen.loaned.Item;
import com.mattallen.loaned.Loan;
import com.mattallen.loaned.Person;
import com.mattallen.loaned.R;

public class ItemHistoryFragment extends LoanHistoryFragment implements OnItemLongClickListener {

	private ListView						mList;
	private TextView						mEmptyState;
	private ProgressBar						mProgress;
	private Item							mItem;
	private ArrayList<Loan>					mLoans;
	private ItemHistoryAdapter				mAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View v = inflater.inflate(R.layout.fragment_loanhistory, container, false);
		mProgress = (ProgressBar)v.findViewById(R.id.loanhistory_progress);
		mEmptyState = (TextView)v.findViewById(R.id.loanhistory_empty);
		mEmptyState.setText(R.string.loanhistory_noloans_item);
		mList = (ListView)v.findViewById(R.id.loanhistory_list);
		mItem = ((LoanHistoryActivity)getActivity()).getItem();
		mList.setOnItemLongClickListener(this);
		updateInformation();
		return v;
	}

	@Override
	public void updateInformation() {
		new UpdateList().execute();
	}

	private class UpdateList extends AsyncTask<Void, Void, Exception>{
		private ArrayList<Person> mPeople;
		@Override
		protected void onPreExecute(){
			mProgress.setVisibility(ProgressBar.VISIBLE);
			mList.setVisibility(ListView.INVISIBLE);
			mEmptyState.setVisibility(TextView.INVISIBLE);
		}
		@Override
		protected Exception doInBackground(Void... params) {
			try{
				mLoans = ((LoanHistoryActivity)getActivity()).getLoans(mItem);
				mPeople = ((LoanHistoryActivity)getActivity()).getAllPeople();
			} catch (Exception e){
				return e;
			}
			return null;
		}
		@Override
		protected void onPostExecute(Exception e){
			mProgress.setVisibility(ProgressBar.INVISIBLE);
			if(e!=null){
				Toast.makeText(getActivity(), R.string.error_gettingloans, Toast.LENGTH_SHORT).show();
				e.printStackTrace();
			}
			if(mLoans!=null && mLoans.size()>0){
				mAdapter = new ItemHistoryAdapter(getActivity(), mLoans, mPeople);
				mList.setAdapter(mAdapter);
				mList.setVisibility(ListView.VISIBLE);
			} else {
				mEmptyState.setVisibility(TextView.VISIBLE);
			}
		}
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		final Loan loan = (Loan)arg1.getTag();
		if(loan!=null){
			String[] items;
			if(loan.getReturnDate()!=null){
				items = new String[3];
				items[0] = getActivity().getResources().getString(R.string.dialog_deleteloan);
				items[1] = (loan.isNotifying())?getActivity().getResources().getString(R.string.dialog_notification_off):
					getActivity().getResources().getString(R.string.dialog_notification_on);
				items[2] = getActivity().getResources().getString(R.string.dialog_notreturned);
			} else {
				items = new String[2];
				items[0] = getActivity().getResources().getString(R.string.dialog_deleteloan);
				items[1] = (loan.isNotifying())?getActivity().getResources().getString(R.string.dialog_notification_off):
					getActivity().getResources().getString(R.string.dialog_notification_on);
			}
			AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
			dialog.setTitle(R.string.dialog_loanhistory_longclick).setNegativeButton(R.string.cancel, null);
			dialog.setItems(items, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					switch (which) {
					case 0: // Delete
						AlertDialog.Builder dialog1 = new AlertDialog.Builder(getActivity());
						dialog1.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								((LoanHistoryActivity)getActivity()).deleteLoan(loan);
							}
						}).setTitle(R.string.dialog_deleteloan).setMessage(R.string.dialog_deleteloan_message)
						.setNegativeButton(R.string.no, null).show();
						break;
					case 1: // Turn Notifications On/Off
						if(loan.isNotifying()){
							loan.setNotifying(false); // Is notifying, turn off
						} else {
							loan.setNotifying(true); // Isn't notifying, turn on
						}
						break;
					case 2: // Mark as not Returned
						try {
							if(!((LoanHistoryActivity)getActivity()).isItemOnLoan(loan.getItemID())){
								// Item is not currently on loan
								loan.setReturnDate(null);
								//mPerson.setItemsOnLoan(mPerson.getItemsOnLoan()+1);
								Person person = ((LoanHistoryActivity)getActivity()).getPersonByID(loan.getPersonID());
								person.setItemsOnLoan(person.getItemsOnLoan()+1);
								mItem.setCurrentlyOnLoan(true);
								((LoanHistoryActivity)getActivity()).updateItem(mItem);
								((LoanHistoryActivity)getActivity()).updateLoan(loan);
								((LoanHistoryActivity)getActivity()).updatePerson(person);
							} else {
								AlertDialog.Builder dialog2 = new AlertDialog.Builder(getActivity());
								dialog2.setTitle(R.string.loanhistory_itemdetails_onloan).setMessage(R.string.dialog_alreadyonloan_message)
								.setPositiveButton(R.string.okay, null).show();
							}
						} catch (ParseException e) {
							Toast.makeText(getActivity(), R.string.error_unable_to_get_loan, Toast.LENGTH_SHORT).show();
							e.printStackTrace();
						}
						break;
					default:
						break;
					}
				}
			}).show();
			return true;
		}
		return false;
	}
}