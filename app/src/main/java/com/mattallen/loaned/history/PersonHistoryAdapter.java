package com.mattallen.loaned.history;

import java.util.ArrayList;

import com.mattallen.loaned.Item;
import com.mattallen.loaned.ItemTypeLookup;
import com.mattallen.loaned.Loan;
import com.mattallen.loaned.R;
import com.mattallen.loaned.settings.SettingsFragment;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class PersonHistoryAdapter extends ArrayAdapter<Loan> {

	private static final String					TAG = PersonHistoryAdapter.class.getSimpleName();

	private ArrayList<Loan>						mLoans;
	private ArrayList<Item>						mItems;
	private Context								mContext;

	public PersonHistoryAdapter(Context context, ArrayList<Loan> objects, ArrayList<Item> itemLookup) {
		super(context, R.layout.fragment_loanhistory_peopleitem, objects);
		mLoans = objects;
		mItems = itemLookup;
		mContext = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		View v = convertView;
		if(v==null){
			LayoutInflater vi = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.fragment_loanhistory_peopleitem, null);
		}
		Loan loan = mLoans.get(position);
		if(loan!=null){
			TextView status = (TextView)v.findViewById(R.id.loanhistory_peopleitem_status);
			ImageView pic = (ImageView)v.findViewById(R.id.loanhistory_peopleitem_pic);
			ImageView indicator = (ImageView)v.findViewById(R.id.loanhistory_peopleitem_indicator);
			TextView dates = (TextView)v.findViewById(R.id.loanhistory_peopleitem_dates);
			TextView name = (TextView)v.findViewById(R.id.loanhistory_peopleitem_name);
			for(int i=0;i<=mItems.size()-1;i++){
				if(mItems.get(i).getItemID()==loan.getItemID()){
					name.setText(mItems.get(i).getName());
					pic.setImageResource(ItemTypeLookup.getDrawableForType(mItems.get(i).getType()));
				}
			}
			String statusText = "";
			if(loan.getReturnDate()==null){
				Log.d(TAG,"Item still on loan");
				statusText = getContext().getResources().getString(R.string.loanhistory_stillonloan);
				dates.setText(SettingsFragment.getFormattedDate(mContext, loan.getStartDate()));
			} else {
				Log.d(TAG,"Not on loan anymore, calculating time difference");
				Log.d(TAG,"Started: "+loan.getStartDate().getTime()+"\nReturned: "+loan.getReturnDate().getTime());
				long timeDifference = loan.getReturnDate().getTime()-loan.getStartDate().getTime();
				int days = (int) (timeDifference / (1000*60*60*24));
				Log.d(TAG,"Time difference: "+timeDifference+" ("+days+" days)");
				if(days==0){
					statusText = getContext().getResources().getString(R.string.loanhistory_loanedlessthanaday);
				} else {
					statusText = String.format(getContext().getResources().getString(R.string.loanhistory_daysloaned), days);
				}
				indicator.setVisibility(ImageView.INVISIBLE);
				dates.setText(SettingsFragment.getFormattedDate(mContext, loan.getStartDate()) +
						" - "+SettingsFragment.getFormattedDate(mContext, loan.getReturnDate()));
			}
			status.setText(statusText);
			v.setTag(loan);
		}
		return v;
	}
}