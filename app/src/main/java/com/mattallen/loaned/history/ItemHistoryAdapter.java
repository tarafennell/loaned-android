package com.mattallen.loaned.history;

import java.util.ArrayList;

import com.mattallen.loaned.Loan;
import com.mattallen.loaned.Person;
import com.mattallen.loaned.R;
import com.mattallen.loaned.RetrieveContactPhoto;
import com.mattallen.loaned.settings.SettingsFragment;
import com.mattallen.loaned.views.RoundedImageView;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ItemHistoryAdapter extends ArrayAdapter<Loan> {

	private static final String				TAG = ItemHistoryAdapter.class.getSimpleName();

	private ArrayList<Loan>					mLoans;
	private ArrayList<Person>				mPeople;
	private Context							mContext;

	public ItemHistoryAdapter(Context context, ArrayList<Loan> objects, ArrayList<Person> peopleLookup) {
		super(context, R.layout.fragment_loanhistory_item, objects);
		mLoans = objects;
		mPeople = peopleLookup;
		mContext = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		View v = convertView;
		if(v==null){
			LayoutInflater vi = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.fragment_loanhistory_item, null);
		}
		Loan loan = mLoans.get(position);
		if(loan!=null){
			TextView name = (TextView)v.findViewById(R.id.loanhistory_item_name);
			TextView dates = (TextView)v.findViewById(R.id.loanhistory_item_dates);
			RoundedImageView pic = (RoundedImageView)v.findViewById(R.id.loanhistory_item_pic);
			TextView status = (TextView)v.findViewById(R.id.loanhistory_item_status);
			ImageView indicator = (ImageView)v.findViewById(R.id.loanhistory_item_indicator);
			for(int i=0;i<=mPeople.size()-1;i++){
				if(mPeople.get(i).getPersonID()==loan.getPersonID()){
					name.setText(mPeople.get(i).getName());
					new RetrieveContactPhoto(mPeople.get(i).getLookupURI(), pic, mContext, R.drawable.friend_image_light).execute();
				}
			}
			String statusText = "";
			if(loan.getReturnDate()==null){
				Log.d(TAG,"Item still on loan");
				statusText = getContext().getString(R.string.loanhistory_stillonloan);
				dates.setText(SettingsFragment.getFormattedDate(mContext, loan.getStartDate()));
			} else {
				Log.d(TAG,"Not on loan anymore, calculating time difference");
				Log.d(TAG,"Started: "+loan.getStartDate().getTime()+"\nReturned: "+loan.getReturnDate().getTime());
				long timeDifference = loan.getReturnDate().getTime()-loan.getStartDate().getTime();
				int days = (int) (timeDifference / (1000*60*60*24));
				Log.d(TAG,"Time difference: "+timeDifference+" ("+days+" days)");
				if(days==0){
					statusText = getContext().getString(R.string.loanhistory_loanedlessthanaday);
				} else {
					statusText = String.format(getContext().getString(R.string.loanhistory_daysloaned),days);
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